package com.amazonaws.codepipeline.jobworker.plugin.thirdparty;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.codepipeline.jobworker.Validator;
import com.amazonaws.codepipeline.jobworker.model.ActionType;
import com.amazonaws.codepipeline.jobworker.JobService;
import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.services.codepipeline.AmazonCodePipelineClient;
import com.amazonaws.services.codepipeline.model.AcknowledgeThirdPartyJobRequest;
import com.amazonaws.services.codepipeline.model.AcknowledgeThirdPartyJobResult;
import com.amazonaws.services.codepipeline.model.GetThirdPartyJobDetailsRequest;
import com.amazonaws.services.codepipeline.model.GetThirdPartyJobDetailsResult;
import com.amazonaws.services.codepipeline.model.PollForThirdPartyJobsRequest;
import com.amazonaws.services.codepipeline.model.PollForThirdPartyJobsResult;
import com.amazonaws.services.codepipeline.model.PutThirdPartyJobFailureResultRequest;
import com.amazonaws.services.codepipeline.model.PutThirdPartyJobSuccessResultRequest;
import com.amazonaws.services.codepipeline.model.ThirdPartyJob;
import com.amazonaws.services.codepipeline.model.ThirdPartyJobDetails;

/**
 * Service interface wrapper for the third party job api.
 */
public class ThirdPartyJobService implements JobService {
    private static final Logger LOGGER = Logger.getLogger(ThirdPartyJobService.class);

    private final AmazonCodePipelineClient codePipelineClient;
    private final ActionType actionType;
    private final ClientTokenProvider clientTokenProvider;

    /**
     * Initializes the third party job service wrapper.
     * @param codePipelineClient service client for the AWS CodePipeline api.
     * @param actionType action type to poll for.
     * @param clientTokenProvider client token provider to look up client token by client id
     *                            in order to get the job details.
     */
    public ThirdPartyJobService(final AmazonCodePipelineClient codePipelineClient, final ActionType actionType, final ClientTokenProvider clientTokenProvider) {
        Validator.notNull(codePipelineClient);
        Validator.notNull(actionType);
        Validator.notNull(clientTokenProvider);

        this.codePipelineClient = codePipelineClient;
        this.actionType = actionType;
        this.clientTokenProvider = clientTokenProvider;
    }

    /**
     * Polls for jobs for the configured action type of the job worker.
     * @param maxBatchSize maximum number of jobs to be returned by the poll api.
     * @return List of work items.
     */
    public List<WorkItem> pollForJobs(final int maxBatchSize) {
        LOGGER.debug(String.format("PollForThirdPartyJobs for action type %s", actionType));
        final List<WorkItem> result = new ArrayList<>();

        final PollForThirdPartyJobsRequest pollForJobsRequest = new PollForThirdPartyJobsRequest();
        pollForJobsRequest.setActionType(getActionType());
        pollForJobsRequest.setMaxBatchSize(maxBatchSize);

        final PollForThirdPartyJobsResult pollForJobsResult = codePipelineClient.pollForThirdPartyJobs(pollForJobsRequest);
        for (final ThirdPartyJob job : pollForJobsResult.getJobs()) {
            LOGGER.debug("GetThirdPartyJobDetails");
            final ThirdPartyJobDetails jobDetails = getJobDetails(job.getJobId(), job.getClientId());
            result.add(ThirdPartyJobConverter.convert(job.getClientId(), jobDetails));
        }
        return result;
    }

    /**
     * Acknowledges a job to indicate that the job worker started working on it.
     * If a job is not acknowledged in time it will be handed out another time by the poll for jobs api.
     * @param jobId job id
     * @param clientId client id
     * @param nonce job nonce
     * @return job status to indicate if the job worker should continue working on it
     */
    public JobStatus acknowledgeJob(final String jobId, final String clientId, final String nonce) {
        LOGGER.debug(String.format("AcknowledgeThirdPartyJob %s with clientId %s and nonce %s", jobId, clientId, nonce));
        final AcknowledgeThirdPartyJobRequest request = new AcknowledgeThirdPartyJobRequest();
        request.setJobId(jobId);
        request.setNonce(nonce);
        request.setClientToken(clientTokenProvider.lookupClientSecret(clientId));
        final AcknowledgeThirdPartyJobResult result = codePipelineClient.acknowledgeThirdPartyJob(request);
        return JobStatus.valueOf(result.getStatus());
    }

    /**
     * Marks a job as successful.
     * @param jobId job id
     * @param clientId client id
     * @param executionDetails execution details
     * @param currentRevision current revision
     * @param continuationToken continuation token
     */
    public void putJobSuccess(final String jobId,
                              final String clientId,
                              final ExecutionDetails executionDetails,
                              final CurrentRevision currentRevision,
                              final String continuationToken) {
        LOGGER.debug(String.format("PutThirdPartyJobSuccessResult %s", jobId));
        final PutThirdPartyJobSuccessResultRequest request = new PutThirdPartyJobSuccessResultRequest();
        request.setJobId(jobId);
        request.setClientToken(clientTokenProvider.lookupClientSecret(clientId));
        request.setExecutionDetails(ThirdPartyJobConverter.convert(executionDetails));
        request.setCurrentRevision(ThirdPartyJobConverter.convert(currentRevision));
        request.setContinuationToken(continuationToken);
        codePipelineClient.putThirdPartyJobSuccessResult(request);
    }

    /**
     * Marks a job as failed.
     * @param jobId job id
     * @param clientId client id
     * @param failureDetails failure details
     */
    public void putJobFailure(final String jobId, final String clientId, final FailureDetails failureDetails) {
        LOGGER.debug(String.format("PutThirdPartyJobFailureResult %s", jobId));
        final PutThirdPartyJobFailureResultRequest request = new PutThirdPartyJobFailureResultRequest();
        request.setJobId(jobId);
        request.setClientToken(clientTokenProvider.lookupClientSecret(clientId));
        request.setFailureDetails(ThirdPartyJobConverter.convert(failureDetails));
        codePipelineClient.putThirdPartyJobFailureResult(request);
    }

    private ThirdPartyJobDetails getJobDetails(final String jobId, final String clientId) {
        final GetThirdPartyJobDetailsRequest getJobDetailsRequest = new GetThirdPartyJobDetailsRequest();
        getJobDetailsRequest.setJobId(jobId);
        getJobDetailsRequest.setClientToken(clientTokenProvider.lookupClientSecret(clientId));
        final GetThirdPartyJobDetailsResult getJobDetailsResult = codePipelineClient.getThirdPartyJobDetails(getJobDetailsRequest);
        return getJobDetailsResult.getJobDetails();
    }

    private com.amazonaws.services.codepipeline.model.ActionType getActionType() {
        return ThirdPartyJobConverter.convert(actionType);
    }
}

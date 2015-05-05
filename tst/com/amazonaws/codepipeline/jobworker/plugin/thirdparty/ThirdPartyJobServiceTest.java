package com.amazonaws.codepipeline.jobworker.plugin.thirdparty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.codepipeline.jobworker.JobService;
import com.amazonaws.codepipeline.jobworker.model.ActionType;
import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureType;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.plugin.JobAssertion;
import com.amazonaws.services.codepipeline.AmazonCodePipelineClient;
import com.amazonaws.services.codepipeline.model.AcknowledgeThirdPartyJobRequest;
import com.amazonaws.services.codepipeline.model.AcknowledgeThirdPartyJobResult;
import com.amazonaws.services.codepipeline.model.GetThirdPartyJobDetailsResult;
import com.amazonaws.services.codepipeline.model.PollForThirdPartyJobsResult;
import com.amazonaws.services.codepipeline.model.PutThirdPartyJobFailureResultRequest;
import com.amazonaws.services.codepipeline.model.PutThirdPartyJobSuccessResultRequest;
import com.amazonaws.services.codepipeline.model.ThirdPartyJob;
import com.amazonaws.services.codepipeline.model.ThirdPartyJobDetails;

public class ThirdPartyJobServiceTest {

    private JobService jobService;
    private ActionType actionType = new ActionType("Build", "ThirdParty", "MyProvider", "1.0");

    @Mock
    private AmazonCodePipelineClient codePipelineClient;

    @Captor
    private ArgumentCaptor<PutThirdPartyJobSuccessResultRequest> putThirdPartyJobSuccessResultRequestCaptor;

    @Captor
    private ArgumentCaptor<PutThirdPartyJobFailureResultRequest> putThirdPartyJobFailureResultRequestCaptor;

    @Captor
    private ArgumentCaptor<AcknowledgeThirdPartyJobRequest> acknowledgeThirdPartyJobRequestCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobService = new ThirdPartyJobService(codePipelineClient, actionType, new DefaultClientTokenProvider());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenCodePipelineClientIsNull() {
        new ThirdPartyJobService(null, actionType, new DefaultClientTokenProvider());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenActionTypeIsNull() {
        new ThirdPartyJobService(codePipelineClient, null, new DefaultClientTokenProvider());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenClientTokenProviderIsNull() {
        new ThirdPartyJobService(codePipelineClient, actionType, null);
    }

    @Test
    public void shouldCallPollForThirdPartyJobsAndGetThirdPartyJobDetails() {
        // given
        final PollForThirdPartyJobsResult pollForThirdPartyJobsResult = generatePollForThirdPartyJobsResult();
        final ThirdPartyJob thirdPartyJob = pollForThirdPartyJobsResult.getJobs().get(0);
        when(codePipelineClient.pollForThirdPartyJobs(any()))
                .thenReturn(pollForThirdPartyJobsResult);
        final GetThirdPartyJobDetailsResult getThirdPartyJobDetailsResult = generateGetThirdPartyJobDetailsResult(
                thirdPartyJob.getJobId());
        when(codePipelineClient.getThirdPartyJobDetails(any())).
                thenReturn(getThirdPartyJobDetailsResult);

        // when
        final List<WorkItem> workItems = jobService.pollForJobs(5);

        // then
        assertNotNull(workItems);
        assertEquals(1, workItems.size());
        final WorkItem workItem = workItems.get(0);
        assertNotNull(workItem);
        assertEquals(thirdPartyJob.getJobId(), workItem.getJobId());
        assertEquals(thirdPartyJob.getClientId(), workItem.getClientId());
        assertEquals(getThirdPartyJobDetailsResult.getJobDetails().getNonce(), workItem.getJobNonce());
        JobAssertion.assertJobDataEquals(getThirdPartyJobDetailsResult.getJobDetails().getData(), workItem.getJobData());
    }

    @Test
    public void shouldCallAcknowledgeThirdPartyJob() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();
        final String nonce = UUID.randomUUID().toString();

        final AcknowledgeThirdPartyJobResult acknowledgeThirdPartyJobResult = new AcknowledgeThirdPartyJobResult();
        acknowledgeThirdPartyJobResult.setStatus(com.amazonaws.services.codepipeline.model.JobStatus.Failed);
        when(codePipelineClient.acknowledgeThirdPartyJob(acknowledgeThirdPartyJobRequestCaptor.capture()))
                .thenReturn(acknowledgeThirdPartyJobResult);

        // when
        final JobStatus jobStatus = jobService.acknowledgeJob(jobId, clientId, nonce);

        // then
        assertEquals(jobStatus, JobStatus.Failed);
        final AcknowledgeThirdPartyJobRequest request = acknowledgeThirdPartyJobRequestCaptor.getValue();
        assertEquals(jobId, request.getJobId());
        assertEquals(DefaultClientTokenProvider.DEFAULT_CLIENT_TOKEN, request.getClientToken());
        assertEquals(nonce, request.getNonce());
    }

    @Test
    public void shouldCallPutThirdPartyJobSuccessResult() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();

        // when
        jobService.putJobSuccess(jobId, clientId, null, null, null);

        // then
        verify(codePipelineClient).putThirdPartyJobSuccessResult(any());
    }

    @Test
    public void shouldCallPutThirdPartyJobSuccessResultWithTheCorrectArguments() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();
        final ExecutionDetails executionDetails = new ExecutionDetails("summary", UUID.randomUUID().toString(), 90);
        final CurrentRevision currentRevision = new CurrentRevision("revision id", "change identifier");
        final String continuationToken = UUID.randomUUID().toString();

        // when
        jobService.putJobSuccess(jobId, clientId, executionDetails, currentRevision, continuationToken);

        // then
        verify(codePipelineClient).putThirdPartyJobSuccessResult(putThirdPartyJobSuccessResultRequestCaptor.capture());

        final PutThirdPartyJobSuccessResultRequest request = putThirdPartyJobSuccessResultRequestCaptor.getValue();
        assertEquals(jobId, request.getJobId());
        assertEquals(DefaultClientTokenProvider.DEFAULT_CLIENT_TOKEN, request.getClientToken());
        assertEquals(executionDetails.getSummary(), request.getExecutionDetails().getSummary());
        assertEquals(executionDetails.getExternalExecutionId(), request.getExecutionDetails().getExternalExecutionId());
        assertEquals(executionDetails.getPercentComplete(), request.getExecutionDetails().getPercentComplete().intValue());
        assertEquals(currentRevision.getRevision(), request.getCurrentRevision().getRevision());
        assertEquals(currentRevision.getChangeIdentifier(), request.getCurrentRevision().getChangeIdentifier());
        assertEquals(continuationToken, request.getContinuationToken());
    }

    @Test
    public void shouldCallPutThirdPartyJobFailureResult() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();
        final FailureDetails failureDetails = new FailureDetails(FailureType.JobFailed, "test message");

        // when
        jobService.putJobFailure(jobId, clientId, failureDetails);

        // then
        verify(codePipelineClient).putThirdPartyJobFailureResult(putThirdPartyJobFailureResultRequestCaptor.capture());

        final PutThirdPartyJobFailureResultRequest request = putThirdPartyJobFailureResultRequestCaptor.getValue();
        assertEquals(jobId, request.getJobId());
        assertEquals(DefaultClientTokenProvider.DEFAULT_CLIENT_TOKEN, request.getClientToken());
        assertEquals(failureDetails.getType().toString(), request.getFailureDetails().getType());
        assertEquals(failureDetails.getMessage(), request.getFailureDetails().getMessage());
    }

    private PollForThirdPartyJobsResult generatePollForThirdPartyJobsResult() {
        final ThirdPartyJob thirdPartyJob = new ThirdPartyJob();
        thirdPartyJob.setJobId(UUID.randomUUID().toString());
        thirdPartyJob.setClientId(UUID.randomUUID().toString());
        final PollForThirdPartyJobsResult result = new PollForThirdPartyJobsResult();
        result.setJobs(Arrays.asList(thirdPartyJob));
        return result;
    }

    private GetThirdPartyJobDetailsResult generateGetThirdPartyJobDetailsResult(final String jobId) {
        final com.amazonaws.services.codepipeline.model.ThirdPartyJobData jobData = new com.amazonaws.services.codepipeline.model.ThirdPartyJobData();
        jobData.setContinuationToken(UUID.randomUUID().toString());

        final ThirdPartyJobDetails thirdPartyJobDetails = new ThirdPartyJobDetails();
        thirdPartyJobDetails.setId(jobId);
        thirdPartyJobDetails.setNonce(UUID.randomUUID().toString());
        thirdPartyJobDetails.setData(jobData);
        final GetThirdPartyJobDetailsResult result = new GetThirdPartyJobDetailsResult();
        result.setJobDetails(thirdPartyJobDetails);
        return result;
    }
}

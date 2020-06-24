/*
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.amazonaws.codepipeline.jobworker.plugin.customaction;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.codepipeline.jobworker.JobService;
import com.amazonaws.codepipeline.jobworker.Validator;
import com.amazonaws.codepipeline.jobworker.model.ActionTypeId;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;

import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.plugin.JobConverter;
import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.model.AcknowledgeJobRequest;
import com.amazonaws.services.codepipeline.model.AcknowledgeJobResult;
import com.amazonaws.services.codepipeline.model.Job;
import com.amazonaws.services.codepipeline.model.PollForJobsRequest;
import com.amazonaws.services.codepipeline.model.PollForJobsResult;
import com.amazonaws.services.codepipeline.model.PutJobFailureResultRequest;
import com.amazonaws.services.codepipeline.model.PutJobSuccessResultRequest;

import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;

/**
 * Service interface wrapper for the custom action job api.
 */
public class CustomActionJobService implements JobService {
    private static final Logger LOGGER = LogManager.getLogger(CustomActionJobService.class);

    private final AWSCodePipeline codePipelineClient;
    private final ActionTypeId actionTypeId;

    /**
     * Initializes the custom action job service wrapper.
     * @param codePipelineClient service client for the AWS CodePipeline api.
     * @param actionTypeId action type id to poll for.
     */
    public CustomActionJobService(final AWSCodePipeline codePipelineClient, final ActionTypeId actionTypeId) {
        Validator.notNull(codePipelineClient);
        Validator.notNull(actionTypeId);

        this.codePipelineClient = codePipelineClient;
        this.actionTypeId = actionTypeId;
    }

    /**
     * Polls for jobs for the configured action type of the job worker.
     * @param maxBatchSize maximum number of jobs to be returned by the poll api.
     * @return List of work items.
     */
    @Override
    public List<WorkItem> pollForJobs(final int maxBatchSize) {
        LOGGER.info(String.format("PollForJobs for action type id %s", actionTypeId));
        final List<WorkItem> result = new ArrayList<>();

        final PollForJobsRequest pollForJobsRequest = new PollForJobsRequest();
        pollForJobsRequest.setActionTypeId(getActionTypeId());
        pollForJobsRequest.setMaxBatchSize(maxBatchSize);

        final PollForJobsResult pollForJobsResult = codePipelineClient.pollForJobs(pollForJobsRequest);
        for (final Job job : pollForJobsResult.getJobs()) {
            result.add(JobConverter.convert(job));
        }
        return result;
    }

    /**
     * Acknowledges a job to indicate that the job worker started working on it.
     * If a job is not acknowledged in time it will be handed out another time by the poll for jobs api.
     * @param jobId job id
     * @param clientId aws account id
     * @param nonce job nonce
     * @return job status to indicate if the job worker should continue working on it
     */
    @Override
    public JobStatus acknowledgeJob(final String jobId, final String clientId, final String nonce) {
        LOGGER.info(String.format("AcknowledgeJob for job '%s' and nonce '%s'", jobId, nonce));
        final AcknowledgeJobRequest request = new AcknowledgeJobRequest();
        request.setJobId(jobId);
        request.setNonce(nonce);
        final AcknowledgeJobResult result = codePipelineClient.acknowledgeJob(request);
        return JobStatus.valueOf(result.getStatus());
    }

    /**
     * Marks a job as successful.
     * @param jobId job id
     * @param clientId aws account id
     * @param executionDetails execution details
     * @param currentRevision current revision
     * @param continuationToken continuation token
     */
    @Override
    public void putJobSuccess(final String jobId,
                              final String clientId,
                              final ExecutionDetails executionDetails,
                              final CurrentRevision currentRevision,
                              final String continuationToken) {
        LOGGER.info(String.format("PutJobSuccessResult for job '%s'", jobId));
        final PutJobSuccessResultRequest request = new PutJobSuccessResultRequest();
        request.setJobId(jobId);
        request.setExecutionDetails(JobConverter.convert(executionDetails));
        request.setCurrentRevision(JobConverter.convert(currentRevision));
        request.setContinuationToken(continuationToken);
        codePipelineClient.putJobSuccessResult(request);
    }

    /**
     * Marks a job as failed.
     * @param jobId job id
     * @param clientId aws account id
     * @param failureDetails failure details
     */
    @Override
    public void putJobFailure(final String jobId, final String clientId, final FailureDetails failureDetails) {
        LOGGER.info(String.format("PutJobFailureResult for job '%s'", jobId));
        final PutJobFailureResultRequest request = new PutJobFailureResultRequest();
        request.setJobId(jobId);
        request.setFailureDetails(JobConverter.convert(failureDetails));
        codePipelineClient.putJobFailureResult(request);
    }

    private com.amazonaws.services.codepipeline.model.ActionTypeId getActionTypeId() {
        return JobConverter.convert(actionTypeId);
    }
}

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
package com.amazonaws.codepipeline.jobworker;

import java.util.List;

import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;

/**
 * Service interface wrapper for the job provider.
 */
public interface JobService {
    /**
     * Polls for jobs for the configured action type of the job worker.
     * @param maxBatchSize maximum number of jobs to be returned by the poll api.
     * @return List of work items.
     */
    List<WorkItem> pollForJobs(int maxBatchSize);

    /**
     * Acknowledges a job to indicate that the job worker started working on it.
     * If a job is not acknowledged in time it will be handed out another time by the poll for jobs api.
     * @param jobId job id
     * @param clientId client id
     * @param nonce job nonce
     * @return job status to indicate if the job worker should continue working on it
     */
    JobStatus acknowledgeJob(String jobId, String clientId, String nonce);

    /**
     * Marks a job as successful.
     * @param jobId job id
     * @param clientId client id
     * @param executionDetails execution details
     * @param currentRevision current revision
     * @param continuationToken continuation token
     */
    void putJobSuccess(String jobId,
                       String clientId,
                       ExecutionDetails executionDetails,
                       CurrentRevision currentRevision,
                       String continuationToken);

    /**
     * Marks a job as failed.
     * @param jobId job id
     * @param clientId client id
     * @param failureDetails failure details
     */
    void putJobFailure(String jobId,
                       String clientId,
                       FailureDetails failureDetails);
}

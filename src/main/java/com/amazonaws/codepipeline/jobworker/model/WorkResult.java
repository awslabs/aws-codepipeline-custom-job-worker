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
package com.amazonaws.codepipeline.jobworker.model;

import com.amazonaws.codepipeline.jobworker.Validator;

/**
 * Structure containing the result of the work item processing.
 */
public class WorkResult {
    private final String jobId;
    private final WorkResultStatus status;
    private final ExecutionDetails executionDetails;
    private final CurrentRevision currentRevision;
    private final String continuationToken;
    private final FailureDetails failureDetails;

    /**
     * Initializes a work result with success status.
     * use static methods to initialize.
     * @param jobId job id
     * @param executionDetails execution details
     * @param currentRevision current revision
     * @param continuationToken continuation token, indicates that the job is asynchronous and not finished yet.
     *                          additional job will be scheduled to request status update.
     */
    private WorkResult(final String jobId,
                       final ExecutionDetails executionDetails,
                       final CurrentRevision currentRevision,
                       final String continuationToken) {
        Validator.notNull(jobId);
        this.jobId = jobId;
        this.status = WorkResultStatus.Success;
        this.executionDetails = executionDetails;
        this.currentRevision = currentRevision;
        this.continuationToken = continuationToken;
        this.failureDetails = null;
    }

    /**
     * Initializes a work result with failed status.
     * use static methods to initialize.
     * @param jobId job id
     * @param failureDetails failure details
     */
    private WorkResult(final String jobId, final FailureDetails failureDetails) {
        Validator.notNull(jobId);
        Validator.notNull(failureDetails);
        this.jobId = jobId;
        this.status = WorkResultStatus.Failure;
        this.executionDetails = null;
        this.currentRevision = null;
        this.continuationToken = null;
        this.failureDetails = failureDetails;
    }

    /**
     * @return job id
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return job status
     */
    public WorkResultStatus getStatus() {
        return status;
    }

    /**
     * @return execution details
     */
    public ExecutionDetails getExecutionDetails() {
        return executionDetails;
    }

    /**
     * @return current revision
     */
    public CurrentRevision getCurrentRevision() {
        return currentRevision;
    }

    /**
     * @return continuation token, indicates that the job is asynchronous and not finished yet.
     *         additional job will be scheduled to request status update.
     */
    public String getContinuationToken() {
        return continuationToken;
    }

    /**
     * @return failure details
     */
    public FailureDetails getFailureDetails() {
        return failureDetails;
    }

    /**
     * Initializes the work result with success status.
     * @param jobId job id
     * @return work result with success status.
     */
    public static WorkResult success(final String jobId) {
        return new WorkResult(jobId, null, null, null);
    }

    /**
     * Initializes the work result with success status.
     * @param jobId job id
     * @param executionDetails execution details
     * @return work result with success status.
     */
    public static WorkResult success(final String jobId,
                                     final ExecutionDetails executionDetails) {
        return new WorkResult(jobId, executionDetails, null, null);
    }

    /**
     * Initializes the work result with success status.
     * @param jobId job id
     * @param executionDetails execution details
     * @param currentRevision current revision
     * @return work result with success status.
     */
    public static WorkResult success(final String jobId,
                                               final ExecutionDetails executionDetails,
                                               final CurrentRevision currentRevision) {
        return new WorkResult(jobId, executionDetails, currentRevision, null);
    }

    /**
     * Initializes the work result with success status and continuation token.
     * The continuation token indicates that the job is asynchronous and not finished yet.
     * An additional job will be scheduled to request status update on this asynchronous job.
     * @param jobId job id
     * @param executionDetails execution details
     * @param continuationToken continuation token, indicates that the job is asynchronous and not finished yet.
     *                          additional job will be scheduled to request status update.
     * @return work result with success status.
     */
    public static WorkResult success(final String jobId,
                                     final ExecutionDetails executionDetails,
                                     final String continuationToken) {
        return new WorkResult(jobId, executionDetails, null, continuationToken);
    }

    /**
     * Initializes the work result with success status, current revision and continuation token.
     * The continuation token indicates that the job is asynchronous and not finished yet.
     * An additional job will be scheduled to request status update on this asynchronous job.
     * @param jobId job id
     * @param executionDetails execution details
     * @param currentRevision current revision
     * @param continuationToken continuation token, indicates that the job is asynchronous and not finished yet.
     *                          additional job will be scheduled to request status update.
     * @return work result with success status.
     */
    public static WorkResult success(final String jobId,
                                               final ExecutionDetails executionDetails,
                                               final CurrentRevision currentRevision,
                                               final String continuationToken) {
        return new WorkResult(jobId, executionDetails, currentRevision, continuationToken);
    }

    /**
     * Initializes the work result with failed status.
     * @param jobId job id
     * @param failureDetails failure details
     * @return work result with failed status.
     */
    public static WorkResult failure(final String jobId, final FailureDetails failureDetails) {
        return new WorkResult(jobId, failureDetails);
    }
}
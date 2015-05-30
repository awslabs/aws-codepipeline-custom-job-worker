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

/**
 * Job status enum.
 */
public enum JobStatus {
    /**
     * Job has been created but not enqueued yet. Is not available for polling.
     */
    Created,
    /**
     * Job has been enqueued and is available for polling.
     */
    Queued,
    /**
     * Job has been handed out by poll for jobs.
     */
    Dispatched,
    /**
     * Job worker acknowledged the job.
     */
    InProgress,
    /**
     * Job timed out because it has not been processed in time.
     */
    TimedOut,
    /**
     * Job worker reported success.
     */
    Succeeded,
    /**
     * Job worker reported failure.
     */
    Failed
}

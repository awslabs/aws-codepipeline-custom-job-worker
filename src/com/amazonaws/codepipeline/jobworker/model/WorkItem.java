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
 * A unit of work which is processed by the job worker.
 */
public class WorkItem {
    private final String jobId;
    private final String jobNonce;
    private final JobData jobData;
    private final String clientId;

    /**
     * @param jobId unique identifier of the job
     * @param jobNonce job nonce
     * @param jobData job details
     * @param clientId identifies the owner of the job
     */
    public WorkItem(final String jobId, final String jobNonce, final JobData jobData, final String clientId) {
        this.clientId = clientId;
        this.jobId = jobId;
        this.jobNonce = jobNonce;
        this.jobData = jobData;
    }

    /**
     * @return unique identifier of the job
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @return job nonce
     */
    public String getJobNonce() {
        return jobNonce;
    }

    /**
     * @return job details
     */
    public JobData getJobData() {
        return jobData;
    }

    /**
     * @return identifies the owner of the job
     */
    public String getClientId() {
        return clientId;
    }
}

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
 * Execution details structure.
 */
public class ExecutionDetails {
    private final String summary;
    private final String externalExecutionId;
    private final int percentComplete;

    /**
     * Initializes the execution details.
     * @param summary a summary of the execution.
     * @param externalExecutionId the id of the external execution.
     * @param percentComplete value between 0 and 100 which indicates the process.
     */
    public ExecutionDetails(final String summary, final String externalExecutionId, final int percentComplete) {
        this.summary = summary;
        this.externalExecutionId = externalExecutionId;
        this.percentComplete = percentComplete;
    }

    /**
     * @return a summary of the execution.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return the id of the external execution.
     */
    public String getExternalExecutionId() {
        return externalExecutionId;
    }

    /**
     * @return value between 0 and 100 which indicates the process.
     */
    public int getPercentComplete() {
        return percentComplete;
    }
}

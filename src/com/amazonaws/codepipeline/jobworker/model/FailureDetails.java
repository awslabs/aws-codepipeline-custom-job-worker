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
 * Failure details structure is used to report the reason for the job failure.
 */
public class FailureDetails {
    private final FailureType type;
    private final String message;
    private final String externalExecutionId;

    /**
     * Initializes the failure details to report the reason for the job failure.
     * @param type failure type / failure class
     */
    public FailureDetails(final FailureType type) {
        Validator.notNull(type);
        this.type = type;
        this.message = null;
        this.externalExecutionId = null;
    }

    /**
     * Initializes the failure details to report the reason for the job failure.
     * @param type failure type / failure class
     * @param message message indicating why the job failed
     */
    public FailureDetails(final FailureType type, final String message) {
        Validator.notNull(type);
        this.type = type;
        this.message = message;
        this.externalExecutionId = null;
    }

    /**
     * Initializes the failure details to report the reason for the job failure.
     * @param type failure type / failure class
     * @param message message indicating why the job failed
     * @param externalExecutionId the id of the external execution.
     */
    public FailureDetails(final FailureType type, final String message, final String externalExecutionId) {
        Validator.notNull(type);
        this.type = type;
        this.message = message;
        this.externalExecutionId = externalExecutionId;
    }

    /**
     * @return failure type / failure class
     */
    public FailureType getType() {
        return type;
    }

    /**
     * @return message indicating why the job failed
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the id of the external execution.
     */
    public String getExternalExecutionId() {
        return externalExecutionId;
    }
}

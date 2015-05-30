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
 * List of different failure types / failure codes.
 */
public enum FailureType {
    /**
     * Generic job failed error.
     */
    JobFailed,
    /**
     * Configuration error,
     * e.g. wrong action configuration.
     */
    ConfigurationError,
    /**
     * Permissions error,
     * e.g. caller has no access to the given resource.
     */
    PermissionError,
    /**
     * Revision has changed and is not the expected one.
     */
    RevisionOutOfSync,
    /**
     * No revision has been provided.
     */
    RevisionUnavailable,
    /**
     * System is temporarily not available.
     */
    SystemUnavailable
}

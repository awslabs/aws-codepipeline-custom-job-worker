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
 * Temporary AWS session-based credentials which can be used to access AWS resources.
 * (e.g. Input and Output artifacts in S3)
 */
public class AWSSessionCredentials {
    private final String accessKeyId;
    private final String secretAccessKey;
    private final String sessionToken;

    /**
     * Initializes the AWS session credentials.
     * @param accessKeyId AWS access key
     * @param secretAccessKey AWS secret key
     * @param sessionToken AWS session token
     */
    public AWSSessionCredentials(final String accessKeyId, final String secretAccessKey, final String sessionToken) {
        Validator.notNull(accessKeyId);
        Validator.notNull(secretAccessKey);
        Validator.notNull(sessionToken);
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
    }

    /**
     * @return AWS access key
     */
    public String getAccessKeyId() {
        return accessKeyId;
    }

    /**
     * @return AWS secret key
     */
    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    /**
     * @return AWS session token
     */
    public String getSessionToken() {
        return sessionToken;
    }
}

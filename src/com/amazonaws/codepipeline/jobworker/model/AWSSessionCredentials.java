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

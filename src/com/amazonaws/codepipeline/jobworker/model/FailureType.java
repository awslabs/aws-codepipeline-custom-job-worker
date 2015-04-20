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

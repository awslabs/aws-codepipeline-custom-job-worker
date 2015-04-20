package com.amazonaws.codepipeline.jobworker.model;

/**
 * Result of the work item processing.
 * Indicates if the action execution has been successful or failed.
 */
public enum WorkResultStatus {
    /**
     * Work item was successful.
     */
    Success,
    /**
     * Work item failed.
     */
    Failure
}

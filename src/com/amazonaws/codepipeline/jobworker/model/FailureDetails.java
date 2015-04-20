package com.amazonaws.codepipeline.jobworker.model;

import com.amazonaws.codepipeline.jobworker.Validator;

/**
 * Failure details structure is used to report the reason for the job failure.
 */
public class FailureDetails {
    private final FailureType type;
    private final String message;

    /**
     * Initializes the failure details to report the reason for the job failure.
     * @param type failure type / failure class
     */
    public FailureDetails(final FailureType type) {
        Validator.notNull(type);
        this.type = type;
        this.message = null;
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
}

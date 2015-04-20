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

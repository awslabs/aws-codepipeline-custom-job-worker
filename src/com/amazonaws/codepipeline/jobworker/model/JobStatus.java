package com.amazonaws.codepipeline.jobworker.model;

/**
 * Job status enum.
 */
public enum JobStatus {
    /**
     * Job has been created but not enqueued yet. Is not available for polling.
     */
    Created,
    /**
     * Job has been enqueued and is available for polling.
     */
    Queued,
    /**
     * Job has been handed out by poll for jobs.
     */
    Dispatched,
    /**
     * Job worker acknowledged the job.
     */
    InProgress,
    /**
     * Job timed out because it has not been processed in time.
     */
    TimedOut,
    /**
     * Job worker reported success.
     */
    Succeeded,
    /**
     * Job worker reported failure.
     */
    Failed
}

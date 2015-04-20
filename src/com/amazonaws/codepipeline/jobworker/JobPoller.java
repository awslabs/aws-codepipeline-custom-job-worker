package com.amazonaws.codepipeline.jobworker;

/**
 * The poller keeps sending requests to the job api for new jobs.
 * It acknowledges the jobs and starts a new worker thread (JobProcessor) for each job.
 * It waits for the result from the JobProcessor and reports back success or failure
 * to the JobService.
 *
 * It only takes on a single job per available worker thread.
 */
public interface JobPoller {
    /**
     * This method is invoked in a regular polling interval by the daemon.
     * It polls for jobs, acknowledges them and reports back the status.
     */
    void execute();
}

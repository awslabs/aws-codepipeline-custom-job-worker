package com.amazonaws.codepipeline.jobworker.configuration;

import com.amazonaws.codepipeline.jobworker.JobPoller;

/**
 * Configuration for settings and dependencies of the job worker.
 */
public interface JobWorkerConfiguration {

    /**
     * @return job poller implementation
     */
    public JobPoller jobPoller();

    /**
     * @return the poll interval in milliseconds
     */
    public long getPollingIntervalInMs();
}

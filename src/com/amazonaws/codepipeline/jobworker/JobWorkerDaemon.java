package com.amazonaws.codepipeline.jobworker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;

/**
 * The daemon schedules the poller at a fixed time rate.
 */
public class JobWorkerDaemon implements Daemon {

    private static final Logger LOGGER = Logger.getLogger(JobWorkerDaemon.class);

    private final ScheduledExecutorService executorService;
    private final JobPoller jobPoller;

    /**
     * Initializes the daemon with default settings:
     * Scheduled Thread Pool with pool size 1 to invoke job poller on a fixed rate.
     * (Default every second)
     */
    public JobWorkerDaemon() {
        executorService = Executors.newScheduledThreadPool(1);
        jobPoller = JobWorkerConfiguration.jobPoller();
    }

    /**
     * Initializes daemon with a custom scheduled exector service and a custom poller.
     * @param executorService scheduled executor service
     * @param jobPoller job poller
     */
    public JobWorkerDaemon(final ScheduledExecutorService executorService, final JobPoller jobPoller) {
        Validator.notNull(executorService);
        Validator.notNull(jobPoller);
        this.executorService = executorService;
        this.jobPoller = jobPoller;
    }

    /**
     * Initializes the daemon.
     * @param context daemon context.
     * @throws DaemonInitException exception during initialization
     */
    public void init(final DaemonContext context) throws DaemonInitException {
        LOGGER.info("Initialize daemon.");
    }

    /**
     * Starts the daemon. Initializes the executor service to execute the job poller at a fixed rate.
     * @throws Exception exception during start up
     */
    public void start() throws Exception {
        LOGGER.info("Starting up daemon.");

        executorService.scheduleAtFixedRate(jobPollerRunnable(), JobWorkerConfiguration.POLL_INTERVAL_MS, JobWorkerConfiguration.POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the daemon. Shuts down the executor service gracefully.
     * Waits until the job poller and job processors finished their work.
     * @throws Exception exception during shutdown
     */
    public void stop() throws Exception {
        LOGGER.info("Stopping daemon.");

        this.executorService.shutdown();
        try {
            if (!this.executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                this.executorService.shutdownNow();
                if (!this.executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                    throw new IllegalStateException("Failed graceful shutdown of executor threads");
                }
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Stopped daemon.");
    }

    /**
     * Destroys the daemon.
     */
    public void destroy() {
        LOGGER.info("Destroying daemon.");
    }

    private Runnable jobPollerRunnable() {
        return () -> {
            try {
                jobPoller.execute();
            } catch (final RuntimeException e) { // NOPMD
                LOGGER.error("Caught exception while processing jobs", e);
            }
        };
    }
}

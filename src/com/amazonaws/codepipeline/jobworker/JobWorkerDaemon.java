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
package com.amazonaws.codepipeline.jobworker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;

import com.amazonaws.codepipeline.jobworker.configuration.CustomActionJobWorkerConfiguration;
import com.amazonaws.codepipeline.jobworker.configuration.JobWorkerConfiguration;
import com.amazonaws.codepipeline.jobworker.model.RegionNotFoundException;

/**
 * The daemon schedules the poller at a fixed time rate.
 */
public class JobWorkerDaemon implements Daemon {

    private static final Logger LOGGER = Logger.getLogger(JobWorkerDaemon.class);

    private final ScheduledExecutorService executorService;

    private JobPoller jobPoller;
    private long pollingIntervalInMs;

    /**
     * Initializes the daemon with default settings:
     * Scheduled Thread Pool with pool size 1 to invoke job poller on a fixed rate.
     * (Default every 30 seconds)
     * Uses third party action configuration as a default.
     */
    public JobWorkerDaemon() {
        this(Executors.newScheduledThreadPool(1), new CustomActionJobWorkerConfiguration());
    }

    /**
     * Initializes daemon with a custom scheduled executor service and poller.
     * @param executorService scheduled executor service
     * @param jobWorkerConfiguration job worker configuration class defining settings and dependencies
     */
    public JobWorkerDaemon(final ScheduledExecutorService executorService, final JobWorkerConfiguration jobWorkerConfiguration) {
        Validator.notNull(executorService);
        Validator.notNull(jobWorkerConfiguration);
        this.executorService = executorService;
        initConfiguration(jobWorkerConfiguration);
    }

    /**
     * Initializes the daemon.
     * @param context daemon context.
     * @throws DaemonInitException exception during initialization
     */
    @Override
    public void init(final DaemonContext context) throws DaemonInitException {
        LOGGER.info("Initialize daemon.");

        final String[] arguments = context.getArguments();
        if (arguments != null){
            LOGGER.debug(String.format("JobWorker arguments '%s'", String.join(", ", arguments)));
            loadConfiguration(arguments);
        }
    }

    /**
     * Starts the daemon. Initializes the executor service to execute the job poller at a fixed rate.
     * @throws Exception exception during start up
     */
    @Override
    public void start() throws Exception {
        LOGGER.info("Starting up daemon.");

        executorService.scheduleAtFixedRate(jobPollerRunnable(),
                pollingIntervalInMs,
                pollingIntervalInMs,
                TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the daemon. Shuts down the executor service gracefully.
     * Waits until the job poller and job processors finished their work.
     * @throws Exception exception during shutdown
     */
    @Override
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
        } catch (final InterruptedException e) {
            this.executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Stopped daemon.");
    }

    /**
     * Destroys the daemon.
     */
    @Override
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

    private void loadConfiguration(final String[] arguments) throws DaemonInitException {
        if (arguments.length == 1) {
            final String configurationClassName = arguments[0];
            try {
                final JobWorkerConfiguration jobWorkerConfiguration = (JobWorkerConfiguration) Class.forName(configurationClassName).newInstance();
                initConfiguration(jobWorkerConfiguration);
            } catch (final InstantiationException | IllegalAccessException |
                    ClassNotFoundException | ClassCastException | RegionNotFoundException e) {
                throw new DaemonInitException(
                        String.format("Provided job worker configuration class '%s' could not be loaded.", configurationClassName),
                        e);
            }
        }
    }

    private void initConfiguration(final JobWorkerConfiguration jobWorkerConfiguration) {
        this.jobPoller = jobWorkerConfiguration.jobPoller();
        this.pollingIntervalInMs = jobWorkerConfiguration.getPollingIntervalInMs();
    }
}

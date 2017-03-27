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
package com.amazonaws.codepipeline.jobworker.configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.amazonaws.codepipeline.jobworker.CodePipelineJobPoller;
import com.amazonaws.codepipeline.jobworker.JobPoller;
import com.amazonaws.codepipeline.jobworker.JobProcessor;
import com.amazonaws.codepipeline.jobworker.JobService;
import com.amazonaws.codepipeline.jobworker.model.RegionNotFoundException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.AWSCodePipelineClient;
import com.amazonaws.util.StringUtils;

import com.amazonaws.codepipeline.jobworker.CodePipelineJobProcessor;

/**
 * Default implementation for the job worker configuration. Can be used as a base configuration for different
 * job service implementations. Only the implementation of the job service has to be provided.
 * By default: sets the polling interval to 30000 ms and starts 10 worker threads.
 */
public abstract class DefaultJobWorkerConfiguration implements JobWorkerConfiguration {

    /**
     * The polling interval the daemon schedules the job poller which polls for new jobs.
     */
    private static final long POLL_INTERVAL_MS = 30000L;

    /**
     * Number of worker threads. Indicates how many jobs can be processed in parallel.
     */
    private static final int WORKER_THREADS = 10;

    /**
     * Number of jobs returned in a batch, should be typically the same like the worker threads.
     */
    private static final int POLL_BATCH_SIZE = WORKER_THREADS;

    /**
     * Environment variable to override region.
     */
    private static final String AWS_REGION = "AWS_REGION";

    /**
     * @return the poll interval in milliseconds
     */
    @Override
    public long getPollingIntervalInMs() {
        return POLL_INTERVAL_MS;
    }

    /**
     * @return job poller implementation
     */
    @Override
    public JobPoller jobPoller() {
        return new CodePipelineJobPoller(jobService(), jobProcessor(), threadPoolExecutor(), POLL_BATCH_SIZE);
    }

    /**
     * @return job processor implementation
     */
    protected JobProcessor jobProcessor() {
        return new CodePipelineJobProcessor();
    }

    /**
     * @return code pipeline client implementation
     */
    protected AWSCodePipeline codePipelineClient() {
        final AWSCodePipeline codePipelineClient = new AWSCodePipelineClient();
        codePipelineClient.setRegion(getRegion());
        return codePipelineClient;
    }

    /**
     * @return thread pool executor implementation
     */
    protected ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(WORKER_THREADS);
    }

    /**
     * @return job service implementation
     */
    protected abstract JobService jobService();

    /**
     * Fetch region from environment variable, if not found fetch from EC2 instance metadata.
     *
     * @return Region the region
     */
    protected static Region getRegion() {
        final String awsRegion = System.getProperty(AWS_REGION);
        if (!StringUtils.isNullOrEmpty(awsRegion)) {
            try {
                return Region.getRegion(Regions.fromName(awsRegion));
            } catch (final IllegalArgumentException e) {
                throw new RegionNotFoundException(String.format("Unknown AWS region: '%s'. Choose a valid value for environment variable AWS_REGION", awsRegion), e);
            }
        }

        final Region region = Regions.getCurrentRegion();
        if (region == null) {
            throw new RegionNotFoundException("Region could not be determined from EC2 instance metadata");
        }
        return region;
    }
}

package com.amazonaws.codepipeline.jobworker;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.amazonaws.codepipeline.jobworker.model.ActionType;
import com.amazonaws.codepipeline.jobworker.plugin.thirdparty.ClientTokenProvider;
import com.amazonaws.codepipeline.jobworker.plugin.thirdparty.DefaultClientTokenProvider;
import com.amazonaws.codepipeline.jobworker.plugin.thirdparty.ThirdPartyJobService;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codepipeline.AmazonCodePipelineClient;

/**
 * Configuration class for settings and dependencies of the job worker.
 */
public class JobWorkerConfiguration {
    /**
     * The polling interval the daemon schedules the job poller which polls for new jobs.
     */
    public static final long POLL_INTERVAL_MS = 1000L;

    /**
     * Number of worker threads. Indicates how many jobs can be processed in parallel.
     */
    private static final int WORKER_THREADS = 10;

    /**
     * Number of jobs returned in a batch, should be typically the same like the worker threads.
     */
    private static final int POLL_BATCH_SIZE = WORKER_THREADS;

    /**
     * Action type this job worker is polling and processing jobs for.
     */
    private static final ActionType ACTION_TYPE = new ActionType("Deploy", "ThirdParty", "TestProvider", "1");

    /**
     * AWS Region.
     */
    private static final Region AWS_REGION = Region.getRegion(Regions.US_EAST_1);

    /**
     * @return job processor implementation
     */
    public static JobProcessor jobProcessor() {
        return new CodePipelineJobProcessor();
    }

    /**
     * @return job poller implementation
     */
    public static JobPoller jobPoller() {
        return new CodePipelineJobPoller(jobService(), jobProcessor(), threadPoolExecutor(), POLL_BATCH_SIZE);
    }

    /**
     * @return job service implementation
     */
    public static JobService jobService() { return new ThirdPartyJobService(codePipelineClient(), ACTION_TYPE, clientTokenProvider()); }

    /**
     * @return code pipeline client implementation
     */
    public static AmazonCodePipelineClient codePipelineClient() {
        AmazonCodePipelineClient codePipelineClient = new AmazonCodePipelineClient();
        codePipelineClient.setRegion(AWS_REGION);
        return codePipelineClient;
    }

    /**
     * @return client token provider implementation
     */
    public static ClientTokenProvider clientTokenProvider() {
        return new DefaultClientTokenProvider();
    }

    /**
     * @return thread pool executor implementation
     */
    public static ThreadPoolExecutor threadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(WORKER_THREADS);
    }
}

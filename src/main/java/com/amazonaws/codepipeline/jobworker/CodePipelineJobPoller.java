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

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkResult;
import com.amazonaws.codepipeline.jobworker.model.WorkResultStatus;

/**
 * The poller keeps sending requests to the job api for new jobs.
 * It acknowledges the jobs and starts a new worker thread (JobProcessor) for each job.
 * It waits for the result from the JobProcessor and reports back success or failure
 * to the JobService.
 *
 * It only takes on a single job per available worker thread.
 */
public class CodePipelineJobPoller implements JobPoller {

    private static final Logger LOGGER = Logger.getLogger(CodePipelineJobPoller.class);

    private final JobProcessor jobProcessor;
    private final JobService jobService;
    private final ThreadPoolExecutor executorService;
    private final int pollBatchSize;

    /**
     * Initializes a new instance of the code pipeline job poller.
     * @param jobService job service API to poll for jobs, acknowledge them and report status.
     * @param jobProcessor job processor which executes a given work item and returns the result.
     * @param executorService thread pool executor used to schedule now job processor threads.
     * @param pollBatchSize default poll batch size, should be the number of available worker threads.
     */
    public CodePipelineJobPoller(final JobService jobService,
                                 final JobProcessor jobProcessor,
                                 final ThreadPoolExecutor executorService,
                                 final int pollBatchSize) {
        Validator.notNull(jobService);
        Validator.notNull(jobProcessor);
        Validator.notNull(executorService);
        this.jobService = jobService;
        this.jobProcessor = jobProcessor;
        this.executorService = executorService;
        this.pollBatchSize = pollBatchSize;
    }

    /**
     * This method is invoked in a regular polling interval by the daemon.
     * It polls for jobs, acknowledges them and reports back the status.
     */
    @Override
    public void execute() {
        LOGGER.debug("New polling iteration");

        final int batchSize = pollBatchSize - executorService.getActiveCount();
        if (batchSize > 0) {
            final int pollingBatchSize = Math.min(batchSize, pollBatchSize);
            LOGGER.debug("PollForJobs with batch size: " + pollingBatchSize);
            final List<WorkItem> workItems = jobService.pollForJobs(pollingBatchSize);

            for (final WorkItem workItem : workItems) {
                try {
                    executorService.submit(newProcessWorkItemRunnable(workItem));
                } catch (final RejectedExecutionException e) {
                    LOGGER.error("Executor service rejected task scheduling", e);
                }
            }
        }
    }

    private Runnable newProcessWorkItemRunnable(final WorkItem workItem) {
        return () -> {
            try {
                final JobStatus jobStatus = jobService.acknowledgeJob(workItem.getJobId(), workItem.getClientId(), workItem.getJobNonce());
                if (JobStatus.InProgress.equals(jobStatus)) {
                    LOGGER.info(String.format("Handing workItem for job %s to JobWorker", workItem.getJobId()));
                    final WorkResult result = jobProcessor.process(workItem);

                    reportJobStatus(workItem, result);
                } else {
                    LOGGER.warn(String.format("Cannot process work item since AcknowledgeJob for job %s with nonce %s returned status %s",
                            workItem.getJobId(), workItem.getJobNonce(), jobStatus));
                }
            } catch(final RuntimeException e) {
                LOGGER.error(String.format("Error occurred processing work item for job %s", workItem.getJobId()), e);
            }
        };
    }

    private void reportJobStatus(final WorkItem workItem, final WorkResult result) {
        Validator.notNull(workItem);
        Validator.notNull(result);

        if (WorkResultStatus.Success.equals(result.getStatus())) {
            LOGGER.info(String.format("Job %s succeeded.", workItem.getJobId()));
            jobService.putJobSuccess(workItem.getJobId(),
                    workItem.getClientId(),
                    result.getExecutionDetails(),
                    result.getCurrentRevision(),
                    result.getContinuationToken());
        } else {
            LOGGER.info(String.format("Job %s failed.", workItem.getJobId()));
            jobService.putJobFailure(workItem.getJobId(),
                    workItem.getClientId(),
                    result.getFailureDetails());
        }
    }
}

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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureType;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.model.WorkResult;

public class CodePipelineJobPollerTest {
    private final static int POLL_BATCH_SIZE = 10;

    @Mock
    private ThreadPoolExecutor executorService;

    @Mock
    private JobService jobService;

    @Mock
    private JobProcessor jobProcessor;

    @Captor
    private ArgumentCaptor<Runnable> processWorkRunnables;

    private JobPoller jobPoller;
    private WorkResult workResult;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobPoller = new CodePipelineJobPoller(jobService, jobProcessor, executorService, POLL_BATCH_SIZE);

        workResult = WorkResult.success(
                UUID.randomUUID().toString(),
                new ExecutionDetails("test summary", UUID.randomUUID().toString(), 100),
                new CurrentRevision("test revision", "test change identifier"));

        when(jobProcessor.process(any()))
                .thenReturn(workResult);

        when(jobService.acknowledgeJob(any(), any(), any()))
                .thenReturn(JobStatus.InProgress);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenJobServiceIsNull() {
        new CodePipelineJobPoller(null, jobProcessor, executorService, POLL_BATCH_SIZE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenJobProcessorIsNull() {
        new CodePipelineJobPoller(jobService, null, executorService, POLL_BATCH_SIZE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenExecutorServiceIsNull() {
        new CodePipelineJobPoller(jobService, jobProcessor, null, POLL_BATCH_SIZE);
    }

    @Test
    public void shouldPollForJobsWhenThereAreNoActiveWorkerThreads() {
        // given
        when(executorService.getActiveCount())
                .thenReturn(0);

        // when
        jobPoller.execute();

        // then
        verify(jobService).pollForJobs(POLL_BATCH_SIZE);
    }

    @Test
    public void shouldPollForJobsWithBatchSizeWhenNotAllWorkerThreadsAreBusy() {
        // given
        final int actionExecutionCount = 4;
        when(executorService.getActiveCount())
                .thenReturn(actionExecutionCount);

        // when
        jobPoller.execute();

        // then
        verify(jobService).pollForJobs(POLL_BATCH_SIZE - actionExecutionCount);
    }

    @Test
    public void shouldStartThreadsForAllReturnedJobs() {
        // given
        final int jobCount = 6;
        when(jobService.pollForJobs(POLL_BATCH_SIZE))
                .thenReturn(randomWorkItems(jobCount));

        // when
        jobPoller.execute();

        // then
        verify(executorService, times(jobCount)).submit(any(Runnable.class));
    }

    @Test
    public void shouldAcknowledgeAllReturnedJobs() {
        // when
        final int jobCount = 7;
        executeProcessWorkRunnables(jobCount);

        // then
        verify(jobService, times(jobCount)).acknowledgeJob(any(), any(), any());
    }

    @Test
    public void shouldHandOutWorkToJobProcessorWhenStatusInProgress() {
        // when
        final int jobCount = 8;
        executeProcessWorkRunnables(jobCount);

        // then
        verify(jobProcessor, times(jobCount)).process(any());
    }

    @Test
    public void shouldNotHandOutWorkToJobProcessorWhenStatusFailed() {
        // given
        when(jobService.acknowledgeJob(any(), any(), any()))
                .thenReturn(JobStatus.Failed);

        // when
        final int jobCount = 10;
        executeProcessWorkRunnables(jobCount);

        // then
        verify(jobProcessor, never()).process(any());
    }

    @Test
    public void shouldHandleAllRuntimeExceptions() {
        // given
        when(jobService.acknowledgeJob(any(), any(), any()))
                .thenThrow(new RuntimeException("Test Exception"));

        // when
        final int jobCount = 1;
        executeProcessWorkRunnables(jobCount);
    }

    @Test
    public void shouldReportSuccessWhenProcessorReturnsSuccess() {
        // when
        final int jobCount = 1;
        executeProcessWorkRunnables(jobCount);

        // then
        verify(jobService).putJobSuccess(any(),
                any(),
                eq(workResult.getExecutionDetails()),
                eq(workResult.getCurrentRevision()),
                eq(workResult.getContinuationToken()));
    }

    @Test
    public void shouldReportFailureWhenProcessorReturnsFailure() {
        // given
        workResult = WorkResult.failure(
                UUID.randomUUID().toString(),
                new FailureDetails(FailureType.JobFailed, "failure message"));
        when(jobProcessor.process(any()))
                .thenReturn(workResult);

        // when
        final int jobCount = 1;
        executeProcessWorkRunnables(jobCount);

        // then
        verify(jobService).putJobFailure(any(),
                any(),
                eq(workResult.getFailureDetails()));
    }

    private void executeProcessWorkRunnables(final int workItemCount) {
        when(jobService.pollForJobs(POLL_BATCH_SIZE)).thenReturn(randomWorkItems(workItemCount));

        jobPoller.execute();

        verify(executorService, times(workItemCount)).submit(processWorkRunnables.capture());
        for (final Runnable processWorkRunnable : processWorkRunnables.getAllValues()) {
            processWorkRunnable.run();
        }
    }

    private List<WorkItem> randomWorkItems(final int count) {
        final List<WorkItem> workItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            workItems.add(randomWorkItem());
        }
        return workItems;
    }

    private WorkItem randomWorkItem() {
        return new WorkItem(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                null,
                UUID.randomUUID().toString());
    }
}

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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.codepipeline.jobworker.configuration.JobWorkerConfiguration;

public class JobWorkerDaemonTest {

    private static final long POLL_INTERVAL_MS = 30000L;

    @Mock
    private ScheduledExecutorService executorService;

    @Mock
    private JobWorkerConfiguration jobWorkerConfiguration;

    @Mock
    private JobPoller jobPoller;

    @Mock
    private DaemonContext daemonContext;

    @Captor
    private ArgumentCaptor<Runnable> pollerRunnable;

    private JobWorkerDaemon jobWorkerDaemon;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(jobWorkerConfiguration.getPollingIntervalInMs()).thenReturn(POLL_INTERVAL_MS);
        when(jobWorkerConfiguration.jobPoller()).thenReturn(jobPoller);

        jobWorkerDaemon = new JobWorkerDaemon(executorService, jobWorkerConfiguration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenExecutorServiceIsNull() {
        new JobWorkerDaemon(null, jobWorkerConfiguration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenJobWorkerConfigurationIsNull() {
        new JobWorkerDaemon(executorService, null);
    }

    @Test
    public void shouldInitializeDefaultConfiguration() throws Exception {
        // given
        when(daemonContext.getArguments()).thenReturn(new String[0]);

        // when
        jobWorkerDaemon.init(daemonContext);
    }

    @Test
    public void shouldLoadCustomActionConfiguration() throws Exception {
        // given
        when(daemonContext.getArguments()).thenReturn(
                new String[] { "com.amazonaws.codepipeline.jobworker.configuration.CustomActionJobWorkerConfiguration" });

        // when
        jobWorkerDaemon.init(daemonContext);
    }

    @Test
    public void shouldLoadThirdPartyConfiguration() throws Exception {
        // given
        when(daemonContext.getArguments()).thenReturn(
                new String[] { "com.amazonaws.codepipeline.jobworker.configuration.ThirdPartyJobWorkerConfiguration" });

        // when
        jobWorkerDaemon.init(daemonContext);
    }

    @Test(expected = DaemonInitException.class)
    public void shouldThrowOnInitIfNonExistentClassProvided() throws Exception {
        // given
        when(daemonContext.getArguments()).thenReturn(new String[] { "non-existent-class" });

        // when
        jobWorkerDaemon.init(daemonContext);
    }

    @Test(expected = DaemonInitException.class)
    public void shouldThrowOnInitIfInvalidClassProvided() throws Exception {
        // given
        when(daemonContext.getArguments()).thenReturn(new String[] { "java.lang.String" });

        // when
        jobWorkerDaemon.init(daemonContext);
    }

    @Test
    public void shouldStartSchedulingJobPollerEverySecond() throws Exception {
        // when
        jobWorkerDaemon.start();

        // then
        verify(executorService).scheduleAtFixedRate(pollerRunnable.capture(),
                eq(POLL_INTERVAL_MS),
                eq(POLL_INTERVAL_MS),
                eq(TimeUnit.MILLISECONDS));
        assertNotNull(pollerRunnable.getValue());

        // when
        pollerRunnable.getValue().run();

        // then
        verify(jobPoller).execute();
    }

    @Test
    public void shouldStopSchedulingJobPoller() throws Exception {
        // given
        when(executorService.awaitTermination(1, TimeUnit.MINUTES))
                .thenReturn(true);

        // when
        jobWorkerDaemon.stop();

        // then
        verify(executorService).shutdown();
    }

    @Test
    public void shouldForceStoppingSchedulingJobPollerAfterOneMinute() throws Exception {
        // given
        when(executorService.awaitTermination(1, TimeUnit.MINUTES))
                .thenReturn(false)
                .thenReturn(true);

        // when
        jobWorkerDaemon.stop();

        // then
        verify(executorService).shutdown();
        verify(executorService).shutdownNow();
    }

    @Test
    public void shouldForceStoppingSchedulingJobPollerWhenInterruptedExceptionIsThrow() throws Exception {
        // given
        when(executorService.awaitTermination(1, TimeUnit.MINUTES))
                .thenThrow(new InterruptedException());

        // when
        jobWorkerDaemon.stop();

        // then
        verify(executorService).shutdown();
        verify(executorService).shutdownNow();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenThreadsCannotBeStopped() throws Exception {
        // when
        jobWorkerDaemon.stop();
    }
}

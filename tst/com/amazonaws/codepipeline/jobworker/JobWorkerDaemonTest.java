package com.amazonaws.codepipeline.jobworker;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JobWorkerDaemonTest {

    @Mock
    private ScheduledExecutorService executorService;

    @Mock
    private JobPoller jobPoller;

    @Captor
    private ArgumentCaptor<Runnable> pollerRunnable;

    private JobWorkerDaemon jobWorkerDaemon;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobWorkerDaemon = new JobWorkerDaemon(executorService, jobPoller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenExecutorServiceIsNull() {
        new JobWorkerDaemon(null, jobPoller);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenJobPollerisNull() {
        new JobWorkerDaemon(executorService, null);
    }

    @Test
    public void shouldStartSchedulingJobPollerEverySecond() throws Exception {
        // when
        jobWorkerDaemon.start();

        // then
        verify(executorService).scheduleAtFixedRate(pollerRunnable.capture(),
                eq(JobWorkerConfiguration.POLL_INTERVAL_MS),
                eq(JobWorkerConfiguration.POLL_INTERVAL_MS),
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

package com.amazonaws.codepipeline.jobworker;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.model.WorkResult;
import com.amazonaws.codepipeline.jobworker.model.WorkResultStatus;

public class CodePipelineJobProcessorTest {

    private JobProcessor jobProcessor;

    @Before
    public void setUp() {
        jobProcessor = new CodePipelineJobProcessor();
    }

    @Test
    public void shouldProcessWorkItemSuccessfully() {
        // when
        final WorkItem workItem = randomWorkItem();
        final WorkResult workResult = jobProcessor.process(workItem);

        // then
        assertEquals(workItem.getJobId(), workResult.getJobId());
        assertEquals(WorkResultStatus.Success, workResult.getStatus());
    }

    private WorkItem randomWorkItem() {
        return new WorkItem(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                null,
                UUID.randomUUID().toString());
    }

}

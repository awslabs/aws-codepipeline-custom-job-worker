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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.codepipeline.jobworker.CodePipelineJobProcessor;
import com.amazonaws.codepipeline.jobworker.JobProcessor;
import com.amazonaws.codepipeline.jobworker.model.JobData;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.model.WorkResult;
import com.amazonaws.codepipeline.jobworker.model.WorkResultStatus;

public class CodePipelineJobProcessorTest {

    private static final String JOB_STATUS = "JobStatus";

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

    @Test
    public void shouldProcessWorkItemSuccessfullyWhenJobConfiguredToSucceed() {
        // when
        final WorkItem workItem = randomWorkItem(JobStatus.Succeeded);
        final WorkResult workResult = jobProcessor.process(workItem);

        // then
        assertEquals(workItem.getJobId(), workResult.getJobId());
        assertEquals(WorkResultStatus.Success, workResult.getStatus());
    }

    @Test
    public void shouldFailWorkItemWhenJobConfiguredToFail() {
        // when
        final WorkItem workItem = randomWorkItem(JobStatus.Failed);
        final WorkResult workResult = jobProcessor.process(workItem);

        // then
        assertEquals(workItem.getJobId(), workResult.getJobId());
        assertEquals(WorkResultStatus.Failure, workResult.getStatus());
    }

    private WorkItem randomWorkItem() {
        return new WorkItem(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                randomJobData(null),
                UUID.randomUUID().toString());
    }

    private WorkItem randomWorkItem(final JobStatus jobStatus) {
        return new WorkItem(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                randomJobData(jobStatus),
                UUID.randomUUID().toString());
    }

    private JobData randomJobData(final JobStatus jobStatus) {
        final Map<String, String> actionConfiguration = new HashMap<>();
        if (jobStatus != null) {
            actionConfiguration.put(JOB_STATUS, jobStatus.toString());
        }
        return new JobData(actionConfiguration, null, null, null, null, null);
    }
}

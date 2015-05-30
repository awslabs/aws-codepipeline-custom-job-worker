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

import java.util.Map;
import java.util.UUID;

import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureType;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.model.WorkResult;

/**
 * Implementation of a sample job processor which always returns success.
 * TODO: Replace this implementation with your job processing logic.
 */
public class CodePipelineJobProcessor implements JobProcessor {

    private static final String JOB_STATUS = "JobStatus";

    /**
     * Processes a single work item and reports status about the result.
     * @param workItem work item
     * @return work result
     */
    @Override
    public WorkResult process(final WorkItem workItem) {

        final Map<String, String> actionCofiguration = workItem.getJobData().getActionConfiguration();

        // for testing purposes: if the job was configured to fail, return a failure
        if (actionCofiguration.containsKey(JOB_STATUS)) {
            if (actionCofiguration.get(JOB_STATUS).equals(JobStatus.Failed.toString())) {

                return WorkResult.failure(
                        workItem.getJobId(),
                        new FailureDetails(FailureType.JobFailed, "job failed"));
            }
        }

        return WorkResult.success(
                workItem.getJobId(),
                new ExecutionDetails("test summary", UUID.randomUUID().toString(), 100),
                new CurrentRevision("test revision", "test change identifier"));
    }
}

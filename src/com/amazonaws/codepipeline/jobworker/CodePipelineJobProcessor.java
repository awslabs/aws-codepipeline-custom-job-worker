package com.amazonaws.codepipeline.jobworker;

import java.util.UUID;

import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.model.WorkResult;

/**
 * Implementation of a sample job processor which always returns success.
 * TODO: Replace this implementation with your job processing logic.
 */
public class CodePipelineJobProcessor implements JobProcessor {
    /**
     * Processes a single work item and reports status about the result.
     * @param workItem work item
     * @return work result
     */
    public WorkResult process(final WorkItem workItem) {
        return WorkResult.success(
                workItem.getJobId(),
                new ExecutionDetails("test summary", UUID.randomUUID().toString(), 100),
                new CurrentRevision("test revision", "test change identifier"));
    }
}

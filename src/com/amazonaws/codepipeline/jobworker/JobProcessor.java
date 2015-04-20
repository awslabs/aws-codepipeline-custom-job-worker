package com.amazonaws.codepipeline.jobworker;

import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.model.WorkResult;

/**
 * Job processor gets a work item, executes the actions and reports back the status.
 */
public interface JobProcessor {
    /**
     * Processes a single work item and reports status about the result.
     * @param workItem work item
     * @return work result
     */
    WorkResult process(WorkItem workItem);
}

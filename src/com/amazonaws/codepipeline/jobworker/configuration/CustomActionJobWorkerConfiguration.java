package com.amazonaws.codepipeline.jobworker.configuration;

import com.amazonaws.codepipeline.jobworker.JobService;
import com.amazonaws.codepipeline.jobworker.model.ActionType;
import com.amazonaws.codepipeline.jobworker.plugin.customaction.CustomActionJobService;

/**
 * Configuration class for settings and dependencies of the custom action job worker.
 */
public class CustomActionJobWorkerConfiguration extends DefaultJobWorkerConfiguration {

    /**
     * Action type this job worker is polling and processing jobs for.
     * @return action type identifier
     */
    public ActionType getActionType() {
        return new ActionType("Deploy", "Custom", "MyCustomAction", "1");
    }

    /**
     * @return job service implementation fpr the custom action API.
     */
    public JobService jobService() { return new CustomActionJobService(codePipelineClient(), getActionType()); }
}

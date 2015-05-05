package com.amazonaws.codepipeline.jobworker.configuration;

import com.amazonaws.codepipeline.jobworker.JobService;
import com.amazonaws.codepipeline.jobworker.model.ActionType;
import com.amazonaws.codepipeline.jobworker.plugin.thirdparty.ClientTokenProvider;
import com.amazonaws.codepipeline.jobworker.plugin.thirdparty.DefaultClientTokenProvider;
import com.amazonaws.codepipeline.jobworker.plugin.thirdparty.ThirdPartyJobService;

/**
 * Configuration class for settings and dependencies of the third party job worker.
 */
public class ThirdPartyJobWorkerConfiguration extends DefaultJobWorkerConfiguration {

    /**
     * Action type this job worker is polling and processing jobs for.
     * @return action type identifier
     */
    public ActionType getActionType() {
        return new ActionType("Deploy", "ThirdParty", "ThirdPartyDeployProvider", "1");
    }

    /**
     * @return job service implementation for the third party API.
     */
    public JobService jobService() { return new ThirdPartyJobService(codePipelineClient(), getActionType(), clientTokenProvider()); }

    /**
     * @return client token provider implementation
     */
    public ClientTokenProvider clientTokenProvider() {
        return new DefaultClientTokenProvider();
    }
}

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
    @Override
    public JobService jobService() { return new ThirdPartyJobService(codePipelineClient(), getActionType(), clientTokenProvider()); }

    /**
     * @return client token provider implementation
     */
    public ClientTokenProvider clientTokenProvider() {
        return new DefaultClientTokenProvider();
    }
}

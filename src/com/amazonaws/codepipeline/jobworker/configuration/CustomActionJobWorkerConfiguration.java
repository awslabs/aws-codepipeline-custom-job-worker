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
import com.amazonaws.codepipeline.jobworker.model.ActionTypeId;
import com.amazonaws.codepipeline.jobworker.plugin.customaction.CustomActionJobService;

/**
 * Configuration class for settings and dependencies of the custom action job worker.
 */
public class CustomActionJobWorkerConfiguration extends DefaultJobWorkerConfiguration {

    /**
     * Action type this job worker is polling and processing jobs for.
     * @return action type identifier
     */
    public ActionTypeId getActionTypeId() {
        return new ActionTypeId("Deploy", "Custom", "MyCustomAction", "1");
    }

    /**
     * @return job service implementation fpr the custom action API.
     */
    @Override
    public JobService jobService() { return new CustomActionJobService(codePipelineClient(), getActionTypeId()); }
}

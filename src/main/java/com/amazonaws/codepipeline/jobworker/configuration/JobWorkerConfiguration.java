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

import com.amazonaws.codepipeline.jobworker.JobPoller;

/**
 * Configuration for settings and dependencies of the job worker.
 */
public interface JobWorkerConfiguration {

    /**
     * @return job poller implementation
     */
    public JobPoller jobPoller();

    /**
     * @return the poll interval in milliseconds
     */
    public long getPollingIntervalInMs();
}

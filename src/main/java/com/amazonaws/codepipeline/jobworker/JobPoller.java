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

/**
 * The poller keeps sending requests to the job api for new jobs.
 * It acknowledges the jobs and starts a new worker thread (JobProcessor) for each job.
 * It waits for the result from the JobProcessor and reports back success or failure
 * to the JobService.
 *
 * It only takes on a single job per available worker thread.
 */
public interface JobPoller {
    /**
     * This method is invoked in a regular polling interval by the daemon.
     * It polls for jobs, acknowledges them and reports back the status.
     */
    void execute();
}

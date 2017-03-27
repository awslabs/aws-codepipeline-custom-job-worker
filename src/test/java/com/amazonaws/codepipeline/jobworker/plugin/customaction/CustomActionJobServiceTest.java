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
package com.amazonaws.codepipeline.jobworker.plugin.customaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.codepipeline.jobworker.JobService;
import com.amazonaws.codepipeline.jobworker.model.ActionTypeId;
import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureType;
import com.amazonaws.codepipeline.jobworker.model.JobStatus;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.codepipeline.jobworker.plugin.JobAssertion;
import com.amazonaws.codepipeline.jobworker.plugin.JobDataGenerator;

import com.amazonaws.codepipeline.jobworker.plugin.customaction.CustomActionJobService;
import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.model.AcknowledgeJobRequest;
import com.amazonaws.services.codepipeline.model.AcknowledgeJobResult;
import com.amazonaws.services.codepipeline.model.Job;
import com.amazonaws.services.codepipeline.model.PollForJobsResult;
import com.amazonaws.services.codepipeline.model.PutJobFailureResultRequest;
import com.amazonaws.services.codepipeline.model.PutJobSuccessResultRequest;

public class CustomActionJobServiceTest {

    private JobService jobService;
    private final ActionTypeId actionTypeId = new ActionTypeId("Build", "Custom", "MyCustomAction", "1.0");

    @Mock
    private AWSCodePipeline codePipelineClient;

    @Captor
    private ArgumentCaptor<PutJobSuccessResultRequest> putJobSuccessResultRequestCaptor;

    @Captor
    private ArgumentCaptor<PutJobFailureResultRequest> putJobFailureResultRequestCaptor;

    @Captor
    private ArgumentCaptor<AcknowledgeJobRequest> acknowledgeJobRequestCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobService = new CustomActionJobService(codePipelineClient, actionTypeId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenCodePipelineClientIsNull() {
        new CustomActionJobService(null, actionTypeId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenActionTypeIdIsNull() {
        new CustomActionJobService(codePipelineClient, null);
    }

    @Test
    public void shouldCallPollForJobs() {
        // given
        final PollForJobsResult pollForJobsResult = generatePollForJobsResult();
        final Job job = pollForJobsResult.getJobs().get(0);
        when(codePipelineClient.pollForJobs(any()))
                .thenReturn(pollForJobsResult);

        // when
        final List<WorkItem> workItems = jobService.pollForJobs(5);

        // then
        assertNotNull(workItems);
        assertEquals(1, workItems.size());
        final WorkItem workItem = workItems.get(0);
        assertNotNull(workItem);
        assertEquals(job.getId(), workItem.getJobId());
        assertEquals(job.getAccountId(), workItem.getClientId());
        assertEquals(job.getNonce(), workItem.getJobNonce());
        JobAssertion.assertJobDataEquals(job.getData(), workItem.getJobData());
    }

    @Test
    public void shouldCallAcknowledgeJob() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();
        final String nonce = UUID.randomUUID().toString();

        final AcknowledgeJobResult acknowledgeJobResult = new AcknowledgeJobResult();
        acknowledgeJobResult.setStatus(com.amazonaws.services.codepipeline.model.JobStatus.Failed);
        when(codePipelineClient.acknowledgeJob(acknowledgeJobRequestCaptor.capture()))
                .thenReturn(acknowledgeJobResult);

        // when
        final JobStatus jobStatus = jobService.acknowledgeJob(jobId, clientId, nonce);

        // then
        assertEquals(jobStatus, JobStatus.Failed);
        final AcknowledgeJobRequest request = acknowledgeJobRequestCaptor.getValue();
        assertEquals(jobId, request.getJobId());
        assertEquals(nonce, request.getNonce());
    }

    @Test
    public void shouldCallPutJobSuccessResult() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();

        // when
        jobService.putJobSuccess(jobId, clientId, null, null, null);

        // then
        verify(codePipelineClient).putJobSuccessResult(any());
    }

    @Test
    public void shouldCallPutJobSuccessResultWithTheCorrectArguments() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();
        final ExecutionDetails executionDetails = new ExecutionDetails("summary", UUID.randomUUID().toString(), 90);
        final CurrentRevision currentRevision = new CurrentRevision("revision id", "change identifier");
        final String continuationToken = UUID.randomUUID().toString();

        // when
        jobService.putJobSuccess(jobId, clientId, executionDetails, currentRevision, continuationToken);

        // then
        verify(codePipelineClient).putJobSuccessResult(putJobSuccessResultRequestCaptor.capture());

        final PutJobSuccessResultRequest request = putJobSuccessResultRequestCaptor.getValue();
        assertEquals(jobId, request.getJobId());
        assertEquals(executionDetails.getSummary(), request.getExecutionDetails().getSummary());
        assertEquals(executionDetails.getExternalExecutionId(), request.getExecutionDetails().getExternalExecutionId());
        assertEquals(executionDetails.getPercentComplete(), request.getExecutionDetails().getPercentComplete().intValue());
        assertEquals(currentRevision.getRevision(), request.getCurrentRevision().getRevision());
        assertEquals(currentRevision.getChangeIdentifier(), request.getCurrentRevision().getChangeIdentifier());
        assertEquals(continuationToken, request.getContinuationToken());
    }

    @Test
    public void shouldCallPutJobFailureResult() {
        // given
        final String jobId = UUID.randomUUID().toString();
        final String clientId = UUID.randomUUID().toString();
        final FailureDetails failureDetails = new FailureDetails(FailureType.JobFailed, "test message");

        // when
        jobService.putJobFailure(jobId, clientId, failureDetails);

        // then
        verify(codePipelineClient).putJobFailureResult(putJobFailureResultRequestCaptor.capture());

        final PutJobFailureResultRequest request = putJobFailureResultRequestCaptor.getValue();
        assertEquals(jobId, request.getJobId());
        assertEquals(failureDetails.getType().toString(), request.getFailureDetails().getType());
        assertEquals(failureDetails.getMessage(), request.getFailureDetails().getMessage());
    }

    private PollForJobsResult generatePollForJobsResult() {
        final com.amazonaws.services.codepipeline.model.JobData jobData = new com.amazonaws.services.codepipeline.model.JobData();
        jobData.setContinuationToken(UUID.randomUUID().toString());
        jobData.setInputArtifacts(Arrays.asList(JobDataGenerator.generateArtifact()));
        jobData.setOutputArtifacts(Arrays.asList(JobDataGenerator.generateArtifact()));
        jobData.setActionConfiguration(JobDataGenerator.generateActionConfiguration());
        jobData.setPipelineContext(JobDataGenerator.generatePipelineContext());
        jobData.setActionTypeId(JobDataGenerator.generateActionTypeId());
        jobData.setArtifactCredentials(JobDataGenerator.generateAWSSessionCredentials());

        final Job job = new Job();
        job.setId(UUID.randomUUID().toString());
        job.setNonce(UUID.randomUUID().toString());
        job.setData(jobData);
        job.setAccountId(UUID.randomUUID().toString());
        final PollForJobsResult result = new PollForJobsResult();
        result.setJobs(Arrays.asList(job));
        return result;
    }
}

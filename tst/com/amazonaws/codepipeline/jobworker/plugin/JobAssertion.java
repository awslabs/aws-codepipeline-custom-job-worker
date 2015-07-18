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
package com.amazonaws.codepipeline.jobworker.plugin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import com.amazonaws.codepipeline.jobworker.model.AWSSessionCredentials;
import com.amazonaws.codepipeline.jobworker.model.JobData;
import com.amazonaws.services.codepipeline.model.ActionConfiguration;
import com.amazonaws.services.codepipeline.model.Artifact;

/**
 * Helper class to assert that job data structures contain the same data.
 * This class is used in the third party and custom action job service tests.
 */
public final class JobAssertion {

    private JobAssertion() {
        // Utility class
    }

    /**
     * Asserts that the given Job Worker Job Data structure contains the same data like the Job Data from the FrontEnd.
     * @param expectedJobData FrontEnd Job Data structure
     * @param actualJobData Job Worker Job Data structure
     */
    public static void assertJobDataEquals(final com.amazonaws.services.codepipeline.model.JobData expectedJobData, final JobData actualJobData) {
        assertEquals(expectedJobData.getContinuationToken(), actualJobData.getContinuationToken());
        assertActionConfiguration(expectedJobData.getActionConfiguration(), actualJobData.getActionConfiguration());
        assertArtifacts(expectedJobData.getInputArtifacts(), actualJobData.getInputArtifacts());
        assertArtifacts(expectedJobData.getOutputArtifacts(), actualJobData.getOutputArtifacts());
        assertArtifactCredentials(expectedJobData.getArtifactCredentials(), actualJobData.getArtifactCredentials());
    }

    /**
     * Asserts that the given Job Worker Job Data structure contains the same data like the Third Party Job Data from the FrontEnd.
     * @param expectedJobData FrontEnd Third Party Job Data structure
     * @param actualJobData Job Worker Job Data structure
     */
    public static void assertJobDataEquals(final com.amazonaws.services.codepipeline.model.ThirdPartyJobData expectedJobData, final JobData actualJobData) {
        assertEquals(expectedJobData.getContinuationToken(), actualJobData.getContinuationToken());
        assertActionConfiguration(expectedJobData.getActionConfiguration(), actualJobData.getActionConfiguration());
        assertArtifacts(expectedJobData.getInputArtifacts(), actualJobData.getInputArtifacts());
        assertArtifacts(expectedJobData.getOutputArtifacts(), actualJobData.getOutputArtifacts());
        assertArtifactCredentials(expectedJobData.getArtifactCredentials(), actualJobData.getArtifactCredentials());
    }

    private static void assertArtifactCredentials(final com.amazonaws.services.codepipeline.model.AWSSessionCredentials expectedArtifactCredentials, final AWSSessionCredentials actualArtifactCredentials) {
        if (expectedArtifactCredentials == null) {
            assertNull(actualArtifactCredentials);
        } else {
            assertEquals(expectedArtifactCredentials.getAccessKeyId(), actualArtifactCredentials.getAccessKeyId());
            assertEquals(expectedArtifactCredentials.getSecretAccessKey(), actualArtifactCredentials.getSecretAccessKey());
            assertEquals(expectedArtifactCredentials.getSessionToken(), actualArtifactCredentials.getSessionToken());
        }
    }

    private static void assertActionConfiguration(final ActionConfiguration expectedActionConfiguration, final Map<String, String> actualActionConfiguration) {
        if (expectedActionConfiguration == null || expectedActionConfiguration.getConfiguration() == null) {
            assertEquals(0, actualActionConfiguration.size());
        } else {
            assertThat(expectedActionConfiguration.getConfiguration().entrySet(), equalTo(actualActionConfiguration.entrySet()));
        }
    }

    private static void assertArtifacts(final List<Artifact> expectedArtifacts, final List<com.amazonaws.codepipeline.jobworker.model.Artifact> actualArtifacts) {
        assertEquals(expectedArtifacts.size(), actualArtifacts.size());
        for(int i = 0; i < expectedArtifacts.size(); i++) {
            assertArtifact(expectedArtifacts.get(i), actualArtifacts.get(i));
        }
    }

    private static void assertArtifact(final Artifact expectedArtifact, final com.amazonaws.codepipeline.jobworker.model.Artifact actualArtifact) {
        assertEquals(expectedArtifact.getName(), actualArtifact.getName());
        assertEquals(expectedArtifact.getRevision(), actualArtifact.getRevision());
        assertEquals(expectedArtifact.getLocation().getS3Location().getBucketName(), actualArtifact.getS3BucketName());
        assertEquals(expectedArtifact.getLocation().getS3Location().getObjectKey(), actualArtifact.getS3ObjectKey());
    }
}

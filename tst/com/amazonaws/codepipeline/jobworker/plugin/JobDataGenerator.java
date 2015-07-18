package com.amazonaws.codepipeline.jobworker.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.codepipeline.model.AWSSessionCredentials;
import com.amazonaws.services.codepipeline.model.ActionConfiguration;
import com.amazonaws.services.codepipeline.model.ActionContext;
import com.amazonaws.services.codepipeline.model.ActionTypeId;
import com.amazonaws.services.codepipeline.model.Artifact;
import com.amazonaws.services.codepipeline.model.ArtifactLocation;
import com.amazonaws.services.codepipeline.model.PipelineContext;
import com.amazonaws.services.codepipeline.model.S3ArtifactLocation;
import com.amazonaws.services.codepipeline.model.StageContext;

/**
 * Helper class generate job data structures.
 * This class generates all the fields of the job data like artifacts, action configurations, pipeline contexts,
 * action type ids and AWS session credentials.
 */
public final class JobDataGenerator {

    private JobDataGenerator() {
        // Utility class
    }

    /**
     * Generates a random artifact. Can be used as input or output artifact.
     * @return artifact data structure.
     */
    public static Artifact generateArtifact() {
        final S3ArtifactLocation s3ArtifactLocation = new S3ArtifactLocation();
        s3ArtifactLocation.setBucketName("my-bucket");
        s3ArtifactLocation.setObjectKey("my-object-key");

        final ArtifactLocation artifactLocation = new ArtifactLocation();
        artifactLocation.setType("S3");
        artifactLocation.setS3Location(s3ArtifactLocation);

        final Artifact artifact = new Artifact();
        artifact.setName("MyArtifact");
        artifact.setRevision(UUID.randomUUID().toString());
        artifact.setLocation(artifactLocation);
        return artifact;
    }

    /**
     * Generates a random action configuration structure.
     * @return action configuration structure.
     */
    public static ActionConfiguration generateActionConfiguration() {
        final Map<String, String> actionConfigurationMap = new HashMap<>();
        actionConfigurationMap.put("my-key", "my-value");

        final ActionConfiguration actionConfiguration = new ActionConfiguration();
        actionConfiguration.setConfiguration(actionConfigurationMap);

        return actionConfiguration;
    }

    /**
     * Generates a random pipeline context.
     * @return pipeline context structure.
     */
    public static PipelineContext generatePipelineContext() {
        final ActionContext actionContext = new ActionContext();
        actionContext.setName("MyPipeline");

        final StageContext stageContext = new StageContext();
        stageContext.setName("MyPipeline");

        final PipelineContext pipelineContext = new PipelineContext();
        pipelineContext.setPipelineName("MyPipeline");
        pipelineContext.setStage(stageContext);
        pipelineContext.setAction(actionContext);

        return pipelineContext;
    }

    /**
     * Generates a random action type id.
     * @return action type id structure.
     */
    public static ActionTypeId generateActionTypeId() {
        final ActionTypeId actionTypeId = new ActionTypeId();
        actionTypeId.setCategory("Deploy");
        actionTypeId.setOwner("Custom");
        actionTypeId.setProvider("MyProvider");
        actionTypeId.setVersion("1");

        return actionTypeId;
    }

    /**
     * Generates random AWS session credentials.
     * @return AWS session credentials structure.
     */
    public static AWSSessionCredentials generateAWSSessionCredentials() {
        final AWSSessionCredentials awsSessionCredentials = new AWSSessionCredentials();
        awsSessionCredentials.setAccessKeyId(UUID.randomUUID().toString());
        awsSessionCredentials.setSecretAccessKey(UUID.randomUUID().toString());
        awsSessionCredentials.setSessionToken(UUID.randomUUID().toString());

        return awsSessionCredentials;
    }
}

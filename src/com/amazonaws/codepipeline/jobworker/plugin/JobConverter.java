package com.amazonaws.codepipeline.jobworker.plugin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.codepipeline.jobworker.model.AWSSessionCredentials;
import com.amazonaws.codepipeline.jobworker.model.ActionType;
import com.amazonaws.codepipeline.jobworker.model.Artifact;
import com.amazonaws.codepipeline.jobworker.model.CurrentRevision;
import com.amazonaws.codepipeline.jobworker.model.ExecutionDetails;
import com.amazonaws.codepipeline.jobworker.model.FailureDetails;
import com.amazonaws.codepipeline.jobworker.model.JobData;
import com.amazonaws.codepipeline.jobworker.model.WorkItem;
import com.amazonaws.services.codepipeline.model.Job;
import com.amazonaws.services.codepipeline.model.S3ArtifactLocation;
import com.amazonaws.services.codepipeline.model.ThirdPartyJobDetails;

/**
 * Converts between the CodePipeline FrontEnd Job data structures and internal job worker structures.
 */
public class JobConverter {

    /**
     * Converts the custom action job details into a work item
     * which can be processed by the job worker.
     * @param job the custom action job details
     * @return work item which contains all details needed to process the job
     */
    public final static WorkItem convert(final Job job) {
        return new WorkItem(job.getId(),
                job.getNonce(),
                JobConverter.convert(job.getData()),
                job.getAccountId());
    }

    /**
     * Converts the third party job details into a work item
     * which can be processed by the job worker.
     * @param clientId the client id which identifies the customer
     * @param jobDetails the third party job details
     * @return work item which contains all details needed to process the job
     */
    public final static WorkItem convert(final String clientId, final ThirdPartyJobDetails jobDetails) {
        return new WorkItem(jobDetails.getId(),
                jobDetails.getNonce(),
                convert(jobDetails.getData()),
                clientId);
    }

    private final static JobData convert(final com.amazonaws.services.codepipeline.model.JobData jobData) {
        Map<String, String> actionConfiguration = null;
        if (jobData.getActionConfiguration() != null) {
            actionConfiguration = jobData.getActionConfiguration().getConfiguration();
        }
        return new JobData(actionConfiguration,
                convert(jobData.getInputArtifacts()),
                convert(jobData.getOutputArtifacts()),
                convert(jobData.getArtifactCredentials()),
                jobData.getContinuationToken());
    }

    private final static JobData convert(final com.amazonaws.services.codepipeline.model.ThirdPartyJobData jobData) {
        Map<String, String> actionConfiguration = null;
        if (jobData.getActionConfiguration() != null) {
            actionConfiguration = jobData.getActionConfiguration().getConfiguration();
        }
        return new JobData(actionConfiguration,
                convert(jobData.getInputArtifacts()),
                convert(jobData.getOutputArtifacts()),
                convert(jobData.getArtifactCredentials()),
                jobData.getContinuationToken());
    }

    private final static AWSSessionCredentials convert(final com.amazonaws.services.codepipeline.model.AWSSessionCredentials actionCredentials) {
        if (actionCredentials == null) {
            return null;
        }
        return new AWSSessionCredentials(actionCredentials.getAccessKeyId(),
                actionCredentials.getSecretAccessKey(),
                actionCredentials.getSessionToken());
    }

    private final static List<Artifact> convert(final List<com.amazonaws.services.codepipeline.model.Artifact> artifacts) {
        if (artifacts == null) {
            return null;
        }
        return artifacts.stream()
                .map(a -> convert(a))
                .collect(Collectors.toList());
    }

    private final static Artifact convert(final com.amazonaws.services.codepipeline.model.Artifact artifact) {
        String bucketName = null;
        String objectKey = null;
        if (artifact.getLocation() != null && artifact.getLocation().getS3Location() != null) {
            S3ArtifactLocation s3ArtifactLocation = artifact.getLocation().getS3Location();
            bucketName = s3ArtifactLocation.getBucketName();
            objectKey = s3ArtifactLocation.getObjectKey();
        }
        return new Artifact(artifact.getName(),
                artifact.getRevision(),
                bucketName,
                objectKey);
    }

    /**
     * Converts the current revision structure to the third party model.
     * @param currentRevision current revision
     * @return third party model current revision
     */
    public final static com.amazonaws.services.codepipeline.model.CurrentRevision convert(final CurrentRevision currentRevision) {
        if (currentRevision == null) {
            return null;
        }
        com.amazonaws.services.codepipeline.model.CurrentRevision result = new com.amazonaws.services.codepipeline.model.CurrentRevision();
        result.setChangeIdentifier(currentRevision.getChangeIdentifier());
        result.setRevision(currentRevision.getRevision());
        return result;
    }

    /**
     * Converts the execution details structure to the third party model.
     * @param executionDetails execution details
     * @return third party model execution details
     */
    public final static com.amazonaws.services.codepipeline.model.ExecutionDetails convert(final ExecutionDetails executionDetails) {
        if (executionDetails == null) {
            return null;
        }
        com.amazonaws.services.codepipeline.model.ExecutionDetails result = new com.amazonaws.services.codepipeline.model.ExecutionDetails();
        result.setExternalExecutionId(executionDetails.getExternalExecutionId());
        result.setSummary(executionDetails.getSummary());
        result.setPercentComplete(executionDetails.getPercentComplete());
        return result;
    }

    /**
     * Converts the failure details structure to the third party model.
     * @param failureDetails failure details describe why the job did not succeed
     * @return third party model failure details
     */
    public final static com.amazonaws.services.codepipeline.model.FailureDetails convert(final FailureDetails failureDetails) {
        com.amazonaws.services.codepipeline.model.FailureDetails result = new com.amazonaws.services.codepipeline.model.FailureDetails();
        result.setType(failureDetails.getType().toString());
        result.setMessage(failureDetails.getMessage());
        return result;
    }

    /**
     * Converts the third party action type structure into the internal job worker structure.
     * @param actionType action type identifier
     * @return action type identifier (internal model)
     */
    public final static com.amazonaws.services.codepipeline.model.ActionType convert(final ActionType actionType) {
        com.amazonaws.services.codepipeline.model.ActionType result = new com.amazonaws.services.codepipeline.model.ActionType();
        result.setCategory(actionType.getCategory());
        result.setOwner(actionType.getOwner());
        result.setProvider(actionType.getProvider());
        result.setVersion(actionType.getVersion());
        return result;
    }
}

package com.amazonaws.codepipeline.jobworker.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Job data structure contains the details necessary to process the job.
 */
public class JobData {
    private final Map<String,String> actionConfiguration;
    private final List<Artifact> inputArtifacts;
    private final List<Artifact> outputArtifacts;
    private final AWSSessionCredentials artifactCredentials;
    private final String continuationToken;

    /**
     * Initializes the job data structure.
     * @param actionConfiguration configuration values from the pipeline action.
     * @param inputArtifacts input artifacts which can be downloaded from S3 and processed.
     * @param outputArtifacts output artifacts which have to be provided by this job worker.
     * @param artifactCredentials credentials to access input and output artifacts in S3.
     * @param continuationToken continuation token from the previous job if this is a follow up job.
     */
    public JobData(final Map<String,String> actionConfiguration,
                   final List<Artifact> inputArtifacts,
                   final List<Artifact> outputArtifacts,
                   final AWSSessionCredentials artifactCredentials,
                   final String continuationToken) {
        if (actionConfiguration == null) {
            this.actionConfiguration = Collections.emptyMap();
        } else {
            this.actionConfiguration = Collections.unmodifiableMap(actionConfiguration);
        }

        this.inputArtifacts = initArtifacts(inputArtifacts);
        this.outputArtifacts = initArtifacts(outputArtifacts);
        this.artifactCredentials = artifactCredentials;
        this.continuationToken = continuationToken;
    }

    private List<Artifact> initArtifacts(final List<Artifact> artifacts) {
        if (artifacts == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(artifacts);
        }
    }

    /**
     * @return configuration values from the pipeline action.
     */
    public Map<String, String> getActionConfiguration() {
        return actionConfiguration;
    }

    /**
     * @return input artifacts which can be downloaded from S3 and processed.
     */
    public List<Artifact> getInputArtifacts() {
        return inputArtifacts;
    }

    /**
     * @return output artifacts which have to be provided by this job worker.
     */
    public List<Artifact> getOutputArtifacts() {
        return outputArtifacts;
    }

    /**
     * @return credentials to access input and output artifacts in S3.
     */
    public AWSSessionCredentials getArtifactCredentials() {
        return artifactCredentials;
    }

    /**
     * @return continuation token from the previous job if this is a follow up job.
     */
    public String getContinuationToken() {
        return continuationToken;
    }
}

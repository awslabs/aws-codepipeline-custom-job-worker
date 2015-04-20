package com.amazonaws.codepipeline.jobworker.model;

import com.amazonaws.codepipeline.jobworker.Validator;

/**
 * Structure for the current revision.
 */
public class CurrentRevision {
    private final String revision;
    private final String changeIdentifier;

    /**
     * Initializes the current revision.
     * @param revision revision id.
     * @param changeIdentifier the change identifier.
     */
    public CurrentRevision(final String revision, final String changeIdentifier) {
        Validator.notNull(revision);
        Validator.notNull(changeIdentifier);
        this.revision = revision;
        this.changeIdentifier = changeIdentifier;
    }

    /**
     * @return revision id.
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @return the change identifier.
     */
    public String getChangeIdentifier() {
        return changeIdentifier;
    }
}

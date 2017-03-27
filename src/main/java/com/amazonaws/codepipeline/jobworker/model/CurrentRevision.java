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

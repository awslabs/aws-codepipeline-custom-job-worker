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

/**
 * Artifact structure.
 */
public class Artifact {
    private final String name;
    private final String revision;
    private final String s3BucketName;
    private final String s3ObjectKey;

    /**
     * Initializes the artifact structure.
     * @param name artifact name.
     * @param revision revision identifier.
     * @param s3BucketName S3 bucket name which contains the artifact.
     * @param s3ObjectKey S3 object key where the artifact is stored.
     */
    public Artifact(final String name, final String revision, final String s3BucketName, final String s3ObjectKey) {
        this.name = name;
        this.revision = revision;
        this.s3BucketName = s3BucketName;
        this.s3ObjectKey = s3ObjectKey;
    }

    /**
     * @return artifact name
     */
    public String getName() {
        return name;
    }

    /**
     * @return revision identifier
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @return S3 bucket name which contains the artifact
     */
    public String getS3BucketName() {
        return s3BucketName;
    }

    /**
     * @return S3 object key where the artifact is stored
     */
    public String getS3ObjectKey() {
        return s3ObjectKey;
    }
}

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
 * Represents information about the key used to encrypt data in the artifact store.
 * (e.g. an AWS Key Management Service (AWS KMS) key)
 */
public class EncryptionKey {
    private final EncryptionKeyType type;
    private final String id;

    /**
     * Initializes the encryption key.
     * @param type The type of encryption key, such as KMS
     * @param id The key ID or ARN
     */
    public EncryptionKey(final EncryptionKeyType type, final String id) {
        Validator.notNull(type);
        Validator.notNull(id);
        this.type = type;
        this.id = id;
    }

    /**
     * @return encryption key type
     */
    public EncryptionKeyType getType() {
        return type;
    }

    /**
     * @return encryption key id or ARN
     */
    public String getId() {
        return id;
    }
}

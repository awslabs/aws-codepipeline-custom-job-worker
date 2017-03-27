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
package com.amazonaws.codepipeline.jobworker.plugin.thirdparty;

/**
 * The client token provider looks up the corresponding client token for a given client id.
 * TODO: Replace this implementation with your own lookup method.
 */
public class DefaultClientTokenProvider implements ClientTokenProvider {
    /**
     * Static client token returned by all look ups.
     */
    public final static String DEFAULT_CLIENT_TOKEN = "DEFAULT_CLIENT_TOKEN";

    /**
     * This default implementation returns a static client token for all look ups.
     * @param clientId client id
     * @return 'DEFAULT_CLIENT_TOKEN' string
     */
    @Override
    public String lookupClientSecret(final String clientId) {
        return DEFAULT_CLIENT_TOKEN;
    }
}


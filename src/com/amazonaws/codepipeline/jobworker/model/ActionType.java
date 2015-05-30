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
 * Action type uniquely identifies a specific action which can be used in pipelines.
 */
public class ActionType {
    private final String category;
    private final String owner;
    private final String provider;
    private final String version;

    /**
     * Initializes the action type
     * @param category action category (Source, Build, Test, Deploy, etc...)
     * @param owner action owner (AWS, ThirdParty or CustomAction)
     * @param provider action provider (CodeDeploy, ElasticBeanstalk, OpsWorks, etc...)
     * @param version action version
     */
    public ActionType(final String category, final String owner, final String provider, final String version) {
        Validator.notNull(category);
        Validator.notNull(owner);
        Validator.notNull(provider);
        Validator.notNull(version);
        this.category = category;
        this.owner = owner;
        this.provider = provider;
        this.version = version;
    }

    /**
     * @return action category (Source, Build, Test, Deploy, etc...)
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return action owner (AWS, ThirdParty or CustomAction)
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @return action provider (CodeDeploy, ElasticBeanstalk, OpsWorks, etc...)
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @return action version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return action type as string
     */
    @Override
    public String toString() {
        return "[category=" + category + ", owner=" + owner + ", provider=" + provider + ", version=" + version + "]";
    }
}

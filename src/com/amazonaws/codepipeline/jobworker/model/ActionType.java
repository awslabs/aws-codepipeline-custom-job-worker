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

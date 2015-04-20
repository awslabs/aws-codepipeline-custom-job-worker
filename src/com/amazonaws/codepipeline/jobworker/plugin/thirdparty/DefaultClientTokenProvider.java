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
    public String lookupClientSecret(final String clientId) {
        return DEFAULT_CLIENT_TOKEN;
    }
}


package com.amazonaws.codepipeline.jobworker.plugin.thirdparty;

/**
 * The client token provider looks up the corresponding client token for a given client id.
 */
public interface ClientTokenProvider {
    /**
     * Looks up the client token for a given client id.
     * @param clientId client id
     * @return client token
     */
    String lookupClientSecret(String clientId);
}

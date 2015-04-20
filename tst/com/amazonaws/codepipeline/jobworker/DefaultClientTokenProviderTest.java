package com.amazonaws.codepipeline.jobworker;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.amazonaws.codepipeline.jobworker.plugin.thirdparty.DefaultClientTokenProvider;

public class DefaultClientTokenProviderTest {

    @Test
    public void should() {
        // when
        final String clientToken = new DefaultClientTokenProvider().lookupClientSecret(UUID.randomUUID().toString());

        // then
        assertEquals(clientToken, "DEFAULT_CLIENT_TOKEN");
    }
}

package com.unisoft.core.security.http.policy;

import com.unisoft.core.http.*;
import com.unisoft.core.security.credential.BasicAuthenticationCredential;
import com.unisoft.core.util.Base64Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthenticationPolicyTest {

    @Test
    void getAndSetAuthorizationHeader() {
        String password = "password";
        String username = "username";
        String expectedHeaderValue = "basic " + Base64Util.encodeToString((username + ":" + password).getBytes());
        final BasicAuthenticationCredential credential = new BasicAuthenticationCredential(username, password.toCharArray());

        final BasicAuthenticationPolicy basicAuthenticationPolicy = new BasicAuthenticationPolicy(credential);

        final HttpPipeline pipeline = new HttpPipelineBuilder()
                .httpClient(new HttpClient() {
                    @Override
                    public Mono<HttpResponse> send(HttpRequest request) {
                        final String authorization = request.getHeaders().getValue("authorization");
                        assertNotNull(authorization);
                        assertEquals(expectedHeaderValue, authorization);
                        return Mono.empty();
                    }
                })
                .policies(basicAuthenticationPolicy)
                .build();

        final Mono<HttpResponse> responseMono = pipeline.send(new HttpRequest(HttpMethod.GET, "http://test.com"));
        assertDoesNotThrow((ThrowingSupplier<HttpResponse>) responseMono::block);
    }
}
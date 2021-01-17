package com.unisoft.core.security.http.policy;

import com.unisoft.core.http.*;
import com.unisoft.core.security.credential.AccessToken;
import com.unisoft.core.security.credential.TokenCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class BearerAuthenticationPolicyTest {

    @Test
    void getAndSetAuthorizationHeader() {
        String expectedHeaderValue = "bearer access-token";
        final TokenCredential credential = new TokenCredential() {
            @Override
            public Mono<AccessToken> getToken() {
                return Mono.just(new AccessToken("access-token", OffsetDateTime.now().plus(2, ChronoUnit.MINUTES)));
            }
        };

        final BearerAuthenticationPolicy bearerAuthenticationPolicy = new BearerAuthenticationPolicy(credential);

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
                .policies(bearerAuthenticationPolicy)
                .build();

        final Mono<HttpResponse> responseMono = pipeline.send(new HttpRequest(HttpMethod.GET, "http://test.com"));
        assertDoesNotThrow((ThrowingSupplier<HttpResponse>) responseMono::block);
    }

}
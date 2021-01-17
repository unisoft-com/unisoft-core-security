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

class AccessTokenPolicyTest {

    @Test
    void instantiate() {
        TokenCredential credential = new TokenCredential() {
            @Override
            public Mono<AccessToken> getToken() {
                return Mono.empty();
            }
        };
        final AccessTokenPolicy policy = assertDoesNotThrow(() -> new AccessTokenPolicy("header", "prefix ", credential));
        assertThrows(IllegalArgumentException.class, () -> new AccessTokenPolicy("", "prefix", credential));
        assertThrows(NullPointerException.class, () -> new AccessTokenPolicy(null, "prefix", credential));
        assertThrows(NullPointerException.class, () -> new AccessTokenPolicy("header", "prefix", null));
        assertThrows(NullPointerException.class, () -> new AccessTokenPolicy("header", null, credential));

        assertNotNull(policy);
    }

    @Test
    void getAndSetAccessHeader() {
        TokenCredential credential = new TokenCredential() {
            @Override
            public Mono<AccessToken> getToken() {
                return Mono.just(new AccessToken("access-token", OffsetDateTime.now().plus(2, ChronoUnit.MINUTES)));
            }
        };

        final AccessTokenPolicy accessTokenPolicy = new AccessTokenPolicy("header", "prefix ", credential);

        final HttpPipeline pipeline = new HttpPipelineBuilder()
                .httpClient(new HttpClient() {
                    @Override
                    public Mono<HttpResponse> send(HttpRequest request) {
                        final String authorization = request.getHeaders().getValue("header");
                        assertNotNull(authorization);
                        assertEquals("prefix access-token", authorization);
                        return Mono.empty();
                    }
                })
                .policies(accessTokenPolicy)
                .build();

        final Mono<HttpResponse> responseMono = pipeline.send(new HttpRequest(HttpMethod.GET, "http://test.com"));
        assertDoesNotThrow((ThrowingSupplier<HttpResponse>) responseMono::block);

    }
}
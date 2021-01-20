package com.unisoft.core.security.credential;

import reactor.core.publisher.Mono;

/**
 * Contract for credentials that can provide a token.
 *
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
@FunctionalInterface
public interface TokenCredential {

    /**
     * Asynchronously get a token for a given resource/audience.
     *
     * @return a Publisher that emits a single access token
     */
    Mono<? extends AccessGranter> getToken();
}

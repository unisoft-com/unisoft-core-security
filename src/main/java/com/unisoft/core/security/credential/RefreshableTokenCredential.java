package com.unisoft.core.security.credential;

import reactor.core.publisher.Mono;

/**
 * a credential tha supports refreshing an access token by a refresh token
 *
 * @author omar.H.Ajmi
 * @since 20/01/2021
 */
public interface RefreshableTokenCredential extends TokenCredential {
    /**
     * Asynchronously refreshes a token using a given refresh token.
     *
     * @return a Publisher that emits a single access token
     */
    Mono<RefreshableAccessGranter> getRefreshToken(String refreshToken);
}

package com.unisoft.core.security.credential;

import com.unisoft.core.annotations.Immutable;

import java.time.OffsetDateTime;

/**
 * Basic {@link AccessGranter} implementation
 *
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
@Immutable
public class AccessToken implements AccessGranter {

    private final String token;
    private final OffsetDateTime expiresAt;

    /**
     * Creates an access token instance.
     *
     * @param token     the token string.
     * @param expiresAt the expiration time.
     */
    public AccessToken(String token, OffsetDateTime expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt.minusSeconds(10); // 10 seconds before token expires
    }

    /**
     * @return the token string.
     */
    @Override
    public String getToken() {
        return token;
    }

    /**
     * @return the time when the token expires, in UTC.
     */
    @Override
    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * @return if the token has expired.
     */
    @Override
    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(this.expiresAt);
    }
}

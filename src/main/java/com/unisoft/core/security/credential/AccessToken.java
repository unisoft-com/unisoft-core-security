package com.unisoft.core.security.credential;

import java.time.OffsetDateTime;

/**
 * Represents an immutable access token with a token string and an expiration time.
 *
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
public class AccessToken {

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
    public String getToken() {
        return token;
    }

    /**
     * @return the time when the token expires, in UTC.
     */
    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * @return if the token has expired.
     */
    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(this.expiresAt);
    }
}

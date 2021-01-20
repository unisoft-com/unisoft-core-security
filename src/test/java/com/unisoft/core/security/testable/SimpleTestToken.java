package com.unisoft.core.security.testable;

import com.unisoft.core.security.credential.AccessToken;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * simple {@link AccessToken} implementation
 *
 * @author omar.H.Ajmi
 * @since 20/01/2021
 */
public class SimpleTestToken extends AccessToken {
    private final String token;
    private final OffsetDateTime expiry;

    public SimpleTestToken(String token) {
        this(token, 5000);
    }

    public SimpleTestToken(String token, long validityInMillis) {
        super(token, OffsetDateTime.now().plus(Duration.ofMillis(validityInMillis)));
        this.token = token;
        this.expiry = OffsetDateTime.now().plus(Duration.ofMillis(validityInMillis));
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public OffsetDateTime getExpiresAt() {
        return expiry;
    }

    @Override
    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiry);
    }
}
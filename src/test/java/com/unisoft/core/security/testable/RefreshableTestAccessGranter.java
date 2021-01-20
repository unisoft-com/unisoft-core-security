package com.unisoft.core.security.testable;

import com.unisoft.core.security.credential.AccessToken;
import com.unisoft.core.security.credential.RefreshableAccessGranter;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * refreshable {@link AccessToken} implementation
 *
 * @author omar.H.Ajmi
 * @since 20/01/2021
 */
public class RefreshableTestAccessGranter extends AccessToken implements RefreshableAccessGranter {

    private final String token;
    private final String refreshToken;
    private final OffsetDateTime expiry;

    public RefreshableTestAccessGranter(String token) {
        this(token, 5000);
    }

    public RefreshableTestAccessGranter(String token, long validityInMillis) {
        super(token, OffsetDateTime.now().plus(Duration.ofMillis(validityInMillis)));
        this.token = token;
        this.refreshToken = "dummy-refresh-token";
        this.expiry = OffsetDateTime.now().plus(Duration.ofMillis(validityInMillis));
    }

    @Override
    public String getRefreshToken() {
        return this.refreshToken;
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

package com.unisoft.core.security.credential;

import java.time.OffsetDateTime;

/**
 * Represents an immutable access token with a token string and an expiration time.
 *
 * @author omar.H.Ajmi
 * @since 20/01/2021
 */
public interface AccessGranter {


    /**
     * gets the access token
     *
     * @return access token
     */
    String getToken();

    /**
     * gets the token expiration datetime
     *
     * @return expiration datetime
     */
    OffsetDateTime getExpiresAt();

    /**
     * determines if access token is expired
     *
     * @return true if expired else if  not
     */
    boolean isExpired();
}

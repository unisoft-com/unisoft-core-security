package com.unisoft.core.security.credential;

/**
 * represents a refreshable access granter
 *
 * @author omar.H.Ajmi
 * @since 20/01/2021
 */
public interface RefreshableAccessGranter extends AccessGranter {

    /**
     * gets the refresh token
     *
     * @return refresh token
     */
    String getRefreshToken();
}

package com.unisoft.core.security.http.policy;

import com.unisoft.core.security.credential.TokenCredential;

/**
 * Bearer authentication header provider
 *
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
public class BearerAuthenticationPolicy extends AccessTokenPolicy {
    public BearerAuthenticationPolicy(TokenCredential credential) {
        super("authorization", "bearer ", credential);
    }
}

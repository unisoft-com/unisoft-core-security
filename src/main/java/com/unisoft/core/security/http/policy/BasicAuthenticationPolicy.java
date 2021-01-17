package com.unisoft.core.security.http.policy;

import com.unisoft.core.security.credential.BasicAuthenticationCredential;

/**
 * basic authentication header provider
 *
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
public class BasicAuthenticationPolicy extends AccessTokenPolicy {
    public BasicAuthenticationPolicy(BasicAuthenticationCredential credential) {
        super("authorization", "basic ", credential);
    }
}

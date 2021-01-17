package com.unisoft.core.security.credential;

import com.unisoft.core.util.CoreUtil;

/**
 * contract for credential disposal behavior
 *
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
public interface DisposableCredential {
    /**
     * disposes of credential (password)
     * by filling its array of char with random chars
     * dispose of credentials
     *
     * @param disable to be disposed of
     */
    default void dispose(char[] disable) {
        CoreUtil.dispose(disable);
    }
}

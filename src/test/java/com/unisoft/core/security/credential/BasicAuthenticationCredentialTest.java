package com.unisoft.core.security.credential;

import com.unisoft.core.util.Base64Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthenticationCredentialTest {

    @Test
    void instantiate() {
        final char[] password = "password".toCharArray();
        final char[] emptyPassword = "".toCharArray();
        final BasicAuthenticationCredential credential = assertDoesNotThrow(() -> new BasicAuthenticationCredential("username", password));
        assertThrows(NullPointerException.class, () -> new BasicAuthenticationCredential(null, password));
        assertThrows(IllegalArgumentException.class, () -> new BasicAuthenticationCredential("", emptyPassword));
        assertThrows(NullPointerException.class, () -> new BasicAuthenticationCredential("username", null));
        assertNotNull(credential);
    }

    @Test
    void getToken() {
        String username = "username";
        String password = "password";
        final String encodedToken = Base64Util.encodeToString((username + ":" + password).getBytes());
        final BasicAuthenticationCredential credential = new BasicAuthenticationCredential(username, password.toCharArray());
        credential.getToken()
                .doOnNext(accessToken -> assertEquals(encodedToken, accessToken.getToken()))
                .block();
    }
}
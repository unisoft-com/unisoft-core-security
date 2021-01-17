package com.unisoft.core.security.credential;

import com.unisoft.core.util.Base64Util;
import com.unisoft.core.util.CoreUtil;
import com.unisoft.core.util.log.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
public class BasicAuthenticationCredential implements TokenCredential, DisposableCredential {
    private static final Logger log = LoggerFactory.getLogger(BasicAuthenticationCredential.class);

    private final String username;
    private final char[] password;

    public BasicAuthenticationCredential(String username, char[] password) {
        LogUtil.logExceptionAsError(log, () -> CoreUtil.requireNonNullOrEmpty(username, "'username' cannot be null or empty"));
        LogUtil.logExceptionAsError(log, () -> Objects.requireNonNull(password, "'password' cannot be null"));
        this.username = username;
        this.password = password;
    }

    /**
     * Asynchronously base64 encode a token.
     *
     * @return Publisher that emits a base64 encoded access token
     * @throws RuntimeException If the UTF-8 encoding isn't supported.
     */
    @Override
    public Mono<AccessToken> getToken() {
        String encodedCredential;
        encodedCredential = Base64Util.encodeToString((this.username + ":" + new String(this.password)).getBytes(StandardCharsets.UTF_8));
        return Mono.just(new AccessToken(encodedCredential, OffsetDateTime.MAX))
                .doOnNext(accessToken -> this.dispose(this.password));
    }
}

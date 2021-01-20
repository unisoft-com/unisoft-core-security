package com.unisoft.core.security.http.policy;

import com.unisoft.core.http.HttpPipelineCallContext;
import com.unisoft.core.http.HttpPipelineNextPolicy;
import com.unisoft.core.http.HttpResponse;
import com.unisoft.core.http.policy.HttpPipelinePolicy;
import com.unisoft.core.security.credential.SimpleTokenCache;
import com.unisoft.core.security.credential.TokenCredential;
import com.unisoft.core.util.CoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * generic access token authentication header provider
 *
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
public class AccessTokenPolicy implements HttpPipelinePolicy {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenPolicy.class);

    protected final String header;
    protected final String prefix;
    private final SimpleTokenCache tokenCache;

    public AccessTokenPolicy(String header, String prefix, TokenCredential credential) {
        CoreUtil.requireNonNullOrEmpty(header, "'header' cannot be empty null");
        Objects.requireNonNull(prefix, "'prefix' cannot be null");
        Objects.requireNonNull(credential, "'credential' cannot be null");

        this.header = header;
        this.prefix = prefix;
        tokenCache = new SimpleTokenCache(credential::getToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<HttpResponse> process(HttpPipelineCallContext context, HttpPipelineNextPolicy next) {
        return this.tokenCache.getToken()
                .flatMap(accessToken -> {
                    log.debug("injecting access-token");
                    context.getHttpRequest().setHeader(this.header, this.prefix + accessToken.getToken());
                    return next.process();
                });
    }
}

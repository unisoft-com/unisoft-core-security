package com.unisoft.core.security.credential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * a token cache that refreshes token when needed
 *
 * @author omar.H.Ajmi
 * @since 20/01/2021
 */
public class RefreshableTokenCache implements TokenCache {

    private static final Logger log = LoggerFactory.getLogger(RefreshableTokenCache.class);

    private final AtomicBoolean wip;
    private final ReplayProcessor<RefreshableAccessGranter> emitterProcessor = ReplayProcessor.create(1);
    private final FluxSink<RefreshableAccessGranter> sink = emitterProcessor.sink(FluxSink.OverflowStrategy.BUFFER);
    private final RefreshableTokenCredential credential;
    private RefreshableAccessGranter cache;

    public RefreshableTokenCache(RefreshableTokenCredential credential) {
        this.credential = credential;
        this.wip = new AtomicBoolean(false);
    }

    /**
     * Asynchronously get a token from either the cache or replenish the cache with a new token.
     *
     * @return a Publisher that emits an AccessToken
     */
    @Override
    public Mono<RefreshableAccessGranter> getToken() {
        if (this.cache != null) {
            // token available but expired
            if (this.cache.isExpired()) {
                return Mono.defer(() -> {
                    if (!this.wip.getAndSet(true)) {
                        return this.credential.getRefreshToken(this.cache.getRefreshToken())
                                .doOnNext(ac -> {
                                    this.cache = ac;
                                    log.debug("token cached! will expire after {} seconds", ac.getExpiresAt().getSecond());
                                })
                                .doOnNext(this.sink::next)
                                .doOnError(this.sink::error)
                                .doOnTerminate(() -> wip.set(false));
                    } else {
                        return this.emitterProcessor.next();
                    }
                });
            } else {
                // token available and valid
                log.debug("token is valid! returning cached token");
                return Mono.just(this.cache);
            }
        }

        // no token available then get a new one
        return Mono.defer(() -> {
            if (!wip.getAndSet(true)) {
                return this.credential.getToken().doOnNext(ac -> {
                    this.cache = (RefreshableAccessGranter) ac;
                    log.debug("token cached! will expire after {} seconds", ac.getExpiresAt().getSecond());
                })
                        .map(accessToken -> (RefreshableAccessGranter) accessToken)
                        .doOnNext(this.sink::next)
                        .doOnError(this.sink::error)
                        .doOnTerminate(() -> wip.set(false));
            } else {
                return this.emitterProcessor.next();
            }
        });
    }
}

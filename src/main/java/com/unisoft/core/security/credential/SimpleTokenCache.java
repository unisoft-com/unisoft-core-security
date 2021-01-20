package com.unisoft.core.security.credential;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author omar.H.Ajmi
 * @since 23/10/2020
 */
public class SimpleTokenCache implements TokenCache {
    private static final Logger log = LoggerFactory.getLogger(SimpleTokenCache.class);

    private final AtomicBoolean wip;
    private final ReplayProcessor<AccessToken> emitterProcessor = ReplayProcessor.create(1);
    private final FluxSink<AccessToken> sink = emitterProcessor.sink(FluxSink.OverflowStrategy.BUFFER);
    private final Supplier<Mono<? extends AccessGranter>> tokenSupplier;
    private AccessToken cache;

    /**
     * Creates an instance of RefreshableTokenCredential with default scheme "Bearer".
     *
     * @param tokenSupplier a method to get a new token
     */
    public SimpleTokenCache(Supplier<Mono<? extends AccessGranter>> tokenSupplier) {
        this.wip = new AtomicBoolean(false);
        this.tokenSupplier = tokenSupplier;
    }

    /**
     * Asynchronously get a token from either the cache or replenish the cache with a new token.
     *
     * @return a Publisher that emits an AccessToken
     */
    public Mono<? extends AccessGranter> getToken() {
        if (this.cache != null && !this.cache.isExpired()) {
            log.debug("token is valid! returning cached token");
            return Mono.just(this.cache);
        }
        return Mono.defer(() -> {
            if (!wip.getAndSet(true)) {
                return this.tokenSupplier.get().doOnNext(ac -> {
                    this.cache = (AccessToken) ac;
                    log.debug("token cached! will expire after {} seconds", ac.getExpiresAt().getSecond());
                })
                        .doOnNext(t -> this.sink.next((AccessToken) t))
                        .doOnError(this.sink::error)
                        .doOnTerminate(() -> wip.set(false));
            } else {
                return this.emitterProcessor.next();
            }
        });
    }
}

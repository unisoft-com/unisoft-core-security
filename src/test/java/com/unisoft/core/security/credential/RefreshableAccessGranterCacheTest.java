package com.unisoft.core.security.credential;

import com.unisoft.core.security.testable.RefreshableTestAccessGranter;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.unisoft.core.security.testable.TokenCacheTestUtil.incrementalRemoteGetTokenAsync;
import static com.unisoft.core.security.testable.TokenCacheTestUtil.remoteGetTokenThatExpiresSoonAsync;

class RefreshableAccessGranterCacheTest extends TokenCacheTest {

    @Test
    void testOnlyOneThreadRefreshesToken() throws Exception {
        // Token acquisition time grows in 1 sec, 2 sec... To make sure only one token acquisition is run
        RefreshableTokenCache cache = new RefreshableTokenCache(new RefreshableTokenCredential() {
            @Override
            public Mono<RefreshableAccessGranter> getRefreshToken(String refreshToken) {
                return Mono.just(new RefreshableTestAccessGranter("refreshed-access-token", 1000L));
            }

            @Override
            public Mono<? extends AccessGranter> getToken() {
                return incrementalRemoteGetTokenAsync(new AtomicInteger(1), RefreshableTestAccessGranter.class, 1);
            }
        });
        super.testOnlyOneThreadRefreshesToken(cache);
    }

    @Test
    void testLongRunningWontOverflow() throws Exception {
        AtomicLong refreshes = new AtomicLong(0);
        RefreshableTokenCache cache = new RefreshableTokenCache(new RefreshableTokenCredential() {
            @Override
            public Mono<RefreshableAccessGranter> getRefreshToken(String refreshToken) {
                refreshes.incrementAndGet();
                return Mono.just(new RefreshableTestAccessGranter("refreshed-access-token", 1000L));
            }

            @Override
            public Mono<? extends AccessGranter> getToken() {
                return remoteGetTokenThatExpiresSoonAsync(1000, 0, RefreshableTestAccessGranter.class, 1);
            }
        });
        super.testLongRunningWontOverflow(cache, refreshes);
    }
}
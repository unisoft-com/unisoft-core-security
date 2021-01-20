package com.unisoft.core.security.credential;

import com.unisoft.core.security.testable.SimpleTestToken;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.unisoft.core.security.testable.TokenCacheTestUtil.incrementalRemoteGetTokenAsync;
import static com.unisoft.core.security.testable.TokenCacheTestUtil.remoteGetTokenThatExpiresSoonAsync;

class SimpleTokenCacheTest extends TokenCacheTest {

    @Test
    void testOnlyOneThreadRefreshesToken() throws Exception {
        // Token acquisition time grows in 1 sec, 2 sec... To make sure only one token acquisition is run
        SimpleTokenCache cache = new SimpleTokenCache(() -> incrementalRemoteGetTokenAsync(new AtomicInteger(1), SimpleTestToken.class, 1));
        super.testOnlyOneThreadRefreshesToken(cache);
    }

    @Test
    void testLongRunningWontOverflow() throws Exception {
        AtomicLong refreshes = new AtomicLong(0);
        // token expires on creation. Run this 100 times to simulate running the application a long time
        SimpleTokenCache cache = new SimpleTokenCache(() -> {
            refreshes.incrementAndGet();
            return remoteGetTokenThatExpiresSoonAsync(1000, 0, SimpleTestToken.class, 1);
        });
        super.testLongRunningWontOverflow(cache, refreshes);
    }
}
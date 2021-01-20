package com.unisoft.core.security.credential;

import org.junit.jupiter.api.Assertions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

class TokenCacheTest {

    void testOnlyOneThreadRefreshesToken(TokenCache cache) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicLong maxMillis = new AtomicLong(0);

        Flux.range(1, 10)
                .flatMap(i -> Mono.just(OffsetDateTime.now())
                        // Runs cache.getToken() on 10 different threads
                        .subscribeOn(Schedulers.newParallel("pool", 10))
                        .flatMap(start -> cache.getToken()
                                .map(t -> Duration.between(start, OffsetDateTime.now()).toMillis())
                                .doOnNext(millis -> {
                                    if (millis > maxMillis.get()) {
                                        maxMillis.set(millis);
                                    }
//                        System.out.format("Thread: %s\tDuration: %smillis%n",
//                            Thread.currentThread().getName(), Duration.between(start, OffsetDateTime.now()).toMillis());
                                })))
                .doOnComplete(latch::countDown)
                .subscribe();

        latch.await();
        Assertions.assertTrue(maxMillis.get() > 1000);
        Assertions.assertTrue(maxMillis.get() < 2000); // Big enough for any latency, small enough to make sure no get token is called twice
    }

    void testLongRunningWontOverflow(TokenCache cache, AtomicLong refreshes) throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        Flux.interval(Duration.ofMillis(100))
                .take(100)
                .flatMap(i -> Mono.just(OffsetDateTime.now())
                        // Runs cache.getToken() on 10 different threads
                        .subscribeOn(Schedulers.newParallel("pool", 100))
                        .flatMap(start -> cache.getToken()
                                .map(t -> Duration.between(start, OffsetDateTime.now()).toMillis())
                                .doOnNext(millis -> {
//                        System.out.format("Thread: %s\tDuration: %smillis%n",
//                            Thread.currentThread().getName(), Duration.between(start, OffsetDateTime.now()).toMillis());
                                })))
                .doOnComplete(latch::countDown)
                .subscribe();

        latch.await();
        // At most 10 requests should do actual token acquisition, use 11 for safe
        Assertions.assertTrue(refreshes.get() <= 11);
    }
}
package com.unisoft.core.security.testable;

import com.unisoft.core.security.credential.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author omar.H.Ajmi
 * @since 20/01/2021
 */
public final class TokenCacheTestUtil {
    private static final Logger log = LoggerFactory.getLogger(TokenCacheTestUtil.class);
    private static final Random RANDOM = new Random();

    // First token takes latency seconds, and adds 1 sec every subsequent call
    public static Mono<? extends AccessToken> incrementalRemoteGetTokenAsync(AtomicInteger latency, Class<? extends AccessToken> tokenType, int constructorArgsCount) {
        Constructor<? extends AccessToken> typeConstructor = determineTargetConstructorOrFail(tokenType, constructorArgsCount);

        return Mono.delay(Duration.ofSeconds(latency.getAndIncrement()))
                .flatMap(l -> constructType(typeConstructor))
                .doOnError(throwable -> log.error("could not instantiate target token type {}", tokenType, throwable));
    }

    public static Mono<? extends AccessToken> remoteGetTokenAsync(long delayInMillis, Class<? extends AccessToken> tokenType, int constructorArgsCount) {
        Constructor<? extends AccessToken> typeConstructor = determineTargetConstructorOrFail(tokenType, constructorArgsCount);
        return Mono.delay(Duration.ofMillis(delayInMillis))
                .flatMap(l -> constructType(typeConstructor))
                .doOnError(throwable -> log.error("could not instantiate target token type {}", tokenType, throwable));
    }

    public static Mono<? extends AccessToken> remoteGetTokenThatExpiresSoonAsync(long delayInMillis, long validityInMillis, Class<? extends AccessToken> tokenType, int constructorArgsCount) {
        Constructor<? extends AccessToken> typeConstructor = determineTargetConstructorOrFail(tokenType, constructorArgsCount);
        return Mono.delay(Duration.ofMillis(delayInMillis))
                .flatMap(l -> constructType(typeConstructor))
                .doOnError(throwable -> log.error("could not instantiate target token type {}", tokenType, throwable));
    }

    /**
     * creates a new instance of an elected {@link AccessToken} constructor
     *
     * @param constructor constructor
     * @return instance of {@link AccessToken}
     */
    private static Mono<? extends AccessToken> constructType(Constructor<? extends AccessToken> constructor) {
        try {
            return Mono.just(constructor.newInstance(Integer.toString(RANDOM.nextInt(100))));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            return Mono.just(new SimpleTestToken("dummy-access-token"));
        }
    }

    /**
     * determines a constructor for an {@link AccessToken} implementation by number of args and args type
     *
     * @param tokenType            {@link AccessToken} implementation type
     * @param constructorArgsCount constructor args count
     * @return an elected constructor for {@link AccessToken} implementation
     */
    @SuppressWarnings("unchecked")
    private static Constructor<? extends AccessToken> determineTargetConstructorOrFail(Class<? extends AccessToken> tokenType, int constructorArgsCount) {
        return Arrays.stream(tokenType.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == constructorArgsCount)
                .findFirst()
                .map(constructor -> (Constructor<? extends AccessToken>) constructor)
                .orElseThrow(() -> new IllegalArgumentException("could not instantiate target token type: " + tokenType));
    }
}

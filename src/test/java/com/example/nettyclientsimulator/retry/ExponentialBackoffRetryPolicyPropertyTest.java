package com.example.nettyclientsimulator.retry;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 指数退避重试策略的属性测试
 *
 * @author sunxu
 */
class ExponentialBackoffRetryPolicyPropertyTest {

    /**
     * **Feature: netty-client-optimization, Property 1: 指数退避重试延迟计算**
     * 
     * 对于任意重试次数 n（n >= 0），指数退避算法计算的延迟时间应满足公式：
     * delay = min(initialDelay * multiplier^n, maxDelay)
     */
    @Property(tries = 100)
    void exponentialBackoffDelayCalculation(
            @ForAll @IntRange(min = 0, max = 10) int attemptNumber,
            @ForAll @LongRange(min = 100, max = 5000) long initialDelay,
            @ForAll @DoubleRange(min = 1.5, max = 3.0) double multiplier,
            @ForAll @LongRange(min = 10000, max = 60000) long maxDelay
    ) {
        // 确保 maxDelay >= initialDelay
        long actualMaxDelay = Math.max(maxDelay, initialDelay);
        int maxRetries = 15; // 确保 attemptNumber < maxRetries

        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                maxRetries, initialDelay, actualMaxDelay, multiplier
        );

        long actualDelay = policy.getNextRetryDelay(attemptNumber);

        // 计算期望的延迟
        long expectedDelay = Math.min(
                (long) (initialDelay * Math.pow(multiplier, attemptNumber)),
                actualMaxDelay
        );

        assertThat(actualDelay)
                .as("Delay for attempt %d should match formula", attemptNumber)
                .isEqualTo(expectedDelay);
    }

    /**
     * **Feature: netty-client-optimization, Property 1: 指数退避重试延迟计算**
     * 
     * 延迟时间应该随着重试次数增加而增加（直到达到最大值）
     */
    @Property(tries = 100)
    void delayIncreasesWithAttemptNumber(
            @ForAll @LongRange(min = 100, max = 1000) long initialDelay,
            @ForAll @DoubleRange(min = 1.5, max = 3.0) double multiplier
    ) {
        long maxDelay = 100000L;
        int maxRetries = 10;

        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                maxRetries, initialDelay, maxDelay, multiplier
        );

        long previousDelay = 0;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            long currentDelay = policy.getNextRetryDelay(attempt);
            assertThat(currentDelay)
                    .as("Delay should be non-decreasing for attempt %d", attempt)
                    .isGreaterThanOrEqualTo(previousDelay);
            previousDelay = currentDelay;
        }
    }

    /**
     * **Feature: netty-client-optimization, Property 1: 指数退避重试延迟计算**
     * 
     * 延迟时间永远不应超过配置的最大延迟
     */
    @Property(tries = 100)
    void delayNeverExceedsMaxDelay(
            @ForAll @IntRange(min = 0, max = 20) int attemptNumber,
            @ForAll @LongRange(min = 100, max = 1000) long initialDelay,
            @ForAll @DoubleRange(min = 1.5, max = 5.0) double multiplier,
            @ForAll @LongRange(min = 1000, max = 10000) long maxDelay
    ) {
        long actualMaxDelay = Math.max(maxDelay, initialDelay);
        int maxRetries = 25;

        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                maxRetries, initialDelay, actualMaxDelay, multiplier
        );

        long delay = policy.getNextRetryDelay(attemptNumber);

        if (delay != -1) {
            assertThat(delay)
                    .as("Delay should never exceed maxDelay")
                    .isLessThanOrEqualTo(actualMaxDelay);
        }
    }

    /**
     * **Feature: netty-client-optimization, Property 1: 指数退避重试延迟计算**
     * 
     * 当重试次数超过最大重试次数时，应返回 -1
     */
    @Property(tries = 100)
    void returnsNegativeOneWhenExceedingMaxRetries(
            @ForAll @IntRange(min = 1, max = 10) int maxRetries,
            @ForAll @LongRange(min = 100, max = 1000) long initialDelay
    ) {
        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                maxRetries, initialDelay, 10000L, 2.0
        );

        // 当 attemptNumber >= maxRetries 时应返回 -1
        assertThat(policy.getNextRetryDelay(maxRetries))
                .as("Should return -1 when attempt equals maxRetries")
                .isEqualTo(-1);

        assertThat(policy.getNextRetryDelay(maxRetries + 1))
                .as("Should return -1 when attempt exceeds maxRetries")
                .isEqualTo(-1);
    }

    /**
     * **Feature: netty-client-optimization, Property 1: 指数退避重试延迟计算**
     * 
     * shouldRetry 应该在未超过最大重试次数且异常可重试时返回 true
     */
    @Property(tries = 100)
    void shouldRetryForRetryableExceptions(
            @ForAll @IntRange(min = 0, max = 5) int attemptNumber
    ) {
        int maxRetries = 10;
        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                maxRetries, 1000L, 30000L, 2.0
        );

        // IOException 应该是可重试的
        assertThat(policy.shouldRetry(attemptNumber, new IOException("test")))
                .as("IOException should be retryable")
                .isTrue();
    }

    /**
     * **Feature: netty-client-optimization, Property 1: 指数退避重试延迟计算**
     * 
     * shouldRetry 应该在超过最大重试次数时返回 false
     */
    @Property(tries = 100)
    void shouldNotRetryWhenExceedingMaxRetries(
            @ForAll @IntRange(min = 1, max = 10) int maxRetries
    ) {
        ExponentialBackoffRetryPolicy policy = new ExponentialBackoffRetryPolicy(
                maxRetries, 1000L, 30000L, 2.0
        );

        assertThat(policy.shouldRetry(maxRetries, new IOException("test")))
                .as("Should not retry when exceeding maxRetries")
                .isFalse();
    }
}

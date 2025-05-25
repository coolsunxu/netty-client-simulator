package com.example.nettyclientsimulator.metric;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 连接指标属性测试
 *
 * @author sunxu
 */
class ConnectionMetricsPropertyTest {

    /**
     * **Feature: netty-client-optimization, Property 8: 连接指标准确性**
     * 
     * 对于任意次数的 incrementTotal 操作，getTotalConnections 应返回正确的计数
     */
    @Property(tries = 100)
    void incrementTotal_accuratelyTracksCount(
            @ForAll @IntRange(min = 1, max = 100) int incrementCount
    ) {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ConnectionMetrics metrics = new ConnectionMetrics(registry);

        for (int i = 0; i < incrementCount; i++) {
            metrics.incrementTotal();
        }

        assertThat(metrics.getTotalConnections())
                .as("Total connections should equal increment count")
                .isEqualTo(incrementCount);
    }

    /**
     * **Feature: netty-client-optimization, Property 8: 连接指标准确性**
     * 
     * 对于任意次数的 increment 和 decrement 操作，最终计数应正确
     */
    @Property(tries = 100)
    void incrementAndDecrement_accuratelyTracksCount(
            @ForAll @IntRange(min = 1, max = 50) int incrementCount,
            @ForAll @IntRange(min = 0, max = 50) int decrementCount
    ) {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ConnectionMetrics metrics = new ConnectionMetrics(registry);

        for (int i = 0; i < incrementCount; i++) {
            metrics.incrementTotal();
        }

        int actualDecrement = Math.min(decrementCount, incrementCount);
        for (int i = 0; i < actualDecrement; i++) {
            metrics.decrementTotal();
        }

        int expectedCount = incrementCount - actualDecrement;
        assertThat(metrics.getTotalConnections())
                .as("Total connections should equal increment - decrement")
                .isEqualTo(expectedCount);
    }

    /**
     * **Feature: netty-client-optimization, Property 8: 连接指标准确性**
     * 
     * 活跃连接数应独立于总连接数进行跟踪
     */
    @Property(tries = 100)
    void activeConnections_trackedIndependently(
            @ForAll @IntRange(min = 1, max = 50) int totalIncrements,
            @ForAll @IntRange(min = 1, max = 50) int activeIncrements
    ) {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ConnectionMetrics metrics = new ConnectionMetrics(registry);

        for (int i = 0; i < totalIncrements; i++) {
            metrics.incrementTotal();
        }

        for (int i = 0; i < activeIncrements; i++) {
            metrics.incrementActive();
        }

        assertThat(metrics.getTotalConnections())
                .as("Total connections should be tracked independently")
                .isEqualTo(totalIncrements);

        assertThat(metrics.getActiveConnections())
                .as("Active connections should be tracked independently")
                .isEqualTo(activeIncrements);
    }

    /**
     * **Feature: netty-client-optimization, Property 8: 连接指标准确性**
     * 
     * 初始状态下所有计数应为 0
     */
    @Example
    void initialState_allCountsAreZero() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        ConnectionMetrics metrics = new ConnectionMetrics(registry);

        assertThat(metrics.getTotalConnections())
                .as("Initial total connections should be 0")
                .isEqualTo(0);

        assertThat(metrics.getActiveConnections())
                .as("Initial active connections should be 0")
                .isEqualTo(0);
    }
}

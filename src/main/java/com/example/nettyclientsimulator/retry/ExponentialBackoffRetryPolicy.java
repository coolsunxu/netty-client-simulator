package com.example.nettyclientsimulator.retry;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * 指数退避重试策略
 * 延迟计算公式: delay = min(initialDelayMs * multiplier^attemptNumber, maxDelayMs)
 *
 * @author sunxu
 */
public class ExponentialBackoffRetryPolicy implements RetryPolicy {

    private final int maxRetries;
    private final long initialDelayMs;
    private final long maxDelayMs;
    private final double multiplier;
    private final Set<Class<? extends Throwable>> retryableExceptions;

    /**
     * 创建指数退避重试策略
     *
     * @param maxRetries     最大重试次数
     * @param initialDelayMs 初始延迟时间（毫秒）
     * @param maxDelayMs     最大延迟时间（毫秒）
     * @param multiplier     延迟乘数
     */
    public ExponentialBackoffRetryPolicy(int maxRetries, long initialDelayMs, long maxDelayMs, double multiplier) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries must be non-negative");
        }
        if (initialDelayMs <= 0) {
            throw new IllegalArgumentException("initialDelayMs must be positive");
        }
        if (maxDelayMs < initialDelayMs) {
            throw new IllegalArgumentException("maxDelayMs must be >= initialDelayMs");
        }
        if (multiplier < 1.0) {
            throw new IllegalArgumentException("multiplier must be >= 1.0");
        }

        this.maxRetries = maxRetries;
        this.initialDelayMs = initialDelayMs;
        this.maxDelayMs = maxDelayMs;
        this.multiplier = multiplier;

        // 默认可重试的异常类型
        this.retryableExceptions = new HashSet<>(Arrays.asList(
                IOException.class,
                ConnectException.class,
                TimeoutException.class
        ));
    }

    @Override
    public long getNextRetryDelay(int attemptNumber) {
        if (attemptNumber < 0) {
            throw new IllegalArgumentException("attemptNumber must be non-negative");
        }
        if (attemptNumber >= maxRetries) {
            return -1;
        }

        // 计算指数退避延迟: initialDelay * multiplier^attempt
        double delay = initialDelayMs * Math.pow(multiplier, attemptNumber);

        // 确保不超过最大延迟
        return Math.min((long) delay, maxDelayMs);
    }

    @Override
    public boolean shouldRetry(int attemptNumber, Throwable exception) {
        if (attemptNumber >= maxRetries) {
            return false;
        }

        if (exception == null) {
            return true;
        }

        // 检查异常是否可重试
        for (Class<? extends Throwable> retryableException : retryableExceptions) {
            if (retryableException.isInstance(exception)) {
                return true;
            }
            // 检查 cause
            Throwable cause = exception.getCause();
            while (cause != null) {
                if (retryableException.isInstance(cause)) {
                    return true;
                }
                cause = cause.getCause();
            }
        }

        return false;
    }

    @Override
    public int getMaxRetries() {
        return maxRetries;
    }

    public long getInitialDelayMs() {
        return initialDelayMs;
    }

    public long getMaxDelayMs() {
        return maxDelayMs;
    }

    public double getMultiplier() {
        return multiplier;
    }
}

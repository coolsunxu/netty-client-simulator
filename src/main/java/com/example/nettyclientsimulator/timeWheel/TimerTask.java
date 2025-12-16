package com.example.nettyclientsimulator.timeWheel;

import lombok.extern.slf4j.Slf4j;

/**
 * 时间轮任务基类，支持重试机制
 *
 * @author sunxu
 */
@Slf4j
public abstract class TimerTask implements Runnable {

    /**
     * 延迟时间（毫秒）
     */
    protected Long delayMs = 30000L;

    protected TimerTaskEntry timerTaskEntry;

    /**
     * 最大重试次数
     */
    protected int maxRetries = 0;

    /**
     * 当前重试次数
     */
    protected int currentRetry = 0;

    /**
     * 重试延迟（毫秒）
     */
    protected long retryDelayMs = 1000L;

    public void cancel() {
        synchronized (this) {
            if (timerTaskEntry != null) {
                timerTaskEntry.remove();
            }
            timerTaskEntry = null;
        }
    }

    public void setTimerTaskEntry(TimerTaskEntry entry) {
        synchronized (this) {
            if (timerTaskEntry != null && timerTaskEntry != entry) {
                timerTaskEntry.remove();
            }
            timerTaskEntry = entry;
        }
    }

    public TimerTaskEntry getTimerTaskEntry() {
        return timerTaskEntry;
    }

    public Long getDelayMs() {
        return delayMs;
    }

    /**
     * 设置重试配置
     *
     * @param maxRetries   最大重试次数
     * @param retryDelayMs 重试延迟（毫秒）
     */
    public void setRetryConfig(int maxRetries, long retryDelayMs) {
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return currentRetry < maxRetries;
    }

    /**
     * 增加重试计数
     */
    public void incrementRetry() {
        currentRetry++;
    }

    public int getCurrentRetry() {
        return currentRetry;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getRetryDelayMs() {
        return retryDelayMs;
    }
}

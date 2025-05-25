package com.example.nettyclientsimulator.retry;

/**
 * 重试策略接口
 * 定义重试行为的抽象，支持不同的重试算法实现
 *
 * @author sunxu
 */
public interface RetryPolicy {

    /**
     * 计算下次重试的延迟时间
     *
     * @param attemptNumber 当前重试次数（从0开始）
     * @return 延迟毫秒数，-1 表示不再重试
     */
    long getNextRetryDelay(int attemptNumber);

    /**
     * 判断是否应该继续重试
     *
     * @param attemptNumber 当前重试次数（从0开始）
     * @param exception     发生的异常
     * @return true 表示应该重试，false 表示不再重试
     */
    boolean shouldRetry(int attemptNumber, Throwable exception);

    /**
     * 获取最大重试次数
     *
     * @return 最大重试次数
     */
    int getMaxRetries();
}

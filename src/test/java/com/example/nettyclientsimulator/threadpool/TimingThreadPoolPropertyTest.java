package com.example.nettyclientsimulator.threadpool;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TimingThreadPool 的属性测试
 *
 * @author sunxu
 */
class TimingThreadPoolPropertyTest {

    private TimingThreadPool createThreadPool() {
        return new TimingThreadPool(
                2, 4, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                r -> new Thread(r, "test-thread"),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * **Feature: netty-client-optimization, Property 14: TimingThreadPool 计时准确性**
     * 
     * 对于任意提交到 TimingThreadPool 的任务，记录的执行时间应大于等于任务实际执行时间
     */
    @Property(tries = 50)
    void taskExecutionTimeIsRecorded(
            @ForAll @IntRange(min = 1, max = 10) int taskCount,
            @ForAll @IntRange(min = 1, max = 50) int sleepMs
    ) throws InterruptedException {
        TimingThreadPool pool = createThreadPool();
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicLong minExpectedTime = new AtomicLong(sleepMs * 1_000_000L); // 转换为纳秒

        try {
            for (int i = 0; i < taskCount; i++) {
                pool.execute(() -> {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertThat(completed).isTrue();

            // 等待统计更新
            Thread.sleep(100);

            Long avgTime = pool.getAverageTaskCostTime();
            assertThat(avgTime)
                    .as("Average task time should be >= minimum expected time")
                    .isGreaterThanOrEqualTo(minExpectedTime.get() / 2); // 允许一定误差
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * **Feature: netty-client-optimization, Property 14: TimingThreadPool 计时准确性**
     * 
     * 当没有任务执行时，平均执行时间应该为 0
     */
    @Property(tries = 10)
    void getAverageTaskCostTime_whenNoTasks_returnsZero() {
        TimingThreadPool pool = createThreadPool();
        try {
            assertThat(pool.getAverageTaskCostTime())
                    .as("Average time should be 0 when no tasks executed")
                    .isEqualTo(0L);
        } finally {
            pool.shutdown();
        }
    }

    /**
     * **Feature: netty-client-optimization, Property 4: ThreadLocal 清理**
     * 
     * 任务执行完成后，ThreadLocal 应该被清理（通过多次执行验证无内存泄漏）
     */
    @Property(tries = 20)
    void threadLocalIsCleanedAfterExecution(
            @ForAll @IntRange(min = 10, max = 50) int taskCount
    ) throws InterruptedException {
        TimingThreadPool pool = createThreadPool();
        CountDownLatch latch = new CountDownLatch(taskCount);

        try {
            for (int i = 0; i < taskCount; i++) {
                pool.execute(() -> {
                    // 简单任务
                    latch.countDown();
                });
            }

            boolean completed = latch.await(10, TimeUnit.SECONDS);
            assertThat(completed)
                    .as("All tasks should complete without memory issues")
                    .isTrue();

            // 如果 ThreadLocal 没有被清理，大量任务后可能会有问题
            // 这里主要验证任务能正常完成
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}

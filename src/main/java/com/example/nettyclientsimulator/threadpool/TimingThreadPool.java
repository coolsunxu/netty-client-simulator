package com.example.nettyclientsimulator.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sunxu
 */
@Slf4j
public class TimingThreadPool extends ThreadPoolExecutor {


    private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public TimingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.info("cost time {}", taskTime);
        } finally {
            super.afterExecute(r, t);
        }
    }

    public Long getAverageTaskCostTime() {
        if (numTasks.get() == 0) {
            return 0L;
        }
        return totalTime.get() / numTasks.get();
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return this;
    }
}


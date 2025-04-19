package com.example.nettyclientsimulator.config.beans;

import com.example.nettyclientsimulator.config.props.ThreadPoolConfig;
import com.example.nettyclientsimulator.threadpool.TimingThreadPool;
import com.example.nettyclientsimulator.util.ThreadFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sunxu
 */
@Configuration
@Slf4j
public class ExecutorConfiguration {

    private final ThreadPoolConfig threadPoolConfig;

    public ExecutorConfiguration(ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }

    @Bean(name = "commonExecutor", destroyMethod = "shutdown")
    public TimingThreadPool commonExecutor() {
        return createExecutor(
                threadPoolConfig.getCommon().getCorePoolSize(),
                threadPoolConfig.getCommon().getMaxPoolSize(),
                threadPoolConfig.getCommon().getKeepAlive(),
                threadPoolConfig.getCommon().getQueueCapacity(),
                threadPoolConfig.getCommon().getPrefix(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "connectExecutor", destroyMethod = "shutdown")
    public TimingThreadPool connectExecutor() {
        return createExecutor(
                threadPoolConfig.getConnect().getCorePoolSize(),
                threadPoolConfig.getConnect().getMaxPoolSize(),
                threadPoolConfig.getConnect().getKeepAlive(),
                threadPoolConfig.getConnect().getQueueCapacity(),
                threadPoolConfig.getConnect().getPrefix(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "asyncExecutor", destroyMethod = "shutdown")
    public TimingThreadPool asyncExecutor() {
        return createExecutor(
                threadPoolConfig.getAsync().getCorePoolSize(),
                threadPoolConfig.getAsync().getMaxPoolSize(),
                threadPoolConfig.getAsync().getKeepAlive(),
                threadPoolConfig.getAsync().getQueueCapacity(),
                threadPoolConfig.getAsync().getPrefix(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "delayTaskExecutor", destroyMethod = "shutdown")
    public TimingThreadPool delayTaskExecutor() {
        return createExecutor(
                threadPoolConfig.getDelayTask().getCorePoolSize(),
                threadPoolConfig.getDelayTask().getMaxPoolSize(),
                threadPoolConfig.getDelayTask().getKeepAlive(),
                threadPoolConfig.getDelayTask().getQueueCapacity(),
                threadPoolConfig.getDelayTask().getPrefix(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    private TimingThreadPool createExecutor(
            int defCorePoolSize,
            int defMaxPoolSize,
            int defKeepAlive,
            int defQueueCapacity,
            String threadNamePrefix,
            RejectedExecutionHandler rejectedExecutionHandler
    ) {

        return new TimingThreadPool(
                defCorePoolSize,
                defMaxPoolSize,
                defKeepAlive,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(defQueueCapacity),
                ThreadFactoryUtil.create(threadNamePrefix),
                rejectedExecutionHandler
        );
    }

}


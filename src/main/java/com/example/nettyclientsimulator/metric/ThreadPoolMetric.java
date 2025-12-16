package com.example.nettyclientsimulator.metric;

import com.example.nettyclientsimulator.threadpool.TimingThreadPool;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sunxu
 */
@Configuration
public class ThreadPoolMetric {

    private final PrometheusMeterRegistry registry;

    private final TimingThreadPool commonExecutor;

    private final TimingThreadPool connectExecutor;


    public ThreadPoolMetric(PrometheusMeterRegistry registry, TimingThreadPool connectExecutor, TimingThreadPool commonExecutor) {
        this.registry = registry;
        this.connectExecutor = connectExecutor;
        this.commonExecutor = commonExecutor;
    }

    private static final String SERVICE_EXECUTOR_THREADS_ACTIVE = "service.executor.threads.active";
    private static final String SERVICE_EXECUTOR_THREADS_WAITING = "service.executor.threads.waiting";
    private static final String SERVICE_EXECUTOR_THREADS_COST = "service.executor.threads.cost";
    private static final String SERVICE_EXECUTOR_POOL_SIZE = "service.executor.pool.size";
    private static final String SERVICE_EXECUTOR_QUEUE_CAPACITY = "service.executor.queue.capacity";
    private static final String SERVICE_EXECUTOR_COMPLETED_TASKS = "service.executor.completed.tasks";
    private static final String EXECUTOR_NAME = "executor.name";

    @PostConstruct
    public void init() {
        Map<String, TimingThreadPool> executorMap = new HashMap<>(2);
        executorMap.put("connect-executor", connectExecutor);
        executorMap.put("common-executor", commonExecutor);

        for (Map.Entry<String, TimingThreadPool> entry : executorMap.entrySet()) {
            // 活跃线程数指标
            Gauge.builder(SERVICE_EXECUTOR_THREADS_ACTIVE, entry.getValue(), TimingThreadPool::getActiveCount)
                    .description("当前活跃线程数")
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);

            // 队列中等待任务数指标
            Gauge.builder(SERVICE_EXECUTOR_THREADS_WAITING, entry.getValue(), e -> e.getThreadPoolExecutor().getQueue().size())
                    .description("队列中等待执行的任务数")
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);

            // 任务平均耗时指标
            Gauge.builder(SERVICE_EXECUTOR_THREADS_COST, entry.getValue(), TimingThreadPool::getAverageTaskCostTime)
                    .description("任务平均执行耗时（毫秒）")
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);

            // 当前线程池大小指标
            Gauge.builder(SERVICE_EXECUTOR_POOL_SIZE, entry.getValue(), e -> e.getThreadPoolExecutor().getPoolSize())
                    .description("当前线程池大小")
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);

            // 队列剩余容量指标
            Gauge.builder(SERVICE_EXECUTOR_QUEUE_CAPACITY, entry.getValue(), e -> e.getThreadPoolExecutor().getQueue().remainingCapacity())
                    .description("队列剩余容量")
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);

            // 已完成任务数指标
            Gauge.builder(SERVICE_EXECUTOR_COMPLETED_TASKS, entry.getValue(), e -> e.getThreadPoolExecutor().getCompletedTaskCount())
                    .description("已完成任务总数")
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);
        }
    }
}

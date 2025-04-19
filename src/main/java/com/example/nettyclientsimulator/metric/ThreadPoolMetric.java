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
    private static final String EXECUTOR_NAME = "executor.name";

    @PostConstruct
    public void init() {
        Map<String, TimingThreadPool> executorMap = new HashMap<>(2);
        executorMap.put("connect-executor", connectExecutor);
        executorMap.put("common-executor", commonExecutor);


        for (Map.Entry<String, TimingThreadPool> entry : executorMap.entrySet()) {
            // active thread metric
            Gauge.builder(SERVICE_EXECUTOR_THREADS_ACTIVE, entry.getValue(), TimingThreadPool::getActiveCount)
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);

            // task in queue metric
            Gauge.builder(SERVICE_EXECUTOR_THREADS_WAITING, entry.getValue(), e -> e.getThreadPoolExecutor().getQueue().size())
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);

            // task cost metric
            Gauge.builder(SERVICE_EXECUTOR_THREADS_COST, entry.getValue(), TimingThreadPool::getAverageTaskCostTime)
                    .tags(EXECUTOR_NAME, entry.getKey())
                    .register(registry);
        }
    }
}

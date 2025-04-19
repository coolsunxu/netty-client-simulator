package com.example.nettyclientsimulator.aop;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author sunxu
 */
@Aspect
@Component
@Slf4j
public class TaskCostTimeAop {

    private final PrometheusMeterRegistry meterRegistry;

    public TaskCostTimeAop(PrometheusMeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    private static final String SERVICE_QUEUE_TASK_COUNT = "service.queue.task.count";
    private static final String SERVICE_QUEUE_COST_TIME = "service.queue.cost.time";

    private static final String WRITE_METHOD = "write";

    private static final String READ_METHOD = "read";

    @Pointcut("execution(* com.example.nettyclientsimulator.service.impl.ClientServiceImpl.*(..))")
    public void readWriteMonitor() {
        // pointcut definition
    }

    @Around("readWriteMonitor()")
    public Object readWriteMonitorAround(ProceedingJoinPoint pjp) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object object = pjp.proceed();

        long endTime = System.currentTimeMillis();

        String methodName = pjp.getSignature().getName();

        log.debug("get method name {}",methodName);
        if (!Objects.equals(methodName, WRITE_METHOD) && !Objects.equals(methodName, READ_METHOD)) {
            return object;
        }

        meterRegistry.counter(SERVICE_QUEUE_TASK_COUNT, "methodName",pjp.getSignature().getName()).increment();
        meterRegistry.timer(SERVICE_QUEUE_COST_TIME, "methodName",pjp.getSignature().getName())
                .record((endTime - startTime), TimeUnit.MILLISECONDS);

        return object;
    }
}

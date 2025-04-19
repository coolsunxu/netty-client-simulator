package com.example.nettyclientsimulator.aop;

import com.example.nettyclientsimulator.annotation.MethodMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author sunxu
 */

@Aspect
@Component
public class HttpMethodMetricsAspect {

    private final MeterRegistry meterRegistry;


    public HttpMethodMetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Pointcut("@annotation(com.example.nettyclientsimulator.annotation.MethodMetrics)")
    public void pointcut() {

    }

    @Around(value = "pointcut()")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method currentMethod = ClassUtils.getUserClass(joinPoint.getTarget().getClass()).getDeclaredMethod(targetMethod.getName(),
                targetMethod.getParameterTypes());

        if (currentMethod.isAnnotationPresent(MethodMetrics.class)) {
            MethodMetrics methodMetrics = currentMethod.getAnnotation(MethodMetrics.class);
            return processMetric(joinPoint, currentMethod, methodMetrics);
        } else {
            return joinPoint.proceed();
        }
    }

    private Object processMetric(ProceedingJoinPoint joinPoint, Method currentmethod, MethodMetrics methodMetrics) {
        String name = methodMetrics.name();
        if (!StringUtils.hasText(name)) {
            name = currentmethod.getName();
        }

        String desc = methodMetrics.des();
        if (!StringUtils.hasText(desc)) {
            desc = name;
        }

        String[] tags = methodMetrics.tags();
        // 需要对tags判空
        if (tags.length == 0) {
            tags = new String[2];
            tags[0] = name;
            tags[1] = name;
        }

        Timer timer = Timer.builder(name).tags(tags)
                .description(desc)
                .register(meterRegistry);

        return timer.record(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new IllegalStateException(throwable);
            }
        });

    }
}

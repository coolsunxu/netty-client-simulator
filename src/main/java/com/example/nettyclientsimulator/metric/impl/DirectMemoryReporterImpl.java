package com.example.nettyclientsimulator.metric.impl;

import com.example.nettyclientsimulator.metric.DirectMemoryReporter;
import com.example.nettyclientsimulator.util.ThreadFactoryUtil;
import io.netty.util.internal.PlatformDependent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author sunxu
 */

@Slf4j
@Component
public class DirectMemoryReporterImpl implements DirectMemoryReporter {

    private static final int _1K = 1024;
    private static final String BUSINESS_KEY = "netty_direct_memory";

    private static final ScheduledExecutorService LOG_DIRECT_MEMORY_COUNTER = new ScheduledThreadPoolExecutor(1, ThreadFactoryUtil.create("log-mem"),
            new ThreadPoolExecutor.AbortPolicy());

    @PostConstruct
    public void init() {
        startReport();
    }

    @Override
    public void startReport() {
        LOG_DIRECT_MEMORY_COUNTER.scheduleWithFixedDelay(this::doReport, 0, 1, TimeUnit.SECONDS);
    }

    private void doReport() {
        try {
            int memoryInKb = (int) (PlatformDependent.usedDirectMemory() / _1K);
            log.info("{}: {} k", BUSINESS_KEY, memoryInKb);
        } catch (Exception e) {
            log.warn("get memoryInKb error", e);
        }
    }
}

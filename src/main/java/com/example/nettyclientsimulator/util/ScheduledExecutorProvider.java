package com.example.nettyclientsimulator.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sunxu
 */
@Slf4j
public class ScheduledExecutorProvider extends ExecutorProvider {

    public ScheduledExecutorProvider(int numThreads, String poolName) {
        super(numThreads, poolName);
    }

    @Override
    protected ExecutorService createExecutor(ExtendedThreadFactory threadFactory) {
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }
}

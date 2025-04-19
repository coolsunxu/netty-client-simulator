package com.example.nettyclientsimulator.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@Slf4j
public class ThreadFactoryUtil {

    /**
     * Creates a {@link ThreadFactory} with given nameFormat and an {@link UncaughtExceptionHandler} to log every
     * uncaught exception.
     *
     * @param nameFormat the format of the name
     */
    public static ThreadFactory create(final String nameFormat) {
        return new ThreadFactoryBuilder().setNameFormat(nameFormat)
                .setUncaughtExceptionHandler(new UncaughtExceptionHandler())
                .build();
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(final Thread thread, final Throwable throwable) {
            log.error("Uncaught exception in thread '{}'.", thread.getName(), throwable);
        }
    }
}

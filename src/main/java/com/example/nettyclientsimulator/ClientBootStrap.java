package com.example.nettyclientsimulator;

import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author sunxu
 */

@Service
@Slf4j
public class ClientBootStrap {
    private final ShutdownHooks shutdownHooks;

    @Autowired
    public ClientBootStrap(ShutdownHooks shutdownHooks) {
        this.shutdownHooks = shutdownHooks;
    }


    @PostConstruct
    public void run() {
        try {
            start();
        } catch (final Exception e) {
            log.warn("client simulator start was cancelled. {}", e.getMessage());
        }
    }

    public void start() {

        final long startTime = System.nanoTime();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "shutdown-thread"));

        log.info("todo what you need to do to start the service");

        log.info("client simulator started in {}ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));

        afterStart();
    }

    public void stop() {
        // Already shutdown.
        if (shutdownHooks.isShuttingDown()) {
            return;
        }

        shutdownHooks.runShutdownHooks();
    }

    public void afterStart() {
        if (shutdownHooks.isShuttingDown()) {
            log.warn("client simulator is shutting down");
        }

        // todo what to do after the service is started
        log.info("todo what to do after the service is started");
    }
}

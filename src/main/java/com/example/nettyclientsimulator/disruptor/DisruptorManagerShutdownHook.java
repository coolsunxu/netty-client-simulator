package com.example.nettyclientsimulator.disruptor;

import com.example.nettyclientsimulator.shutdown.ClientShutdownHook;
import lombok.extern.slf4j.Slf4j;


/**
 * @author sunxu
 */
@Slf4j
public class DisruptorManagerShutdownHook implements ClientShutdownHook {

    private final DisruptorManager disruptorManager;

    public DisruptorManagerShutdownHook(DisruptorManager disruptorManager) {
        this.disruptorManager = disruptorManager;
    }

    @Override
    public String name() {
        return "DisruptorManagerShutdownHook";
    }

    @Override
    public ClientShutdownHook.Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public void run() {
        log.info("Shutting down DisruptorManager");
        disruptorManager.shutdown();
    }
}

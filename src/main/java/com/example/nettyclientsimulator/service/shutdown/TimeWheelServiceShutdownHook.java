package com.example.nettyclientsimulator.service.shutdown;

import com.example.nettyclientsimulator.service.impl.TimeWheelServiceImpl;
import com.example.nettyclientsimulator.shutdown.ClientShutdownHook;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sunxu
 */

@Slf4j
public class TimeWheelServiceShutdownHook implements ClientShutdownHook {

    private final TimeWheelServiceImpl timeWheelService;

    public TimeWheelServiceShutdownHook(TimeWheelServiceImpl timeWheelService) {
        this.timeWheelService = timeWheelService;
    }

    @Override
    public String name() {
        return "TimeWheelServiceShutdownHook";
    }

    @Override
    public ClientShutdownHook.Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public void run() {
        log.info("Shutting down TimeWheelService");
        timeWheelService.shutdown();
    }
}

package com.example.nettyclientsimulator.service.shutdown;

import com.example.nettyclientsimulator.service.impl.ConnectionServiceImpl;
import com.example.nettyclientsimulator.shutdown.ClientShutdownHook;
import lombok.extern.slf4j.Slf4j;


/**
 * @author sunxu
 */

@Slf4j
public class ConnectionServiceShutdownHook implements ClientShutdownHook {

    private final ConnectionServiceImpl connectionService;

    public ConnectionServiceShutdownHook(ConnectionServiceImpl connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public String name() {
        return "ConnectionServiceShutdownHook";
    }

    @Override
    public ClientShutdownHook.Priority priority() {
        // ConnectionService 应该最先关闭，停止新的连接尝试
        return Priority.VERY_HIGH;
    }

    @Override
    public void run() {
        log.info("Shutting down ConnectionService");
        connectionService.shutdown();
    }
}

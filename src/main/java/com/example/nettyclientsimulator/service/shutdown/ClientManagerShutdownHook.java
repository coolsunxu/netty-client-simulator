package com.example.nettyclientsimulator.service.shutdown;

import com.example.nettyclientsimulator.service.impl.ClientManagerImpl;
import com.example.nettyclientsimulator.shutdown.ClientShutdownHook;
import lombok.extern.slf4j.Slf4j;


/**
 * @author sunxu
 */
@Slf4j
public class ClientManagerShutdownHook implements ClientShutdownHook {

    private final ClientManagerImpl clientManager;

    public ClientManagerShutdownHook(ClientManagerImpl clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public String name() {
        return "ClientManagerShutdownHook";
    }

    @Override
    public ClientShutdownHook.Priority priority() {
        // ClientManager 包含 EventLoopGroup，应该在业务线程池之后关闭
        return Priority.MEDIUM;
    }

    @Override
    public void run() {
        log.info("Shutting down ClientManager");
        clientManager.shutdown();
    }
}

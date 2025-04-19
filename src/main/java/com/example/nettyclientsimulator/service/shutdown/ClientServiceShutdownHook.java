package com.example.nettyclientsimulator.service.shutdown;

import com.example.nettyclientsimulator.service.impl.ClientServiceImpl;
import com.example.nettyclientsimulator.shutdown.ClientShutdownHook;
import lombok.extern.slf4j.Slf4j;


/**
 * @author sunxu
 */
@Slf4j
public class ClientServiceShutdownHook implements ClientShutdownHook {

    private final ClientServiceImpl clientService;

    public ClientServiceShutdownHook(ClientServiceImpl clientService) {
        this.clientService = clientService;
    }


    @Override
    public String name() {
        return "ClientServiceShutdownHook";
    }

    @Override
    public ClientShutdownHook.Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public void run() {
        log.info("Shutting down ClientService");
        clientService.shutdown();
    }
}

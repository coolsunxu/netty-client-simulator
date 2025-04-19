package com.example.nettyclientsimulator.disruptor.event;

import com.example.nettyclientsimulator.service.impl.ClientServiceImpl;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author sunxu
 */
@Component
public class WriteEventListener implements ApplicationListener<WriteEvent> {

    private final ClientServiceImpl clientService;

    public WriteEventListener(ClientServiceImpl clientService) {
        this.clientService = clientService;
    }

    @Override
    public void onApplicationEvent(WriteEvent writeEvent) {
        WriteTask task = writeEvent.getTask();
        clientService.write(task.getStr(), task.getClientId());
    }
}

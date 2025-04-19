package com.example.nettyclientsimulator.disruptor.handler;

import com.example.nettyclientsimulator.disruptor.element.WriteElement;
import com.example.nettyclientsimulator.disruptor.event.WriteEvent;
import com.example.nettyclientsimulator.disruptor.event.WriteTask;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author sunxu
 */
@Slf4j
public class WriteEventHandler implements EventHandler<WriteElement> {

    private final ApplicationEventPublisher eventPublisher;

    public WriteEventHandler(ApplicationEventPublisher eventPublisher) {

        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onEvent(WriteElement element, long l, boolean b) {
        String clientId = element.getClientId();
        try {
            eventPublisher.publishEvent(new WriteEvent(WriteTask.builder()
                    .str(element.getStr())
                    .clientId(clientId)
                    .build()));
        } catch (Exception e) {
            log.warn("write message to client {} error {}", clientId, e);
        }
    }
}

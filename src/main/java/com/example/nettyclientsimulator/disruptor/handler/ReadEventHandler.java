package com.example.nettyclientsimulator.disruptor.handler;

import com.example.nettyclientsimulator.disruptor.element.ReadElement;
import com.example.nettyclientsimulator.disruptor.event.ReadEvent;
import com.example.nettyclientsimulator.disruptor.event.ReadTask;
import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @author sunxu
 */
@Slf4j
public class ReadEventHandler implements EventHandler<ReadElement> {
    private final ApplicationEventPublisher eventPublisher;

    public ReadEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void onEvent(ReadElement element, long l, boolean b) {
        String clientId = element.getClientId();
        try {
            eventPublisher.publishEvent(new ReadEvent(ReadTask.builder()
                    .taskContext(element.getMsg())
                    .clientId(clientId)
                    // 设置任务开始时间
                    .timeStamp(System.currentTimeMillis())
                    .build()));
        } catch (Exception e) {
            log.warn("write message to client {} error {}", clientId, e);
        }
    }
}


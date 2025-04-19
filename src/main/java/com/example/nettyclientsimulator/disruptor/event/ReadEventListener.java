package com.example.nettyclientsimulator.disruptor.event;

import com.example.nettyclientsimulator.config.props.DisruptorConfig;
import com.example.nettyclientsimulator.service.impl.ClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author sunxu
 */
@Component
@Slf4j
public class ReadEventListener implements ApplicationListener<ReadEvent> {

    private final ClientServiceImpl clientService;
    private final DisruptorConfig disruptorConfig;

    public ReadEventListener(ClientServiceImpl clientService, DisruptorConfig disruptorConfig) {
        this.clientService = clientService;
        this.disruptorConfig = disruptorConfig;
    }

    @Override
    public void onApplicationEvent(ReadEvent readEvent) {
        ReadTask task = readEvent.getTask();
        // 判断任务是否已过期，当出现任务堆积的情况，可以快速消费
        if (System.currentTimeMillis() - task.getTimeStamp() > disruptorConfig.getReadQueue().getExpirationTime()) {
            log.info("client {} message with timeStamp {} has expired, do not consume it", task.getClientId(), task.getTimeStamp());
            return;
        }

        clientService.read(task);
    }
}


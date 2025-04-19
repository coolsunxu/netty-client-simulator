package com.example.nettyclientsimulator.disruptor.event;

import lombok.*;

/**
 * @author sunxu
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class ReadTask {
    private Long id;

    private String clientId;

    private Object taskContext;

    private boolean finish;

    /**
     * 记录任务时间，不消费过期的任务，加速队列中消息的消费
     */
    private Long timeStamp;
}

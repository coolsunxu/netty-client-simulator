package com.example.nettyclientsimulator.disruptor.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author sunxu
 */
public class WriteEvent extends ApplicationEvent {

    private final WriteTask task;

    public WriteEvent(WriteTask task) {
        super(task);
        this.task = task;
    }

    public WriteTask getTask() {
        return task;
    }
}

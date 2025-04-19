package com.example.nettyclientsimulator.disruptor.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author sunxu
 */
public class ReadEvent extends ApplicationEvent {

    private final ReadTask task;

    public ReadEvent(ReadTask task) {
        super(task);
        this.task = task;
    }

    public ReadTask getTask() {
        return task;
    }
}

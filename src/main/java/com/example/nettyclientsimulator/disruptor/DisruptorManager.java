package com.example.nettyclientsimulator.disruptor;

import com.example.nettyclientsimulator.disruptor.impl.DisruptorReadQueueImpl;
import com.example.nettyclientsimulator.disruptor.impl.DisruptorWriteQueueImpl;
import com.example.nettyclientsimulator.disruptor.impl.ReadEventPublisherImpl;
import com.example.nettyclientsimulator.disruptor.impl.WriteEventPublisherImpl;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author sunxu
 */
@Component
public class DisruptorManager {
    private final ShutdownHooks shutdownHooks;
    private final DisruptorReadQueueImpl disruptorReadQueue;

    private final DisruptorWriteQueueImpl disruptorWriteQueue;

    private final ReadEventPublisherImpl readEventPublisher;

    private final WriteEventPublisherImpl writeEventPublisher;

    public DisruptorManager(ShutdownHooks shutdownHooks, DisruptorReadQueueImpl disruptorReadQueue, DisruptorWriteQueueImpl disruptorWriteQueue) {
        this.shutdownHooks = shutdownHooks;
        this.disruptorReadQueue = disruptorReadQueue;
        this.disruptorWriteQueue = disruptorWriteQueue;

        this.readEventPublisher = new ReadEventPublisherImpl(disruptorReadQueue.getDisruptor());
        this.writeEventPublisher = new WriteEventPublisherImpl(disruptorWriteQueue.getDisruptor());
    }

    @PostConstruct
    public void init() {
        start();
        shutdownHooks.add(new DisruptorManagerShutdownHook(this));
    }

    public void start() {
        this.disruptorReadQueue.start();
        this.disruptorWriteQueue.start();
    }

    public void readPublish(String clientId, Object msg, int index) {
        readEventPublisher.publish(clientId, msg, index);
    }

    public void writePublish(String clientId, String str, int index) {
        writeEventPublisher.publish(clientId, str, index);
    }

    public int getReadDisruptorQueueSize() {
        return this.disruptorReadQueue.getQueueSize();
    }

    public int getWriteDisruptorQueueSize() {
        return this.disruptorWriteQueue.getQueueSize();
    }

    public void shutdown() {
        this.disruptorReadQueue.shutdown();
        this.disruptorWriteQueue.shutdown();
    }
}


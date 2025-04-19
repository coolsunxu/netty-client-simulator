package com.example.nettyclientsimulator.disruptor.impl;

import com.example.nettyclientsimulator.config.props.DisruptorConfig;
import com.example.nettyclientsimulator.disruptor.DisruptorWriteQueue;
import com.example.nettyclientsimulator.disruptor.element.WriteElement;
import com.example.nettyclientsimulator.disruptor.handler.WriteEventHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author sunxu
 */
@Component
public class DisruptorWriteQueueImpl implements DisruptorWriteQueue {

    private final ArrayList<Disruptor<WriteElement>> writeDisruptor;

    private final DisruptorConfig disruptorConfig;

    private final ApplicationEventPublisher eventPublisher;


    public DisruptorWriteQueueImpl(DisruptorConfig disruptorConfig, ApplicationEventPublisher eventPublisher) {
        this.disruptorConfig = disruptorConfig;
        this.eventPublisher = eventPublisher;

        this.writeDisruptor = new ArrayList<>(disruptorConfig.getWriteQueue().getQueueSize());
    }

    public void start() {
        for (int i = 0; i < this.disruptorConfig.getWriteQueue().getQueueSize(); i++) {

            int finalI = i;
            Disruptor<WriteElement> disruptor = new Disruptor<>(
                    WriteElement::new,
                    this.disruptorConfig.getWriteQueue().getBufferSize(),
                    r -> {
                        return new Thread(r, disruptorConfig.getWriteQueue().getPrefix() + finalI);
                    },
                    ProducerType.SINGLE,
                    new BlockingWaitStrategy());

            // 设置EventHandler
            disruptor.handleEventsWith(new WriteEventHandler(eventPublisher));

            writeDisruptor.add(disruptor);

            writeDisruptor.get(i).start();
        }
    }

    @Override
    public ArrayList<Disruptor<WriteElement>> getDisruptor() {
        return this.writeDisruptor;
    }


    @Override
    public int getQueueSize() {
        return disruptorConfig.getWriteQueue().getQueueSize();
    }

    @Override
    public int getBufferSize() {
        return disruptorConfig.getWriteQueue().getBufferSize();
    }

    @Override
    public void shutdown() {
        for (Disruptor<WriteElement> disruptor : writeDisruptor) {
            disruptor.shutdown();
        }
    }

    @Override
    public Long getRemainingCapacity(int index) {

        return writeDisruptor.get(index).getRingBuffer().remainingCapacity();

    }


}

package com.example.nettyclientsimulator.disruptor.impl;

import com.example.nettyclientsimulator.config.props.DisruptorConfig;
import com.example.nettyclientsimulator.disruptor.DisruptorReadQueue;
import com.example.nettyclientsimulator.disruptor.element.ReadElement;
import com.example.nettyclientsimulator.disruptor.handler.DisruptorExceptionHandler;
import com.example.nettyclientsimulator.disruptor.handler.ReadEventHandler;
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
public class DisruptorReadQueueImpl implements DisruptorReadQueue {

    private final ArrayList<Disruptor<ReadElement>> readDisruptor;

    private final DisruptorConfig disruptorConfig;

    private final ApplicationEventPublisher eventPublisher;

    public DisruptorReadQueueImpl(DisruptorConfig disruptorConfig, ApplicationEventPublisher eventPublisher) {
        this.disruptorConfig = disruptorConfig;
        this.eventPublisher = eventPublisher;
        this.readDisruptor = new ArrayList<>(disruptorConfig.getReadQueue().getQueueSize());
    }

    public void start() {
        for (int i = 0; i < this.disruptorConfig.getReadQueue().getQueueSize(); i++) {

            int finalI = i;
            Disruptor<ReadElement> disruptor = new Disruptor<>(
                    ReadElement::new,
                    this.disruptorConfig.getReadQueue().getBufferSize(),
                    r -> {
                        return new Thread(r, disruptorConfig.getReadQueue().getPrefix() + finalI);
                    },
                    ProducerType.SINGLE,
                    new BlockingWaitStrategy());

            // 设置 ExceptionHandler
            disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler<>("ReadQueue-" + finalI));
            
            // 设置EventHandler
            disruptor.handleEventsWith(new ReadEventHandler(eventPublisher));

            readDisruptor.add(disruptor);

            readDisruptor.get(i).start();
        }
    }


    @Override
    public ArrayList<Disruptor<ReadElement>> getDisruptor() {
        return this.readDisruptor;
    }


    @Override
    public int getQueueSize() {
        return disruptorConfig.getReadQueue().getQueueSize();
    }

    @Override
    public int getBufferSize() {
        return disruptorConfig.getReadQueue().getBufferSize();
    }

    @Override
    public void shutdown() {
        for (Disruptor<ReadElement> disruptor : readDisruptor) {
            disruptor.shutdown();
        }
    }


    @Override
    public Long getRemainingCapacity(int index) {

        return readDisruptor.get(index).getRingBuffer().remainingCapacity();

    }

}

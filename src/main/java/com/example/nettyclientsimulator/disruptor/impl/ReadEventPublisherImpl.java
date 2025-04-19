package com.example.nettyclientsimulator.disruptor.impl;

import com.example.nettyclientsimulator.disruptor.ReadEventPublisher;
import com.example.nettyclientsimulator.disruptor.element.ReadElement;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.ArrayList;

/**
 * @author sunxu
 */
public class ReadEventPublisherImpl implements ReadEventPublisher {

    private final ArrayList<Disruptor<ReadElement>> readDisruptor;


    public ReadEventPublisherImpl(ArrayList<Disruptor<ReadElement>> readDisruptor) {
        this.readDisruptor = readDisruptor;
    }

    @Override
    public void publish(String clientId, Object msg, int index) {
        RingBuffer<ReadElement> ringBuffer = readDisruptor.get(index).getRingBuffer();
        // 获取下一个可用位置的下标
        long sequence = ringBuffer.next();
        try {
            // 返回可用位置的元素
            ReadElement event = ringBuffer.get(sequence);
            // 设置该位置元素的值
            event.setClientId(clientId);
            event.setMsg(msg);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}

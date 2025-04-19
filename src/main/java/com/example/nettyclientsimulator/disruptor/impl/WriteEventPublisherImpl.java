package com.example.nettyclientsimulator.disruptor.impl;

import com.example.nettyclientsimulator.disruptor.WriteEventPublisher;
import com.example.nettyclientsimulator.disruptor.element.WriteElement;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.ArrayList;

/**
 * @author sunxu
 */
public class WriteEventPublisherImpl implements WriteEventPublisher {
    private final ArrayList<Disruptor<WriteElement>> writeDisruptor;


    public WriteEventPublisherImpl(ArrayList<Disruptor<WriteElement>> writeDisruptor) {
        this.writeDisruptor = writeDisruptor;
    }

    @Override
    public void publish(String clientId, String str, int index) {
        RingBuffer<WriteElement> ringBuffer = writeDisruptor.get(index).getRingBuffer();
        // 获取下一个可用位置的下标
        long sequence = ringBuffer.next();
        try {
            // 返回可用位置的元素
            WriteElement event = ringBuffer.get(sequence);
            // 设置该位置元素的值
            event.setClientId(clientId);
            event.setStr(str);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}

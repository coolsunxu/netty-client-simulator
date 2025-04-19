package com.example.nettyclientsimulator.disruptor;

import com.example.nettyclientsimulator.disruptor.element.ReadElement;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.ArrayList;

/**
 * @author sunxu
 */
public interface DisruptorReadQueue {

    /**
     * 获取容量
     *
     * @param index 队列的索引
     * @return {@link Long }
     */
    Long getRemainingCapacity(int index);

    /**
     * 获取缓存任务的存储结构
     *
     * @return 返回存储的队列
     */
    ArrayList<Disruptor<ReadElement>> getDisruptor();

    /**
     * 获取总共有多少队列
     *
     * @return int
     */
    int getQueueSize();

    /**
     * 获取buffer-size大小
     *
     * @return int
     */
    int getBufferSize();

    /**
     * 关机
     */
    void shutdown();
}

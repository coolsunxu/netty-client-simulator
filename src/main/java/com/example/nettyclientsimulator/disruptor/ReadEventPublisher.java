package com.example.nettyclientsimulator.disruptor;

/**
 * @author sunxu
 */
public interface ReadEventPublisher {
    /**
     * 发布消息到disruptor队列中
     *
     * @param clientId 客户端标识符
     * @param msg      数据
     * @param index    需要写入到哪个队列中
     */
    void publish(String clientId, Object msg, int index);
}


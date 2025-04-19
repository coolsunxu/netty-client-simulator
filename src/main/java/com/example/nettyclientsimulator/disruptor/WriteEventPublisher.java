package com.example.nettyclientsimulator.disruptor;

/**
 * @author sunxu
 */
public interface WriteEventPublisher {
    /**
     * 发布消息到disruptor队列中
     *
     * @param clientId 客户端标识符
     * @param str    字符串
     * @param index    需要写入到哪个队列中
     */
    void publish(String clientId, String str, int index);
}

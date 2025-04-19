package com.example.nettyclientsimulator.service;

import com.example.nettyclientsimulator.disruptor.event.ReadTask;

/**
 * @author sunxu
 */
public interface ClientService {

    /**
     * 客户端回复server的信息
     *
     * @param str    字节
     * @param clientId 客户端ID
     */
    void write(String str, String clientId);

    /**
     * 处理来自server的信息
     *
     * @param readTask 读取到的队列中的消息
     */
    void read(ReadTask readTask);

    /**
     * 关机
     */
    void shutdown();

}


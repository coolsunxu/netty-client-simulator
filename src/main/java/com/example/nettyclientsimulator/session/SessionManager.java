package com.example.nettyclientsimulator.session;

import com.example.nettyclientsimulator.client.Client;

import java.util.Map;

/**
 * @author sunxu
 */

public interface SessionManager {

    /**
     * 添加client
     * @param clientId 客户端ID
     * @param client 客户端
     */
    void addClient(String clientId, Client client);

    /**
     * 根据clientId获取client
     * @param clientId 客户端ID
     * @return 客户端实体
     */
    Client getClient(String clientId);


    /**
     * 获取所有客户端集合
     * @return 返回客户端集合
     */
    Map<String, Client> getClientMap();

    /**
     * 获取client的总数
     * @return 返回client的总数
     */
    int getClientMapSize();


    /**
     * 关机
     */
    void shutdown();

}

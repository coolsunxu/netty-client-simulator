package com.example.nettyclientsimulator.session;

import com.example.nettyclientsimulator.client.Client;

import java.util.Map;
import java.util.Optional;

/**
 * 会话管理器接口
 * 管理所有客户端会话的生命周期
 *
 * @author sunxu
 */
public interface SessionManager {

    /**
     * 添加client
     *
     * @param clientId 客户端ID
     * @param client   客户端
     */
    void addClient(String clientId, Client client);

    /**
     * 根据clientId获取client
     *
     * @param clientId 客户端ID
     * @return 客户端实体的 Optional 包装，不存在时返回 Optional.empty()
     */
    Optional<Client> getClient(String clientId);

    /**
     * 移除客户端
     *
     * @param clientId 客户端ID
     * @return 被移除的客户端，如果不存在则返回 Optional.empty()
     */
    Optional<Client> removeClient(String clientId);

    /**
     * 获取所有客户端集合
     *
     * @return 返回客户端集合
     */
    Map<String, Client> getClientMap();

    /**
     * 获取client的总数
     *
     * @return 返回client的总数
     */
    int getClientMapSize();

    /**
     * 关机
     */
    void shutdown();
}

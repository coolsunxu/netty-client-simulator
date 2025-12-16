package com.example.nettyclientsimulator.session.impl;

import com.example.nettyclientsimulator.client.Client;
import com.example.nettyclientsimulator.metric.ConnectionMetrics;
import com.example.nettyclientsimulator.session.SessionManager;
import com.example.nettyclientsimulator.session.SessionManagerShutdownHook;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器实现
 *
 * @author sunxu
 */
@Component
public class SessionManagerImpl implements SessionManager {

    private final ShutdownHooks shutdownHooks;

    private final ConnectionMetrics connectionMetrics;

    /**
     * 客户端映射表
     * 初始容量设置为 65536（2^16），负载因子 0.75
     * 预期支持约 50000 个客户端连接
     */
    private final Map<String, Client> clientIdMap = new ConcurrentHashMap<>(65536, 0.75f);

    public SessionManagerImpl(ShutdownHooks shutdownHooks, ConnectionMetrics connectionMetrics) {
        this.shutdownHooks = shutdownHooks;
        this.connectionMetrics = connectionMetrics;
    }

    @Override
    public void addClient(String clientId, Client client) {
        clientIdMap.put(clientId, client);
        connectionMetrics.incrementTotal();
    }

    @Override
    public Optional<Client> getClient(String clientId) {
        return Optional.ofNullable(clientIdMap.get(clientId));
    }

    @Override
    public Optional<Client> removeClient(String clientId) {
        Client removed = clientIdMap.remove(clientId);
        if (removed != null) {
            connectionMetrics.decrementTotal();
        }
        return Optional.ofNullable(removed);
    }

    @Override
    public Map<String, Client> getClientMap() {
        return clientIdMap;
    }

    @Override
    public int getClientMapSize() {
        return clientIdMap.size();
    }

    @PostConstruct
    public void run() {
        shutdownHooks.add(new SessionManagerShutdownHook(this));
    }

    @Override
    public void shutdown() {
        // 使用 entrySet 遍历以提升性能
        for (Map.Entry<String, Client> entry : clientIdMap.entrySet()) {
            Client client = entry.getValue();
            if (client != null) {
                client.shutdown();
            }
        }
    }
}

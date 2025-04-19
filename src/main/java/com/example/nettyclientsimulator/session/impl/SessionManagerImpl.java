package com.example.nettyclientsimulator.session.impl;

import com.example.nettyclientsimulator.client.Client;
import com.example.nettyclientsimulator.session.SessionManager;
import com.example.nettyclientsimulator.session.SessionManagerShutdownHook;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunxu
 */

@Component
public class SessionManagerImpl implements SessionManager {

    private final ShutdownHooks shutdownHooks;

    public SessionManagerImpl(ShutdownHooks shutdownHooks) {
        this.shutdownHooks = shutdownHooks;
    }

    private final Map<String, Client> clientIdMap = new ConcurrentHashMap<String, Client>(50000);

    @Override
    public void addClient(String clientId, Client client) {
        clientIdMap.put(clientId, client);
    }

    @Override
    public Client getClient(String clientId) {
        return clientIdMap.get(clientId);
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
        for (String key : clientIdMap.keySet()) {
            Client client = clientIdMap.get(key);
            if (client == null) {
                continue;
            }
            client.shutdown();
        }
    }
}

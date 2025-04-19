package com.example.nettyclientsimulator.service.impl;

import com.example.nettyclientsimulator.config.props.ClientConfig;
import com.example.nettyclientsimulator.config.props.ClientManagerConfig;
import com.example.nettyclientsimulator.service.ConnectionService;
import com.example.nettyclientsimulator.service.shutdown.ConnectionServiceShutdownHook;
import com.example.nettyclientsimulator.session.impl.SessionManagerImpl;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import com.example.nettyclientsimulator.util.ThreadFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sunxu
 */

@Slf4j
@Service
public class ConnectionServiceImpl implements ConnectionService {

    private final ShutdownHooks shutdownHooks;
    private final ThreadPoolExecutor connectExecutor;

    private static final ScheduledExecutorService DETECTING_CONNECTION_STATUS = new ScheduledThreadPoolExecutor(1, ThreadFactoryUtil.create("detecting-connect"),
            new ThreadPoolExecutor.AbortPolicy());
    private final SessionManagerImpl sessionManager;

    private final ClientConfig clientConfig;

    private final ClientManagerConfig clientManagerConfig;


    public ConnectionServiceImpl(ShutdownHooks shutdownHooks, ThreadPoolExecutor connectExecutor, SessionManagerImpl sessionManager, ClientConfig clientConfig, ClientManagerConfig clientManagerConfig) {
        this.shutdownHooks = shutdownHooks;
        this.connectExecutor = connectExecutor;
        this.sessionManager = sessionManager;
        this.clientConfig = clientConfig;
        this.clientManagerConfig = clientManagerConfig;
    }

    @PostConstruct
    public void init() {
        // 查看连接状态
        checkConnectionStatus();
        shutdownHooks.add(new ConnectionServiceShutdownHook(this));
    }

    private void checkConnectionStatus() {

        DETECTING_CONNECTION_STATUS.scheduleWithFixedDelay(() -> {
            log.info("clientIdMap length is {}", sessionManager.getClientMapSize());
            int count = 0;
            int onlineCount = 0;
            for (String key : sessionManager.getClientMap().keySet()) {
                boolean connected = sessionManager.getClientMap().get(key).connected();
                if (!connected) {
                    if (count >= clientManagerConfig.getConnectLimitSize()) {
                        continue;
                    }

                    // create connection
                    connectExecutor.execute(() -> {
                        try {
                            sessionManager.getClientMap().get(key).connect(clientConfig.getHost(), clientConfig.getPort());
                        } catch (InterruptedException e) {
                            log.warn("connect to server get error", e);
                        }
                    });
                    count++;
                    continue;
                }
                onlineCount++;
            }
            log.info("onlineCount is  {}", onlineCount);
        }, 0, clientManagerConfig.getConnectCheckInterval(), TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        connectExecutor.shutdown();
        DETECTING_CONNECTION_STATUS.shutdown();
    }
}

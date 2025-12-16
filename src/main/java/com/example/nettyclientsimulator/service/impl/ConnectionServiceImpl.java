package com.example.nettyclientsimulator.service.impl;

import com.example.nettyclientsimulator.config.props.ClientConfig;
import com.example.nettyclientsimulator.config.props.ClientManagerConfig;
import com.example.nettyclientsimulator.config.props.RetryConfig;
import com.example.nettyclientsimulator.retry.ExponentialBackoffRetryPolicy;
import com.example.nettyclientsimulator.retry.RetryPolicy;
import com.example.nettyclientsimulator.service.ConnectionService;
import com.example.nettyclientsimulator.service.shutdown.ConnectionServiceShutdownHook;
import com.example.nettyclientsimulator.session.SessionManager;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import com.example.nettyclientsimulator.util.ThreadFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private final SessionManager sessionManager;

    private final ClientConfig clientConfig;

    private final ClientManagerConfig clientManagerConfig;

    private final RetryPolicy retryPolicy;

    /**
     * 记录每个客户端的重试次数
     */
    private final Map<String, Integer> retryAttempts = new ConcurrentHashMap<>();

    public ConnectionServiceImpl(ShutdownHooks shutdownHooks, ThreadPoolExecutor connectExecutor, 
                                  SessionManager sessionManager, ClientConfig clientConfig, 
                                  ClientManagerConfig clientManagerConfig, RetryConfig retryConfig) {
        this.shutdownHooks = shutdownHooks;
        this.connectExecutor = connectExecutor;
        this.sessionManager = sessionManager;
        this.clientConfig = clientConfig;
        this.clientManagerConfig = clientManagerConfig;
        this.retryPolicy = new ExponentialBackoffRetryPolicy(
                retryConfig.getMaxRetries(),
                retryConfig.getInitialDelayMs(),
                retryConfig.getMaxDelayMs(),
                retryConfig.getMultiplier()
        );
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
            
            // 使用 entrySet 遍历以提升性能，避免重复查找
            for (java.util.Map.Entry<String, com.example.nettyclientsimulator.client.Client> entry : 
                    sessionManager.getClientMap().entrySet()) {
                String clientId = entry.getKey();
                com.example.nettyclientsimulator.client.Client client = entry.getValue();
                
                if (!client.connected()) {
                    if (count >= clientManagerConfig.getConnectLimitSize()) {
                        continue;
                    }

                    // 检查重试次数
                    int currentAttempt = retryAttempts.getOrDefault(clientId, 0);
                    if (!retryPolicy.shouldRetry(currentAttempt, null)) {
                        log.debug("Client {} exceeded max retries, skipping", clientId);
                        continue;
                    }

                    // create connection with retry
                    connectExecutor.execute(() -> connectWithRetry(clientId, client, currentAttempt));
                    count++;
                    continue;
                }
                // 连接成功，重置重试计数
                retryAttempts.remove(clientId);
                onlineCount++;
            }
            log.info("onlineCount is {}", onlineCount);
        }, 0, clientManagerConfig.getConnectCheckInterval(), TimeUnit.SECONDS);
    }

    /**
     * 带重试机制的连接方法
     */
    private void connectWithRetry(String clientId, com.example.nettyclientsimulator.client.Client client, int attempt) {
        try {
            // 如果不是第一次尝试，等待退避时间
            if (attempt > 0) {
                long delay = retryPolicy.getNextRetryDelay(attempt - 1);
                if (delay > 0) {
                    Thread.sleep(delay);
                }
            }
            
            client.connect(clientConfig.getHost(), clientConfig.getPort());
            // 连接成功，清除重试计数
            retryAttempts.remove(clientId);
            log.info("Client {} connected successfully after {} attempts", clientId, attempt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Connection interrupted for client: {}", clientId, e);
        } catch (Exception e) {
            // 记录重试次数
            retryAttempts.put(clientId, attempt + 1);
            long nextDelay = retryPolicy.getNextRetryDelay(attempt);
            if (nextDelay > 0) {
                log.warn("Connection failed for client: {}, attempt: {}, next retry in {} ms", 
                        clientId, attempt + 1, nextDelay, e);
            } else {
                log.error("Connection failed for client: {}, max retries exceeded", clientId, e);
            }
        }
    }

    @Override
    public void shutdown() {
        connectExecutor.shutdown();
        DETECTING_CONNECTION_STATUS.shutdown();
    }
}

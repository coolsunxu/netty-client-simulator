package com.example.nettyclientsimulator.session;

import com.example.nettyclientsimulator.client.Client;
import com.example.nettyclientsimulator.metric.ConnectionMetrics;
import com.example.nettyclientsimulator.session.impl.SessionManagerImpl;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * SessionManager 的属性测试
 *
 * @author sunxu
 */
class SessionManagerPropertyTest {

    private SessionManager createSessionManager() {
        ShutdownHooks shutdownHooks = mock(ShutdownHooks.class);
        ConnectionMetrics connectionMetrics = mock(ConnectionMetrics.class);
        return new SessionManagerImpl(shutdownHooks, connectionMetrics);
    }

    private Client createMockClient(String clientId) {
        return mock(Client.class);
    }

    /**
     * **Feature: netty-client-optimization, Property 2: SessionManager 获取不存在 Client 返回 Optional.empty**
     * 
     * 对于任意不存在于 SessionManager 中的 clientId，调用 getClient 方法应返回 Optional.empty()
     */
    @Property(tries = 100)
    void getClient_whenClientNotExists_returnsEmptyOptional(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String clientId
    ) {
        SessionManager sessionManager = createSessionManager();

        Optional<Client> result = sessionManager.getClient(clientId);

        assertThat(result)
                .as("getClient should return Optional.empty() for non-existent clientId: %s", clientId)
                .isEmpty();
    }

    /**
     * **Feature: netty-client-optimization, Property 2: SessionManager 获取不存在 Client 返回 Optional.empty**
     * 
     * 对于任意已添加的 Client，getClient 应返回包含该 Client 的 Optional
     */
    @Property(tries = 100)
    void getClient_whenClientExists_returnsOptionalWithClient(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String clientId
    ) {
        SessionManager sessionManager = createSessionManager();
        Client client = createMockClient(clientId);

        sessionManager.addClient(clientId, client);
        Optional<Client> result = sessionManager.getClient(clientId);

        assertThat(result)
                .as("getClient should return Optional with client for existing clientId: %s", clientId)
                .isPresent()
                .contains(client);
    }

    /**
     * **Feature: netty-client-optimization, Property 13: SessionManager CRUD 操作一致性**
     * 
     * 对于任意 SessionManager 的增删查操作序列，操作结果应保持一致性
     * 添加后可查询、删除后不可查询
     */
    @Property(tries = 100)
    void crudOperationsAreConsistent(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String clientId
    ) {
        SessionManager sessionManager = createSessionManager();
        Client client = createMockClient(clientId);

        // 初始状态：不存在
        assertThat(sessionManager.getClient(clientId)).isEmpty();
        assertThat(sessionManager.getClientMapSize()).isEqualTo(0);

        // 添加后：存在
        sessionManager.addClient(clientId, client);
        assertThat(sessionManager.getClient(clientId)).isPresent().contains(client);
        assertThat(sessionManager.getClientMapSize()).isEqualTo(1);

        // 删除后：不存在
        Optional<Client> removed = sessionManager.removeClient(clientId);
        assertThat(removed).isPresent().contains(client);
        assertThat(sessionManager.getClient(clientId)).isEmpty();
        assertThat(sessionManager.getClientMapSize()).isEqualTo(0);

        // 再次删除：返回空
        Optional<Client> removedAgain = sessionManager.removeClient(clientId);
        assertThat(removedAgain).isEmpty();
    }

    /**
     * **Feature: netty-client-optimization, Property 13: SessionManager CRUD 操作一致性**
     * 
     * 添加多个 Client 后，每个都应该可以独立查询和删除
     */
    @Property(tries = 50)
    void multipleClientsCanBeAddedAndRetrieved(
            @ForAll @Size(min = 1, max = 10) java.util.List<@AlphaChars @StringLength(min = 5, max = 15) String> clientIds
    ) {
        SessionManager sessionManager = createSessionManager();

        // 添加所有 client
        for (String clientId : clientIds) {
            Client client = createMockClient(clientId);
            sessionManager.addClient(clientId, client);
        }

        // 验证所有 client 都存在
        for (String clientId : clientIds) {
            assertThat(sessionManager.getClient(clientId))
                    .as("Client %s should exist", clientId)
                    .isPresent();
        }

        // 验证 size
        // 注意：如果有重复的 clientId，size 可能小于 clientIds.size()
        assertThat(sessionManager.getClientMapSize())
                .isGreaterThan(0)
                .isLessThanOrEqualTo(clientIds.size());
    }

    /**
     * **Feature: netty-client-optimization, Property 13: SessionManager CRUD 操作一致性**
     * 
     * 覆盖添加同一 clientId 应该替换原有 Client
     */
    @Property(tries = 100)
    void addClient_withSameId_replacesExisting(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String clientId
    ) {
        SessionManager sessionManager = createSessionManager();
        Client client1 = createMockClient(clientId);
        Client client2 = createMockClient(clientId);

        sessionManager.addClient(clientId, client1);
        assertThat(sessionManager.getClient(clientId)).contains(client1);

        sessionManager.addClient(clientId, client2);
        assertThat(sessionManager.getClient(clientId)).contains(client2);
        assertThat(sessionManager.getClientMapSize()).isEqualTo(1);
    }
}

package com.example.nettyclientsimulator.config;

import com.example.nettyclientsimulator.config.beans.SslContextProvider;
import com.example.nettyclientsimulator.config.props.ClientConfig;
import io.netty.handler.ssl.SslContext;
import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * SslContextProvider 的属性测试
 *
 * @author sunxu
 */
class SslContextProviderPropertyTest {

    /**
     * **Feature: netty-client-optimization, Property 3: SslContext 实例复用**
     * 
     * 对于任意多次调用 getSslContext()，应该返回同一个 SslContext 实例
     */
    @Property(tries = 100)
    void getSslContext_alwaysReturnsSameInstance(
            @ForAll @net.jqwik.api.constraints.IntRange(min = 2, max = 20) int callCount
    ) {
        ClientConfig clientConfig = mock(ClientConfig.class);
        when(clientConfig.getUseTls()).thenReturn(true);

        SslContextProvider provider = new SslContextProvider(clientConfig);
        provider.init();

        SslContext firstContext = provider.getSslContext();
        assertThat(firstContext).isNotNull();

        // 多次调用应该返回同一个实例
        for (int i = 0; i < callCount; i++) {
            SslContext currentContext = provider.getSslContext();
            assertThat(currentContext)
                    .as("Call %d should return the same SslContext instance", i)
                    .isSameAs(firstContext);
        }
    }

    /**
     * **Feature: netty-client-optimization, Property 3: SslContext 实例复用**
     * 
     * 当 TLS 未启用时，getSslContext() 应该返回 null
     */
    @Property(tries = 10)
    void getSslContext_whenTlsDisabled_returnsNull() {
        ClientConfig clientConfig = mock(ClientConfig.class);
        when(clientConfig.getUseTls()).thenReturn(false);

        SslContextProvider provider = new SslContextProvider(clientConfig);
        provider.init();

        assertThat(provider.getSslContext())
                .as("SslContext should be null when TLS is disabled")
                .isNull();
        assertThat(provider.isSslEnabled())
                .as("isSslEnabled should return false when TLS is disabled")
                .isFalse();
    }

    /**
     * **Feature: netty-client-optimization, Property 3: SslContext 实例复用**
     * 
     * 当 TLS 启用时，isSslEnabled() 应该返回 true
     */
    @Property(tries = 10)
    void isSslEnabled_whenTlsEnabled_returnsTrue() {
        ClientConfig clientConfig = mock(ClientConfig.class);
        when(clientConfig.getUseTls()).thenReturn(true);

        SslContextProvider provider = new SslContextProvider(clientConfig);
        provider.init();

        assertThat(provider.isSslEnabled())
                .as("isSslEnabled should return true when TLS is enabled")
                .isTrue();
    }
}

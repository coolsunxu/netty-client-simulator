package com.example.nettyclientsimulator.exception;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.io.IOException;
import java.net.ConnectException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 异常体系的属性测试
 *
 * @author sunxu
 */
class ExceptionPropertyTest {

    /**
     * **Feature: netty-client-optimization, Property 5: 连接异常转换为业务异常**
     * 
     * 对于任意连接参数，ConnectionException 应该正确包装异常信息
     */
    @Property(tries = 100)
    void connectionException_wrapsExceptionCorrectly(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String host,
            @ForAll @IntRange(min = 1, max = 65535) int port
    ) {
        IOException cause = new IOException("test error");
        ConnectionException exception = new ConnectionException("Connection failed", host, port, cause);

        assertThat(exception.getHost()).isEqualTo(host);
        assertThat(exception.getPort()).isEqualTo(port);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorCode()).isEqualTo(503);
    }

    /**
     * **Feature: netty-client-optimization, Property 5: 连接异常转换为业务异常**
     * 
     * ConnectionTimeoutException 应该包含超时信息
     */
    @Property(tries = 100)
    void connectionTimeoutException_containsTimeoutInfo(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String host,
            @ForAll @IntRange(min = 1, max = 65535) int port,
            @ForAll @LongRange(min = 1, max = 60000) long timeoutMs
    ) {
        ConnectionTimeoutException exception = new ConnectionTimeoutException(host, port, timeoutMs);

        assertThat(exception.getHost()).isEqualTo(host);
        assertThat(exception.getPort()).isEqualTo(port);
        assertThat(exception.getTimeoutMs()).isEqualTo(timeoutMs);
        assertThat(exception.getMessage()).contains(String.valueOf(timeoutMs));
    }

    /**
     * **Feature: netty-client-optimization, Property 6: 获取不存在 Client 抛出特定异常**
     * 
     * ClientNotFoundException 应该包含 clientId 信息
     */
    @Property(tries = 100)
    void clientNotFoundException_containsClientId(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String clientId
    ) {
        ClientNotFoundException exception = new ClientNotFoundException(clientId);

        assertThat(exception.getClientId()).isEqualTo(clientId);
        assertThat(exception.getMessage()).contains(clientId);
        assertThat(exception.getErrorCode()).isEqualTo(404);
    }

    /**
     * **Feature: netty-client-optimization, Property 5: 连接异常转换为业务异常**
     * 
     * ConnectionRefusedException 应该正确设置主机和端口
     */
    @Property(tries = 100)
    void connectionRefusedException_setsHostAndPort(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String host,
            @ForAll @IntRange(min = 1, max = 65535) int port
    ) {
        ConnectException cause = new ConnectException("Connection refused");
        ConnectionRefusedException exception = new ConnectionRefusedException(host, port, cause);

        assertThat(exception.getHost()).isEqualTo(host);
        assertThat(exception.getPort()).isEqualTo(port);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    /**
     * **Feature: netty-client-optimization, Property 5: 连接异常转换为业务异常**
     * 
     * BusinessException 应该正确设置错误码
     */
    @Property(tries = 100)
    void businessException_setsErrorCode(
            @ForAll @IntRange(min = 100, max = 599) int errorCode,
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String message
    ) {
        BusinessException exception = new BusinessException(errorCode, message);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}

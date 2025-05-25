package com.example.nettyclientsimulator.config;

import com.example.nettyclientsimulator.config.props.ClientConfig;
import com.example.nettyclientsimulator.config.props.NettyConfig;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 配置类属性测试
 *
 * @author sunxu
 */
class ClientConfigPropertyTest {

    private final Validator validator;

    ClientConfigPropertyTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * **Feature: netty-client-optimization, Property 12: 配置校验有效性**
     * 
     * 有效的 ClientConfig 应该通过所有校验
     */
    @Property(tries = 100)
    void validClientConfig_passesValidation(
            @ForAll @IntRange(min = 1, max = 3600) int keepAlive,
            @ForAll @IntRange(min = 1, max = 65535) int port,
            @ForAll @IntRange(min = 100, max = 60000) int connectTimeoutMs,
            @ForAll @IntRange(min = 100, max = 300000) int readTimeoutMs,
            @ForAll @IntRange(min = 100, max = 60000) int writeTimeoutMs
    ) {
        ClientConfig config = new ClientConfig();
        config.setUseTls(true);
        config.setKeepAlive(keepAlive);
        config.setHost("localhost");
        config.setPort(port);
        config.setConnectTimeoutMs(connectTimeoutMs);
        config.setReadTimeoutMs(readTimeoutMs);
        config.setWriteTimeoutMs(writeTimeoutMs);

        Set<ConstraintViolation<ClientConfig>> violations = validator.validate(config);

        assertThat(violations)
                .as("Valid config should have no violations")
                .isEmpty();
    }

    /**
     * **Feature: netty-client-optimization, Property 12: 配置校验有效性**
     * 
     * keepAlive 超出范围应该校验失败
     */
    @Property(tries = 50)
    void invalidKeepAlive_failsValidation(
            @ForAll @IntRange(min = 3601, max = 10000) int invalidKeepAlive
    ) {
        ClientConfig config = createValidConfig();
        config.setKeepAlive(invalidKeepAlive);

        Set<ConstraintViolation<ClientConfig>> violations = validator.validate(config);

        assertThat(violations)
                .as("Invalid keepAlive should fail validation")
                .isNotEmpty();
    }

    /**
     * **Feature: netty-client-optimization, Property 12: 配置校验有效性**
     * 
     * port 超出范围应该校验失败
     */
    @Property(tries = 50)
    void invalidPort_failsValidation(
            @ForAll @IntRange(min = 65536, max = 100000) int invalidPort
    ) {
        ClientConfig config = createValidConfig();
        config.setPort(invalidPort);

        Set<ConstraintViolation<ClientConfig>> violations = validator.validate(config);

        assertThat(violations)
                .as("Invalid port should fail validation")
                .isNotEmpty();
    }

    /**
     * **Feature: netty-client-optimization, Property 12: 配置校验有效性**
     * 
     * 空 host 应该校验失败
     */
    @Example
    void emptyHost_failsValidation() {
        ClientConfig config = createValidConfig();
        config.setHost("");

        Set<ConstraintViolation<ClientConfig>> violations = validator.validate(config);

        assertThat(violations)
                .as("Empty host should fail validation")
                .isNotEmpty();
    }

    /**
     * **Feature: netty-client-optimization, Property 12: 配置校验有效性**
     * 
     * null useTls 应该校验失败
     */
    @Example
    void nullUseTls_failsValidation() {
        ClientConfig config = createValidConfig();
        config.setUseTls(null);

        Set<ConstraintViolation<ClientConfig>> violations = validator.validate(config);

        assertThat(violations)
                .as("Null useTls should fail validation")
                .isNotEmpty();
    }

    /**
     * **Feature: netty-client-optimization, Property: NettyConfig 智能线程数配置**
     * 
     * 当 threadSize 为 0 或 null 时，getEffectiveThreadSize 应返回 CPU 核心数 * 2
     */
    @Example
    void nettyConfig_zeroThreadSize_returnsAutoCalculated() {
        NettyConfig config = new NettyConfig();
        config.setThreadSize(0);
        config.setPrefix("test");

        int effectiveSize = config.getEffectiveThreadSize();
        int expectedSize = Runtime.getRuntime().availableProcessors() * 2;

        assertThat(effectiveSize)
                .as("Zero threadSize should return CPU cores * 2")
                .isEqualTo(expectedSize);
    }

    /**
     * **Feature: netty-client-optimization, Property: NettyConfig 智能线程数配置**
     * 
     * 当 threadSize 大于 0 时，getEffectiveThreadSize 应返回配置值
     */
    @Property(tries = 50)
    void nettyConfig_positiveThreadSize_returnsConfiguredValue(
            @ForAll @IntRange(min = 1, max = 100) int threadSize
    ) {
        NettyConfig config = new NettyConfig();
        config.setThreadSize(threadSize);
        config.setPrefix("test");

        int effectiveSize = config.getEffectiveThreadSize();

        assertThat(effectiveSize)
                .as("Positive threadSize should return configured value")
                .isEqualTo(threadSize);
    }

    private ClientConfig createValidConfig() {
        ClientConfig config = new ClientConfig();
        config.setUseTls(true);
        config.setKeepAlive(60);
        config.setHost("localhost");
        config.setPort(8080);
        config.setConnectTimeoutMs(5000);
        config.setReadTimeoutMs(30000);
        config.setWriteTimeoutMs(10000);
        return config;
    }
}

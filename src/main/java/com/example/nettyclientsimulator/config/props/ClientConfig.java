package com.example.nettyclientsimulator.config.props;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 客户端配置
 *
 * @author sunxu
 */
@ConfigurationProperties(prefix = "client")
@Component
@Data
@NoArgsConstructor
public class ClientConfig {
    /**
     * 是否使用 TLS 加密连接
     */
    @NotNull(message = "useTls 不能为空")
    private Boolean useTls;

    /**
     * 客户端与服务端的心跳保活时间（秒）
     */
    @NotNull(message = "keepAlive 不能为空")
    @Min(value = 1, message = "keepAlive 最小值为 1 秒")
    @Max(value = 3600, message = "keepAlive 最大值为 3600 秒")
    private Integer keepAlive;

    /**
     * 客户端连接的服务端地址
     */
    @NotBlank(message = "host 不能为空")
    private String host;

    /**
     * 客户端连接的服务端端口
     */
    @NotNull(message = "port 不能为空")
    @Min(value = 1, message = "port 最小值为 1")
    @Max(value = 65535, message = "port 最大值为 65535")
    private Integer port;

    /**
     * 连接超时时间（毫秒），默认 5000ms
     */
    @Min(value = 100, message = "connectTimeoutMs 最小值为 100ms")
    @Max(value = 60000, message = "connectTimeoutMs 最大值为 60000ms")
    private Integer connectTimeoutMs = 5000;

    /**
     * 读取超时时间（毫秒），默认 30000ms
     */
    @Min(value = 100, message = "readTimeoutMs 最小值为 100ms")
    @Max(value = 300000, message = "readTimeoutMs 最大值为 300000ms")
    private Integer readTimeoutMs = 30000;

    /**
     * 写入超时时间（毫秒），默认 10000ms
     */
    @Min(value = 100, message = "writeTimeoutMs 最小值为 100ms")
    @Max(value = 60000, message = "writeTimeoutMs 最大值为 60000ms")
    private Integer writeTimeoutMs = 10000;

    /**
     * SSL 证书路径（可选）
     */
    private String sslCertPath;

    /**
     * SSL 密钥路径（可选）
     */
    private String sslKeyPath;

    /**
     * SSL 密码（可选）
     */
    private String sslPassword;
}

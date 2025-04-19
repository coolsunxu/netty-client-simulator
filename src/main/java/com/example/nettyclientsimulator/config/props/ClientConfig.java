package com.example.nettyclientsimulator.config.props;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sunxu
 */
@ConfigurationProperties(prefix = "client")
@Component
@Data
@NoArgsConstructor
public class ClientConfig {
    /**
     * 是否使用Tls
     */
    private Boolean useTls;

    /**
     * 客户端与服务端的心跳保活时间
     */
    private Integer keepAlive;

    /**
     * 客户端连接的服务端地址
     */
    private String host;

    /**
     * 客户端连接的服务端端口
     */
    private Integer port;

}

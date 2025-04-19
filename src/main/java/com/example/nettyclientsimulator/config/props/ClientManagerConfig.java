package com.example.nettyclientsimulator.config.props;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sunxu
 */
@ConfigurationProperties(prefix = "client-manager")
@Component
@Data
@NoArgsConstructor
public class ClientManagerConfig {

    /**
     * 客户端连接时，每批数量限制
     */
    private Integer connectLimitSize;

    /**
     * 检查客户端的连接时间间隔
     */
    private Integer connectCheckInterval;
}

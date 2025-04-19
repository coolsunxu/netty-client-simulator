package com.example.nettyclientsimulator.config.props;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sunxu
 */

@ConfigurationProperties(prefix = "netty")
@Component
@Data
@NoArgsConstructor
public class NettyConfig {

    /**
     * 客户端所有client注册的reactor的数量
     */
    private Integer threadSize;

    /**
     * reactor的线程名前缀
     */
    private String prefix;

}

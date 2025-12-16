package com.example.nettyclientsimulator.config.props;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * Netty 配置
 *
 * @author sunxu
 */
@ConfigurationProperties(prefix = "netty")
@Component
@Data
@NoArgsConstructor
public class NettyConfig {

    /**
     * 客户端所有 client 注册的 reactor 线程数量
     * 设置为 0 时自动计算为 CPU 核心数 * 2
     */
    @Min(value = 0, message = "threadSize 最小值为 0")
    private Integer threadSize;

    /**
     * reactor 的线程名前缀
     */
    @NotBlank(message = "prefix 不能为空")
    private String prefix;

    /**
     * 初始化后处理：当 threadSize 为 0 或 null 时，自动设置为 CPU 核心数 * 2
     */
    @PostConstruct
    public void init() {
        if (threadSize == null || threadSize == 0) {
            threadSize = Runtime.getRuntime().availableProcessors() * 2;
        }
    }

    /**
     * 获取实际线程数（确保不为 0）
     */
    public Integer getEffectiveThreadSize() {
        if (threadSize == null || threadSize == 0) {
            return Runtime.getRuntime().availableProcessors() * 2;
        }
        return threadSize;
    }
}

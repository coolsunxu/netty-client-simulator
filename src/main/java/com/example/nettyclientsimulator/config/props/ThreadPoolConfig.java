package com.example.nettyclientsimulator.config.props;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @author sunxu
 */
@Validated
@Getter
@Setter
@ToString
@NoArgsConstructor
@ConfigurationProperties(prefix = "thread-pool")
@Component
public class ThreadPoolConfig {

    @NotNull
    private ThreadPoolSetting connect;

    @NotNull
    private ThreadPoolSetting async;

    @NotNull
    private ThreadPoolSetting common;

    @NotNull
    private ThreadPoolSetting delayTask;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ThreadPoolSetting {

        private String prefix;

        private Integer corePoolSize;

        private Integer maxPoolSize;

        private Integer keepAlive;

        private Integer queueCapacity;
    }
}


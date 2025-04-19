package com.example.nettyclientsimulator.config.props;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @author sunxu
 */

@Validated
@ConfigurationProperties(prefix = "disruptor")
@Component
@Data
@NoArgsConstructor
public class DisruptorConfig {

    @NotNull
    private DisruptorSetting readQueue;

    @NotNull
    private DisruptorSetting writeQueue;

    @Data
    @ToString
    @NoArgsConstructor
    public static class DisruptorSetting {
        /**
         * 线程前缀
         */
        private String prefix;

        /**
         * 队列总数
         */
        private Integer queueSize;

        /**
         * 每个队列的容量
         */
        private Integer bufferSize;

        /**
         * 任务的最大过期时间
         */
        private Long expirationTime;
    }
}

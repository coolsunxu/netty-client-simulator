package com.example.nettyclientsimulator.task;


import lombok.Getter;
import lombok.Setter;

/**
 * @author sunxu
 */

@Getter
@Setter
public class Qoe {

    /**
     * qoe开关是否开启
     */
    private Boolean enable;

    /**
     * 上报时间间隔
     */
    private Integer interval;

    /**
     * 最近一次生效时间
     */
    private Long lastActiveTime;
}

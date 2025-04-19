package com.example.nettyclientsimulator.service;

import com.example.nettyclientsimulator.timeWheel.TimerTask;

/**
 * @author sunxu
 */
public interface TimeWheelService {

    /**
     * 向时间轮中添加任务
     * @param timerTask 任务
     */
    void add(TimerTask timerTask);

    /**
     * 停止检测任务
     */
    void stopRuning();


    /**
     * 关闭程序
     */
    void shutdown();
}

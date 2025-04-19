package com.example.nettyclientsimulator.timeWheel;

import com.example.nettyclientsimulator.threadpool.TimingThreadPool;
import com.example.nettyclientsimulator.util.ServiceThread;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sunxu
 */

@Slf4j
public class TickThread extends ServiceThread {

    private final SystemTimer systemTimer;

    public TickThread(TimingThreadPool delayTaskExecutor) {
        this.systemTimer = new SystemTimer("delayTask", delayTaskExecutor);;
    }


    @Override
    public String getServiceName() {
        return "tick-thread";
    }

    @Override
    public void run() {
        while (!this.isStopped()) {
            try {
                systemTimer.advanceClock(200);
            } catch (Exception e) {
                log.error("get error", e);
            }
        }
    }

    public void addTask(TimerTask timerTask) {
        systemTimer.add(timerTask);
    }
}

package com.example.nettyclientsimulator.service.impl;

import com.example.nettyclientsimulator.service.TimeWheelService;
import com.example.nettyclientsimulator.service.shutdown.TimeWheelServiceShutdownHook;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import com.example.nettyclientsimulator.threadpool.TimingThreadPool;
import com.example.nettyclientsimulator.timeWheel.TickThread;
import com.example.nettyclientsimulator.timeWheel.TimerTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author sunxu
 */
@Service
public class TimeWheelServiceImpl implements TimeWheelService {

    private final TickThread tickThread;

    private final ShutdownHooks shutdownHooks;

    public TimeWheelServiceImpl(TimingThreadPool delayTaskExecutor, ShutdownHooks shutdownHooks) {
        this.tickThread = new TickThread(delayTaskExecutor);
        this.shutdownHooks = shutdownHooks;
        this.tickThread.start();
    }

    @PostConstruct
    public void run() {
        shutdownHooks.add(new TimeWheelServiceShutdownHook(this));
    }


    @Override
    public void add(TimerTask timerTask) {
        this.tickThread.addTask(timerTask);
    }

    @Override
    public void stopRuning() {
        this.tickThread.makeStop();
    }

    @Override
    public void shutdown() {
        this.tickThread.shutdown();
    }

}

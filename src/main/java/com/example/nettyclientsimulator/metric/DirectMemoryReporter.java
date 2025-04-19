package com.example.nettyclientsimulator.metric;

/**
 * @author sunxu
 */
public interface DirectMemoryReporter {

    /**
     * 打印netty堆外内存使用
     */
    void startReport();
}

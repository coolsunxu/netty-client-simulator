package com.example.nettyclientsimulator.service;

/**
 * @author sunxu
 */
public interface ReportQoeService {

    /**
     * 上报QoE信息
     * @param clientId 客户端Id
     */
    void reportQoE(String clientId);
}

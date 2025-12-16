package com.example.nettyclientsimulator.task;

import com.example.nettyclientsimulator.service.ReportQoeService;
import com.example.nettyclientsimulator.session.SessionManager;
import com.example.nettyclientsimulator.timeWheel.TimerTask;

/**
 * QoE 上报任务
 *
 * @author sunxu
 */
public class ReportQoeTask extends TimerTask {

    private final ReportQoeService reportQoeService;

    private final String clientId;

    public ReportQoeTask(SessionManager sessionManager, ReportQoeService reportQoeService, String clientId) {
        this.reportQoeService = reportQoeService;
        this.clientId = clientId;
        // 使用 Optional 安全获取延迟时间
        super.delayMs = sessionManager.getClient(clientId)
                .map(client -> Long.valueOf(client.getQoe().getInterval()))
                .orElse(0L);
    }

    @Override
    public void run() {
        reportQoeService.reportQoE(clientId);
    }
}

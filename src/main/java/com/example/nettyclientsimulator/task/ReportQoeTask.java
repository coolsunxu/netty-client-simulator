package com.example.nettyclientsimulator.task;

import com.example.nettyclientsimulator.service.impl.ReportQoeServiceImpl;
import com.example.nettyclientsimulator.session.impl.SessionManagerImpl;
import com.example.nettyclientsimulator.timeWheel.TimerTask;

/**
 * @author sunxu
 */
public class ReportQoeTask extends TimerTask {

    private final ReportQoeServiceImpl reportQoeService;

    private final String clientId;


    public ReportQoeTask(SessionManagerImpl sessionManager, ReportQoeServiceImpl reportQoeService, String clientId) {
        this.reportQoeService = reportQoeService;
        this.clientId = clientId;
        super.delayMs = Long.valueOf(sessionManager.getClient(clientId).getQoe().getInterval());
    }

    @Override
    public void run() {
        reportQoeService.reportQoE(clientId);
    }
}

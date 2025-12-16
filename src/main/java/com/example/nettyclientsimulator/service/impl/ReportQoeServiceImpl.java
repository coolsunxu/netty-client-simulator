package com.example.nettyclientsimulator.service.impl;


import com.alibaba.fastjson2.JSON;
import com.example.nettyclientsimulator.codec.Message;
import com.example.nettyclientsimulator.disruptor.DisruptorManager;
import com.example.nettyclientsimulator.service.ReportQoeService;
import com.example.nettyclientsimulator.service.TimeWheelService;
import com.example.nettyclientsimulator.session.SessionManager;
import com.example.nettyclientsimulator.task.ReportQoeTask;
import com.example.nettyclientsimulator.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author sunxu
 */
@Service
@Slf4j
public class ReportQoeServiceImpl implements ReportQoeService {
    private final DisruptorManager disruptorManager;

    private final SessionManager sessionManager;

    private final TimeWheelService timeWheelService;

    public ReportQoeServiceImpl(
            DisruptorManager disruptorManager,
            SessionManager sessionManager,
            TimeWheelService timeWheelService) {
        this.disruptorManager = disruptorManager;
        this.sessionManager = sessionManager;
        this.timeWheelService = timeWheelService;
    }

    @Override
    public void reportQoE(String clientId) {
        sessionManager.getClient(clientId).ifPresent(client -> {
            // 开关没有打开 不进行上报, 或者任务已过期 不上报
            if (!client.getQoe().getEnable() || client.getQoe().getLastActiveTime() > System.currentTimeMillis()) {
                return;
            }

            // 开始上报
            log.info("client {} start to report QoE", client.getClientId());
            Message request = Message.builder()
                    .id(client.getClientId())
                    .data("qoe")
                    .build();
            String str = JSON.toJSONString(request);
            int index = MathUtil.getHashCode(client.getClientId()) % disruptorManager.getWriteDisruptorQueueSize();
            disruptorManager.writePublish(client.getClientId(), str, index);

            // 继续添加任务
            timeWheelService.add(new ReportQoeTask(sessionManager, this, clientId));
        });
    }
}

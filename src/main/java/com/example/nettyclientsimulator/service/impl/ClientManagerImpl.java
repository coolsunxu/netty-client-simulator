package com.example.nettyclientsimulator.service.impl;

import com.example.nettyclientsimulator.client.Client;
import com.example.nettyclientsimulator.config.props.ClientConfig;
import com.example.nettyclientsimulator.config.props.NettyConfig;
import com.example.nettyclientsimulator.metric.NettyMetric;
import com.example.nettyclientsimulator.nettyhandler.ClientHandler;
import com.example.nettyclientsimulator.nettyhandler.HeartbeatHandler;
import com.example.nettyclientsimulator.service.ClientManager;
import com.example.nettyclientsimulator.service.shutdown.ClientManagerShutdownHook;
import com.example.nettyclientsimulator.session.impl.SessionManagerImpl;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import com.example.nettyclientsimulator.task.ReportQoeTask;
import com.example.nettyclientsimulator.threadpool.TimingThreadPool;
import com.example.nettyclientsimulator.util.MathUtil;
import com.example.nettyclientsimulator.web.dto.CloseConnectionDTO;
import com.example.nettyclientsimulator.web.dto.ReportQoeDTO;
import com.example.nettyclientsimulator.web.vo.CloseConnectionVO;
import com.example.nettyclientsimulator.web.vo.ReportQoeVO;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import net.openhft.affinity.AffinityStrategies;
import net.openhft.affinity.AffinityThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadFactory;

/**
 * @author sunxu
 */
@Slf4j
@Component
public class ClientManagerImpl implements ClientManager {

    private final ShutdownHooks shutdownHooks;

    private EventLoopGroup bossGroup;

    private final ClientHandler clientHandler;

    private final HeartbeatHandler heartbeatHandler;

    private final StringEncoder stringEncoder = new StringEncoder();

    private final StringDecoder stringDecoder = new StringDecoder();

    private final TimingThreadPool commonExecutor;

    private final ReportQoeServiceImpl reportQoeService;

    private final SessionManagerImpl sessionManager;

    private final TimeWheelServiceImpl timeWheelService;

    private final NettyMetric nettyMetric;

    private final NettyConfig nettyConfig;

    private final ClientConfig clientConfig;

    public ClientManagerImpl(
            ShutdownHooks shutdownHooks, ClientHandler clientHandler,
            HeartbeatHandler heartbeatHandler,
            TimingThreadPool commonExecutor,
            ReportQoeServiceImpl reportQoeService,
            SessionManagerImpl sessionManager,
            TimeWheelServiceImpl timeWheelService,
            NettyMetric nettyMetric, NettyConfig nettyConfig, ClientConfig clientConfig) {
        this.shutdownHooks = shutdownHooks;
        this.clientHandler = clientHandler;
        this.heartbeatHandler = heartbeatHandler;
        this.commonExecutor = commonExecutor;
        this.reportQoeService = reportQoeService;
        this.sessionManager = sessionManager;
        this.timeWheelService = timeWheelService;
        this.nettyMetric = nettyMetric;
        this.nettyConfig = nettyConfig;
        this.clientConfig = clientConfig;
    }

    @PostConstruct
    public void init() {
        // 绑定CPU线程亲和性，提高利用率
        ThreadFactory threadFactory = new AffinityThreadFactory(nettyConfig.getPrefix(), AffinityStrategies.DIFFERENT_CORE);
        bossGroup = new NioEventLoopGroup(nettyConfig.getThreadSize(), threadFactory);
        // 添加监控
        nettyMetric.addEventLoopGroupMetric(bossGroup, "netty-thread");

        // 添加优雅关机的注册钩子
        shutdownHooks.add(new ClientManagerShutdownHook(this));
    }


    @Override
    public void addClient(int batch) {
        commonExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < batch; i++) {
                    try {
                        String clientId = MathUtil.generateRandomString(10) + "-" + i;
                        Client client = new Client(clientId, clientConfig.getKeepAlive(), bossGroup, clientHandler,
                                stringEncoder, stringDecoder, heartbeatHandler, clientConfig.getUseTls());
                        sessionManager.addClient(clientId, client);
                    } catch (Exception e) {
                        log.warn("create client error", e);
                    }
                }
            }
        });
    }

    @Override
    public ReportQoeVO reportQoe(ReportQoeDTO reportQoeDTO) {
        log.info("client {}, add delay task after {} ms", reportQoeDTO.getClientId(), reportQoeDTO.getDelay());
        commonExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Client client = sessionManager.getClient(reportQoeDTO.getClientId());
                Long timestamp = System.currentTimeMillis() + reportQoeDTO.getDelay();
                client.setQoe(reportQoeDTO.getEnable(), Math.toIntExact(reportQoeDTO.getDelay()), timestamp);

                // 如果是开启上报，添加延时任务
                if (reportQoeDTO.getEnable()) {
                    timeWheelService.add(new ReportQoeTask(sessionManager, reportQoeService, reportQoeDTO.getClientId()));
                }
            }
        });
        return ReportQoeVO.builder()
                .clientId(reportQoeDTO.getClientId())
                .delay(reportQoeDTO.getDelay())
                .build();
    }

    @Override
    public CloseConnectionVO closeConnection(CloseConnectionDTO closeConnectionDTO) {
        log.info("client {} close connection", closeConnectionDTO.getClientId());
        commonExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sessionManager.getClient(closeConnectionDTO.getClientId()).closeConnection();
            }
        });
        return CloseConnectionVO.builder()
                .clientId(closeConnectionDTO.getClientId())
                .build();
    }

    @Override
    public void shutdown() {
        // 关闭bossGroup
        bossGroup.shutdownGracefully();

        commonExecutor.shutdown();

    }
}


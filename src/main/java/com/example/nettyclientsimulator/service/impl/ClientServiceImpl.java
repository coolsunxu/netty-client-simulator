package com.example.nettyclientsimulator.service.impl;

import com.example.nettyclientsimulator.client.Client;
import com.example.nettyclientsimulator.codec.Message;
import com.example.nettyclientsimulator.disruptor.DisruptorManager;
import com.example.nettyclientsimulator.disruptor.event.ReadTask;
import com.example.nettyclientsimulator.result.OperateResult;
import com.example.nettyclientsimulator.result.OperateStatus;
import com.example.nettyclientsimulator.service.ClientService;
import com.example.nettyclientsimulator.service.shutdown.ClientServiceShutdownHook;
import com.example.nettyclientsimulator.session.SessionManager;
import com.example.nettyclientsimulator.shutdown.ShutdownHooks;
import com.example.nettyclientsimulator.threadpool.TimingThreadPool;
import com.example.nettyclientsimulator.transport.Dispatcher;
import com.example.nettyclientsimulator.transport.Flusher;
import com.example.nettyclientsimulator.util.SerializeHelper;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author sunxu
 */
@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ShutdownHooks shutdownHooks;

    private final DisruptorManager disruptorManager;
    private final Dispatcher dispatcher;

    private final TimingThreadPool asyncExecutor;

    private final SessionManager sessionManager;


    public ClientServiceImpl(ShutdownHooks shutdownHooks, DisruptorManager disruptorManager, Dispatcher dispatcher,
                             TimingThreadPool asyncExecutor,
                             SessionManager sessionManager) {
        this.shutdownHooks = shutdownHooks;
        this.disruptorManager = disruptorManager;
        this.dispatcher = dispatcher;
        this.asyncExecutor = asyncExecutor;
        this.sessionManager = sessionManager;
    }

    @PostConstruct
    public void run() {
        shutdownHooks.add(new ClientServiceShutdownHook(this));
    }


    @Override
    public void write(String str, String clientId) {
        // 先判断 channel 是否可写，高低水位，保护系统
        sessionManager.getClient(clientId).ifPresentOrElse(client -> {
            final Channel channel = client.getChannel();
            if (channel == null || !channel.isActive()) {
                log.warn("client {} channel is closed", clientId);
                return;
            }

            if (!channel.isWritable()) {
                log.warn("client {} channel is not writable", clientId);
                return;
            }

            dispatcher.flush(new Flusher.FlushItem<>(channel, str));
        }, () -> log.warn("client {} not found", clientId));
    }

    @Override
    public void read(ReadTask readTask) {
        try {

            // 1 一些简单的同步操作
            String str = readTask.getTaskContext().toString();
            Message request = SerializeHelper.deserialize(str.getBytes(), Message.class);
            if (!Optional.ofNullable(request).isPresent()) {
                String actualClientId = sessionManager.getClient(readTask.getClientId())
                        .map(Client::getClientId)
                        .orElse(readTask.getClientId());
                log.info("client {} decode message error", actualClientId);
            }

            assert request != null;
            log.info("client {} get message {} from server", request.getId(), request.getData());

            // 2 一些异步操作，CompletableFuture，释放当前线程
            handleAsyncOperate(readTask.getClientId());

        } finally {
            // 必须释放 msg 数据
            ReferenceCountUtil.release(readTask.getTaskContext());
        }
    }

    @Override
    public void shutdown() {
        asyncExecutor.shutdown();
    }

    private void handleAsyncOperate(String clientId) {
        CompletableFuture<OperateResult> operateFuture = handleOperate(clientId, new OperateResult(OperateStatus.PUT_OK));

        operateFuture.thenAcceptAsync(putMessageResult -> {
            log.info("clientId {} async operate all complete thread name {}", clientId, Thread.currentThread().getName());
        }, this.asyncExecutor);
        return;
    }

    private CompletableFuture<OperateResult> handleOperate(String clientId, OperateResult operateResult) {
        CompletableFuture<OperateStatus> flushResultFuture = handleOperate1(clientId);
        CompletableFuture<OperateStatus> replicaResultFuture = handleOperate2(clientId);

        return flushResultFuture.thenCombine(replicaResultFuture, (flushStatus, replicaStatus) -> {
            if (flushStatus != OperateStatus.PUT_OK) {
                operateResult.setOperateStatus(flushStatus);
            }
            if (replicaStatus != OperateStatus.PUT_OK) {
                operateResult.setOperateStatus(replicaStatus);
            }
            return operateResult;
        });
    }

    private CompletableFuture<OperateStatus> handleOperate1(String clientId) {
        log.info("clientId {} async operate1 complete thread name {}", clientId, Thread.currentThread().getName());
        return CompletableFuture.completedFuture(OperateStatus.PUT_OK);
    }

    private CompletableFuture<OperateStatus> handleOperate2(String clientId) {
        log.info("clientId {} async operate2 complete thread name {}", clientId, Thread.currentThread().getName());
        return CompletableFuture.completedFuture(OperateStatus.PUT_OK);
    }

}

package com.example.nettyclientsimulator.nettyhandler;

import com.alibaba.fastjson.JSON;
import com.example.nettyclientsimulator.codec.Message;
import com.example.nettyclientsimulator.disruptor.DisruptorManager;
import com.example.nettyclientsimulator.util.MathUtil;
import com.example.nettyclientsimulator.util.NettyHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;


/**
 * @author sunxu
 */

@ChannelHandler.Sharable
@Component
@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private final DisruptorManager disruptorManager;

    public HeartbeatHandler(DisruptorManager disruptorManager) {
        this.disruptorManager = disruptorManager;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                String clientId = NettyHelper.getClientID(ctx.channel());
                log.info("clientId {} need send heartbeat message to server {}", clientId, LocalTime.now());

                Message request = Message.builder()
                        .id(clientId)
                        .data("heartbeat")
                        .build();
                String str = JSON.toJSONString(request);
                int index = MathUtil.getHashCode(clientId) % disruptorManager.getWriteDisruptorQueueSize();
                disruptorManager.writePublish(clientId, str, index);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("get exception {}", cause.getMessage());
        ctx.channel().close();
    }

}

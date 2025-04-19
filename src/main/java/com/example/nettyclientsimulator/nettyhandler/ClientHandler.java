package com.example.nettyclientsimulator.nettyhandler;

import com.example.nettyclientsimulator.disruptor.DisruptorManager;
import com.example.nettyclientsimulator.util.MathUtil;
import com.example.nettyclientsimulator.util.NettyHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author sunxu
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final DisruptorManager disruptorManager;

    public ClientHandler(DisruptorManager disruptorManager) {
        this.disruptorManager = disruptorManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String clientId = NettyHelper.getClientID(ctx.channel());
        int index = MathUtil.getHashCode(clientId) % disruptorManager.getReadDisruptorQueueSize();
        disruptorManager.readPublish(clientId, msg, index);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.debug("read message complete");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("read message err", cause);
        ctx.close();
    }

}


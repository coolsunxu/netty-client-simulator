package com.example.nettyclientsimulator.client;

import com.example.nettyclientsimulator.nettyhandler.ClientHandler;
import com.example.nettyclientsimulator.nettyhandler.HeartbeatHandler;
import com.example.nettyclientsimulator.task.Qoe;
import com.example.nettyclientsimulator.util.NettyHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;

/**
 * @author sunxu
 */
@Slf4j
public class Client {

    private final String clientId;
    private Channel channel;
    private final Bootstrap bs;
    private final boolean useSsl;

    private final Qoe qoe = new Qoe();

    public Client(String clientId,
                  int keepAlive,
                  EventLoopGroup bossGroup,
                  ClientHandler clientHandler,
                  StringEncoder stringEncoder,
                  StringDecoder stringDecoder,
                  HeartbeatHandler heartbeatHandler,
                  boolean useTls) {
        this.clientId = clientId;
        this.useSsl = useTls;
        bs = new Bootstrap();
        bs.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(io.netty.channel.socket.SocketChannel socketChannel) throws SSLException {
                        if (useSsl) {
                            SslContext sslContext = SslContextBuilder.forClient().build();
                            socketChannel.pipeline().addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
                        }
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, keepAlive, 0));
                        socketChannel.pipeline().addLast(heartbeatHandler);
                        // 可以采用多种编码方式，protobuf string等等
                        // protobuf可以参考亿级流量Java高并发与网络编程实战
                        socketChannel.pipeline().addLast(stringEncoder);
                        socketChannel.pipeline().addLast(stringDecoder);
                        socketChannel.pipeline().addLast(clientHandler);
                    }
                });
    }

    public synchronized void connect(String host, int port) throws InterruptedException {

        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture cf = bs.connect(host, port).sync();
        if (cf.isSuccess()) {
            log.info("client {} connect to server succeed", this.clientId);
            this.channel = cf.channel();
            NettyHelper.setClientID(this.channel, this.clientId);
        } else {
            log.info("client {} connect to server failed", this.clientId);
        }

    }

    public String getClientId() {
        return this.clientId;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public boolean connected() {
        return channel != null && channel.isActive();
    }

    public void closeConnection() {
        if(this.channel != null && this.channel.isActive()) {
            this.channel.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("close connection succeed");
                }
            });
        }
    }

    public void setQoe(boolean enable,int interval,Long lastActiveTime) {
        this.qoe.setEnable(enable);
        this.qoe.setInterval(interval);
        this.qoe.setLastActiveTime(lastActiveTime);
    }

    public Qoe getQoe() {
        return this.qoe;
    }

    public void shutdown() {
        if (this.channel != null) {
            this.channel.close();
        }
    }

}

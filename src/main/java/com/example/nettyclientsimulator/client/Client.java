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
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 客户端
 *
 * @author sunxu
 */
@Slf4j
public class Client {

    private final String clientId;
    private Channel channel;
    private final Bootstrap bs;
    private final boolean useSsl;
    private final SslContext sslContext;

    private final Qoe qoe = new Qoe();

    /**
     * 创建客户端（使用共享的 SslContext）
     *
     * @param clientId         客户端ID
     * @param keepAlive        心跳间隔（秒）
     * @param bossGroup        事件循环组
     * @param clientHandler    客户端处理器
     * @param stringEncoder    字符串编码器
     * @param stringDecoder    字符串解码器
     * @param heartbeatHandler 心跳处理器
     * @param useTls           是否使用 TLS
     * @param sslContext       共享的 SslContext（可为 null）
     */
    public Client(String clientId,
                  int keepAlive,
                  EventLoopGroup bossGroup,
                  ClientHandler clientHandler,
                  StringEncoder stringEncoder,
                  StringDecoder stringDecoder,
                  HeartbeatHandler heartbeatHandler,
                  boolean useTls,
                  SslContext sslContext) {
        this(clientId, keepAlive, bossGroup, clientHandler, stringEncoder, stringDecoder,
                heartbeatHandler, useTls, sslContext, 5000);
    }

    /**
     * 创建客户端（使用共享的 SslContext，支持超时配置）
     *
     * @param clientId          客户端ID
     * @param keepAlive         心跳间隔（秒）
     * @param bossGroup         事件循环组
     * @param clientHandler     客户端处理器
     * @param stringEncoder     字符串编码器
     * @param stringDecoder     字符串解码器
     * @param heartbeatHandler  心跳处理器
     * @param useTls            是否使用 TLS
     * @param sslContext        共享的 SslContext（可为 null）
     * @param connectTimeoutMs  连接超时时间（毫秒）
     */
    public Client(String clientId,
                  int keepAlive,
                  EventLoopGroup bossGroup,
                  ClientHandler clientHandler,
                  StringEncoder stringEncoder,
                  StringDecoder stringDecoder,
                  HeartbeatHandler heartbeatHandler,
                  boolean useTls,
                  SslContext sslContext,
                  int connectTimeoutMs) {
        this.clientId = clientId;
        this.useSsl = useTls;
        this.sslContext = sslContext;
        bs = new Bootstrap();
        bs.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        if (useSsl && sslContext != null) {
                            socketChannel.pipeline().addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
                        }
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, keepAlive, 0));
                        socketChannel.pipeline().addLast(heartbeatHandler);
                        // 可以采用多种编码方式，protobuf string等等
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
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close().addListener((ChannelFutureListener) future -> 
                log.info("Client {} connection closed, success: {}", clientId, future.isSuccess())
            );
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

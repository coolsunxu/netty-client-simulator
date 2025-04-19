package com.example.nettyclientsimulator.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author sunxu
 */
public class NettyHelper {
    private static final String ATTR_CLIENTID = "clientId";

    private static final AttributeKey<Object> ATTR_KEY_CLIENTID = AttributeKey.valueOf(ATTR_CLIENTID);

    public static void setClientID(Channel channel, String clientId) {
        channel.attr(NettyHelper.ATTR_KEY_CLIENTID).set(clientId);
    }

    public static String getClientID(Channel channel) {
        return (String) channel.attr(NettyHelper.ATTR_KEY_CLIENTID).get();
    }

}

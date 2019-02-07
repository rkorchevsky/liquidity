package com.onytrex.liquidity.vertx.channel;

import com.onytrex.liquidity.common.CurrencyPair;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.impl.ws.WebSocketFrameImpl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class Channels {

    private static final Logger logger = LoggerFactory.getLogger(Channels.class.getName());

    private static final Map<String, WebSocketChannel<CurrencyPair, ServerWebSocket>> CHANNELS = new HashMap<>();

    private Channels() {
        throw new AssertionError("No com.onytrex.liquidity.vertx.channel.Channels instances for you!");
    }

    public static void writeFrame(ServerWebSocket webSocket, String frame) throws IllegalStateException {
        webSocket.writeFrame(new WebSocketFrameImpl(frame));
    }

    public static void removeChannel(Class<? extends WebSocketChannel> tClass, ServerWebSocket webSocket) {
        final var channel = CHANNELS.get(tClass.getName());

        if (nonNull(channel))
            channel.removeChannel(webSocket);
    }

    public static WebSocketChannel channel(Class<? extends WebSocketChannel> tClass) {
        final var className = tClass.getName();

        if (CHANNELS.containsKey(className))
            return CHANNELS.get(className);

        return null;
    }

    public static Collection<WebSocketChannel<CurrencyPair, ServerWebSocket>> channelCollection() {
        return CHANNELS.values();
    }

    @SafeVarargs
    public static void registerChannels(WebSocketChannel<CurrencyPair, ServerWebSocket>... webSocketChannels) {
        for (var channel: webSocketChannels)
            CHANNELS.put(channel.getClass().getName(), channel);

        logger.info("Registered " + webSocketChannels.length + " channels");
    }

    public static void registerChannel(Class<? extends WebSocketChannel> tClass, WebSocketChannel<CurrencyPair, ServerWebSocket> channel) {
        CHANNELS.put(tClass.getName(), channel);
    }
}

package com.onytrex.liquidity.binance.subscriber.streamer;

import com.onytrex.liquidity.binance.domain.event.TradePayloadEvent;
import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.vertx.channel.Channels;
import com.onytrex.liquidity.vertx.channel.cache.TradeChannelCacheWs;
import com.onytrex.liquidity.common.JsonUtil;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public final class TradeEventStreamer {

    private static final Logger logger = LoggerFactory.getLogger(TradeEventStreamer.class.getName());

    private final JsonUtil JSON_UTIL;

    private final TradeChannelCacheWs TRADE_STREAM_CHANNEL_CACHE;

    private CurrencyPair symbolCode;

    public static TradeEventStreamer newInstance() {
        return new TradeEventStreamer();
    }

    private TradeEventStreamer() {
        this.JSON_UTIL = JsonUtil.getInstance();
        this.TRADE_STREAM_CHANNEL_CACHE = (TradeChannelCacheWs) Channels.channel(TradeChannelCacheWs.class);
    }

    public TradeEventStreamer forSymbol(int symbol) {
        this.symbolCode = CurrencyPair.getByCode(symbol);
        return this;
    }

    public void streamViaWebSocket(TradePayloadEvent tradePayloadEvents) {
        final var wsClientSet = TRADE_STREAM_CHANNEL_CACHE.get(symbolCode);

        if (!wsClientSet.isEmpty()) {

            String json = null;

            for (var websocketClient: wsClientSet) {

                if (json == null) json = JSON_UTIL.toJson(tradePayloadEvents);

                try {
                    Channels.writeFrame(websocketClient, json);
                } catch (IllegalStateException e) {
                    logger.error("Client [" + websocketClient.remoteAddress() + "] already closed connection via websocket");
                    Channels.removeChannel(TradeChannelCacheWs.class, websocketClient);
                }
            }
        }
    }
}

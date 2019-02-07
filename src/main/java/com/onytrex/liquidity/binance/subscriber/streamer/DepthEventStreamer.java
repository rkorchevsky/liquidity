package com.onytrex.liquidity.binance.subscriber.streamer;

import com.onytrex.liquidity.binance.domain.market.OrderBookEntry;
import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.model.DepthEventModel;
import com.onytrex.liquidity.vertx.channel.Channels;
import com.onytrex.liquidity.vertx.channel.cache.DepthChannelCacheWs;
import com.onytrex.liquidity.common.JsonUtil;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;

public final class DepthEventStreamer {

    private static final Logger logger = LoggerFactory.getLogger(DepthEventStreamer.class.getName());

    private final DepthChannelCacheWs DEPTH_CHANNEL_CACHE;
    private final JsonUtil jsonUtil;

    public static DepthEventStreamer newInstance() {
        return new DepthEventStreamer();
    }

    private DepthEventStreamer() {
        jsonUtil = JsonUtil.getInstance();
        DEPTH_CHANNEL_CACHE = (DepthChannelCacheWs) Channels.channel(DepthChannelCacheWs.class);
    }

    public void streamViaWebSocket(CurrencyPair pair, List<OrderBookEntry> asksList, List<OrderBookEntry> bidsList) {
        Map<String, String> asks = new TreeMap<>(),
                            bids = new TreeMap<>();

        for (int i = 0; i < asksList.size(); i++) {
            final var order = asksList.get(i);
            asks.put(order.getPrice(), order.getQty());
        }

        for (int i = 0; i < bidsList.size(); i++) {
            final var order = bidsList.get(i);
            bids.put(order.getPrice(), order.getQty());
        }

        DepthEventModel model = null;

        for (var entry: DEPTH_CHANNEL_CACHE.entrySet()) {
            final var key = entry.getKey();
            final var wsClientSet = entry.getValue();

            if (wsClientSet.isEmpty()) continue;

            if (key.code() == pair.code()) {

                if (isNull(model))
                    model = new DepthEventModel(pair.description(), asks, bids);

                final var json = jsonUtil.toJson(model);
                for (var websocketClient: wsClientSet) {
                    try {
                        Channels.writeFrame(websocketClient, json);
                    } catch (IllegalStateException e) {
                        logger.error("Client [" + websocketClient.remoteAddress() + "] already closed connection via websocket");
                        Channels.removeChannel(DepthChannelCacheWs.class, websocketClient);
                    }
                }
            }
        }
    }
}

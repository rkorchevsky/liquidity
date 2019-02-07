package com.onytrex.liquidity.binance.subscriber.streamer;

import com.onytrex.liquidity.binance.domain.event.AllMarketTickersEvent;
import com.onytrex.liquidity.vertx.channel.Channels;
import com.onytrex.liquidity.vertx.channel.WebSocketChannel;
import com.onytrex.liquidity.vertx.channel.cache.AllMarketChannelCacheWs;

import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;

import static java.util.Objects.isNull;

public final class AllMarketEventStreamer {

    private static final Logger logger = LoggerFactory.getLogger(AllMarketEventStreamer.class.getName());

    private AllMarketChannelCacheWs WS_CLIENT_SET;

    public static AllMarketEventStreamer newInstance() {
        return new AllMarketEventStreamer();
    }

    public AllMarketEventStreamer withCache(Class<? extends WebSocketChannel> tClass) {
        WS_CLIENT_SET = (AllMarketChannelCacheWs) Channels.channel(tClass);
        return this;
    }

    public final void streamViaWebSocket(List<AllMarketTickersEvent> list) {
        JsonArray shaded = null;
        String json = null;

        if (WS_CLIENT_SET.isEmpty()) return;

        for (var websocketClient: WS_CLIENT_SET) {

            if (isNull(shaded)) {
                shaded = new JsonArray(list);
                json = shaded.toString();
            }

            try {
                Channels.writeFrame(websocketClient, json);
            } catch (IllegalStateException e) {
                logger.error("Client [" + websocketClient.remoteAddress() + "] already closed connection via websocket");
                Channels.removeChannel(AllMarketChannelCacheWs.class, websocketClient);
            }
        }
        shaded.clear();
    }
}

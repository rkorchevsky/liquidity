package com.onytrex.liquidity.vertx.channel.cache;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.vertx.channel.WebSocketChannel;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.impl.ConcurrentHashSet;

public class AllMarketChannelCacheWs extends ConcurrentHashSet<ServerWebSocket> implements WebSocketChannel<CurrencyPair, ServerWebSocket> {

    AllMarketChannelCacheWs() {
        super();
    }

    @Override
    public void removeChannel(ServerWebSocket channel) {
        remove(channel);
    }

    @Override
    public void putChannel(CurrencyPair key, ServerWebSocket channel) {
        add(channel);
    }
}

package com.onytrex.liquidity.vertx.channel.cache;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.vertx.channel.WebSocketChannel;

import io.vertx.core.http.ServerWebSocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DepthChannelCacheWs extends ConcurrentHashMap<CurrencyPair, Set<ServerWebSocket>> implements WebSocketChannel<CurrencyPair, ServerWebSocket> {

    DepthChannelCacheWs() {
        super();
    }

    @Override
    public void removeChannel(ServerWebSocket channel) {
        for (var entrySet: entrySet()) {
            for (var value: entrySet.getValue()) {
                if (value.equals(channel)) {
                    entrySet.getValue().remove(value);
                    return;
                }
            }
        }
    }

    @Override
    public void putChannel(CurrencyPair pair, ServerWebSocket channel) {
        computeIfPresent(pair, (key, value) -> {
            value.add(channel);
            return value;
        });
    }
}

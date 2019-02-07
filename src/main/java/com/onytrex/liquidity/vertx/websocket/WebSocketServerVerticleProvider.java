package com.onytrex.liquidity.vertx.websocket;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.onytrex.liquidity.vertx.channel.Channels;
import com.onytrex.liquidity.vertx.channel.WebSocketChannel;
import com.onytrex.liquidity.vertx.channel.cache.AllMarketChannelCacheWs;
import com.onytrex.liquidity.vertx.channel.cache.DepthChannelCacheWs;
import com.onytrex.liquidity.vertx.channel.cache.TradeChannelCacheWs;

import com.typesafe.config.Config;

public class WebSocketServerVerticleProvider implements Provider<WebSocketServerVerticle> {

    private final Config config;
    private final DepthChannelCacheWs depthChannelCacheWs;
    private final TradeChannelCacheWs tradeChannelCacheWs;
    private final AllMarketChannelCacheWs allMarketChannelCacheWs;

    @Inject
    public WebSocketServerVerticleProvider(Config config, DepthChannelCacheWs depthChannelCacheWs, TradeChannelCacheWs tradeChannelCacheWs, AllMarketChannelCacheWs allMarketChannelCacheWs) {
        this.config = config;
        this.depthChannelCacheWs = depthChannelCacheWs;
        this.tradeChannelCacheWs = tradeChannelCacheWs;
        this.allMarketChannelCacheWs = allMarketChannelCacheWs;

        registerInitial(depthChannelCacheWs, tradeChannelCacheWs, allMarketChannelCacheWs);
    }

    private void registerInitial(WebSocketChannel... webSocketChannels) {
        //noinspection unchecked
        Channels.registerChannels(webSocketChannels);
    }

    @Override
    public WebSocketServerVerticle get() {
        return new WebSocketServerVerticle(config, depthChannelCacheWs, tradeChannelCacheWs, allMarketChannelCacheWs);
    }
}

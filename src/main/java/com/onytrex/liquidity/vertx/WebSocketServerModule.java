package com.onytrex.liquidity.vertx;

import com.google.inject.AbstractModule;

import com.onytrex.liquidity.common.ConfigModule;
import com.onytrex.liquidity.vertx.channel.cache.*;
import com.onytrex.liquidity.vertx.websocket.WebSocketServerVerticle;
import com.onytrex.liquidity.vertx.websocket.WebSocketServerVerticleProvider;
import com.onytrex.liquidity.vertx.websocket.WebSocketServerLifeCycle;

public class WebSocketServerModule extends AbstractModule {

    private final ConfigModule configModule;

    public WebSocketServerModule(ConfigModule configModule) {
        this.configModule = configModule;
    }

    @Override
    protected void configure() {
        install(configModule);
        bind(DepthChannelCacheWsProvider.class).asEagerSingleton();
        bind(DepthChannelCacheWs.class).toProvider(DepthChannelCacheWsProvider.class);
        bind(TradeChannelCacheWsProvider.class).asEagerSingleton();
        bind(TradeChannelCacheWs.class).toProvider(TradeChannelCacheWsProvider.class);
        bind(AllMarketChannelCacheWsProvider.class).asEagerSingleton();
        bind(AllMarketChannelCacheWs.class).toProvider(AllMarketChannelCacheWsProvider.class);
        bind(WebSocketServerLifeCycle.class).asEagerSingleton();
        bind(WebSocketServerVerticleProvider.class).asEagerSingleton();
        bind(WebSocketServerVerticle.class).asEagerSingleton();
    }
}

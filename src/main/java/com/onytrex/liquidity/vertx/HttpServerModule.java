package com.onytrex.liquidity.vertx;

import com.google.inject.AbstractModule;

import com.onytrex.liquidity.common.ConfigModule;
import com.onytrex.liquidity.vertx.http.HttpServerVerticle;
import com.onytrex.liquidity.vertx.http.HttpServerVerticleProvider;
import com.onytrex.liquidity.vertx.http.HttpServerLifeCycle;
import com.onytrex.liquidity.stocks.StockConsumerModule;

public class HttpServerModule extends AbstractModule {
    private final ConfigModule configModule;
    private final StockConsumerModule stockConsumerModule;

    public HttpServerModule(ConfigModule configModule, StockConsumerModule stockConsumerModule) {
        this.configModule = configModule;
        this.stockConsumerModule = stockConsumerModule;
    }

    @Override
    protected void configure() {
        install(configModule);
        install(stockConsumerModule);
        bind(HttpServerLifeCycle.class).asEagerSingleton();
        bind(HttpServerVerticleProvider.class);
        bind(HttpServerVerticle.class);
    }
}

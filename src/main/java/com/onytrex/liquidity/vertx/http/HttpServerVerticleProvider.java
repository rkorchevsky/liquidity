package com.onytrex.liquidity.vertx.http;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.onytrex.liquidity.stocks.pool.BinanceConsumerPool;

import com.typesafe.config.Config;

public class HttpServerVerticleProvider implements Provider<HttpServerVerticle> {

    private final Config config;

    private final BinanceConsumerPool binanceConsumerPool;

    @Inject
    public HttpServerVerticleProvider(Config config, BinanceConsumerPool binanceConsumerPool) {
        this.config = config;
        this.binanceConsumerPool = binanceConsumerPool;
    }

    @Override
    public HttpServerVerticle get() {
        return new HttpServerVerticle(config, binanceConsumerPool);
    }
}

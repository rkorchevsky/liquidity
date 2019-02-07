package com.onytrex.liquidity.binance;

import com.google.inject.AbstractModule;
import com.onytrex.liquidity.binance.api.ApiClientFactory;

public class BinanceClientModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApiClientFactory.class).toProvider(ApiClientFactory::newInstance).asEagerSingleton();
    }
}

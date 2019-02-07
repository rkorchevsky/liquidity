package com.onytrex.liquidity.stocks;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.onytrex.liquidity.binance.BinanceClientModule;
import com.onytrex.liquidity.kafka.KafkaModule;
import com.onytrex.liquidity.common.ConfigModule;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyInitializer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class StockConsumerModule extends AbstractModule {
    private final ConfigModule configModule;
    //workaround
    private final Config config = ConfigFactory.load();
    private final BinanceClientModule binanceClientModule;

    public StockConsumerModule(ConfigModule configModule, KafkaModule kafkaModule, BinanceClientModule binanceClientModule) {
        this.configModule = configModule;
        this.binanceClientModule = binanceClientModule;
    }

    public StockConsumerModule(ConfigModule configModule, BinanceClientModule binanceClientModule) {
        this.configModule = configModule;
        this.binanceClientModule = binanceClientModule;
    }

    @Override
    protected void configure() {
        install(configModule);
        install(binanceClientModule);
        bind(BinanceConsumerLifeCycle.class).asEagerSingleton();
        bind(StockCurrencyInitializer.class).asEagerSingleton();
        final var stockConsumerServiceMultibinder = Multibinder.newSetBinder(binder(), StockConsumerService.class);
        for (var config : config.getConfigList("stocks")) {
            final var stock = StockResource.valueOf(config.getString("name"));
            bind(stock.getClz()).asEagerSingleton();
            stockConsumerServiceMultibinder.addBinding().to(stock.getClz());
        }
    }
}

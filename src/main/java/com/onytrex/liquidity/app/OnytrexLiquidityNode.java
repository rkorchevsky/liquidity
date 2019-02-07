package com.onytrex.liquidity.app;

import com.google.inject.Injector;
import com.onytrex.liquidity.binance.BinanceClientModule;
import com.onytrex.liquidity.common.AbstractLifeCycleComponent;
import com.onytrex.liquidity.common.ConfigModule;
import com.onytrex.liquidity.common.LifeCycleService;
import com.onytrex.liquidity.common.ModulesBuilder;
import com.onytrex.liquidity.vertx.HttpServerModule;
import com.onytrex.liquidity.vertx.WebSocketServerModule;
import com.onytrex.liquidity.stocks.StockConsumerModule;

public class OnytrexLiquidityNode extends AbstractLifeCycleComponent {

    private final ModulesBuilder modulesBuilder = new ModulesBuilder();
    private LifeCycleService lifeCycleService;

    @Override
    protected void doStart() {
        final var configModule = new ConfigModule();
        final var binanceClientModule = new BinanceClientModule();
        final var stockConsumerModule = new StockConsumerModule(configModule, binanceClientModule);
        modulesBuilder.add(configModule);

        modulesBuilder.add(new OnytrexModule(configModule));
        modulesBuilder.add(new HttpServerModule(configModule, stockConsumerModule));
        modulesBuilder.add(new WebSocketServerModule(configModule));
        modulesBuilder.add(binanceClientModule);

        Injector injector = modulesBuilder.createInjector();
        lifeCycleService = injector.getInstance(LifeCycleService.class);
        lifeCycleService.start();
    }

    @Override
    protected void doStop() {
        lifeCycleService.stop();
    }

    @Override
    protected void doClose() {
        lifeCycleService.close();
    }
}

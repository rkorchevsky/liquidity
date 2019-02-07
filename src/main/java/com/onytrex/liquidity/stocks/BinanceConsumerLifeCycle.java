package com.onytrex.liquidity.stocks;

import com.google.inject.Inject;

import com.onytrex.liquidity.common.AbstractLifeCycleComponent;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Set;

public class BinanceConsumerLifeCycle extends AbstractLifeCycleComponent {

    private static final Logger logger = LoggerFactory.getLogger(BinanceConsumerLifeCycle.class.getName());

    private final Set<StockConsumerService> consumers;

    @Inject
    public BinanceConsumerLifeCycle(Set<StockConsumerService> consumers) { this.consumers = consumers; }

    @Override
    protected void doStart() {
        logger.info("Initializing before running the application: creating stock consumer pool");
        for (var consumerService : consumers) {
            consumerService.startConsumers();
        }
    }

    @Override
    protected void doStop() {
        logger.warn("Stop not implemented");
    }

    @Override
    protected void doClose() {
        for(var consumerService: consumers) {
            try {
                consumerService.shutdownNow();
            } catch (Exception e) {
                logger.error("Error while closing stock consumer", e);
            }
        }
    }

}

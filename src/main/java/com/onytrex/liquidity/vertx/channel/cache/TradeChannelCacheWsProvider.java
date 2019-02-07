package com.onytrex.liquidity.vertx.channel.cache;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyInitializer;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class TradeChannelCacheWsProvider implements Provider<TradeChannelCacheWs> {

    private static final TradeChannelCacheWs CACHE;
    private static final Logger logger = LoggerFactory.getLogger(TradeChannelCacheWsProvider.class.getName());
    private final StockCurrencyInitializer stockCurrencyInitializer;

    static {
        CACHE = new TradeChannelCacheWs();
    }

    @Inject
    public TradeChannelCacheWsProvider(StockCurrencyInitializer stockCurrencyInitializer) {
        this.stockCurrencyInitializer = stockCurrencyInitializer;
        init();
    }

    private void init() {
        for (var symbol : stockCurrencyInitializer.getAllCurrencyPairs())
            CACHE.put(symbol, new ConcurrentHashSet<>());

        logger.info("Trade channel cache initialized");
    }

    public static TradeChannelCacheWs channelCache() {
        return CACHE;
    }

    @Override
    public TradeChannelCacheWs get() {
        return CACHE;
    }
}

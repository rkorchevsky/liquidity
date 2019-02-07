package com.onytrex.liquidity.vertx.channel.cache;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyInitializer;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class DepthChannelCacheWsProvider implements Provider<DepthChannelCacheWs> {

        private static final DepthChannelCacheWs CACHE;
        private static final Logger logger = LoggerFactory.getLogger(DepthChannelCacheWsProvider.class.getName());

        private final StockCurrencyInitializer stockCurrencyInitializer;

        static {
            CACHE = new DepthChannelCacheWs();
        }

        @Inject
        public DepthChannelCacheWsProvider(StockCurrencyInitializer stockCurrencyInitializer) {
            this.stockCurrencyInitializer = stockCurrencyInitializer;
            init();
        }

        private void init() {
            for (var symbol : stockCurrencyInitializer.getAllCurrencyPairs())
                CACHE.put(symbol, new ConcurrentHashSet<>());

            logger.info("Depth channel cache initialized");
        }

        public static DepthChannelCacheWs channelCache() {
            return CACHE;
        }

        @Override
        public DepthChannelCacheWs get() {
            return CACHE;
        }
}

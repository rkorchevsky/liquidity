package com.onytrex.liquidity.vertx.channel.cache;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.inject.Provider;

public class AllMarketChannelCacheWsProvider implements Provider<AllMarketChannelCacheWs> {

    private static final AllMarketChannelCacheWs CACHE;

    private static final Logger logger = LoggerFactory.getLogger(AllMarketChannelCacheWsProvider.class.getName());

    static {
        CACHE = new AllMarketChannelCacheWs();
        logger.info("All market channel cache initialized");
    }

    @Override
    public AllMarketChannelCacheWs get() {
        return CACHE;
    }
}

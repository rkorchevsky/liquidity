package com.onytrex.liquidity.stocks;

import com.onytrex.liquidity.stocks.pool.BinanceConsumerPool;

public enum StockResource {

    BINANCE(),
    HUOBI(),
    KRAKEN(),
    LMAX();

    private final Class<? extends StockConsumerService> clz;

    StockResource() {
        this.clz = BinanceConsumerPool.class;
    }

    public Class<? extends StockConsumerService> getClz() {
        return clz;
    }
}

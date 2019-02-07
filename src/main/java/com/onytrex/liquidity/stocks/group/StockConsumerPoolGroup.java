package com.onytrex.liquidity.stocks.group;

import com.onytrex.liquidity.stocks.StockConsumerService;
import com.onytrex.liquidity.stocks.StockResource;

import java.util.Optional;

// common group of all consumer resources: Binance, Huobi, LMAX ant etc...
public interface StockConsumerPoolGroup<T> {

    Optional<StockConsumerService> selectByResource(StockResource resource);

    void mergeResourcesStream(StockResource... resources);
}

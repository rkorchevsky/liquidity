package com.onytrex.liquidity.kafka.handler;

import com.onytrex.liquidity.common.CurrencyPair;

import java.util.concurrent.ConcurrentNavigableMap;

@FunctionalInterface
public interface DataUpdatedCallback {

    void process(CurrencyPair symbol, ConcurrentNavigableMap<Long, Long> asks, ConcurrentNavigableMap<Long, Long> bids);
}

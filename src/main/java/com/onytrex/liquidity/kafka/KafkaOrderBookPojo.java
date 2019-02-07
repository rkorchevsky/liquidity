package com.onytrex.liquidity.kafka;

import com.onytrex.liquidity.model.PriceQuantityPair;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

class KafkaOrderBookPojo {

    private final NavigableMap<BigDecimal, BigDecimal> asks;
    private final NavigableMap<BigDecimal, BigDecimal> bids;

    public KafkaOrderBookPojo() {
        this.asks = new TreeMap<>();
        this.bids = new TreeMap<>();
    }

    public void fillAsks(PriceQuantityPair pair) {
        asks.put(pair.getPrice(), pair.getQuantity());
    }

    public void fillBids(PriceQuantityPair pair) {
        bids.put(pair.getPrice(), pair.getQuantity());
    }
}

package com.onytrex.liquidity.model;

import java.util.Map;

public class DepthEventModel {

    private final String pair;
    private final Map<String, String> asks;
    private final Map<String, String> bids;

    public DepthEventModel(String pair, Map<String, String> asks, Map<String, String> bids) {
        this.pair = pair;
        this.asks = asks;
        this.bids = bids;
    }

    public String getPair() { return pair; }

    public Map<String, String> getAsks() {
        return asks;
    }

    public Map<String, String> getBids() {
        return bids;
    }
}

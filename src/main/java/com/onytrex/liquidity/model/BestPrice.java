package com.onytrex.liquidity.model;

import io.vertx.core.json.JsonObject;

public class BestPrice extends JsonObject {

    private static final String bestPrice = "bestPrice";

    public BestPrice(final String bestPriceVal, int time) {
        put(bestPrice, bestPriceVal);
        put("time", time);
    }

    public String getBestPrice() {
        return getString("bestPrice");
    }

    public int getTime() {
        return getInteger("time");
    }

}
package com.onytrex.liquidity.model;

import com.onytrex.liquidity.common.CurrencyPair;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.NavigableMap;

public class DepthSnapshotModel extends JsonObject {

    private CurrencyPair symbol;
    private final String asks = "asks";
    private final String bids = "bids";

    private DepthSnapshotModel(CurrencyPair symbol) {
        this.symbol = symbol;
        put("symbol", symbol.description());
    }

    public DepthSnapshotModel() { }

    public JsonObject getAsks() {
        return getJsonObject(asks);
    }

    public void setAsks(NavigableMap<BigDecimal, BigDecimal> asksMap) {
        put(asks, asksMap);
    }

    public JsonObject getBids() {
        return getJsonObject(bids);
    }

    public void setBids(NavigableMap<BigDecimal, BigDecimal> bidsMap) {
        put(bids, bidsMap);
    }

    public static DepthSnapshotModel snapshotForSymbol(CurrencyPair symbol) {
        return new DepthSnapshotModel(symbol);
    }

    @Override
    public String toString() {
        return "DepthSnapshotModel{" +
                "symbol='" + symbol + '\'' +
                ", asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}

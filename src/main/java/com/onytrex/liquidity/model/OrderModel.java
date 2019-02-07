package com.onytrex.liquidity.model;

import io.vertx.core.json.JsonObject;

public class OrderModel extends JsonObject {

    public String getSymbol() {
        return getString(symbol);
    }

    public String getOrderId() {
        return getString(orderId);
    }

    public String getStatus() {
        return getString(status);
    }

    public String getType() {
        return getString(type);
    }

    private static final String symbol = "symbol";

    private static final String orderId = "orderId";

    private static final String status = "status";

    private static final String type = "type";

    private OrderModel(String symbolParam) {
        put(symbol, "pair");
        put(orderId, "non-uid-orderId-ID");
        put(status, "FILLED");
        put(type, "LIMIT");
    }

    public static OrderModel newResponse(String symbol) {
        return new OrderModel(symbol);
    }

}

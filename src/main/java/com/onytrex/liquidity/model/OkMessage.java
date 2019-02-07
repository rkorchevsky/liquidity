package com.onytrex.liquidity.model;

import io.vertx.core.json.JsonObject;

public class OkMessage extends JsonObject {

    private static final String MESSAGE = "message";

    public OkMessage(String message) {
        put(MESSAGE, message);
    }

    public String getMessage() {
        return getString(MESSAGE);
    }
}

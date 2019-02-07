
package com.onytrex.liquidity.common;

import io.vertx.core.json.Json;

public class JsonUtil {

    private static final JsonUtil INSTANCE = new JsonUtil();

    private JsonUtil() { }

    public static JsonUtil getInstance() {
        return INSTANCE;
    }

    public <T> T toPojo(String json, Class<T> tClass) {
        return Json.decodeValue(json, tClass);
    }

    public String toJson(Object obj) {
        return Json.encode(obj);
    }

}
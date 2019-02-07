package com.onytrex.liquidity.vertx.util;

import io.vertx.core.MultiMap;

import static java.util.Objects.nonNull;

public class HttpRequestUtil {

    public static final String SYMBOL = "symbol";
    public static final String AMOUNT = "amount";
    public static final String limit = "limit";
    public static final String PRICE_FOR = "price-for";

    public static final String CURRENCY_1_FIELD = "currency1";
    public static final String CURRENCY_2_FIELD = "currency2";

    public static final String BEST_PRICE_SELL_PARAM = "sell";
    public static final String BEST_PRICE_BUY_PARAM = "buy";

    public static boolean isRequestCorrectFor(HttpRequestCategory category, MultiMap params) {
        final var symbolParam = params.get(SYMBOL);

        switch (category) {

            case SNAPSHOT:
                return nonNull(symbolParam);

            case BEST_PRICE:
                final var priceForParam = params.get(PRICE_FOR);
                final var amountParam = params.get(AMOUNT);

                return nonNull(symbolParam) && nonNull(priceForParam) && nonNull(amountParam);

            default:
                return false;
        }
    }
}

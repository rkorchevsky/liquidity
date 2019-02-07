package com.onytrex.liquidity.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CurrencyPair {

    private final int code;
    private final String description;

    public static final CurrencyPair DEFAULT = new CurrencyPair(0, "");

    private static final Map<Integer, CurrencyPair> codeToPair = new HashMap<>();

    public static CurrencyPair getByCode(int code) {
        return codeToPair.get(code);
    }

    public static void register(CurrencyPair currencyPair) {
        codeToPair.put(currencyPair.code, currencyPair);
    }

    public static Map<Integer, CurrencyPair> currencyPairs() {
        return codeToPair;
    }

    public CurrencyPair(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyPair that = (CurrencyPair) o;
        return code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "CurrencyPair{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}

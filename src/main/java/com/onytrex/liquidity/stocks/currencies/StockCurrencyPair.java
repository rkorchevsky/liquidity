package com.onytrex.liquidity.stocks.currencies;

import com.onytrex.liquidity.common.CurrencyPair;

public class StockCurrencyPair {
    private final CurrencyPair currencyPair;
    private final String stringRepresentation;

    public StockCurrencyPair(CurrencyPair currencyPair, String stringRepresentation) {
        this.currencyPair = currencyPair;
        this.stringRepresentation = stringRepresentation;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }

}

package com.onytrex.liquidity.stocks.currencies;

import com.google.inject.Inject;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.stocks.StockResource;

import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockCurrencyInitializer {

    private final Config config;
    private static final Map<StockResource, List<StockCurrencyPair>> registered = new HashMap<>();

    private void register(StockResource stock, StockCurrencyPair stockCurrencyPair) {
        registered.putIfAbsent(stock, new ArrayList<>());
        registered.get(stock).add(stockCurrencyPair);
    }

    public List<StockCurrencyPair> getAll() {
        return registered.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    public List<CurrencyPair> getAllCurrencyPairs() {
        return getAll().stream().map(StockCurrencyPair::getCurrencyPair).collect(Collectors.toList());
    }
    public Map<StockResource, List<StockCurrencyPair>> registered() {
        return registered;
    }

    @Inject
    public StockCurrencyInitializer(Config config) {
        this.config = config;
        init();
    }

    void init() {
        for (var stockConf: config.getConfigList("stocks")) {
            final var stock = StockResource.valueOf(stockConf.getString("name"));
            for (var currConf : stockConf.getConfigList("curr_pair")) {
                final var descr = currConf.getString("description");
                final var code = currConf.getInt("code");
                final var newPair = new CurrencyPair(code, descr);
                CurrencyPair.register(newPair);
                this.register(stock, new StockCurrencyPair(newPair, descr));
            }
        }
    }
}

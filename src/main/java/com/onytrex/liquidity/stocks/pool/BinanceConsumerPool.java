package com.onytrex.liquidity.stocks.pool;

import com.google.inject.Inject;

import com.onytrex.liquidity.binance.api.ApiClientFactory;
import com.onytrex.liquidity.binance.subscriber.BinanceAllMarketEventSubscriber;
import com.onytrex.liquidity.binance.subscriber.BinanceDepthEventSubscriber;
import com.onytrex.liquidity.binance.subscriber.BinanceTradeEventSubscriber;
import com.onytrex.liquidity.stocks.StreamType;
import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.stocks.StockResource;
import com.onytrex.liquidity.stocks.StockConsumerService;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyInitializer;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyPair;
import com.onytrex.liquidity.stocks.StockSubscriber;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.*;

public class BinanceConsumerPool implements StockConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceConsumerPool.class.getName());

    private final List<StockCurrencyPair> symbols;

    private final Map<CurrencyPair, BinanceDepthEventSubscriber> depthEventSubscriberMap = new HashMap<>();
    private final Map<CurrencyPair, BinanceTradeEventSubscriber> tradeEventSubscriberMap = new HashMap<>();

    private final List<BinanceDepthEventSubscriber> depthEventSubscribers = new ArrayList<>();
    private final List<BinanceTradeEventSubscriber> tradeEventSubscribers = new ArrayList<>();

    private BinanceAllMarketEventSubscriber allMarketEventSubscriber;

    private final ApiClientFactory apiClientFactory;

    @Inject
    public BinanceConsumerPool(StockCurrencyInitializer stockCurrencyInitializer, ApiClientFactory apiClientFactory) {
        this.symbols = stockCurrencyInitializer.registered().get(StockResource.BINANCE);
        this.apiClientFactory= apiClientFactory;
    }

    public void startConsumers() {
        logger.info("Starting Binance consumers");

        this.allMarketEventSubscriber = new BinanceAllMarketEventSubscriber();
        try {
            for (var symbol: symbols) {
                final var binanceDepthEventSubscriber = new BinanceDepthEventSubscriber(apiClientFactory, symbol);
                binanceDepthEventSubscriber.subscribe();
                depthEventSubscribers.add(binanceDepthEventSubscriber);

                final var binanceTradeEventSubscriber = new BinanceTradeEventSubscriber(apiClientFactory, symbol);
                binanceTradeEventSubscriber.subscribe();
                tradeEventSubscribers.add(binanceTradeEventSubscriber);

                this.tradeEventSubscriberMap.put(symbol.getCurrencyPair(), binanceTradeEventSubscriber);
                this.depthEventSubscriberMap.put(symbol.getCurrencyPair(), binanceDepthEventSubscriber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("All consumers started");
    }

    @Override
    public void shutdown(StockCurrencyPair symbol, StreamType type) throws Exception {
        switch (type) {
            case DEPTH_EVENT:
                final var depthEventSubscriber = depthEventSubscriberMap.get(symbol.getCurrencyPair());
                depthEventSubscriber.close();
                break;
            case TRADE_EVENT:
                final var tradeEventSubscriber = tradeEventSubscriberMap.get(symbol.getCurrencyPair());
                tradeEventSubscriber.close();
                break;
        }
    }

    @Override
    public void shutdownNow() throws Exception {
        for (var sub: depthEventSubscribers) sub.close();

        for (var sub: tradeEventSubscribers) sub.close();

        allMarketEventSubscriber.close();
    }

    @Override
    public void shutdownNow(StreamType type) throws Exception {
        switch (type) {
            case DEPTH_EVENT:
                for (var sub: depthEventSubscribers)
                    sub.close();
                break;
            case TRADE_EVENT:
                for (var sub: tradeEventSubscribers)
                    sub.close();
                break;
            case ALL_MARKET_EVENT:
                allMarketEventSubscriber.close();
                break;
        }
    }

    @Override
    public Optional<StockSubscriber<?>> getBySymbol(CurrencyPair symbol, StreamType type) {
        StockSubscriber<?> value = null;
        switch (type) {
            case DEPTH_EVENT:
               value = depthEventSubscriberMap.get(symbol);
               break;
            case TRADE_EVENT:
                value = tradeEventSubscriberMap.get(symbol);
                break;
            case ALL_MARKET_EVENT:
                value = allMarketEventSubscriber;
                break;
        }
        return Optional.ofNullable(value);
    }

    @Override
    public List<StockCurrencyPair> symbols() {
        return symbols;
    }
}

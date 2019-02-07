package com.onytrex.liquidity.stocks;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyPair;

import java.util.List;
import java.util.Optional;

public interface StockConsumerService {

    Optional<?> getBySymbol(CurrencyPair symbol, StreamType t);

    void startConsumers();

    void shutdown(StockCurrencyPair symbol, StreamType t) throws Exception;

    void shutdownNow(StreamType type) throws Exception;

    void shutdownNow() throws Exception;

    List<StockCurrencyPair> symbols();

}

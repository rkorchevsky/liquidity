package com.onytrex.liquidity.stocks.pool;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.stocks.StockConsumerService;
import com.onytrex.liquidity.stocks.StreamType;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyPair;

import java.util.List;
import java.util.Optional;

public class KrakenConsumerPool implements StockConsumerService {

    @Override
    public Optional<?> getBySymbol(CurrencyPair symbol, StreamType t) {
        return Optional.empty();
    }

    @Override
    public void startConsumers() {

    }

    @Override
    public void shutdown(StockCurrencyPair symbol, StreamType t) throws Exception {

    }

    @Override
    public void shutdownNow(StreamType type) throws Exception {

    }

    @Override
    public void shutdownNow() throws Exception {

    }

    @Override
    public List<StockCurrencyPair> symbols() {
        return null;
    }
}

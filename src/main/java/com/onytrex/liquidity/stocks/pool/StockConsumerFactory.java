package com.onytrex.liquidity.stocks.pool;

import com.google.inject.Provider;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.stocks.StockConsumerService;
import com.onytrex.liquidity.kafka.handler.DataUpdatedCallback;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.List;

class StockConsumerFactory implements Provider<BinanceConsumerPool> {

    private DataUpdatedCallback dataUpdatedCallback;

    private BinanceConsumerPool binanceConsumerPool;

    public StockConsumerService newInstance(String className, List<CurrencyPair> currencyPairs) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (dataUpdatedCallback == null) {
            throw new IllegalStateException("Callback wasn't initialized!");
        }

        //noinspection unchecked
        Class<StockConsumerService> clz = (Class<StockConsumerService>) Class.forName(className);
        Constructor<StockConsumerService> stockConsumerConstructor = clz.getConstructor(List.class, DataUpdatedCallback.class);
        binanceConsumerPool = (BinanceConsumerPool) stockConsumerConstructor.newInstance(currencyPairs, dataUpdatedCallback);
        return binanceConsumerPool;
    }

    public void setDataUpdatedCallback(DataUpdatedCallback dataUpdatedCallback) {
        this.dataUpdatedCallback = dataUpdatedCallback;
    }

    /**
     * Temporary workaround
     */
    @Override
    public BinanceConsumerPool get() {
        return binanceConsumerPool;
    }
}

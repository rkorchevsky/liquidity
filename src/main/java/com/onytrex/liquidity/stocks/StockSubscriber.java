package com.onytrex.liquidity.stocks;

import java.io.Closeable;

public interface StockSubscriber<T> extends Closeable {

    void addListener(T listener);

    void subscribe();
}

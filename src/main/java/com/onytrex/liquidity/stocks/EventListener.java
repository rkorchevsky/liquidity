package com.onytrex.liquidity.stocks;

public interface EventListener<T> {

    void fireEvent(T event);
}

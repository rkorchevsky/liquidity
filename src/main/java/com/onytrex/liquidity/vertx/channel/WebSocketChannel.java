package com.onytrex.liquidity.vertx.channel;


public interface WebSocketChannel<T, C> {

    void removeChannel(C channel);

    void putChannel(T key, C channel);

}

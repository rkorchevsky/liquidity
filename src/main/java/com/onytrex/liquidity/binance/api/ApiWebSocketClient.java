package com.onytrex.liquidity.binance.api;


import com.onytrex.liquidity.binance.domain.event.*;
import com.onytrex.liquidity.binance.domain.market.CandlestickInterval;

import java.io.Closeable;
import java.util.List;

/**
 * API data streaming façade, supporting streaming of events through web sockets.
 */
public interface ApiWebSocketClient extends Closeable {

    /**
     * Open a new web socket to receive {@link DepthEvent depthEvents} on a callback.
     *
     * @param symbols  market (one or coma-separated) symbol(s) to subscribe to
     * @param callback the callback to call on new events
     * @return a {@link Closeable} that allows the underlying web socket to be closed.
     */
    Closeable onDepthEvent(String symbols, ApiCallback<DepthEvent> callback);

    /**
     * Open a new web socket to receive {@link CandlestickEvent candlestickEvents} on a callback.
     *
     * @param symbols  market (one or coma-separated) symbol(s) to subscribe to
     * @param interval the interval of the candles tick events required
     * @param callback the callback to call on new events
     * @return a {@link Closeable} that allows the underlying web socket to be closed.
     */
    Closeable onCandlestickEvent(String symbols, CandlestickInterval interval, ApiCallback<CandlestickEvent> callback);

    /**
     * Open a new web socket to receive {@link AggTradeEvent aggTradeEvents} on a callback.
     *
     * @param symbols  market (one or coma-separated) symbol(s) to subscribe to
     * @param callback the callback to call on new events
     * @return a {@link Closeable} that allows the underlying web socket to be closed.
     */
    Closeable onAggTradeEvent(String symbols, ApiCallback<AggTradeEvent> callback);

    /**
     * Open a new web socket to receive {@link UserDataUpdateEvent userDataUpdateEvents} on a callback.
     *
     * @param listenKey the listen key to subscribe to.
     * @param callback  the callback to call on new events
     * @return a {@link Closeable} that allows the underlying web socket to be closed.
     */
    Closeable onUserDataUpdateEvent(String listenKey, ApiCallback<UserDataUpdateEvent> callback);

    /**
     * Open a new web socket to receive {@link AllMarketTickersEvent allMarketTickersEvents} on a callback.
     *
     * @param callback the callback to call on new events
     * @return a {@link Closeable} that allows the underlying web socket to be closed.
     */
    Closeable onAllMarketTickersEvent(ApiCallback<List<AllMarketTickersEvent>> callback);

    /**
     * The Trade Streams push raw trade information; each trade has a unique buyer and seller.
     *
     */
    Closeable onTradeStreams(String symbol, ApiCallback<TradePayloadEvent> callback);

    /**
     * @deprecated This method is no longer functional. Please use the returned {@link Closeable} from any of the other methods to close the web socket.
     */
    @Deprecated
    void close();
}

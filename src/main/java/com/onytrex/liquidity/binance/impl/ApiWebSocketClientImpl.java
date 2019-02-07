package com.onytrex.liquidity.binance.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.onytrex.liquidity.binance.api.ApiCallback;
import com.onytrex.liquidity.binance.api.ApiWebSocketClient;
import com.onytrex.liquidity.binance.constant.ApiConstants;
import com.onytrex.liquidity.binance.domain.event.*;
import com.onytrex.liquidity.binance.domain.market.CandlestickInterval;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Binance API WebSocket client implementation using OkHttp.
 */
public class ApiWebSocketClientImpl implements ApiWebSocketClient, Closeable {

    private final OkHttpClient client;

    public ApiWebSocketClientImpl(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Closeable onDepthEvent(String symbols, ApiCallback<DepthEvent> callback) {
        final String channel = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(s -> String.format("%s@depth", s))
                .collect(Collectors.joining("/"));
        return createNewWebSocket(channel, new ApiWebSocketListener<>(callback, DepthEvent.class));
    }

    @Override
    public Closeable onCandlestickEvent(String symbols, CandlestickInterval interval, ApiCallback<CandlestickEvent> callback) {
        final String channel = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(s -> String.format("%s@kline_%s", s, interval.getIntervalId()))
                .collect(Collectors.joining("/"));
        return createNewWebSocket(channel, new ApiWebSocketListener<>(callback, CandlestickEvent.class));
    }

    public Closeable onAggTradeEvent(String symbols, ApiCallback<AggTradeEvent> callback) {
        final String channel = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .map(s -> String.format("%s@aggTrade", s))
                .collect(Collectors.joining("/"));
        return createNewWebSocket(channel, new ApiWebSocketListener<>(callback, AggTradeEvent.class));
    }

    public Closeable onUserDataUpdateEvent(String listenKey, ApiCallback<UserDataUpdateEvent> callback) {
        return createNewWebSocket(listenKey, new ApiWebSocketListener<>(callback, UserDataUpdateEvent.class));
    }

    public Closeable onAllMarketTickersEvent(ApiCallback<List<AllMarketTickersEvent>> callback) {
        final String channel = "!ticker@arr";
        return createNewWebSocket(channel, new ApiWebSocketListener<>(callback, new TypeReference<>() {
        }));
    }

    public Closeable onTradeStreams(String symbol, ApiCallback<TradePayloadEvent> callback) {
        final var channel = String.format("%s@trade", symbol);
        return createNewWebSocket(channel, new ApiWebSocketListener<>(callback, TradePayloadEvent.class));
    }

    /**
     * @deprecated This method is no longer functional. Please use the returned {@link Closeable} from any of the other methods to close the web socket.
     */
    @Override
    public void close() {
    }

    private Closeable createNewWebSocket(String channel, ApiWebSocketListener<?> listener) {
        String streamingUrl = String.format("%s/%s", ApiConstants.WS_API_BASE_URL, channel);
        Request request = new Request.Builder().url(streamingUrl).build();
        final WebSocket webSocket = client.newWebSocket(request, listener);
        return () -> {
            final int code = 1000;
            listener.onClosing(webSocket, code, null);
            webSocket.close(code, null);
            listener.onClosed(webSocket, code, null);
        };
    }
}

package com.onytrex.liquidity.binance.subscriber;

import com.onytrex.liquidity.binance.api.ApiCallback;
import com.onytrex.liquidity.binance.api.ApiClientFactory;
import com.onytrex.liquidity.binance.api.ApiWebSocketClient;
import com.onytrex.liquidity.binance.domain.event.TradePayloadEvent;
import com.onytrex.liquidity.binance.subscriber.streamer.TradeEventStreamer;
import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyPair;
import com.onytrex.liquidity.stocks.StockSubscriber;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class BinanceTradeEventSubscriber implements StockSubscriber<TradePayloadEvent> {

    private static final Logger logger = LoggerFactory.getLogger(BinanceTradeEventSubscriber.class.getName());

    private final StockCurrencyPair currencyPair;
    private final CurrencyPair symbolPair;
    private final ApiWebSocketClient wsClient;
    private final TradeEventStreamer tradeEventStreamer;

    private final TradePayloadCallback callback = new TradePayloadCallback();
    private final ConcurrentLinkedDeque<TradePayloadEvent> TRADE_BUFFER;
    private Buffer buffer;

    private Closeable websocket;

    public BinanceTradeEventSubscriber(ApiClientFactory apiClientFactory, StockCurrencyPair currencyPair) {
        this.websocket = apiClientFactory.newWebSocketClient();
        this.currencyPair = currencyPair;
        this.symbolPair = currencyPair.getCurrencyPair();
        this.tradeEventStreamer = TradeEventStreamer.newInstance().forSymbol(symbolPair.code());
        this.wsClient = ApiClientFactory.newInstance().newWebSocketClient();
        this.TRADE_BUFFER = new ConcurrentLinkedDeque<>();

        subscribe();
    }

    @Override
    public void addListener(TradePayloadEvent listener) { }

    @Override
    public void subscribe() {
        logger.info("Subscribing to Binance API trade stream for " + symbolPair.description());

        //noinspection MismatchedQueryAndUpdateOfCollection
        final var orders = new ArrayList<TradePayloadEvent>(1);
        callback.setHandler(orders::add);
        this.websocket = wsClient.onTradeStreams(currencyPair.getStringRepresentation(), callback);
        applyOrders();
    }

    private void applyOrders() {
        final Consumer<TradePayloadEvent> streamEventsAccepter = tradePayloadEvents -> streamEventsViaWebSocket(tradePayloadEvents.buyOrSell());

        final Consumer<TradePayloadEvent> accepter = event -> callback.setHandler(streamEventsAccepter);

        callback.setHandler(accepter);
    }

    private void streamEventsViaWebSocket(TradePayloadEvent tradePayloadEvents) {
        TRADE_BUFFER.add(tradePayloadEvents.buyOrSell());
        final var LIMIT = 20;
        if (TRADE_BUFFER.size() > LIMIT) {
                TRADE_BUFFER.pollFirst();
                buffer = Json.encodeToBuffer(TRADE_BUFFER);
        }

        tradeEventStreamer.streamViaWebSocket(tradePayloadEvents);
    }

    public Buffer getBuffer() { return buffer; }

    @Override
    public void close() throws IOException {
        websocket.close();
        logger.info("Binance trade event subscriber for " + symbolPair.description() + " closed");
    }

    private class TradePayloadCallback implements ApiCallback<TradePayloadEvent> {
        private final AtomicReference<Consumer<TradePayloadEvent>> handler = new AtomicReference<>();

        @Override
        public void onResponse(TradePayloadEvent response) {
            try {
                handler.get().accept(response);
            } catch (final Exception e) {
                logger.error("Exception caught processing trade stream event " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Throwable cause) {
            logger.error("WS connection to trade event stream failed. Reconnecting. cause:" + cause.getMessage());
            subscribe();
        }

        void setHandler(Consumer<TradePayloadEvent> handler) {
            this.handler.set(handler);
        }
    }
}

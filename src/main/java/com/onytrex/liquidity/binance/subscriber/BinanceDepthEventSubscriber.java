package com.onytrex.liquidity.binance.subscriber;

import com.onytrex.liquidity.model.DepthSnapshotModel;
import com.onytrex.liquidity.stocks.EventListener;
import com.onytrex.liquidity.binance.api.ApiCallback;
import com.onytrex.liquidity.binance.api.ApiClientFactory;
import com.onytrex.liquidity.binance.api.ApiRestClient;
import com.onytrex.liquidity.binance.api.ApiWebSocketClient;
import com.onytrex.liquidity.binance.domain.event.DepthEvent;
import com.onytrex.liquidity.binance.domain.market.OrderBookEntry;
import com.onytrex.liquidity.binance.subscriber.streamer.DepthEventStreamer;
import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.common.Quotation;
import com.onytrex.liquidity.model.DepthSnapshotConstant;
import com.onytrex.liquidity.stocks.currencies.StockCurrencyPair;
import com.onytrex.liquidity.stocks.StockSubscriber;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

import java.math.BigDecimal;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.onytrex.liquidity.common.Quotation.ASK;
import static com.onytrex.liquidity.common.Quotation.BID;

public class BinanceDepthEventSubscriber implements StockSubscriber<EventListener<DepthEvent>> {

    private static final Logger logger = LoggerFactory.getLogger(BinanceDepthEventSubscriber.class.getName());
    private static final BigDecimal FEE = BigDecimal.valueOf(1.02);

    private final EnumMap<Quotation, NavigableMap<BigDecimal, BigDecimal>> depthCache;
    private final DepthEventStreamer DEPTH_EVENT_STREAMER;
    private final StockCurrencyPair currencyPair;
    private final ApiRestClient restClient;
    private final ApiWebSocketClient wsClient;
    private final DepthSnapshotModel depthSnapshotModel;
    private final WsCallback wsCallback = new WsCallback();
    private final int LIMIT = 20;

    private Buffer snapshotBuffer;

    private volatile Closeable webSocket;
    private final CurrencyPair symbolPair;
    private long lastUpdateId = -1;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<EventListener<DepthEvent>> listeners = new ArrayList<>();

    public BinanceDepthEventSubscriber(ApiClientFactory apiClientFactory, StockCurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
        this.symbolPair = currencyPair.getCurrencyPair();
        this.depthCache = new EnumMap<>(Quotation.class);

        this.depthSnapshotModel = DepthSnapshotModel.snapshotForSymbol(symbolPair);

        this.wsClient = apiClientFactory.newWebSocketClient();
        this.restClient = apiClientFactory.newRestClient();
        this.DEPTH_EVENT_STREAMER = DepthEventStreamer.newInstance();
    }

    @Override
    public void subscribe() {
        logger.info("Subscribing to Binance API depth events for " + currencyPair.getCurrencyPair().description());
        // 1. Subscribe to depth events and cache any events that are received.
        final var pendingDeltas = new ArrayList<DepthEvent>(1);
        wsCallback.setHandler(pendingDeltas::add);
        this.webSocket = wsClient.onDepthEvent(currencyPair.getStringRepresentation(), wsCallback);

        // 2. Get a depthSnapshotModel from the rest endpoint and use it to build your initial depth cache.
        initializeDepthCache();

        // 3. &  4. handled in here.
        applyPendingDeltas(pendingDeltas);
    }

    /**
     * 2. Initializes the depth cache by getting a depthSnapshotModel from the REST API.
     */
    private void initializeDepthCache() {
        final var orderBook = restClient.getOrderBook(currencyPair.getStringRepresentation().toUpperCase(), 20);

        this.lastUpdateId = orderBook.getLastUpdateId();

        final var asks = new ConcurrentSkipListMap<BigDecimal, BigDecimal>();
        for (var ask: orderBook.getAsks())
            asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getQty()));

        final var bids = new ConcurrentSkipListMap<BigDecimal, BigDecimal>();
        for (var bid: orderBook.getBids())
            bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getQty()));

        depthCache.put(ASK, asks);
        depthCache.put(BID, bids);
    }

    private void applyPendingDeltas(final List<DepthEvent> pendingDeltas) {
        final Consumer<DepthEvent> updateOrderBook = newEvent -> {
            if (newEvent.getFinalUpdateId() > lastUpdateId) {
                lastUpdateId = newEvent.getFinalUpdateId();

                final var eventAsks = newEvent.getAsks();
                final var eventBids = newEvent.getBids();

                updateOrderBook(getAsks(), eventAsks, ASK);
                updateOrderBook(getBids(), eventBids, BID);

                if (getAsks().size() > LIMIT)
                    cut(getAsks(), ASK);

                if (getBids().size() > LIMIT)
                    cut(getBids(), BID);

                snapshot(DepthSnapshotConstant.UPDATE);

                DEPTH_EVENT_STREAMER.streamViaWebSocket(symbolPair, newEvent.getAsks(), newEvent.getBids());
            }
        };

        final Consumer<DepthEvent> drainPending = newEvent -> {
            pendingDeltas.add(newEvent);

            for (var e: pendingDeltas)
                if (e.getFinalUpdateId() > lastUpdateId)
                    updateOrderBook.accept(e);


            wsCallback.setHandler(updateOrderBook);
        };
        wsCallback.setHandler(drainPending);
    }

    private void cut(NavigableMap<BigDecimal, BigDecimal> map, Quotation quotation) {
        var count = 0;
        final var subtraction = map.size() - LIMIT;

        switch (quotation) {
            case ASK:
                if (map.size() > LIMIT) {
                    final var asksSub = map.size() - LIMIT;
                    for (int i = 0; i < asksSub; i++) {
                        final var last = map.lastEntry();
                        map.remove(last.getKey());
                    }
                }
            break;
            case BID:
            for (var entries : map.entrySet()) {
                if (count++ == subtraction) {
                    final var key = entries.getKey();
                    map.headMap(key).clear();
                }
            }
            break;
        }
    }

    private NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return depthCache.get(ASK);
    }

    private NavigableMap<BigDecimal, BigDecimal> getBids() {
        return depthCache.get(BID);
    }

    public BigDecimal getBestPriceBuyWithSize(BigDecimal amount) {
        var price = BigDecimal.ZERO;
        var sum = amount;
        for (var entry: getAsks().descendingMap().entrySet())
            if (entry.getValue().compareTo(sum) >= 0) {
                price = price.add(sum.multiply(entry.getKey()));
                break;
            } else {
                price = price.add(entry.getValue()).multiply(entry.getKey());
                sum = sum.add(entry.getValue()).negate();
            }
        return price.multiply(FEE);
    }

    public BigDecimal getBestPriceSellWithSize(BigDecimal amount) {
        var price = BigDecimal.ZERO;
        var sum = amount;

        for (var entry: getBids().entrySet())
            if (entry.getValue().compareTo(sum) >= 0) {
                price = price.add(sum.multiply(entry.getKey()));
                break;
            } else {
                price = price.add(entry.getValue()).multiply(entry.getKey());
                sum = sum.add(entry.getValue().negate());
            }
        return price.multiply(FEE);
    }

    /**
     * Updates an order book (bids or asks) with a delta received from the server.
     * <p>
     * Whenever the qty specified is ZERO, it means the price should was removed from the order book.
     */
    private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries, List<OrderBookEntry> orderBookDeltas, Quotation quotation) {
        for (var orderBookDelta: orderBookDeltas) {
            final var price = new BigDecimal(orderBookDelta.getPrice());
            final var qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0)
                lastOrderBookEntries.remove(price);
            else
                if (lastOrderBookEntries.size() <= LIMIT)
                    lastOrderBookEntries.put(price, qty);
        }
        if (lastOrderBookEntries.size() > LIMIT)
            cut(lastOrderBookEntries, quotation);
    }

    public DepthSnapshotModel snapshot(DepthSnapshotConstant constant) {
        switch (constant) {
            case UPDATE:
                depthSnapshotModel.setAsks(getAsks());
                depthSnapshotModel.setBids(getBids());
                snapshotBuffer = depthSnapshotModel.toBuffer();
                break;
            case GET:
                return depthSnapshotModel;
        }
        return depthSnapshotModel;
    }

    public Buffer snapshot() {
        return snapshotBuffer;
    }

    @Override
    public void addListener(EventListener<DepthEvent> listener) {
        listeners.add(listener);
    }

    public void close() throws IOException {
        logger.info("Binance depth event subscriber for " + symbolPair.description() + " closed");
        webSocket.close();
    }

    private final class WsCallback implements ApiCallback<DepthEvent> {

        private final AtomicReference<Consumer<DepthEvent>> handler = new AtomicReference<>();

        @Override
        public void onResponse(DepthEvent depthEvent) {
            try {
                handler.get().accept(depthEvent);
            } catch (final Exception e) {
                logger.error("Exception caught processing depth event");
                e.printStackTrace(System.err);
            }
        }

        @Override
        public void onFailure(Throwable cause) {
            logger.error("WS connection to DepthEvent failed. Reconnecting. cause:" + cause.getMessage());
            subscribe();
        }

        private void setHandler(final Consumer<DepthEvent> handler) {
            this.handler.set(handler);
        }
    }
}

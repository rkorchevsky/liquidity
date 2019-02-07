package com.onytrex.liquidity.binance.subscriber;

import com.onytrex.liquidity.app.OnytrexUtil;
import com.onytrex.liquidity.binance.*;
import com.onytrex.liquidity.binance.api.ApiCallback;
import com.onytrex.liquidity.binance.api.ApiClientFactory;
import com.onytrex.liquidity.binance.api.ApiWebSocketClient;
import com.onytrex.liquidity.binance.domain.event.AllMarketTickersEvent;
import com.onytrex.liquidity.binance.subscriber.streamer.AllMarketEventStreamer;
import com.onytrex.liquidity.common.MarketCache;
import com.onytrex.liquidity.stocks.StockSubscriber;
import com.onytrex.liquidity.vertx.channel.cache.AllMarketChannelCacheWs;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.concurrent.CompletableFuture.runAsync;

public class BinanceAllMarketEventSubscriber implements StockSubscriber<AllMarketTickersEvent> {

    private static final Logger logger = LoggerFactory.getLogger(BinanceAllMarketEventSubscriber.class.getName());
    private static final Pattern PATTERN = Pattern.compile("ETHUSDT|ETHBTC|BTCUSDT|DASHBTC|LTCUSDT|LTCBTC|LTCETH|OMGETH|OMGBTC|VETUSDT|VETBTC|VETETH");
    private static final Pattern FIAT_PATTERN = Pattern.compile("ETHUSDT|BTCUSDT|LTCUSDT");

    private final ApiWebSocketClient apiWebSocketClient;
    private Closeable websocket;
    private Map<String, AllMarketTickersEvent> ALL_MARKET_24HR_CACHE;
    private Map<String, AllMarketTickersEvent> ALL_MARKET_24HR_CACHE_FIAT;
    private Collection<?> values;
    private Collection<?> valuesFiat;

    private final AllMarketEventStreamer MARKET_EVENT_STREAMER;
    private final AllMarketWsCallback allMarketWsCallback = new AllMarketWsCallback();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<AllMarketTickersEvent> listeners = new CopyOnWriteArrayList<>();
    private final List<AllMarketTickersEvent> EVENTS_CACHE = new CopyOnWriteArrayList<>();
    private final String[] SYMBOLS_BUFFER = new String[2];
    private final String[] FIAT_SYMBOLS_BUFFER = new String[3];
    private final BinanceMarketTickerStatistics statistics;
    private Buffer bufferAllMarket24Hr;
    private Buffer currentEventBuffer;
    private Buffer bufferFiatAllMarket24Hr;

    public BinanceAllMarketEventSubscriber() {
        this.statistics = BinanceMarketTickerStatistics.getInstance();
        this.apiWebSocketClient = ApiClientFactory.newInstance().newWebSocketClient();
        this.websocket = apiWebSocketClient;
        this.ALL_MARKET_24HR_CACHE_FIAT = new HashMap<>(3);
        MARKET_EVENT_STREAMER = AllMarketEventStreamer.newInstance().withCache(AllMarketChannelCacheWs.class);

        subscribe();
    }

    @Override
    public void addListener(AllMarketTickersEvent listener) { listeners.add(listener); }

    @Override
    public void subscribe() {
        logger.info("Subscribing to Binance API for all market ticker events via websocket");
        final var allMarketTickersEvents = new ArrayList<AllMarketTickersEvent>();
        allMarketWsCallback.setHandler(allMarketTickersEvents::addAll);
        this.websocket = apiWebSocketClient.onAllMarketTickersEvent(allMarketWsCallback);

        logger.info("Request via REST: get all 24 hour price statistics");
        runAsync(statistics::all24HrStatistics);

        this.ALL_MARKET_24HR_CACHE = statistics.allMarket24HrTickerCache();

        applyAllMarketTickersEvents(allMarketTickersEvents);

        values = ALL_MARKET_24HR_CACHE.values();
        valuesFiat = ALL_MARKET_24HR_CACHE_FIAT.values();
    }

    private void applyAllMarketTickersEvents(final List<AllMarketTickersEvent> allMarketTickersEvents) {
        final Consumer<List<AllMarketTickersEvent>> streamEventsAccepter = this::process;

        final Consumer<List<AllMarketTickersEvent>> drainPending = newEvent -> {
            allMarketTickersEvents.addAll(newEvent);

            process(newEvent);

            allMarketWsCallback.setHandler(streamEventsAccepter);
        };
        allMarketWsCallback.setHandler(drainPending);
    }

    private void process(List<AllMarketTickersEvent> events) {
        final var filtered = filter(events);
        processFiatPair(events);
        updateCache(filtered);

        streamEventsViaWebSocket(filtered);
    }

    private void processFiatPair(List<AllMarketTickersEvent> list) {
        final var filtered = filterFiat(list);

        for (var i = 0; i < filtered.size(); i++) {
            var eventElem = filtered.get(i);
            ALL_MARKET_24HR_CACHE_FIAT.put(eventElem.getSymbol(), eventElem);
        }

        bufferFiatAllMarket24Hr = Json.encodeToBuffer(valuesFiat);
    }

    private void updateCache(final List<AllMarketTickersEvent> events) {
        EVENTS_CACHE.clear();

        for (var i = 0; i < events.size(); i++) {
            var eventElem = events.get(i);
            ALL_MARKET_24HR_CACHE.put(eventElem.getSymbol(), eventElem);
        }

        bufferAllMarket24Hr = Json.encodeToBuffer(values);

        EVENTS_CACHE.addAll(events);
        currentEventBuffer = Json.encodeToBuffer(EVENTS_CACHE);
    }

    public Buffer getBuffer(MarketCache cache) {
        Buffer buffer = null;
        switch (cache) {
            case CURRENT_TICKER_CACHE:
                buffer = currentEventBuffer;
                break;
            case ALL_MARKET_24_HR_CACHE:
                buffer = bufferAllMarket24Hr;
                break;
            case ALL_MARKET_24_HR_CACHE_FIAT:
                buffer = bufferFiatAllMarket24Hr;
        }
        return buffer;
    }

    private List<AllMarketTickersEvent> filter(List<AllMarketTickersEvent> list) {
        final var filtered = new ArrayList<AllMarketTickersEvent>();

        for (var elem: list) {
            final var symbol = elem.getSymbol();
            if (isSymbolPresent(symbol)) {
                final var dummy = OnytrexUtil.currencyPairDelimeter(symbol, SYMBOLS_BUFFER);
                final var code = OnytrexUtil.byCurrencies(dummy[0], dummy[1]);
                final var allMarketTickersEvent = new AllMarketTickersEvent(code, dummy[0], dummy[1], symbol, elem.getCurrentDaysClosePrice(), elem.getPriceChangePercent(), elem.getHighPrice(), elem.getLowPrice(), elem.getTotalTradedBaseAssetVolume(), elem.getTotalTradedQuoteAssetVolume());
                filtered.add(allMarketTickersEvent);
            }
        }
        return filtered;
    }

    private List<AllMarketTickersEvent> filterFiat(List<AllMarketTickersEvent> list) {
        final var filteredFiatList = new ArrayList<AllMarketTickersEvent>();

        for (int i = 0; i < list.size(); i++) {
            final var elem = list.get(i);
            final var symbol = elem.getSymbol();

            if (isFiatSymbolPresent(symbol)) {
                final var dummy = OnytrexUtil.currencyPairFiatDelimeter(symbol, FIAT_SYMBOLS_BUFFER);
                final var code = OnytrexUtil.byFiatCurrencies(dummy[1], dummy[2]);
                filteredFiatList.add(new AllMarketTickersEvent(code, dummy[1], dummy[2], dummy[0], elem.getCurrentDaysClosePrice(), elem.getPriceChangePercent(), elem.getHighPrice(), elem.getLowPrice(), elem.getTotalTradedBaseAssetVolume(), elem.getTotalTradedQuoteAssetVolume()));
            }
        }
        return filteredFiatList;
    }

    private void streamEventsViaWebSocket(List<AllMarketTickersEvent> allMarketTickersEvents) {
        MARKET_EVENT_STREAMER.streamViaWebSocket(allMarketTickersEvents);
    }

    private boolean isSymbolPresent(String symbol) { return PATTERN.matcher(symbol).find(); }

    private boolean isFiatSymbolPresent(String symbol) { return FIAT_PATTERN.matcher(symbol).find(); }

    @Override
    public void close() throws IOException {
        websocket.close();
        logger.info("All market 24 hour ticker event subscriber closed");
    }

    private class AllMarketWsCallback implements ApiCallback<List<AllMarketTickersEvent>> {

        private final AtomicReference<Consumer<List<AllMarketTickersEvent>>> handler = new AtomicReference<>();

        @Override
        public void onResponse(final List<AllMarketTickersEvent> response) {
            try {
                handler.get().accept(response);
            } catch (final Exception e) {
                logger.error("Exception caught processing all market ticker stream event" + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(final Throwable cause) {
            logger.error("WS connection to AllMarketTickerEvents failed. Reconnecting. cause:" + cause.getMessage());
            subscribe();
        }

        private void setHandler(final Consumer<List<AllMarketTickersEvent>> handler) {
            this.handler.set(handler);
        }
    }
}

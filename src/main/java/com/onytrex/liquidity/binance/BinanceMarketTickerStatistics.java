package com.onytrex.liquidity.binance;

import com.onytrex.liquidity.app.OnytrexUtil;
import com.onytrex.liquidity.binance.api.ApiClientFactory;
import com.onytrex.liquidity.binance.api.ApiRestClient;
import com.onytrex.liquidity.binance.domain.event.AllMarketTickersEvent;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class BinanceMarketTickerStatistics {

    private static final Logger logger = LoggerFactory.getLogger(BinanceMarketTickerStatistics.class.getName());

    private final ScheduledExecutorService scheduledExecutorService;
    private final ApiRestClient apiRestClient;

    private static final long TEN_MINUTES_IN_MILLIS = 600000L;
    private static final Map<String, AllMarketTickersEvent> CACHE = new HashMap<>();
    private static final Pattern PATTERN = Pattern.compile("ETHUSDT|ETHBTC|BTCUSDT|DASHBTC|LTCUSDT|LTCBTC|LTCETH|OMGETH|OMGBTC|VETUSDT|VETBTC|VETETH");

    private BinanceMarketTickerStatistics() {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.apiRestClient = ApiClientFactory.newInstance().newRestClient();
    }

    public static BinanceMarketTickerStatistics getInstance() {
        return new BinanceMarketTickerStatistics();
    }

    public void all24HrStatistics() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("Running task at " + new Date(System.currentTimeMillis()) + " for all 24 hour market data request");
            CACHE.clear();
            final var symbolsBuffer = new String[2];
            final var update = apiRestClient.getAll24HrPriceStatistics();
            for (int i = 0; i < update.size(); i++) {
                final var tickerStatistic = update.get(i);
                final var symbol = tickerStatistic.getSymbol();

                if (isPresented(symbol)) {
                    OnytrexUtil.currencyPairDelimeter(symbol, symbolsBuffer);

                    final var code = OnytrexUtil.byCurrencies(symbolsBuffer[0], symbolsBuffer[1]);
                    CACHE.put(symbol, new AllMarketTickersEvent(code, symbolsBuffer[0], symbolsBuffer[1], symbol, tickerStatistic.getPrevClosePrice(), tickerStatistic.getPriceChangePercent(), tickerStatistic.getHighPrice(), tickerStatistic.getLowPrice(), tickerStatistic.getVolume(), tickerStatistic.getQuoteVolume()));
                }}
        }, 0, TEN_MINUTES_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    public Map<String, AllMarketTickersEvent> allMarket24HrTickerCache() {
        return CACHE;
    }

    private boolean isPresented(String symbol) {
        return PATTERN.matcher(symbol).find();
    }
}

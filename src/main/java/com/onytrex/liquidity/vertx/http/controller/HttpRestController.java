package com.onytrex.liquidity.vertx.http.controller;

import com.onytrex.liquidity.app.OnytrexUtil;
import com.onytrex.liquidity.binance.subscriber.BinanceAllMarketEventSubscriber;
import com.onytrex.liquidity.binance.subscriber.BinanceDepthEventSubscriber;
import com.onytrex.liquidity.binance.subscriber.BinanceTradeEventSubscriber;
import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.common.JsonUtil;
import com.onytrex.liquidity.common.MarketCache;
import com.onytrex.liquidity.model.BestPrice;
import com.onytrex.liquidity.model.OkMessage;
import com.onytrex.liquidity.model.OrderModel;
import com.onytrex.liquidity.stocks.StockConsumerService;
import com.onytrex.liquidity.stocks.StreamType;
import com.onytrex.liquidity.vertx.util.HttpRequestCategory;
import com.onytrex.liquidity.vertx.util.HttpRequestUtil;
import com.onytrex.liquidity.vertx.util.HttpResponseUtil;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.math.BigDecimal;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

public class HttpRestController {

    private static final Logger logger = LoggerFactory.getLogger(HttpRestController.class.getName());

    public static final String HTTP_HEALTH_PATH = "/";
    public static final String HTTP_SNAPSHOT_PATH = "/api/snapshot";
    public static final String HTTP_ORDER_PATH = "/api/order";
    public static final String HTTP_BEST_PRICE_PATH = "/api/best-price";
    public static final String HTTP_CURRENCY_PAIR_ON_RENDERING_PATH = "/api/currency-pair";
    public static final String HTTP_TICKER_PATH = "/api/ticker";
    public static final String HTTP_TICKER_24HR_PATH = "/api/ticker/24hr";
    public static final String HTTP_TICKER_FIAT_24HR_PATH = HTTP_TICKER_24HR_PATH + "/fiat";
    public static final String HTTP_TRADE_PATH = "/api/trades";
    public static final String CURRENCY_PAIR = JsonUtil.getInstance().toJson(OnytrexUtil.currencyPairsWithCode());

    public static final Buffer OK_JSON_OBJECT = new OkMessage("OK").toBuffer();

    private StockConsumerService pool;

    private HttpRestController() {}

    public static HttpRestController controller() { return new HttpRestController(); }

    public void consumerPool(StockConsumerService pool) { this.pool = pool; }

    public void handleHealthCheck(RoutingContext ctx) {
        final var request = ctx.request();

        if (request.method() != HttpMethod.GET)
            HttpResponseUtil.methodNotAllowed(request);

        HttpResponseUtil.ok(request, OK_JSON_OBJECT);
    }

    public void handleOnRenderingRequest(RoutingContext ctx) {
        final var request = ctx.request();
        if (request.method() != HttpMethod.GET) {
            HttpResponseUtil.methodNotAllowed(request);
            return;
        }

        final var response = request.response();
        HttpResponseUtil.fillHeader(response.headers(), 0);
        response.end(CURRENCY_PAIR);
    }

    public void handleTickerRequest(RoutingContext ctx) {
        final var request = ctx.request();
        if (request.method() != HttpMethod.GET) {
            HttpResponseUtil.methodNotAllowed(request);
            return;
        }

        final var monad = pool.getBySymbol(CurrencyPair.DEFAULT, StreamType.ALL_MARKET_EVENT);
        if (monad.isPresent()) {
            final var response = request.response();
            final var subscriber = (BinanceAllMarketEventSubscriber) monad.get();
            final var buffer = subscriber.getBuffer(MarketCache.CURRENT_TICKER_CACHE);
            HttpResponseUtil.fillHeader(response.headers(), buffer.length());
            response.end(buffer);
        }
    }

    public void handle24HrAllMarketTicker(RoutingContext ctx) {
        final var request = ctx.request();
        if (request.method() != HttpMethod.GET) {
            HttpResponseUtil.methodNotAllowed(request);
            return;
        }

        final var monad = pool.getBySymbol(CurrencyPair.DEFAULT, StreamType.ALL_MARKET_EVENT);
        if (monad.isPresent()) {
            final var response = request.response();
            final var subscriber = (BinanceAllMarketEventSubscriber) monad.get();
            final var buffer = subscriber.getBuffer(MarketCache.ALL_MARKET_24_HR_CACHE);
            HttpResponseUtil.fillHeader(response.headers(), buffer.length());
            response.end(buffer);
        }
    }

    public void handleFiat24HrAllMarketTicker(RoutingContext ctx) {
        final var request = ctx.request();
        if (request.method() != HttpMethod.GET) {
            HttpResponseUtil.methodNotAllowed(request);
            return;
        }

        final var monad = pool.getBySymbol(CurrencyPair.DEFAULT, StreamType.ALL_MARKET_EVENT);
        if (monad.isPresent()) {
            final var response = request.response();
            final var subscriber = (BinanceAllMarketEventSubscriber) monad.get();
            final var buffer = subscriber.getBuffer(MarketCache.ALL_MARKET_24_HR_CACHE_FIAT);
            HttpResponseUtil.fillHeader(response.headers(), buffer.length());
            response.end(buffer);
        }
    }

    public void handleTradeBuffer(RoutingContext ctx) {
        final var request = ctx.request();

        if (request.method() == HttpMethod.OPTIONS) {
            HttpResponseUtil.ok(request, OK_JSON_OBJECT);
            return;
        }

        if (request.method() != HttpMethod.POST) {
            HttpResponseUtil.methodNotAllowed(request);
            return;
        }

        request.bodyHandler(body -> {
            JsonObject json;
            String currency1, currency2;
            try {
                json = body.toJsonObject();
                currency1 = json.getString(HttpRequestUtil.CURRENCY_1_FIELD);
                currency2 = json.getString(HttpRequestUtil.CURRENCY_2_FIELD);

                if (isNull(currency1) || isNull(currency2))
                    throw new IllegalArgumentException();
            } catch (final Exception e) {
                HttpResponseUtil.badRequestParameter(request);
                return;
            }

            final var symbol = OnytrexUtil.byCurrencies(currency1, currency2);

            final var stockPool = pool.getBySymbol(CurrencyPair.getByCode(symbol), StreamType.TRADE_EVENT);
            if (stockPool.isPresent()) {
                final var response = request.response();
                final var cache = (BinanceTradeEventSubscriber) stockPool.get();
                final var cacheBuffer = cache.getBuffer();

                HttpResponseUtil.fillHeader(response.headers(), cacheBuffer.length());
                response.end(cacheBuffer);
            } else {
                HttpResponseUtil.nofFound(request, "Not found orders for symbol '" + currency1 + "/" + currency2 + "'");
            }
        });
    }

    public void handleSnapshotRequest(RoutingContext ctx) {
        final var params = ctx.queryParams();
        final var request = ctx.request();

        if (HttpRequestUtil.isRequestCorrectFor(HttpRequestCategory.SNAPSHOT, params)) {
            CurrencyPair symbol;
            try {
                symbol = CurrencyPair.getByCode(parseInt(params.get(HttpRequestUtil.SYMBOL)));
            } catch (NumberFormatException e) {
                logger.error("Incorrect input data type for 'symbol' param: expected int, received " + params.get(HttpRequestUtil.SYMBOL));
                HttpResponseUtil.badRequestParameter(request);
                return;
            }

            final var stockPool = pool.getBySymbol(symbol, StreamType.DEPTH_EVENT);
            if (stockPool.isPresent()) {
                final var response = request.response();
                final var cache = (BinanceDepthEventSubscriber) stockPool.get();
                HttpResponseUtil.fillHeader(response.headers(), 0);
                final var jsonSnapshot = cache.snapshot();
                response.end(jsonSnapshot);
            } else {
                HttpResponseUtil.nofFound(request, "Not found snapshot for a symbol (code) " + params.get(HttpRequestUtil.SYMBOL));
            }
            return;
        }
        HttpResponseUtil.badRequestParameter(request);
    }

    public void handleBestPriceRequest(RoutingContext ctx) {
        final var request = ctx.request();
        final var params = ctx.queryParams();

        if (HttpRequestUtil.isRequestCorrectFor(HttpRequestCategory.BEST_PRICE, params)) {
            CurrencyPair symbol;
            try {
                symbol = CurrencyPair.getByCode(parseInt(params.get(HttpRequestUtil.SYMBOL)));
            } catch (NumberFormatException e) {
                logger.error("Incorrect input data type for 'symbol' param: expected int, received");
                HttpResponseUtil.badRequestParameter(request);
                return;
            }

            final var priceFor = params.get(HttpRequestUtil.PRICE_FOR);
            final var amount = params.get(HttpRequestUtil.AMOUNT);
            final var stockPool = pool.getBySymbol(symbol, StreamType.DEPTH_EVENT);

            if (stockPool.isPresent()) {
                final var response = request.response();
                final var cache = (BinanceDepthEventSubscriber) stockPool.get();
                String best = null;

                switch (priceFor) {
                    case HttpRequestUtil.BEST_PRICE_BUY_PARAM:
                        best = cache.getBestPriceBuyWithSize(new BigDecimal(amount)).toPlainString();
                        break;
                    case HttpRequestUtil.BEST_PRICE_SELL_PARAM:
                        best = cache.getBestPriceSellWithSize(new BigDecimal(amount)).toPlainString();
                        break;
                }
                HttpResponseUtil.fillHeader(response.headers(), 0);
                response.end(new BestPrice(best, 300).toBuffer());
                return;
            } else {
                HttpResponseUtil.nofFound(request, "Not found best price for symbol '" + params.get(HttpRequestUtil.SYMBOL) + "'");
            }
            return;
        }
        HttpResponseUtil.badRequestParameter(request);
    }

    public void handleOrderRequest(RoutingContext ctx) {
        final var request = ctx.request();

        if (request.method() != HttpMethod.POST) {
            HttpResponseUtil.methodNotAllowed(request);
            return;
        }

        request.bodyHandler(body -> {
            final var response = request.response();
            final var json = OrderModel.newResponse("pair").toBuffer();
            final var len = json.length();

            HttpResponseUtil.fillHeader(response.headers(), len);
            response.end(json);
        });
    }
}

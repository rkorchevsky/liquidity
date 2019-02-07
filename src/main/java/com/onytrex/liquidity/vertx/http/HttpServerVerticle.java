package com.onytrex.liquidity.vertx.http;

import com.google.inject.Inject;

import com.onytrex.liquidity.vertx.http.controller.HttpRestController;
import com.onytrex.liquidity.stocks.pool.BinanceConsumerPool;
import com.onytrex.liquidity.vertx.util.HttpResponseUtil;

import com.typesafe.config.Config;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.OpenSSLEngineOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static java.util.Objects.*;

public class HttpServerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class.getName());

    private HttpServer server;

    private final int HTTP_PORT;
    private final HttpRestController restController;

    @Inject
    public HttpServerVerticle(Config config, BinanceConsumerPool pool) {
        restController = HttpRestController.controller();
        restController.consumerPool(pool);
        this.HTTP_PORT = config.getInt("http.port");
    }

    @Override
    public void start() {
        final var app = Router.router(vertx);
        app.route(HttpRestController.HTTP_HEALTH_PATH).handler(restController::handleHealthCheck);
        app.route(HttpRestController.HTTP_SNAPSHOT_PATH).handler(restController::handleSnapshotRequest);
        app.route(HttpRestController.HTTP_ORDER_PATH).handler(restController::handleOrderRequest);
        app.route(HttpRestController.HTTP_BEST_PRICE_PATH).handler(restController::handleBestPriceRequest);
        app.route(HttpRestController.HTTP_CURRENCY_PAIR_ON_RENDERING_PATH).handler(restController::handleOnRenderingRequest);
        app.route(HttpRestController.HTTP_TICKER_PATH).handler(restController::handleTickerRequest);
        app.route(HttpRestController.HTTP_TICKER_24HR_PATH).handler(restController::handle24HrAllMarketTicker);
        app.route(HttpRestController.HTTP_TICKER_FIAT_24HR_PATH).handler(restController::handleFiat24HrAllMarketTicker);
        app.route(HttpRestController.HTTP_TRADE_PATH).handler(restController::handleTradeBuffer);
        app.route().handler(this::defaultRequestHandler);

        server = vertx.createHttpServer(new HttpServerOptions()
                .setTcpQuickAck(true)
                .setReusePort(true)
                .setSsl(true)
                .setOpenSslEngineOptions(new OpenSSLEngineOptions())
                .setKeyCertOptions(new PemKeyCertOptions().setCertPath("certificate.crt").setKeyPath("private.pem"))
                .setClientAuth(ClientAuth.NONE));
        server.requestHandler(app::accept).listen(HTTP_PORT);
        logger.info("Onytrex Liquidity application started at port: " + HTTP_PORT);
    }

    private void defaultRequestHandler(RoutingContext ctx) { HttpResponseUtil.nofFound(ctx.request(), "NOT FOUND"); }

    @Override
    public void stop() { if (nonNull(server)) server.close(); }
}
package com.onytrex.liquidity.vertx.websocket;

import com.google.inject.Inject;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.vertx.channel.Channels;
import com.onytrex.liquidity.vertx.channel.WebSocketChannel;
import com.onytrex.liquidity.vertx.channel.cache.AllMarketChannelCacheWs;
import com.onytrex.liquidity.vertx.channel.cache.DepthChannelCacheWs;
import com.onytrex.liquidity.vertx.channel.cache.TradeChannelCacheWs;

import com.typesafe.config.Config;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.OpenSSLEngineOptions;
import io.vertx.core.net.PemKeyCertOptions;

public class WebSocketServerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerVerticle.class.getName());

    private static final CurrencyPair CONSTANT = new CurrencyPair(201, "btcusdt");
    private static final CurrencyPair DEFAULT = CurrencyPair.DEFAULT;

    private static final String WEBSOCKET_DEPTH_PATH = "/stocks/depth";
    private static final String WEBSOCKET_DEPTH_STREAM_PATH = "/stocks/depth/stream";
    private static final String WEBSOCKET_ALL_MARKET_TICKER_STREAM_PATH = "/stocks/ticker/stream";
    private static final String WEBSOCKET_TRADE_PATH = "/stocks/trades";

    private final WebSocketChannel<CurrencyPair, ServerWebSocket> depthChannelCacheWs;
    private final WebSocketChannel<CurrencyPair, ServerWebSocket> tradeChannelCacheWs;
    private final WebSocketChannel<CurrencyPair, ServerWebSocket> allMarketChannelCache;

    private final int WS_PORT;

    @Inject
    WebSocketServerVerticle(Config config, DepthChannelCacheWs depthChannelWs, TradeChannelCacheWs tradeChannelWs, AllMarketChannelCacheWs allMarketChannelWs) {
        depthChannelCacheWs = depthChannelWs;
        tradeChannelCacheWs = tradeChannelWs;
        allMarketChannelCache = allMarketChannelWs;
        WS_PORT = config.getInt("ws.port");
    }

    @Override
    public void start() throws Exception {
        vertx.createHttpServer(new HttpServerOptions()
                .setTcpQuickAck(true)
                .setReusePort(true)
                .setSsl(true)
                .setOpenSslEngineOptions(new OpenSSLEngineOptions())
                .setKeyCertOptions(new PemKeyCertOptions().setCertPath("certificate.crt").setKeyPath("private.pem"))
                .setClientAuth(ClientAuth.NONE))
                .websocketHandler(this::handle)
                .listen(WS_PORT);
        logger.info("WebSocket Server started at port " + WS_PORT);
    }

    private void handle(ServerWebSocket serverWebSocket) {
        switch (serverWebSocket.path()) {
            case WEBSOCKET_DEPTH_STREAM_PATH:

                serverWebSocket.closeHandler(handler -> depthChannelCacheWs.removeChannel(serverWebSocket));

                serverWebSocket.frameHandler(frame -> accept(frame, serverWebSocket, CHANNEL_CONST.DEPTH));
                break;
            case WEBSOCKET_DEPTH_PATH:
                serverWebSocket.closeHandler(handler -> depthChannelCacheWs.removeChannel(serverWebSocket));

                compute(serverWebSocket, CONSTANT, CHANNEL_CONST.DEPTH);

                Channels.writeFrame(serverWebSocket, "Subscribed to default depth of currency pair: " + CONSTANT.description());
                break;
            case WEBSOCKET_ALL_MARKET_TICKER_STREAM_PATH:
                serverWebSocket.closeHandler(handler -> allMarketChannelCache.removeChannel(serverWebSocket));

                allMarketChannelCache.putChannel(DEFAULT, serverWebSocket);
                break;
            case WEBSOCKET_TRADE_PATH:
                serverWebSocket.closeHandler(handler -> tradeChannelCacheWs.removeChannel(serverWebSocket));

                serverWebSocket.frameHandler(frame -> accept(frame, serverWebSocket, CHANNEL_CONST.TRADE));
                break;
            default:
                serverWebSocket.reject();
                break;
        }
    }

    private void accept(WebSocketFrame frame, ServerWebSocket serverWebSocket, CHANNEL_CONST channel_const) {
        CurrencyPair pair;
        if (frame.isText()) {
            try {
                pair = CurrencyPair.getByCode(Integer.parseInt(frame.textData()));
            } catch (NumberFormatException e) {
                logger.info("Incorrect data input from client [" + serverWebSocket.remoteAddress() + "] " + frame.textData() + ". Client will be subscribed to default currency pair: 201 - BTC/USDT");
                compute(serverWebSocket, CONSTANT, channel_const);

                Channels.writeFrame(serverWebSocket, "Your input data type incorrect. Subscribed to default currency pair: " + CONSTANT.description());
                return;
            }
            compute(serverWebSocket, pair, channel_const);

            Channels.writeFrame(serverWebSocket,"Subscribed to currency pair '" + pair.description() + "'");
        }
    }

    private void compute(ServerWebSocket serverWebSocket, CurrencyPair pair, CHANNEL_CONST channel_const) {
        switch (channel_const) {
            case DEPTH:
                depthChannelCacheWs.putChannel(pair, serverWebSocket);
                break;
            case TRADE:
                tradeChannelCacheWs.putChannel(pair, serverWebSocket);
                break;
        }
    }

    private enum CHANNEL_CONST { DEPTH, TRADE }
}

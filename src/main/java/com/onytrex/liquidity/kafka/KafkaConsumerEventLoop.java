package com.onytrex.liquidity.kafka;

import com.google.inject.Inject;

import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.common.Quotation;
import com.onytrex.liquidity.vertx.channel.Channels;
import com.onytrex.liquidity.vertx.channel.cache.DepthChannelCacheWs;
import com.onytrex.liquidity.model.PriceQuantityPair;
import com.onytrex.liquidity.common.JsonUtil;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.apache.kafka.clients.consumer.Consumer;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.nio.ByteBuffer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class KafkaConsumerEventLoop {

    private static final MathContext ROUND_TO_9_SIGNIFICANT_DIGITS = new MathContext(9, RoundingMode.HALF_DOWN);
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerEventLoop.class.getName());

    private static final int COUNT = 50000;
    private static final long SCHEDULING_PERIOD = 100;

    private ScheduledExecutorService scheduledExecutorService;

    private final KafkaOrderBookPojoHolder orderBookHolder;
    private final JsonUtil jsonUtil;
    private final Consumer<Integer, ByteBuffer> consumer;
    private final DepthChannelCacheWs channelCache;

    @Inject
    public KafkaConsumerEventLoop(KafkaOrderBookPojoHolder holder, Consumer consumer) {
        this.channelCache = (DepthChannelCacheWs) Channels.channel(DepthChannelCacheWs.class);
        this.orderBookHolder = holder;
        //noinspection unchecked
        this.consumer = consumer;
        this.jsonUtil = JsonUtil.getInstance();
    }

    private void util() {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void startConsumerEventLoop() {
        util();
        logger.info("Starting Kafka's consumer event loop with period (" + SCHEDULING_PERIOD + ")" );
        consumeRecordsAndSendToClientViaWs();
    }

    private void consumeRecordsAndSendToClientViaWs() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            final var consumerRecords = consumer.poll(50);
            CurrencyPair symbol;
            if (!consumerRecords.isEmpty()) {
                for (var record: consumerRecords) {
                    symbol = CurrencyPair.getByCode(record.key());
                    if (channelCache.get(symbol).isEmpty()) {
                        consumer.commitAsync();
                        continue;
                    }

                    final var buffer = record.value();

                    final var quotation = Quotation.fetchByCode(buffer.getLong());
                    final var price = buffer.getLong();
                    final var qty = buffer.getLong();

                    final var monad = orderBookHolder.getBySymbolIfPresented(symbol);

                    if (monad.isPresent()) {
                        consumeAndFillBook(new PriceQuantityPair(quotation, new BigDecimal(KafkaLongParserUtil.parseStringToLong(price), ROUND_TO_9_SIGNIFICANT_DIGITS), new BigDecimal(KafkaLongParserUtil.parseStringToLong(qty), ROUND_TO_9_SIGNIFICANT_DIGITS)), monad.get());
                    } else {
                        var book = new KafkaOrderBookPojo();
                        consumeAndFillBook(new PriceQuantityPair(quotation, new BigDecimal(KafkaLongParserUtil.parseStringToLong(price), ROUND_TO_9_SIGNIFICANT_DIGITS), new BigDecimal(KafkaLongParserUtil.parseStringToLong(qty), ROUND_TO_9_SIGNIFICANT_DIGITS)), book);
                        orderBookHolder.put(symbol, book);
                    }
                }

                if (orderBookHolder.isEmpty()) return;

                consumer.commitAsync();

                for (var entry: channelCache.entrySet()) {
                    final var key = entry.getKey();
                    final var wsSet = entry.getValue();

                    if (wsSet.isEmpty()) continue;

                    final var temp = orderBookHolder.get(key);

                    if (temp == null) return;

                    final var json = jsonUtil.toJson(temp);

                    for (var webSocket: wsSet)
                        try {
                            Channels.writeFrame(webSocket, json);
                        } catch (IllegalStateException e) {
                            logger.error("Client [" + webSocket.remoteAddress() + "] already closed connection via websocket");
                            Channels.removeChannel(DepthChannelCacheWs.class, webSocket);
                        }
                }
            }
            orderBookHolder.clear();
        }, 0, SCHEDULING_PERIOD, TimeUnit.MILLISECONDS);
    }

    private void consumeAndFillBook(PriceQuantityPair pair, KafkaOrderBookPojo orderBookUtil) {
        if (pair.getQuotation() == Quotation.ASK) orderBookUtil.fillAsks(pair);
        else orderBookUtil.fillBids(pair);
    }
}
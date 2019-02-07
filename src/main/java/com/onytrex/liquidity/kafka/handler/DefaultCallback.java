package com.onytrex.liquidity.kafka.handler;

import com.google.inject.Inject;
import com.onytrex.liquidity.common.Configs;
import com.onytrex.liquidity.common.CurrencyPair;
import com.onytrex.liquidity.kafka.KafkaByteBufferSerializer;
import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentNavigableMap;

import static com.onytrex.liquidity.common.Quotation.ASK;
import static com.onytrex.liquidity.common.Quotation.BID;

public class DefaultCallback implements DataUpdatedCallback {

    private final Producer<Integer, ByteBuffer> producer;

    private static final Config config = Configs.getConfig();

    @Inject
    public DefaultCallback(Producer producer) {
        //noinspection unchecked,unchecked
        this.producer = producer;
    }

    @Override
    public void process(CurrencyPair s, ConcurrentNavigableMap<Long, Long> asks, ConcurrentNavigableMap<Long, Long> bids) {

        asks.forEach((key, value) -> producer.send(new ProducerRecord<>(config.getString("kafka.topic_name"),
                config.getInt("kafka.base_partition"), s.code(), KafkaByteBufferSerializer.serialize(ASK.getCode(), key, value))));

        bids.forEach((key, value) -> producer.send(new ProducerRecord<>(config.getString("kafka.topic_name"),
                config.getInt("kafka.base_partition"), s.code(), KafkaByteBufferSerializer.serialize(BID.getCode(), key, value))));
    }
}

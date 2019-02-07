package com.onytrex.liquidity.kafka.factory;

import com.google.inject.Provider;

import com.onytrex.liquidity.common.Configs;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

import java.nio.ByteBuffer;

import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerFactory implements Provider<Consumer<Integer, ByteBuffer>> {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerFactory.class.getName());

    @Override
    public Consumer<Integer, ByteBuffer> get() {
        final var props = new Properties();
        final var config = Configs.getConfig();
        final var kafkaBroker = config.getString("kafka.broker");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.broker"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getString("kafka.group_id_config"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.Integer().deserializer().getClass());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Serdes.ByteBuffer().deserializer().getClass());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.getInt("kafka.max_poll_records"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final var consumer = new KafkaConsumer<Integer, ByteBuffer>(props);

        consumer.subscribe(Collections.singletonList(config.getString("kafka.topic_name")));
        logger.info("Kafka consumer successfully subscribed to instance@" + kafkaBroker);
        return consumer;
    }
}

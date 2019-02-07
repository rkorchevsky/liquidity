package com.onytrex.liquidity.kafka.factory;

import com.google.inject.Provider;

import com.onytrex.liquidity.common.Configs;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;

import java.util.Properties;

public class KafkaProducerFactory implements Provider<Producer> {

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducerFactory.class.getName());

	@Override
	public Producer get() {
		final var props = new Properties();
        final var config = Configs.getConfig();
        final var kafkaBroker = config.getString("kafka.broker");

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, config.getString("kafka.client_id"));
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.Integer().serializer().getClass());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.ByteBuffer().serializer().getClass());
		props.put(ProducerConfig.LINGER_MS_CONFIG, config.getString("kafka.linger_ms"));

		logger.info("Kafka producer for instance@" + kafkaBroker + " successfully created! ");
		return new KafkaProducer<>(props);
	}
}
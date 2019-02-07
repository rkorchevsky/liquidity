package com.onytrex.liquidity.kafka;

import com.google.inject.AbstractModule;

import com.onytrex.liquidity.common.ConfigModule;
import com.onytrex.liquidity.kafka.factory.KafkaConsumerFactory;
import com.onytrex.liquidity.kafka.factory.KafkaProducerFactory;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

public class KafkaModule extends AbstractModule {

    private final ConfigModule configModule;

    public KafkaModule(ConfigModule configModule) {
        this.configModule = configModule;
    }

    @Override
    protected void configure() {
        install(configModule);
        bind(Consumer.class).toProvider(KafkaConsumerFactory.class).asEagerSingleton();
        bind(Producer.class).toProvider(KafkaProducerFactory.class).asEagerSingleton();
    }
}

package com.onytrex.liquidity.kafka;

import com.google.inject.AbstractModule;
import com.onytrex.liquidity.common.ConfigModule;

class KafkaConsumerModule extends AbstractModule {
    private final ConfigModule configModule;
    private final KafkaModule kafkaModule;

    public KafkaConsumerModule(ConfigModule configModule, KafkaModule kafkaModule) {
        this.configModule = configModule;
        this.kafkaModule = kafkaModule;
    }

    @Override
    protected void configure() {
        install(configModule);
        install(kafkaModule);
        bind(KafkaConsumerEventLoop.class).asEagerSingleton();
        bind(KafkaConsumerLifeCycle.class).asEagerSingleton();
        bind(KafkaOrderBookPojoHolder.class).asEagerSingleton();
    }
}

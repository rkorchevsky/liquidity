package com.onytrex.liquidity.kafka;

import com.google.inject.Inject;

import com.onytrex.liquidity.common.AbstractLifeCycleComponent;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import static java.util.concurrent.CompletableFuture.runAsync;

public class KafkaConsumerLifeCycle extends AbstractLifeCycleComponent {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerLifeCycle.class.getName());
    private final KafkaConsumerEventLoop kafkaConsumerEventLoop;

    @Inject
    public KafkaConsumerLifeCycle(KafkaConsumerEventLoop kafkaConsumerEventLoop) {
        this.kafkaConsumerEventLoop = kafkaConsumerEventLoop;
    }

    @Override
    protected void doStart() {
        runAsync(kafkaConsumerEventLoop::startConsumerEventLoop);
    }


    @Override
    protected void doStop() {
        logger.warn("Stop not implemented");
    }

    @Override
    protected void doClose() {
        logger.warn("Close not implemented");
    }
}

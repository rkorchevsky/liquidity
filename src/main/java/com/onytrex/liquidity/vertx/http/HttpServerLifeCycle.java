package com.onytrex.liquidity.vertx.http;

import com.google.inject.Inject;
import com.onytrex.liquidity.common.AbstractLifeCycleComponent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HttpServerLifeCycle extends AbstractLifeCycleComponent {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerLifeCycle.class.getName());
    private final HttpServerVerticleProvider verticleProvider;
    private Vertx vertx;

    @Inject
    public HttpServerLifeCycle(HttpServerVerticleProvider verticleProvider) {
        this.verticleProvider = verticleProvider;
    }

    @Override
    protected void doStart() {
        this.vertx = Vertx.vertx(new VertxOptions().setPreferNativeTransport(true));
        if (vertx.isNativeTransportEnabled()) {
            logger.info("Native transport enabled");
        } else {
            logger.debug("Native transport not available, will be used JDK's NIO event loop");
        }
        vertx.exceptionHandler(Throwable::printStackTrace);
        vertx.deployVerticle(verticleProvider::get,
                new DeploymentOptions().setInstances(CpuCoreSensor.availableProcessors() * 2),event -> {
                    if (event.succeeded())
                        logger.debug("Onytrex Liquidity started!");
                    else
                        logger.error("Unable to start Onytrex Liquidity application", event.cause());
                });
    }

    @Override
    protected void doStop() {
        logger.warn("Stop not implemented");
    }

    @Override
    protected void doClose() {
        vertx.close();
    }
}

package com.onytrex.liquidity.vertx.websocket;

import com.google.inject.Inject;
import com.onytrex.liquidity.common.AbstractLifeCycleComponent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class WebSocketServerLifeCycle extends AbstractLifeCycleComponent {

    private Vertx vertx;
    private final WebSocketServerVerticleProvider webSocketServerVerticleProvider;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerLifeCycle.class.getName());

    @Inject
    public WebSocketServerLifeCycle(WebSocketServerVerticleProvider webSocketServerVerticleProvider) {
        this.webSocketServerVerticleProvider = webSocketServerVerticleProvider;
    }

    @Override
    protected void doStart() {
        this.vertx = Vertx.vertx(new VertxOptions().setPreferNativeTransport(true));

        if (vertx.isNativeTransportEnabled()) logger.info("Onytrex Liquidity Web Socket Server: native transport enabled");
        else logger.debug("Onytrex Liquidity Web Socket Server: native transport not available, will be used JDK's NIO event loop");

        vertx.exceptionHandler(Throwable::printStackTrace);
        vertx.deployVerticle(webSocketServerVerticleProvider::get,
                new DeploymentOptions().setInstances(Runtime.getRuntime().availableProcessors()), event -> {
                    if (event.succeeded())
                        logger.info("Onytrex Liquidity Web Socket Server started!");
                    else
                        logger.error("Unable to start Onytrex Liquidity Web Socket Server: " +  event.cause());
                });
    }

    @Override
    protected void doStop() {
        vertx.close();
    }

    @Override
    protected void doClose() {
    }
}

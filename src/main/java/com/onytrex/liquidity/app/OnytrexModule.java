package com.onytrex.liquidity.app;

import com.google.inject.AbstractModule;
import com.onytrex.liquidity.common.ConfigModule;
import com.onytrex.liquidity.common.LifeCycleComponentMatcher;
import com.onytrex.liquidity.common.LifeCycleService;

class OnytrexModule extends AbstractModule {
    private final ConfigModule configModule;

    public OnytrexModule(ConfigModule configModule) {
        this.configModule = configModule;
    }

    @Override
    protected void configure() {
        install(configModule);
        final var lifecycleCompositor = new LifeCycleService();
        bind(LifeCycleService.class).toInstance(lifecycleCompositor);
        bindListener(new LifeCycleComponentMatcher(), lifecycleCompositor);
    }

}

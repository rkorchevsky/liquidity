package com.onytrex.liquidity.common;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Config.class).toProvider(ConfigFactory::load);
    }
}

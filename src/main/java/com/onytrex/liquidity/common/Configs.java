package com.onytrex.liquidity.common;

import com.google.inject.Provider;
import com.typesafe.config.Config;

public final class Configs implements Provider<Config> {

    @Deprecated
    public Configs() {
    }

    @Override
    public Config get() {
        return LazyHolder.CONFIG;
    }

    private static class LazyHolder {
        static final Config CONFIG = com.typesafe.config.ConfigFactory.load();
    }

    public static Config getConfig() {
        return LazyHolder.CONFIG;
    }

}

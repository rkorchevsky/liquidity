package com.onytrex.liquidity.common;

interface LifeCycleComponent {
    void start();

    void stop();

    void close();
}

package com.onytrex.liquidity;

import com.onytrex.liquidity.app.OnytrexLiquidityNode;

import java.util.concurrent.CountDownLatch;

class Main {

    private static volatile boolean hookStarted = false;

    public static void main(String[] args) {
        final var node = new OnytrexLiquidityNode();
        node.start();
        final var countDownLatch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread (() -> {
            hookStarted = true;
            node.close();
            countDownLatch.countDown();
        }));

        while (!hookStarted) {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {
            }
        }
    }
}

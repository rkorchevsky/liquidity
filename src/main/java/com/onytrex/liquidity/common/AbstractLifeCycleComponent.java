package com.onytrex.liquidity.common;

public abstract class AbstractLifeCycleComponent implements LifeCycleComponent {
    private final LifeCycle lifecycle = new LifeCycle();

    protected abstract void doStart();
    protected abstract void doStop();
    protected abstract void doClose();

    @Override
    public void start() {
        lifecycle.start();
        doStart();
    }

    @Override
    public void stop() {
        lifecycle.stop();
        doStop();
    }

    @Override
    public void close() {
        if (lifecycle.getState() == LifeCycle.State.STARTED) {
            this.stop();
        }
        lifecycle.close();
        doClose();
    }

    public LifeCycle getLifecycle() {
        return lifecycle;
    }
}

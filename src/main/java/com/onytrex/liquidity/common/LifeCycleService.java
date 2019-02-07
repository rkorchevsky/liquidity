package com.onytrex.liquidity.common;

import com.google.inject.spi.ProvisionListener;

import java.util.ArrayList;
import java.util.List;

public class LifeCycleService extends AbstractLifeCycleComponent implements ProvisionListener {
    private final List<LifeCycleComponent> lifecycleQueue = new ArrayList<>();

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        final var instance = provision.provision();
        if (instance == this) {
            return;
        }
        lifecycleQueue.add((LifeCycleComponent) instance);
    }

    @Override
    protected void doStart() {
        lifecycleQueue.forEach(LifeCycleComponent::start);
    }

    @Override
    protected void doStop() {
        lifecycleQueue.forEach(LifeCycleComponent::stop);
    }

    @Override
    protected void doClose() {
        lifecycleQueue.forEach(LifeCycleComponent::close);
    }
}

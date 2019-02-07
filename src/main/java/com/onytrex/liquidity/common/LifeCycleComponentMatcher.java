package com.onytrex.liquidity.common;

import com.google.inject.Binding;
import com.google.inject.matcher.AbstractMatcher;

public class LifeCycleComponentMatcher extends AbstractMatcher<Binding<?>> {
    @Override
    public boolean matches(Binding<?> binding) {
        final var key = binding.getKey();
        final var typeLiteral = key.getTypeLiteral();
        return LifeCycleComponent.class.isAssignableFrom(typeLiteral.getRawType());
    }
}

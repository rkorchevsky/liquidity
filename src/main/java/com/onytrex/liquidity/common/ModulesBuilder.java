package com.onytrex.liquidity.common;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModulesBuilder {
    private final List<AbstractModule> modules = new ArrayList<>(8);

    public ModulesBuilder add(AbstractModule... newModules) {
        Collections.addAll(modules, newModules);
        return this;
    }

    public void add(AbstractModule module) {
        modules.add(module);
    }

    public Injector createInjector() {
        return Guice.createInjector(modules);
    }
}

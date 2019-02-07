package com.onytrex.liquidity.common;

public class LifeCycle {
    public enum State {
        INITIALIZED,
        STOPPED,
        STARTED,
        CLOSED
    }

    private volatile State state = State.INITIALIZED;

    void start() {
        if (!(state == State.INITIALIZED || state == State.STOPPED)) {
            throw new IllegalStateException("LifeCycle in " + state + " state can't start");
        }
        state = State.STARTED;
    }

    void stop() {
        if (state != State.STARTED) {
            throw new IllegalStateException("LifeCycle in " + state + " state can't stop");
        }
        state = State.STOPPED;
    }

    void close() {
        if (state != State.STOPPED) {
            throw new IllegalStateException("LifeCycle in " + state + " state can't close");
        }
        state = State.CLOSED;
    }

    public State getState() {
        return state;
    }
}

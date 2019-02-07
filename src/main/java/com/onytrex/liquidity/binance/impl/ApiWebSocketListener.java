package com.onytrex.liquidity.binance.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onytrex.liquidity.binance.api.ApiCallback;
import com.onytrex.liquidity.binance.exception.ApiException;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.IOException;

/**
 * Binance API WebSocket listener.
 */
public class ApiWebSocketListener<T> extends WebSocketListener {

    private ApiCallback<T> callback;

    private Class<T> eventClass;

    private TypeReference<T> eventTypeReference;

    private boolean closing = false;

    public ApiWebSocketListener(ApiCallback<T> callback, Class<T> eventClass) {
        this.callback = callback;
        this.eventClass = eventClass;
    }

    public ApiWebSocketListener(ApiCallback<T> callback, TypeReference<T> eventTypeReference) {
        this.callback = callback;
        this.eventTypeReference = eventTypeReference;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        var mapper = new ObjectMapper();
        try {
            T event;
            if (eventClass == null) {
                event = mapper.readValue(text, eventTypeReference);
            } else {
                event = mapper.readValue(text, eventClass);
            }
            callback.onResponse(event);
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    @Override
    public void onClosing(final WebSocket webSocket, final int code, final String reason) {
        closing = true;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (!closing) {
            callback.onFailure(t);
        }
    }
}
package com.onytrex.liquidity.binance.api;


import com.onytrex.liquidity.binance.impl.ApiAsyncRestClientImpl;
import com.onytrex.liquidity.binance.impl.ApiRestClientImpl;
import com.onytrex.liquidity.binance.impl.ApiServiceGenerator;
import com.onytrex.liquidity.binance.impl.ApiWebSocketClientImpl;

/**
 * A factory for creating Api client objects.
 */
public class ApiClientFactory {

    /**
     * API Key
     */
    private final String apiKey;

    /**
     * Secret.
     */
    private final String secret;

    /**
     * Instantiates a new  api client factory.
     *
     * @param apiKey the API key
     * @param secret the Secret
     */
    private ApiClientFactory(String apiKey, String secret) {
        this.apiKey = apiKey;
        this.secret = secret;
    }

    /**
     * New instance.
     *
     * @param apiKey the API key
     * @param secret the Secret
     * @return the  api client factory
     */
    public static ApiClientFactory newInstance(String apiKey, String secret) {
        return new ApiClientFactory(apiKey, secret);
    }

    /**
     * New instance without authentication.
     *
     * @return the  api client factory
     */
    public static ApiClientFactory newInstance() {
        return new ApiClientFactory(null, null);
    }

    /**
     * Creates a new synchronous/blocking REST client.
     */
    public ApiRestClient newRestClient() {
        return new ApiRestClientImpl(apiKey, secret);
    }

    /**
     * Creates a new asynchronous/non-blocking REST client.
     */
    public ApiAsyncRestClient newAsyncRestClient() {
        return new ApiAsyncRestClientImpl(apiKey, secret);
    }

    /**
     * Creates a new web socket client used for handling data streams.
     */
    public ApiWebSocketClient newWebSocketClient() {
        return new ApiWebSocketClientImpl(ApiServiceGenerator.getSharedClient());
    }
}

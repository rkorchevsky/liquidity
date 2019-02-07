package com.onytrex.liquidity.binance.impl;

import com.onytrex.liquidity.binance.api.ApiCallback;
import com.onytrex.liquidity.binance.exception.ApiException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

import static com.onytrex.liquidity.binance.impl.ApiServiceGenerator.getApiError;

/**
 * An adapter/wrapper which transforms a Callback from Retrofit into a BinanceApiCallback which is exposed to the client.
 */
public class ApiCallbackAdapter<T> implements Callback<T> {

    private final ApiCallback<T> callback;

    public ApiCallbackAdapter(ApiCallback<T> callback) {
        this.callback = callback;
    }

    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            callback.onResponse(response.body());
        } else {
            if (response.code() == 504) {
                // HTTP 504 return code is used when the API successfully sent the message but not get a response within the timeout period.
                // It is important to NOT treat this as a failure; the execution status is UNKNOWN and could have been a success.
                return;
            }
            try {
                var apiError = getApiError(response);
                onFailure(call, new ApiException(apiError));
            } catch (IOException e) {
                onFailure(call, new ApiException(e));
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        if (throwable instanceof ApiException) {
            callback.onFailure(throwable);
        } else {
            callback.onFailure(new ApiException(throwable));
        }
    }
}

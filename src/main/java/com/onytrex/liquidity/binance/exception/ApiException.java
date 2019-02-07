package com.onytrex.liquidity.binance.exception;


import com.onytrex.liquidity.binance.api.ApiError;

/**
 * An exception which can occur while invoking methods of the  API.
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 3788669840036201041L;
    /**
     * Error response object returned by  API.
     */
    private ApiError error;

    /**
     * Instantiates a new  api exception.
     *
     * @param error an error response object
     */
    public ApiException(ApiError error) {
        this.error = error;
    }

    /**
     * Instantiates a new  api exception.
     */
    public ApiException() {
        super();
    }

    /**
     * Instantiates a new  api exception.
     *
     * @param message the message
     */
    public ApiException(String message) {
        super(message);
    }

    /**
     * Instantiates a new api exception.
     *
     * @param cause the cause
     */
    public ApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new api exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @return the response error object from  API, or null if no response object was returned (e.g. server returned 500).
     */
    public ApiError getError() {
        return error;
    }

    @Override
    public String getMessage() {
        if (error != null) {
            return error.getMsg();
        }
        return super.getMessage();
    }
}

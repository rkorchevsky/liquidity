package com.onytrex.liquidity.binance.domain.account.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onytrex.liquidity.binance.constant.ApiConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Base request parameters for order-related methods.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    private final String symbol;

    private Long recvWindow;

    private Long timestamp;

    OrderRequest(String symbol) {
        this.symbol = symbol;
        this.timestamp = System.currentTimeMillis();
        this.recvWindow = ApiConstants.DEFAULT_RECEIVING_WINDOW;
    }

    public String getSymbol() {
        return symbol;
    }

    public Long getRecvWindow() {
        return recvWindow;
    }

    public OrderRequest recvWindow(Long recvWindow) {
        this.recvWindow = recvWindow;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public OrderRequest timestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ApiConstants.TO_STRING_BUILDER_STYLE)
                .append("symbol", symbol)
                .append("recvWindow", recvWindow)
                .append("timestamp", timestamp)
                .toString();
    }
}

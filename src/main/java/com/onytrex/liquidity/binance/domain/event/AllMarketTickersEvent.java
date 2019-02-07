package com.onytrex.liquidity.binance.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.onytrex.liquidity.binance.constant.ApiConstants;
import com.onytrex.liquidity.binance.subscriber.streamer.AllMarketEventStreamer;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllMarketTickersEvent {

    public AllMarketTickersEvent(String symbol, String priceChangePercent, String highPrice, String lowPrice, String totalTradedBaseAssetVolume, String totalTradedQuoteAssetVolume) {
        this.symbol = symbol;
        this.priceChangePercent = priceChangePercent;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.totalTradedBaseAssetVolume = totalTradedBaseAssetVolume;
        this.totalTradedQuoteAssetVolume = totalTradedQuoteAssetVolume;
    }

    public AllMarketTickersEvent(String symbol, String priceChangePercent, String highPrice, String lowPrice, String totalTradedBaseAssetVolume) {
        this.symbol = symbol;
        this.priceChangePercent = priceChangePercent;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.totalTradedBaseAssetVolume = totalTradedBaseAssetVolume;
    }

    public AllMarketTickersEvent(String first, String second, String symbol, String currentDaysClosePrice, String priceChangePercent, String highPrice, String lowPrice, String totalTradedBaseAssetVolume, String totalTradedQuoteAssetVolume) {
        this.first = first;
        this.second = second;
        this.symbol = symbol;
        this.currentDaysClosePrice = currentDaysClosePrice;
        this.priceChangePercent = priceChangePercent;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.totalTradedBaseAssetVolume = totalTradedBaseAssetVolume;
        this.totalTradedQuoteAssetVolume = totalTradedQuoteAssetVolume;
    }

    public AllMarketTickersEvent(int code, String first, String second, String symbol, String currentDaysClosePrice, String priceChangePercent, String highPrice, String lowPrice, String totalTradedBaseAssetVolume, String totalTradedQuoteAssetVolume) {
        this.code = code;
        this.first = first;
        this.second = second;
        this.symbol = symbol;
        this.currentDaysClosePrice = currentDaysClosePrice;
        this.priceChangePercent = priceChangePercent;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.totalTradedBaseAssetVolume = totalTradedBaseAssetVolume;
        this.totalTradedQuoteAssetVolume = totalTradedQuoteAssetVolume;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("code")
    int code;

    @JsonProperty("currency1")
    private String first;

    @JsonProperty("currency2")
    private String second;

    public AllMarketTickersEvent() {}

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("P")
    private String priceChangePercent;

    @JsonProperty("c")
    private String currentDaysClosePrice;

    @JsonProperty("h")
    private String highPrice;

    @JsonProperty("l")
    private String lowPrice;

    @JsonProperty("v")
    private String totalTradedBaseAssetVolume;

    @JsonProperty("q")
    private String totalTradedQuoteAssetVolume;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPriceChangePercent() {
        return priceChangePercent;
    }

    public void setPriceChangePercent(String priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getCurrentDaysClosePrice() {
        return currentDaysClosePrice;
    }

    public void setCurrentDaysClosePrice(String currentDaysClosePrice) {
        this.currentDaysClosePrice = currentDaysClosePrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getTotalTradedBaseAssetVolume() {
        return totalTradedBaseAssetVolume;
    }

    public void setTotalTradedBaseAssetVolume(String totalTradedBaseAssetVolume) {
        this.totalTradedBaseAssetVolume = totalTradedBaseAssetVolume;
    }

    public String getTotalTradedQuoteAssetVolume() {
        return totalTradedQuoteAssetVolume;
    }

    public void setTotalTradedQuoteAssetVolume(String totalTradedQuoteAssetVolume) {
        this.totalTradedQuoteAssetVolume = totalTradedQuoteAssetVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (AllMarketTickersEvent) o;
        return Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ApiConstants.TO_STRING_BUILDER_STYLE)
                .append("symbol", symbol)
                .append("priceChangePercent", priceChangePercent)
                .append("highPrice", highPrice)
                .append("lowPrice", lowPrice)
                .append("totalTradedBaseAssetVolume", totalTradedBaseAssetVolume)
                .append("totalTradedQuoteAssetVolume", totalTradedQuoteAssetVolume)
                .toString();
    }
}

package com.onytrex.liquidity.binance.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradePayloadEvent {

    @Override
    public String toString() {
        return "TradePayloadEvent{" +
                "E=" + E +
                ", s='" + s + '\'' +
                ", p='" + p + '\'' +
                ", q='" + q + '\'' +
                ", m=" + m +
                ", side='" + side + '\'' +
                '}';
    }

    public long getE() {
        return E;
    }

    public String getS() {
        return s;
    }

    public String getP() {
        return p;
    }

    public String getQ() {
        return q;
    }

    public boolean isM() {
        return m;
    }

    public String getSide() {
        return side;
    }

    @JsonProperty("E")
    private long E;

    @JsonProperty("s")
    private String s;

    @JsonProperty("p")
    private String p;

    @JsonProperty("q")
    private String q;

    @JsonProperty("m")
    private boolean m;

    @JsonProperty("side")
    private String side;

    public TradePayloadEvent buyOrSell() {
        this.side = m ? "BUY" : "SELL";
        return this;
    }
}

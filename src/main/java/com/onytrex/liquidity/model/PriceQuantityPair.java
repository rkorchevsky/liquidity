package com.onytrex.liquidity.model;

import com.onytrex.liquidity.common.Quotation;

import java.math.BigDecimal;

public final class PriceQuantityPair {

    private final BigDecimal price;
    private final BigDecimal quantity;

    private Quotation quotation;

    @Override
    public String toString() {
        return "PriceQuantityPair{" +
                "price=" + price +
                ", quantity=" + quantity +
                ", quotation=" + quotation +
                '}';
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public PriceQuantityPair(BigDecimal price, BigDecimal quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public PriceQuantityPair(Quotation quotation, BigDecimal price, BigDecimal quantity) {
        this.quotation = quotation;
        this.price = price;
        this.quantity = quantity;
    }

}

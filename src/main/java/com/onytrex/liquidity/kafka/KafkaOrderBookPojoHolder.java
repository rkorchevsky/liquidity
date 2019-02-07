package com.onytrex.liquidity.kafka;

import com.onytrex.liquidity.common.CurrencyPair;

import java.util.HashMap;
import java.util.Optional;

class KafkaOrderBookPojoHolder extends HashMap<CurrencyPair, KafkaOrderBookPojo> {

    public KafkaOrderBookPojoHolder() {
        super();
    }

    public Optional<KafkaOrderBookPojo> getBySymbolIfPresented(CurrencyPair symbol) {
        final var value = get(symbol);
        return Optional.ofNullable(value);
    }

}

package com.onytrex.liquidity.common;

import java.util.HashMap;
import java.util.Map;

public enum Quotation {
    ASK(97L),
    BID(98L);

    private static final Map<Long, Quotation> codeToQuotation;

    static {
        codeToQuotation = new HashMap<>();

        for (var quotation: Quotation.values())
            codeToQuotation.put(quotation.code, quotation);

    }

    private final long code;

    Quotation(long code) {
        this.code = code;
    }

    public long getCode() {
        return code;
    }

    public static Quotation fetchByCode(long code) {
        return codeToQuotation.get(code);
    }
}
package com.onytrex.liquidity.app;

import java.util.HashMap;
import java.util.Map;

public class OnytrexUtil {

    private static Map<Integer, String> CURRENCY_PAIR = new HashMap<>();

    static {
        CURRENCY_PAIR.put(201, "BTC/USDT");
        CURRENCY_PAIR.put(301, "ETH/USDT");
        CURRENCY_PAIR.put(302, "ETH/BTC");
        CURRENCY_PAIR.put(502, "DASH/BTC");
        CURRENCY_PAIR.put(601, "LTC/USDT");
        CURRENCY_PAIR.put(602, "LTC/BTC");
        CURRENCY_PAIR.put(603, "LTC/ETH");
        CURRENCY_PAIR.put(702, "OMG/BTC");
        CURRENCY_PAIR.put(703, "OMG/ETH");
        CURRENCY_PAIR.put(801, "VET/USDT");
        CURRENCY_PAIR.put(802, "VET/BTC");
        CURRENCY_PAIR.put(803, "VET/ETH");
    }

    public static Map<Integer, String> currencyPairsWithCode() {
        return CURRENCY_PAIR;
    }

    public static int byFiatCurrencies(String currency1, String currency2) {
        int code = 0;
        switch (currency1) {
            case "BTC":
                code = 201;
                break;
            case "ETH":
                code = 301;
                break;
            case "LTC":
                code = 601;
                break;
        }
        return code;
    }

    public static int byCurrencies(String currency1, String currency2) {
        int code = 0;
        switch (currency1) {
            case "BTC":
                code = 201;
                break;
            case "ETH":
                switch (currency2) {
                    case "USDT":
                        code = 301;
                        break;
                    case "BTC":
                        code = 302;
                        break;
                }
                break;
            case "DASH":
                code = 502;
                break;
            case "LTC":
                switch (currency2) {
                    case "BTC":
                        code = 602;
                        break;
                    case "ETH":
                        code = 603;
                        break;
                    case "USDT":
                        code = 601;
                }
                break;
            case "OMG":
                switch (currency2) {
                    case "ETH":
                        code = 703;
                        break;
                    case "BTC":
                        code = 702;
                        break;
                }
                break;
            case "VET":
                switch (currency2) {
                    case "USDT":
                        code = 801;
                        break;
                    case "BTC":
                        code = 802;
                        break;
                    case "ETH":
                        code = 803;
                        break;
                }
                break;
        }
        return code;
    }

    public static String[] currencyPairFiatDelimeter(String fiatSymbol, String[] fiatSymbolsBuffer) {
        switch (fiatSymbol) {
            case "BTCUSDT":
                fiatSymbolsBuffer[0] = "BTCUSD";
                fiatSymbolsBuffer[1] = "BTC";
                fiatSymbolsBuffer[2] = "USD";
                break;
            case "LTCUSDT":
                fiatSymbolsBuffer[0] = "LTCUSD";
                fiatSymbolsBuffer[1] = "LTC";
                fiatSymbolsBuffer[2] = "USD";
                break;
            case "ETHUSDT":
                fiatSymbolsBuffer[0] = "ETHUSD";
                fiatSymbolsBuffer[1] = "ETH";
                fiatSymbolsBuffer[2] = "USD";
                break;
        }
        return fiatSymbolsBuffer;
    }

    public static String[] currencyPairDelimeter(String symbol, String[] symbolsBuffer) {
        switch (symbol) {
            case "ETHUSDT":
                symbolsBuffer[0] = "ETH";
                symbolsBuffer[1] = "USDT";
                break;
            case "ETHBTC":
                symbolsBuffer[0] = "ETH";
                symbolsBuffer[1] = "BTC";
                break;
            case "BTCUSDT":
                symbolsBuffer[0] = "BTC";
                symbolsBuffer[1] = "USDT";
                break;
            case "DASHBTC":
                symbolsBuffer[0] = "DASH";
                symbolsBuffer[1] = "BTC";
                break;
            case "LTCUSDT":
                symbolsBuffer[0] = "LTC";
                symbolsBuffer[1] = "USDT";
                break;
            case "LTCBTC":
                symbolsBuffer[0] = "LTC";
                symbolsBuffer[1] = "BTC";
                break;
            case "LTCETH":
                symbolsBuffer[0] = "LTC";
                symbolsBuffer[1] = "ETH";
                break;
            case "OMGETH":
                symbolsBuffer[0] = "OMG";
                symbolsBuffer[1] = "ETH";
                break;
            case "OMGBTC":
                symbolsBuffer[0] = "OMG";
                symbolsBuffer[1] = "BTC";
                break;
            case "VETUSDT":
                symbolsBuffer[0] = "VET";
                symbolsBuffer[1] = "USDT";
                break;
            case "VETBTC":
                symbolsBuffer[0] = "VET";
                symbolsBuffer[1] = "BTC";
                break;
            case "VETETH":
                symbolsBuffer[0] = "VET";
                symbolsBuffer[1] = "ETH";
                break;
            default:
                throw new IllegalArgumentException();
        }
        return symbolsBuffer;
    }
}

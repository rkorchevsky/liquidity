package com.onytrex.liquidity.binance.impl;


import com.onytrex.liquidity.binance.api.ApiAsyncRestClient;
import com.onytrex.liquidity.binance.api.ApiCallback;
import com.onytrex.liquidity.binance.constant.ApiConstants;
import com.onytrex.liquidity.binance.domain.account.*;
import com.onytrex.liquidity.binance.domain.account.request.*;
import com.onytrex.liquidity.binance.domain.event.ListenKey;
import com.onytrex.liquidity.binance.domain.general.Asset;
import com.onytrex.liquidity.binance.domain.general.ExchangeInfo;
import com.onytrex.liquidity.binance.domain.general.ServerTime;
import com.onytrex.liquidity.binance.domain.market.*;

import java.util.List;

/**
 * Implementation of Binance's REST API using Retrofit with asynchronous/non-blocking method calls.
 */
public class ApiAsyncRestClientImpl implements ApiAsyncRestClient {

    private final ApiService binanceApiService;

    public ApiAsyncRestClientImpl(String apiKey, String secret) {
        binanceApiService = ApiServiceGenerator.createService(ApiService.class, apiKey, secret);
    }

    // General endpoints

    @Override
    public void ping(ApiCallback<Void> callback) {
        binanceApiService.ping().enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getServerTime(ApiCallback<ServerTime> callback) {
        binanceApiService.getServerTime().enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getExchangeInfo(ApiCallback<ExchangeInfo> callback) {
        binanceApiService.getExchangeInfo().enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAllAssets(ApiCallback<List<Asset>> callback) {
        binanceApiService.getAllAssets(ApiConstants.ASSET_INFO_API_BASE_URL + "assetWithdraw/getAllAsset.html")
                .enqueue(new ApiCallbackAdapter<>(callback));
    }

    // Market Data endpoints

    @Override
    public void getOrderBook(String symbol, Integer limit, ApiCallback<OrderBook> callback) {
        binanceApiService.getOrderBook(symbol, limit).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getTrades(String symbol, Integer limit, ApiCallback<List<TradeHistoryItem>> callback) {
        binanceApiService.getTrades(symbol, limit).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getHistoricalTrades(String symbol, Integer limit, Long fromId, ApiCallback<List<TradeHistoryItem>> callback) {
        binanceApiService.getHistoricalTrades(symbol, limit, fromId).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAggTrades(String symbol, String fromId, Integer limit, Long startTime, Long endTime, ApiCallback<List<AggTrade>> callback) {
        binanceApiService.getAggTrades(symbol, fromId, limit, startTime, endTime).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAggTrades(String symbol, ApiCallback<List<AggTrade>> callback) {
        getAggTrades(symbol, null, null, null, null, callback);
    }

    @Override
    public void getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime, ApiCallback<List<Candlestick>> callback) {
        binanceApiService.getCandlestickBars(symbol, interval.getIntervalId(), limit, startTime, endTime).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getCandlestickBars(String symbol, CandlestickInterval interval, ApiCallback<List<Candlestick>> callback) {
        getCandlestickBars(symbol, interval, null, null, null, callback);
    }

    @Override
    public void get24HrPriceStatistics(String symbol, ApiCallback<TickerStatistics> callback) {
        binanceApiService.get24HrPriceStatistics(symbol).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAll24HrPriceStatistics(ApiCallback<List<TickerStatistics>> callback) {
        binanceApiService.getAll24HrPriceStatistics().enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAllPrices(ApiCallback<List<TickerPrice>> callback) {
        binanceApiService.getLatestPrices().enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getPrice(String symbol, ApiCallback<TickerPrice> callback) {
        binanceApiService.getLatestPrice(symbol).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getBookTickers(ApiCallback<List<BookTicker>> callback) {
        binanceApiService.getBookTickers().enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void newOrder(NewOrder order, ApiCallback<NewOrderResponse> callback) {
        binanceApiService.newOrder(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getNewClientOrderId(), order.getStopPrice(),
                order.getIcebergQty(), order.getNewOrderRespType(), order.getRecvWindow(), order.getTimestamp()).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void newOrderTest(NewOrder order, ApiCallback<Void> callback) {
        binanceApiService.newOrderTest(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getNewClientOrderId(), order.getStopPrice(),
                order.getIcebergQty(), order.getNewOrderRespType(), order.getRecvWindow(), order.getTimestamp()).enqueue(new ApiCallbackAdapter<>(callback));
    }

    // Account endpoints

    @Override
    public void getOrderStatus(OrderStatusRequest orderStatusRequest, ApiCallback<Order> callback) {
        binanceApiService.getOrderStatus(orderStatusRequest.getSymbol(),
                orderStatusRequest.getOrderId(), orderStatusRequest.getOrigClientOrderId(),
                orderStatusRequest.getRecvWindow(), orderStatusRequest.getTimestamp()).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void cancelOrder(CancelOrderRequest cancelOrderRequest, ApiCallback<CancelOrderResponse> callback) {
        binanceApiService.cancelOrder(cancelOrderRequest.getSymbol(),
                cancelOrderRequest.getOrderId(), cancelOrderRequest.getOrigClientOrderId(), cancelOrderRequest.getNewClientOrderId(),
                cancelOrderRequest.getRecvWindow(), cancelOrderRequest.getTimestamp()).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getOpenOrders(OrderRequest orderRequest, ApiCallback<List<Order>> callback) {
        binanceApiService.getOpenOrders(orderRequest.getSymbol(),
                orderRequest.getRecvWindow(), orderRequest.getTimestamp()).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAllOrders(AllOrdersRequest orderRequest, ApiCallback<List<Order>> callback) {
        binanceApiService.getAllOrders(orderRequest.getSymbol(),
                orderRequest.getOrderId(), orderRequest.getLimit(),
                orderRequest.getRecvWindow(), orderRequest.getTimestamp()).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAccount(Long recvWindow, Long timestamp, ApiCallback<Account> callback) {
        binanceApiService.getAccount(recvWindow, timestamp).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getAccount(ApiCallback<Account> callback) {
        long timestamp = System.currentTimeMillis();
        binanceApiService.getAccount(ApiConstants.DEFAULT_RECEIVING_WINDOW, timestamp).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getMyTrades(String symbol, Integer limit, Long fromId, Long recvWindow, Long timestamp, ApiCallback<List<Trade>> callback) {
        binanceApiService.getMyTrades(symbol, limit, fromId, recvWindow, timestamp).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getMyTrades(String symbol, Integer limit, ApiCallback<List<Trade>> callback) {
        getMyTrades(symbol, limit, null, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis(), callback);
    }

    @Override
    public void getMyTrades(String symbol, ApiCallback<List<Trade>> callback) {
        getMyTrades(symbol, null, null, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis(), callback);
    }

    @Override
    public void withdraw(String asset, String address, String amount, String name, String addressTag, ApiCallback<WithdrawResult> callback) {
        binanceApiService.withdraw(asset, address, amount, name, addressTag, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis())
                .enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getDepositHistory(String asset, ApiCallback<DepositHistory> callback) {
        binanceApiService.getDepositHistory(asset, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis())
                .enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getWithdrawHistory(String asset, ApiCallback<WithdrawHistory> callback) {
        binanceApiService.getWithdrawHistory(asset, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis())
                .enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void getDepositAddress(String asset, ApiCallback<DepositAddress> callback) {
        binanceApiService.getDepositAddress(asset, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis())
                .enqueue(new ApiCallbackAdapter<>(callback));
    }

    // User stream endpoints

    @Override
    public void startUserDataStream(ApiCallback<ListenKey> callback) {
        binanceApiService.startUserDataStream().enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void keepAliveUserDataStream(String listenKey, ApiCallback<Void> callback) {
        binanceApiService.keepAliveUserDataStream(listenKey).enqueue(new ApiCallbackAdapter<>(callback));
    }

    @Override
    public void closeUserDataStream(String listenKey, ApiCallback<Void> callback) {
        binanceApiService.closeAliveUserDataStream(listenKey).enqueue(new ApiCallbackAdapter<>(callback));
    }
}

package com.onytrex.liquidity.binance.impl;

import com.onytrex.liquidity.binance.api.ApiRestClient;
import com.onytrex.liquidity.binance.constant.ApiConstants;
import com.onytrex.liquidity.binance.domain.account.*;
import com.onytrex.liquidity.binance.domain.account.request.*;
import com.onytrex.liquidity.binance.domain.general.Asset;
import com.onytrex.liquidity.binance.domain.general.ExchangeInfo;
import com.onytrex.liquidity.binance.domain.market.*;

import java.util.List;

/**
 * Implementation of Binance's REST API using Retrofit with synchronous/blocking method calls.
 */
public class ApiRestClientImpl implements ApiRestClient {

    private final ApiService binanceApiService;

    public ApiRestClientImpl(String apiKey, String secret) {
        binanceApiService = ApiServiceGenerator.createService(ApiService.class, apiKey, secret);
    }

    // General endpoints

    @Override
    public void ping() {
        ApiServiceGenerator.executeSync(binanceApiService.ping());
    }

    @Override
    public Long getServerTime() {
        return ApiServiceGenerator.executeSync(binanceApiService.getServerTime()).getServerTime();
    }

    @Override
    public ExchangeInfo getExchangeInfo() {
        return ApiServiceGenerator.executeSync(binanceApiService.getExchangeInfo());
    }

    @Override
    public List<Asset> getAllAssets() {
        return ApiServiceGenerator.executeSync(binanceApiService.getAllAssets(ApiConstants.ASSET_INFO_API_BASE_URL + "assetWithdraw/getAllAsset.html"));
    }

    // Market Data endpoints

    @Override
    public OrderBook getOrderBook(String symbol, Integer limit) {
        return ApiServiceGenerator.executeSync(binanceApiService.getOrderBook(symbol.toUpperCase(), limit));
    }

    @Override
    public List<TradeHistoryItem> getTrades(String symbol, Integer limit) {
        return ApiServiceGenerator.executeSync(binanceApiService.getTrades(symbol, limit));
    }

    @Override
    public List<TradeHistoryItem> getHistoricalTrades(String symbol, Integer limit, Long fromId) {
        return ApiServiceGenerator.executeSync(binanceApiService.getHistoricalTrades(symbol, limit, fromId));
    }

    @Override
    public List<AggTrade> getAggTrades(String symbol, String fromId, Integer limit, Long startTime, Long endTime) {
        return ApiServiceGenerator.executeSync(binanceApiService.getAggTrades(symbol, fromId, limit, startTime, endTime));
    }

    @Override
    public List<AggTrade> getAggTrades(String symbol) {
        return getAggTrades(symbol, null, null, null, null);
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval, Integer limit, Long startTime, Long endTime) {
        return ApiServiceGenerator.executeSync(binanceApiService.getCandlestickBars(symbol, interval.getIntervalId(), limit, startTime, endTime));
    }

    @Override
    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval) {
        return getCandlestickBars(symbol, interval, null, null, null);
    }

    @Override
    public TickerStatistics get24HrPriceStatistics(String symbol) {
        return ApiServiceGenerator.executeSync(binanceApiService.get24HrPriceStatistics(symbol));
    }

    @Override
    public List<TickerStatistics> getAll24HrPriceStatistics() {
        return ApiServiceGenerator.executeSync(binanceApiService.getAll24HrPriceStatistics());
    }

    @Override
    public TickerPrice getPrice(String symbol) {
        return ApiServiceGenerator.executeSync(binanceApiService.getLatestPrice(symbol));
    }

    @Override
    public List<TickerPrice> getAllPrices() {
        return ApiServiceGenerator.executeSync(binanceApiService.getLatestPrices());
    }

    @Override
    public List<BookTicker> getBookTickers() {
        return ApiServiceGenerator.executeSync(binanceApiService.getBookTickers());
    }

    @Override
    public NewOrderResponse newOrder(NewOrder order) {
        return ApiServiceGenerator.executeSync(binanceApiService.newOrder(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getNewClientOrderId(), order.getStopPrice(),
                order.getIcebergQty(), order.getNewOrderRespType(), order.getRecvWindow(), order.getTimestamp()));
    }

    @Override
    public void newOrderTest(NewOrder order) {
        ApiServiceGenerator.executeSync(binanceApiService.newOrderTest(order.getSymbol(), order.getSide(), order.getType(),
                order.getTimeInForce(), order.getQuantity(), order.getPrice(), order.getNewClientOrderId(), order.getStopPrice(),
                order.getIcebergQty(), order.getNewOrderRespType(), order.getRecvWindow(), order.getTimestamp()));
    }

    // Account endpoints

    @Override
    public Order getOrderStatus(OrderStatusRequest orderStatusRequest) {
        return ApiServiceGenerator.executeSync(binanceApiService.getOrderStatus(orderStatusRequest.getSymbol(),
                orderStatusRequest.getOrderId(), orderStatusRequest.getOrigClientOrderId(),
                orderStatusRequest.getRecvWindow(), orderStatusRequest.getTimestamp()));
    }

    @Override
    public CancelOrderResponse cancelOrder(CancelOrderRequest cancelOrderRequest) {
        return ApiServiceGenerator.executeSync(binanceApiService.cancelOrder(cancelOrderRequest.getSymbol(),
                cancelOrderRequest.getOrderId(), cancelOrderRequest.getOrigClientOrderId(), cancelOrderRequest.getNewClientOrderId(),
                cancelOrderRequest.getRecvWindow(), cancelOrderRequest.getTimestamp()));
    }

    @Override
    public List<Order> getOpenOrders(OrderRequest orderRequest) {
        return ApiServiceGenerator.executeSync(binanceApiService.getOpenOrders(orderRequest.getSymbol(), orderRequest.getRecvWindow(), orderRequest.getTimestamp()));
    }

    @Override
    public List<Order> getAllOrders(AllOrdersRequest orderRequest) {
        return ApiServiceGenerator.executeSync(binanceApiService.getAllOrders(orderRequest.getSymbol(),
                orderRequest.getOrderId(), orderRequest.getLimit(),
                orderRequest.getRecvWindow(), orderRequest.getTimestamp()));
    }

    @Override
    public Account getAccount(Long recvWindow, Long timestamp) {
        return ApiServiceGenerator.executeSync(binanceApiService.getAccount(recvWindow, timestamp));
    }

    @Override
    public Account getAccount() {
        return getAccount(ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public List<Trade> getMyTrades(String symbol, Integer limit, Long fromId, Long recvWindow, Long timestamp) {
        return ApiServiceGenerator.executeSync(binanceApiService.getMyTrades(symbol, limit, fromId, recvWindow, timestamp));
    }

    @Override
    public List<Trade> getMyTrades(String symbol, Integer limit) {
        return getMyTrades(symbol, limit, null, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public List<Trade> getMyTrades(String symbol) {
        return getMyTrades(symbol, null, null, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis());
    }

    @Override
    public WithdrawResult withdraw(String asset, String address, String amount, String name, String addressTag) {
        return ApiServiceGenerator.executeSync(binanceApiService.withdraw(asset, address, amount, name, addressTag, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    @Override
    public DepositHistory getDepositHistory(String asset) {
        return ApiServiceGenerator.executeSync(binanceApiService.getDepositHistory(asset, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    @Override
    public WithdrawHistory getWithdrawHistory(String asset) {
        return ApiServiceGenerator.executeSync(binanceApiService.getWithdrawHistory(asset, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    @Override
    public DepositAddress getDepositAddress(String asset) {
        return ApiServiceGenerator.executeSync(binanceApiService.getDepositAddress(asset, ApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis()));
    }

    // User stream endpoints

    @Override
    public String startUserDataStream() {
        return ApiServiceGenerator.executeSync(binanceApiService.startUserDataStream()).toString();
    }

    @Override
    public void keepAliveUserDataStream(String listenKey) {
        ApiServiceGenerator.executeSync(binanceApiService.keepAliveUserDataStream(listenKey));
    }

    @Override
    public void closeUserDataStream(String listenKey) {
        ApiServiceGenerator.executeSync(binanceApiService.closeAliveUserDataStream(listenKey));
    }
}

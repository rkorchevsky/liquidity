package com.onytrex.liquidity.binance.impl;

import com.onytrex.liquidity.binance.constant.ApiConstants;
import com.onytrex.liquidity.binance.domain.OrderSide;
import com.onytrex.liquidity.binance.domain.OrderType;
import com.onytrex.liquidity.binance.domain.TimeInForce;
import com.onytrex.liquidity.binance.domain.account.*;
import com.onytrex.liquidity.binance.domain.account.request.CancelOrderResponse;
import com.onytrex.liquidity.binance.domain.event.ListenKey;
import com.onytrex.liquidity.binance.domain.general.Asset;
import com.onytrex.liquidity.binance.domain.general.ExchangeInfo;
import com.onytrex.liquidity.binance.domain.general.ServerTime;
import com.onytrex.liquidity.binance.domain.market.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * Binance's REST API URL mappings and endpoint security configuration.
 */
public interface ApiService {

    // General endpoints

    @GET("/api/v1/ping")
    Call<Void> ping();

    @GET("/api/v1/time")
    Call<ServerTime> getServerTime();

    @GET("/api/v1/exchangeInfo")
    Call<ExchangeInfo> getExchangeInfo();

    @GET
    Call<List<Asset>> getAllAssets(@Url String url);

    // Market data endpoints

    @GET("/api/v1/depth")
    Call<OrderBook> getOrderBook(@Query("symbol") String symbol, @Query("limit") Integer limit);

    @GET("/api/v1/trades")
    Call<List<TradeHistoryItem>> getTrades(@Query("symbol") String symbol, @Query("limit") Integer limit);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_APIKEY_HEADER)
    @GET("/api/v1/historicalTrades")
    Call<List<TradeHistoryItem>> getHistoricalTrades(@Query("symbol") String symbol, @Query("limit") Integer limit, @Query("fromId") Long fromId);

    @GET("/api/v1/aggTrades")
    Call<List<AggTrade>> getAggTrades(@Query("symbol") String symbol, @Query("fromId") String fromId, @Query("limit") Integer limit,
                                      @Query("startTime") Long startTime, @Query("endTime") Long endTime);

    @GET("/api/v1/klines")
    Call<List<Candlestick>> getCandlestickBars(@Query("symbol") String symbol, @Query("interval") String interval, @Query("limit") Integer limit,
                                               @Query("startTime") Long startTime, @Query("endTime") Long endTime);

    @GET("/api/v1/ticker/24hr")
    Call<TickerStatistics> get24HrPriceStatistics(@Query("symbol") String symbol);

    @GET("/api/v1/ticker/24hr")
    Call<List<TickerStatistics>> getAll24HrPriceStatistics();

    @GET("/api/v1/ticker/allPrices")
    Call<List<TickerPrice>> getLatestPrices();

    @GET("/api/v3/ticker/price")
    Call<TickerPrice> getLatestPrice(@Query("symbol") String symbol);

    @GET("/api/v1/ticker/allBookTickers")
    Call<List<BookTicker>> getBookTickers();

    // Account endpoints

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @POST("/api/v3/order")
    Call<NewOrderResponse> newOrder(@Query("symbol") String symbol, @Query("side") OrderSide side, @Query("type") OrderType type,
                                    @Query("timeInForce") TimeInForce timeInForce, @Query("quantity") String quantity, @Query("price") String price,
                                    @Query("newClientOrderId") String newClientOrderId, @Query("stopPrice") String stopPrice,
                                    @Query("icebergQty") String icebergQty, @Query("newOrderRespType") NewOrderResponseType newOrderRespType,
                                    @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @POST("/api/v3/order/test")
    Call<Void> newOrderTest(@Query("symbol") String symbol, @Query("side") OrderSide side, @Query("type") OrderType type,
                            @Query("timeInForce") TimeInForce timeInForce, @Query("quantity") String quantity, @Query("price") String price,
                            @Query("newClientOrderId") String newClientOrderId, @Query("stopPrice") String stopPrice,
                            @Query("icebergQty") String icebergQty, @Query("newOrderRespType") NewOrderResponseType newOrderRespType,
                            @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/api/v3/order")
    Call<Order> getOrderStatus(@Query("symbol") String symbol, @Query("orderId") Long orderId,
                               @Query("origClientOrderId") String origClientOrderId, @Query("recvWindow") Long recvWindow,
                               @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @DELETE("/api/v3/order")
    Call<CancelOrderResponse> cancelOrder(@Query("symbol") String symbol, @Query("orderId") Long orderId,
                                          @Query("origClientOrderId") String origClientOrderId, @Query("newClientOrderId") String newClientOrderId,
                                          @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/api/v3/openOrders")
    Call<List<Order>> getOpenOrders(@Query("symbol") String symbol, @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/api/v3/allOrders")
    Call<List<Order>> getAllOrders(@Query("symbol") String symbol, @Query("orderId") Long orderId,
                                   @Query("limit") Integer limit, @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/api/v3/account")
    Call<Account> getAccount(@Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/api/v3/myTrades")
    Call<List<Trade>> getMyTrades(@Query("symbol") String symbol, @Query("limit") Integer limit, @Query("fromId") Long fromId,
                                  @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @POST("/wapi/v3/withdraw.html")
    Call<WithdrawResult> withdraw(@Query("asset") String asset, @Query("address") String address, @Query("amount") String amount, @Query("name") String name, @Query("addressTag") String addressTag,
                                  @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);


    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/wapi/v3/depositHistory.html")
    Call<DepositHistory> getDepositHistory(@Query("asset") String asset, @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/wapi/v3/withdrawHistory.html")
    Call<WithdrawHistory> getWithdrawHistory(@Query("asset") String asset, @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED_HEADER)
    @GET("/wapi/v3/depositAddress.html")
    Call<DepositAddress> getDepositAddress(@Query("asset") String asset, @Query("recvWindow") Long recvWindow, @Query("timestamp") Long timestamp);

    // User stream endpoints

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_APIKEY_HEADER)
    @POST("/api/v1/userDataStream")
    Call<ListenKey> startUserDataStream();

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_APIKEY_HEADER)
    @PUT("/api/v1/userDataStream")
    Call<Void> keepAliveUserDataStream(@Query("listenKey") String listenKey);

    @Headers(ApiConstants.ENDPOINT_SECURITY_TYPE_APIKEY_HEADER)
    @DELETE("/api/v1/userDataStream")
    Call<Void> closeAliveUserDataStream(@Query("listenKey") String listenKey);
}

# Java Binance API Event Consumer

## Features
* Support for synchronous and asynchronous REST requests to all [General](https://www.binance.com/restapipub.html#user-content-general-endpoints), [Market Data](https://www.binance.com/restapipub.html#user-content-market-data-endpoints), [Account](https://www.binance.com/restapipub.html#user-content-account-endpoints) endpoints, and [User](https://www.binance.com/restapipub.html#user-content-user-data-stream-endpoints) stream endpoints.
* Support for User Data, Trade, Kline, and Depth event streaming using [Binance WebSocket API](https://www.binance.com/restapipub.html#wss-endpoint).

# Kafka 

## Getting Started

### Prerequisites

Download and install JDK (depends on your environment):

* http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

* https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

Download Kafka:

* https://www.apache.org/dyn/closer.cgi?path=/kafka/2.0.0/kafka_2.11-2.0.0.tgz

```
> tar -xzf kafka_2.11-2.0.0.tgz
> cd kafka_2.11-2.0.0
```

# Installing

* Start the server:
> bin/zookeeper-server-start.sh config/zookeeper.properties

Output:
```
[2018-09-21 15:29:24,495] INFO Reading configuration from: config/zookeeper.properties (org.apache.zookeeper.server.quorum.QuorumPeerConfig)
```

* Now start the Kafka server
> bin/kafka-server-start.sh config/server.properties

Output:
```
[2018-09-21 15:31:37,192] INFO Reading configuration from: config/zookeeper.properties (org.apache.zookeeper.server.quorum.QuorumPeerConfig) 
[2018-09-21 15:31:39,028] INFO Verifying properties (kafka.utils.VerifiableProperties)
```

* Create a topic

Let's create a topic named "orders" with a single partition and only one replica:
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic orders

We can now see that topic if we run the list topic command:
> bin/kafka-topics.sh --list --zookeeper localhost:2181

Output:

```
orders
```

# Running the tests

* Send some record
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic orders

*type it to the console*

TEST_RECORD

* Start a consumer
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic orders --from-beginning

Output: 
```
TEST_RECORD
```

#Running Java application

* Go to the jar directory
```
cd /path/to/jar
```

* Run application with the next VM options: 

```
java -Xms2G  -Xmx2G -server -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseCompressedOops -XX:MaxGCPauseMillis=30 -Dvertx.disableMetrics=true -Dvertx.threadChecks=false -Dvertx.disableContextTimings=true -Dvertx.disableTCCL=true -jar Onytrex_Liquidity-1.0.0.jar
```

# currency pair code
Each currency has an int code from in 1 - 100 range. Currency pair code evaluated as (code_from_currency * 100 + code_to_currency)
>USDT    1<br>
>BTC     2<br>
>ETH     3<br>
>STQ     4<br>
>DASH    5<br>
>LTC     6<br>
>OMG     7<br>
>VET     8<br>



#Stock consumer
Added only if appropriate block in application.conf is present.
In com.onytrex.liquidity.stocks.StockResource enum should be added relevant record with clz implementing stock consumer
>  stocks = [{
>    name = enum_name
>    curr_pair = [{code = 201, description = btcusdt}, {code = 301, description = ethusdt},
>                    {code = 502, description = dashbtc}, {code = 302, description = ethbtc}, {code = 601, description = ltcusdt}, 
>                    {code = 102, description = 602}, {code = 603, description = ltceth}, {code = 702, description = omgbtc},
>                    {code = 703, description = omgeth}, {code = 801, description = vetusdt}, {code = 802, description = vetbtc},
>                    {code = 803, description = veteth}]
>  }]

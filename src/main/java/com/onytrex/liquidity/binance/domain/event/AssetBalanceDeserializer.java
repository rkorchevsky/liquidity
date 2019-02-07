package com.onytrex.liquidity.binance.domain.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.onytrex.liquidity.binance.domain.account.AssetBalance;

import java.io.IOException;

/**
 * Custom deserializer for an AssetBalance, since the streaming API returns an object in the format {"a":"symbol","f":"free","l":"locked"},
 * which is different than the format used in the REST API.
 */
public class AssetBalanceDeserializer extends JsonDeserializer<AssetBalance> {

    @Override
    public AssetBalance deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        var objectCodec = jp.getCodec();
        JsonNode node = objectCodec.readTree(jp);
        final String asset = node.get("a").asText();
        final String free = node.get("f").asText();
        final String locked = node.get("l").asText();

        var assetBalance = new AssetBalance();
        assetBalance.setAsset(asset);
        assetBalance.setFree(free);
        assetBalance.setLocked(locked);
        return assetBalance;
    }
}
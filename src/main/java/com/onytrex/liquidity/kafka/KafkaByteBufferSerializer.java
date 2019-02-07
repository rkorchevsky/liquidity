package com.onytrex.liquidity.kafka;

import java.nio.ByteBuffer;

public class KafkaByteBufferSerializer {

    public static ByteBuffer serialize(long l1, long l2, long l3) {
        final var buf = ByteBuffer.allocate(Long.SIZE * 3);
        buf.putLong(l1);
        buf.putLong(l2);
        buf.putLong(l3);
        return buf;
    }
}

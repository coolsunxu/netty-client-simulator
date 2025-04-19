package com.example.nettyclientsimulator.util;

import com.google.common.primitives.UnsignedBytes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author sunxu
 */
public class Murmur3_32Hash {
    private static final Murmur3_32Hash INSTANCE = new Murmur3_32Hash();

    private static final int CHUNK_SIZE = 4;
    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;
    private final int seed;

    private Murmur3_32Hash() {
        seed = 0;
    }

    public static Murmur3_32Hash getInstance() {
        return INSTANCE;
    }

    public int makeHash(byte[] b) {
        return makeHash0(b) & Integer.MAX_VALUE;
    }

    private int makeHash0(byte[] bytes) {
        int len = bytes.length;
        int reminder = len % CHUNK_SIZE;
        int h1 = seed;

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        while (byteBuffer.remaining() >= CHUNK_SIZE) {
            int k1 = byteBuffer.getInt();

            k1 = mixK1(k1);
            h1 = mixH1(h1, k1);
        }

        int k1 = 0;
        for (int i = 0; i < reminder; i++) {
            k1 ^= UnsignedBytes.toInt(byteBuffer.get()) << (i * 8);
        }

        h1 ^= mixK1(k1);
        h1 ^= len;
        h1 = fmix(h1);

        return h1;
    }

    private int fmix(int h) {
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;

        return h;
    }

    private int mixK1(int k1) {
        k1 *= C1;
        k1 = Integer.rotateLeft(k1, 15);
        k1 *= C2;
        return k1;
    }

    private int mixH1(int h1, int k1) {
        h1 ^= k1;
        h1 = Integer.rotateLeft(h1, 13);
        return h1 * 5 + 0xe6546b64;
    }
}

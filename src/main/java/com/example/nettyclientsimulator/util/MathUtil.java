package com.example.nettyclientsimulator.util;

import java.util.Objects;
import java.util.UUID;

public class MathUtil {

    public static int getHashCode(String str) {
        if (Objects.equals(str, "")) {
            return 0;
        }
        return str.hashCode() & 0x7fffffff;
    }

    public static String generateRandomString(int length) {
        String randomString = UUID.randomUUID().toString();
        randomString = randomString.replace("-", "");
        return randomString.substring(0, length);
    }
}

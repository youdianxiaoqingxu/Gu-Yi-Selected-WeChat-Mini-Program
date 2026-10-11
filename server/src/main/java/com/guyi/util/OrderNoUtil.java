package com.guyi.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderNoUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final SecureRandom RANDOM = new SecureRandom();

    private OrderNoUtil() {
    }

    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        return timestamp + String.format("%06d", RANDOM.nextInt(1000000));
    }
}


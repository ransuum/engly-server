package com.engly.engly_server.utils.passwordgenerateutil;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public final class PasswordUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtils() {}

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));

        return sb.toString();
    }

    public static String generateSecure(int length) {
        if (length < 4) length = 12;

        final var upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final var lower = "abcdefghijklmnopqrstuvwxyz";
        final var digits = "0123456789";
        final var symbols = "!@#$%^&*";

        char[] password = new char[length];

        password[0] = upper.charAt(RANDOM.nextInt(upper.length()));
        password[1] = lower.charAt(RANDOM.nextInt(lower.length()));
        password[2] = digits.charAt(RANDOM.nextInt(digits.length()));
        password[3] = symbols.charAt(RANDOM.nextInt(symbols.length()));

        IntStream.range(4, length)
                .parallel()
                .forEach(i -> password[i] = CHARS.charAt(RANDOM.nextInt(CHARS.length())));

        int finalLength = length;
        IntStream.range(0, length)
                .forEach(i -> {
                    int j = RANDOM.nextInt(finalLength);
                    char temp = password[i];
                    password[i] = password[j];
                    password[j] = temp;
                });

        return new String(password);
    }
}

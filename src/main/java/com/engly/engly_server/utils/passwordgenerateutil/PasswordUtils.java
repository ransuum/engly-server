package com.engly.engly_server.utils.passwordgenerateutil;

import java.security.SecureRandom;

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

        StringBuilder password = new StringBuilder();
        password.append(upper.charAt(RANDOM.nextInt(upper.length())));
        password.append(lower.charAt(RANDOM.nextInt(lower.length())));
        password.append(digits.charAt(RANDOM.nextInt(digits.length())));
        password.append(symbols.charAt(RANDOM.nextInt(symbols.length())));

        for (int i = 4; i < length; i++)
            password.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));

        char[] chars = password.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final int randomIndex = RANDOM.nextInt(chars.length);
            final char temp = chars[i];
            chars[i] = chars[randomIndex];
            chars[randomIndex] = temp;
        }

        return new String(chars);
    }
}

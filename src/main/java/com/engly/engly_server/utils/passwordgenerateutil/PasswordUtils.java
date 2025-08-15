package com.engly.engly_server.utils.passwordgenerateutil;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public final class PasswordUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtils() {
    }

    public static final PasswordGenerator DEFAULT_PASSWORD_GENERATOR = length -> {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    };

    public static final PasswordGenerator SECURE_PASSWORD_GENERATOR = length -> {
        if (length < 4) length = 12;

        char[] password = new char[length];

        password[0] = PasswordGenerator.upper.charAt(RANDOM.nextInt(PasswordGenerator.upper.length()));
        password[1] = PasswordGenerator.lower.charAt(RANDOM.nextInt(PasswordGenerator.lower.length()));
        password[2] = PasswordGenerator.digits.charAt(RANDOM.nextInt(PasswordGenerator.digits.length()));
        password[3] = PasswordGenerator.symbols.charAt(RANDOM.nextInt(PasswordGenerator.symbols.length()));

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
    };
}

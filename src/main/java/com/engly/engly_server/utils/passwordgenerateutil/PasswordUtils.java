package com.engly.engly_server.utils.passwordgenerateutil;

import java.security.SecureRandom;

public final class PasswordUtils {

    private static final ThreadLocal<SecureRandom> RANDOM =
            ThreadLocal.withInitial(SecureRandom::new);

    private PasswordUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final PasswordGenerator SIMPLE = length ->
            generatePassword(length, PasswordGenerator.ALL_CHARS, false);

    public static final PasswordGenerator SECURE = length ->
            generatePassword(Math.max(length, 4), PasswordGenerator.ALL_CHARS, true);

    public static final PasswordGenerator ALPHANUMERIC = length ->
            generatePassword(length, PasswordGenerator.UPPER + PasswordGenerator.LOWER + PasswordGenerator.DIGITS, false);

    private static String generatePassword(int length, String charset, boolean ensureComplexity) {
        if (length <= 0) throw new IllegalArgumentException("Length must be positive");

        final var random = RANDOM.get();
        char[] password = new char[length];

        if (ensureComplexity && length >= 4) {
            password[0] = PasswordGenerator.UPPER.charAt(random.nextInt(PasswordGenerator.UPPER.length()));
            password[1] = PasswordGenerator.LOWER.charAt(random.nextInt(PasswordGenerator.LOWER.length()));
            password[2] = PasswordGenerator.DIGITS.charAt(random.nextInt(PasswordGenerator.DIGITS.length()));
            password[3] = PasswordGenerator.SYMBOLS.charAt(random.nextInt(PasswordGenerator.SYMBOLS.length()));

            for (int i = 4; i < length; i++)
                password[i] = charset.charAt(random.nextInt(charset.length()));

            for (int i = length - 1; i > 0; i--) {
                final int j = random.nextInt(i + 1);
                final char temp = password[i];
                password[i] = password[j];
                password[j] = temp;
            }
        } else
            for (int i = 0; i < length; i++)
                password[i] = charset.charAt(random.nextInt(charset.length()));

        RANDOM.remove();

        return new String(password);
    }
}

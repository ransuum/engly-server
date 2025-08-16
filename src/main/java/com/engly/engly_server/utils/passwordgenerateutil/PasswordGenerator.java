package com.engly.engly_server.utils.passwordgenerateutil;

@FunctionalInterface
public interface PasswordGenerator {
    String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String LOWER = "abcdefghijklmnopqrstuvwxyz";
    String DIGITS = "0123456789";
    String SYMBOLS = "!@#$%^&*";
    String ALL_CHARS = UPPER + LOWER + DIGITS + SYMBOLS;

    String generate(int length);
}

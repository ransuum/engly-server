package com.engly.engly_server.utils.passwordgenerateutil;

@FunctionalInterface
public interface PasswordGenerator {
    final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final String lower = "abcdefghijklmnopqrstuvwxyz";
    final String digits = "0123456789";
    final String symbols = "!@#$%^&*";

    String generate(int length);
}

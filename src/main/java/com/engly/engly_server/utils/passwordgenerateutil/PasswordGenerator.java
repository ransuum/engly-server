package com.engly.engly_server.utils.passwordgenerateutil;

@FunctionalInterface
public interface PasswordGenerator {
    String generate(int length);
}

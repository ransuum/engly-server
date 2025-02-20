package com.engly.engly_server.utils.password_generate_util;

import org.springframework.security.core.token.Sha512DigestUtils;

import java.util.Base64;

public class PasswordGeneratorUtil {
    public static String generatePassword(String email, String name) {
        String hashed = Sha512DigestUtils.shaHex(email + name);
        return Base64.getEncoder().encodeToString(hashed.getBytes()).substring(0, 16);
    }
}

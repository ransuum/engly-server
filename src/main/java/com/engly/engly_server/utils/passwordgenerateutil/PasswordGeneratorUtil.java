package com.engly.engly_server.utils.passwordgenerateutil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.token.Sha512DigestUtils;

import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordGeneratorUtil {
    public static String generatePassword(String email, String name) {
        var hashed = Sha512DigestUtils.shaHex(email + name);
        return Base64.getEncoder().encodeToString(hashed.getBytes()).substring(0, 16);
    }
}

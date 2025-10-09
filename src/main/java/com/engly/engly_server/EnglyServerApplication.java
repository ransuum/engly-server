package com.engly.engly_server;

import com.engly.engly_server.security.jwt.JwtProperties;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {RSAKeyRecord.class, JwtProperties.class})
public class EnglyServerApplication {

    void main(String[] args) {
        SpringApplication.run(EnglyServerApplication.class, args);
    }

}

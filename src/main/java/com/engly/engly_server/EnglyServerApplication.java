package com.engly.engly_server;

import com.engly.engly_server.security.rsa.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(RSAKeyRecord.class)
@EnableScheduling
public class EnglyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglyServerApplication.class, args);
    }

}

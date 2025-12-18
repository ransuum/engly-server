package com.engly.engly_server;

import com.engly.engly_server.security.jwt.JwtProperties;
import com.engly.engly_server.security.rsa.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableConfigurationProperties(value = {RSAKeyRecord.class, JwtProperties.class})
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class EnglyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglyServerApplication.class, args);
    }

}

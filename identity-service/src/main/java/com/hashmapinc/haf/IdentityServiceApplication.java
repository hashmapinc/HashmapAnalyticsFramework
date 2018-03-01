package com.hashmapinc.haf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootConfiguration
@EnableDiscoveryClient
@EnableAutoConfiguration
@EnableResourceServer
@ComponentScan
public class IdentityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}

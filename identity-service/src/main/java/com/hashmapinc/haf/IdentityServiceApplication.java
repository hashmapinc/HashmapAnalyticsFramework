package com.hashmapinc.haf;

import com.hashmapinc.haf.install.IdentityInstallationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootConfiguration
@EnableDiscoveryClient
@EnableAutoConfiguration
@ComponentScan
public class IdentityServiceApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(IdentityServiceApplication.class, args);
        context.getBean(IdentityInstallationService.class).performInstall();
    }
}

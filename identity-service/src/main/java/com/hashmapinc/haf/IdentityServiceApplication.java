package com.hashmapinc.haf;

import com.hashmapinc.haf.install.IdentityInstallationService;
import com.hashmapinc.haf.repository.DefaultBaseRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootConfiguration
@EnableDiscoveryClient
@EnableAutoConfiguration
@EnableScheduling
@EnableJpaRepositories(repositoryBaseClass = DefaultBaseRepository.class)
@ComponentScan
public class IdentityServiceApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(IdentityServiceApplication.class, args);
        context.getBean(IdentityInstallationService.class).performInstall();
    }
}
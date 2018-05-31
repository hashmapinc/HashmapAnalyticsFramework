package com.hashmap.haf.metadata.config;

import com.hashmap.haf.metadata.config.install.MetadataServiceInstall;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@Configuration
@PropertySource("classpath:metadata-api.yml")
public class MetadataConfigApplication {

    private static String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "metadata-api";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MetadataConfigApplication.class);
        ConfigurableApplicationContext context = application.run(updateArguments(args));

        context.getBean(MetadataServiceInstall.class).performInstall();
//        SpringApplication.run(MetadataConfigApplication.class, args);
    }

    private static String[] updateArguments(String[] args) {
        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))) {
            String[] modifiedArgs = new String[args.length + 1];
            System.arraycopy(args, 0, modifiedArgs, 0, args.length);
            modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
            return modifiedArgs;
        }
        return args;
    }
}

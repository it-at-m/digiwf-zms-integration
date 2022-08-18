package io.muenchendigital.digiwf.integration.zms;

import io.muenchendigital.digiwf.integration.zms.properties.ZmsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ZmsProperties.class)
public class DigiwfZmsApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DigiwfZmsApplication.class, args);
    }

}

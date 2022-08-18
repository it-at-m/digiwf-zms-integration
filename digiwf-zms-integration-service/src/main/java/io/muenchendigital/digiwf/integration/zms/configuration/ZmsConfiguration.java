package io.muenchendigital.digiwf.integration.zms.configuration;

import io.muenchendigital.digiwf.spring.cloudstream.utils.api.streaming.infrastructure.RoutingCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ZmsConfiguration {

    public static final String TYPE_HEADER_CREATE_ZMS_ENTRY = "createZmsEntry";

    @Bean
    @ConditionalOnMissingBean
    public MessageRoutingCallback getEventBusRouter() {
        final Map<String, String> typeMappings = new HashMap<>();
        typeMappings.put(TYPE_HEADER_CREATE_ZMS_ENTRY, TYPE_HEADER_CREATE_ZMS_ENTRY);
        return new RoutingCallback(typeMappings);
    }

}

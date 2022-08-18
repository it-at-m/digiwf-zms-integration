package io.muenchendigital.digiwf.integration.zms.client.configuration;

import io.muenchendigital.digiwf.integration.zms.client.properties.ZmsClientProperties;
import io.muenchendigital.digiwf.integration.zms.gen.ApiClient;
import io.muenchendigital.digiwf.integration.zms.gen.api.JsonSchemaApi;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@EnableConfigurationProperties(ZmsClientProperties.class)
public class ZmsClientAutoConfiguration {

    public final ZmsClientProperties schemaRegistryClientProperties;

    @Bean
    private ApiClient apiClient(final RestTemplate restTemplate) {
        final ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(this.schemaRegistryClientProperties.getUrl());
        return apiClient;
    }

    @Bean
    public JsonSchemaApi jsonSchemaApi(final ApiClient apiClient) {
        return new JsonSchemaApi(apiClient);
    }

}

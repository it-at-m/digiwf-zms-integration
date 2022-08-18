package io.muenchendigital.digiwf.integration.zms.service;

import io.muenchendigital.digiwf.integration.zms.gen.api.JsonSchemaApi;
import io.muenchendigital.digiwf.integration.zms.properties.ZmsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZmsService {

    private final ZmsProperties properties;

    private final JsonSchemaApi jsonSchemaApi;

    public String createEntry(final CreateZmsEntryEvent event) {
        log.info("entry created: {}", event);
        log.info("Config: " + this.properties.getConfigprop());
        return "meineTolleId";
    }
}

package io.muenchendigital.digiwf.integration.zms.service;

import io.muenchendigital.digiwf.integration.zms.gen.api.JsonSchemaApi;
import io.muenchendigital.digiwf.integration.zms.properties.ZmsProperties;
import io.muenchendigital.digiwf.spring.cloudstream.utils.api.streaming.process.service.StartProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZmsService {

    private final ZmsProperties properties;

    private final JsonSchemaApi jsonSchemaApi;
    private final StartProcessService startProcessService;

    /**
     * von digiwf
     */
    public String createEntry(final CreateZmsEntryEvent event) {
        log.info("entry created: {}", event);
        return "meineTolleId";
    }

    /**
     * an digiwf
     */
    public void startAppointment(final Appointment appointment) {
        this.startProcessService.startProcess("ZMS_Prozess", Map.of("kunde", appointment.getCustomer()));
        log.info("appointment created: {}", appointment);
    }
}

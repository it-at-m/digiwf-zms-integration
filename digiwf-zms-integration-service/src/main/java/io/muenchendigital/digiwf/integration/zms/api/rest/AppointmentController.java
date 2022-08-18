package io.muenchendigital.digiwf.integration.zms.api.rest;

import io.muenchendigital.digiwf.integration.zms.service.Appointment;
import io.muenchendigital.digiwf.integration.zms.service.ZmsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@Transactional
@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@Tag(name = "Appointment", description = "API to handle appointments")
public class AppointmentController {

    private final ZmsService zmsService;

    @PostMapping(value = "/start")
    public void createAppointment(@RequestBody final Appointment appointment) {
        try {
            this.zmsService.startAppointment(appointment);
        } catch (final Exception e) {
            log.error(e.toString());
        }
    }

}

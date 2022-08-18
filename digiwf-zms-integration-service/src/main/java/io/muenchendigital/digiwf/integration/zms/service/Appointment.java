/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2020
 */
package io.muenchendigital.digiwf.integration.zms.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Start an appointment.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    /**
     * customer
     */
    private String customer;

}

// src/main/java/com/diamco/v1/entities/dtos/alertes/UpdateStatutAlerteDTO.java
package com.diamco.v1.entities.dtos.alertes;

import com.diamco.v1.entities.enums.StatutAlerte;

import java.time.Instant;

public record UpdateStatutAlerteDTO(
        StatutAlerte statut,
        Instant dateResolution
) {}

// src/main/java/com/diamco/v1/entities/dtos/alertes/AlerteDTO.java
package com.diamco.v1.entities.dtos.alertes;

import com.diamco.v1.entities.enums.StatutAlerte;
import java.time.Instant;

public record AlerteDTO(
        String id,
        String titre,
        String message,
        String photoPath,
        Instant dateSoumission,
        Instant dateResolution,
        StatutAlerte statut
) {}

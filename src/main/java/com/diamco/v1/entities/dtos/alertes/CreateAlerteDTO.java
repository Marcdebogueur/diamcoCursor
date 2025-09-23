// src/main/java/com/diamco/v1/entities/dtos/alertes/CreateAlerteDTO.java
package com.diamco.v1.entities.dtos.alertes;

import org.springframework.web.multipart.MultipartFile;

public record CreateAlerteDTO(
        String titre,
        String message,
        MultipartFile photo
) {}

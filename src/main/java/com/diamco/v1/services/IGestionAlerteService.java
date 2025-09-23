// src/main/java/com/diamco/v1/services/IGestionAlerteService.java
package com.diamco.v1.services;

import com.diamco.v1.entities.dtos.alertes.AlerteDTO;
import com.diamco.v1.entities.dtos.alertes.CreateAlerteDTO;
import org.springframework.http.ResponseEntity;
import com.diamco.v1.payload.ApiResponse;
import org.springframework.security.core.Authentication;

public interface IGestionAlerteService {
    ResponseEntity<ApiResponse> create(CreateAlerteDTO dto, Authentication authentication);
    ResponseEntity<ApiResponse> list(Authentication authentication);
}

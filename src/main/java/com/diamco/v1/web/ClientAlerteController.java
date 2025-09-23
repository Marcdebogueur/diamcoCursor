// src/main/java/com/diamco/v1/controllers/ClientAlerteController.java
package com.diamco.v1.web;

import com.diamco.v1.entities.dtos.alertes.CreateAlerteDTO;
import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.services.IGestionAlerteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/client/alertes")
@RequiredArgsConstructor
public class ClientAlerteController {

    private final IGestionAlerteService gestionAlerteService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(
            @RequestParam String titre,
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "file") MultipartFile photo,
            Authentication authentication
    ) {
        return gestionAlerteService.create(new CreateAlerteDTO(titre, message, photo), authentication);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> list(Authentication authentication) {
        return gestionAlerteService.list(authentication);
    }
}

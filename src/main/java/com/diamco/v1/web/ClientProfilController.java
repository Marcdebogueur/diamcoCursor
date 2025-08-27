package com.diamco.v1.web;

import com.diamco.v1.entities.dtos.*;
import com.diamco.v1.services.IGestionProfilClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/userprofil")
@RequiredArgsConstructor
public class ClientProfilController {

    private final IGestionProfilClient gestionProfilClient;

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientProfilDTO> getProfil(Authentication authentication) {
        return gestionProfilClient.getProfil(authentication.getName());
    }

    @PutMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> updateProfil(Authentication authentication, @RequestBody ClientUpdateDTO dto) {
        return gestionProfilClient.updateProfil(authentication.getName(), dto);
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody PasswordUpdateDTO dto) {
        return gestionProfilClient.updatePassword(authentication.getName(), dto);
    }
}

package com.diamco.v1.web;

import com.diamco.v1.entities.dtos.*;
import com.diamco.v1.services.IGestionProfilTechnicien;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/technicien/userprofil")
@RequiredArgsConstructor
public class TechnicienProfilController {

    private final IGestionProfilTechnicien gestionProfilTechnicien;

    @GetMapping
    public ResponseEntity<TechnicienProfilDTO> getProfil(Authentication authentication) {
        log.info("Tentative d'accès au profil pour: {}", authentication.getName());
        log.info("Authorities: {}", authentication.getAuthorities());

        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication null ou nom null");
            return ResponseEntity.badRequest().build();
        }

        return gestionProfilTechnicien.getProfil(authentication.getName());
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody PasswordUpdateDTO dto) {
        log.info("Tentative de mise à jour du mot de passe pour: {}", authentication.getName());

        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication null ou nom null");
            return ResponseEntity.badRequest().build();
        }

        return gestionProfilTechnicien.updatePassword(authentication.getName(), dto);
    }
}
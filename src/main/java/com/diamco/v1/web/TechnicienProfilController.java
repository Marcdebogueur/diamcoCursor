package com.diamco.v1.web;

import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
import com.diamco.v1.entities.dtos.TechnicienProfilDTO;
import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.services.IGestionProfilTechnicien;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse> getProfil(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication null ou nom null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Utilisateur non authentifié")
            );
        }

        return gestionProfilTechnicien.getProfil(authentication.getName());
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> updatePassword(Authentication authentication, @RequestBody PasswordUpdateDTO dto) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication null ou nom null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Utilisateur non authentifié")
            );
        }

        return gestionProfilTechnicien.updatePassword(authentication.getName(), dto);
    }
}

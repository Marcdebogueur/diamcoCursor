package com.diamco.v1.web;

import com.diamco.v1.entities.dtos.ClientUpdateDTO;
import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.services.IGestionProfilClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/client/userprofil")
@RequiredArgsConstructor
public class ClientProfilController {

    private final IGestionProfilClient gestionProfilClient;

    @GetMapping
    public ResponseEntity<ApiResponse> getProfil(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication null ou nom null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Utilisateur non authentifié")
            );
        }
        return gestionProfilClient.getProfil(authentication.getName());
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateProfil(Authentication authentication, @RequestBody ClientUpdateDTO dto) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication null ou nom null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Utilisateur non authentifié")
            );
        }
        return gestionProfilClient.updateProfil(authentication.getName(), dto);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> updatePassword(Authentication authentication, @RequestBody PasswordUpdateDTO dto) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication null ou nom null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Utilisateur non authentifié")
            );
        }
        return gestionProfilClient.updatePassword(authentication.getName(), dto);
    }
}

package com.diamco.v1.web;

import com.diamco.v1.entities.dtos.*;
import com.diamco.v1.services.IGestionProfilTechnicien;
import com.diamco.v1.settings.JwtUtils;
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
        return gestionProfilTechnicien.getProfil(authentication.getName());
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody PasswordUpdateDTO dto) {
        return gestionProfilTechnicien.updatePassword(authentication.getName(), dto);
    }
}

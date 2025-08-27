package com.diamco.v1.services;

import com.diamco.v1.entities.dtos.TechnicienProfilDTO;
import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
import org.springframework.http.ResponseEntity;

public interface IGestionProfilTechnicien {
    ResponseEntity<TechnicienProfilDTO> getProfil(String email);
    ResponseEntity<?> updatePassword(String email, PasswordUpdateDTO dto);
}

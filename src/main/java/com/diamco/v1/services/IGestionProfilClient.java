package com.diamco.v1.services;

import com.diamco.v1.entities.dtos.ClientProfilDTO;
import com.diamco.v1.entities.dtos.ClientUpdateDTO;
import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
import org.springframework.http.ResponseEntity;

public interface IGestionProfilClient {
    ResponseEntity<ClientProfilDTO> getProfil(String email);
    ResponseEntity<?> updateProfil(String email, ClientUpdateDTO dto);
    ResponseEntity<?> updatePassword(String email, PasswordUpdateDTO dto);
}

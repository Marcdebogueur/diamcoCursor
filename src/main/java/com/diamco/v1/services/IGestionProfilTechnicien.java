package com.diamco.v1.services;

import com.diamco.v1.entities.dtos.TechnicienProfilDTO;
import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
import com.diamco.v1.payload.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface IGestionProfilTechnicien {
    public ResponseEntity<ApiResponse> getProfil(String email);
    ResponseEntity<ApiResponse> updatePassword(String email, PasswordUpdateDTO dto);

}

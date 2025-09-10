package com.diamco.v1.services;

import com.diamco.v1.entities.dtos.ClientUpdateDTO;
import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
import com.diamco.v1.payload.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface IGestionProfilClient {
    public ResponseEntity<ApiResponse> getProfil(String email);
    public ResponseEntity<ApiResponse> updateProfil(String email, ClientUpdateDTO dto);
    public ResponseEntity<ApiResponse> updatePassword(String email, PasswordUpdateDTO dto);
}

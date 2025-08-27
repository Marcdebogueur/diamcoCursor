package com.diamco.v1.services;

import com.diamco.v1.entities.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAuth {

    public ResponseEntity<?> register(Utilisateur credential);
    public ResponseEntity<?> login(Utilisateur credential);

    public ResponseEntity<?> logout(String authHeader);
}

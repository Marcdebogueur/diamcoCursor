package com.diamco.v1.web;

import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.services.IAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuth auth;

    @PostMapping("/register")
    //@PreAuthorize("permitAll()")
    public ResponseEntity<?> register(@RequestBody Utilisateur credential) {
        log.info("AuthController.login()...");
        return auth.register(credential);
    }

    @PostMapping("/login")
    //@PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestBody Utilisateur credential) {
        log.info("AuthController.login()...");
        return auth.login(credential);
    }

    @PostMapping("/logout")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        log.info("AuthController.logout()...");
        return auth.logout(authHeader);
    }


}

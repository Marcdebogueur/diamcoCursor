package com.diamco.v1.web;

import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.services.IAuth;
import com.diamco.v1.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@CrossOrigin(origins = "exp://172.17.4.82:8081")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuth auth;
    private final PasswordResetService resetService;

    @PostMapping("/register")
    // @PreAuthorize("permitAll()")
    public ResponseEntity<?> register(@RequestBody Utilisateur credential) {
        log.info("AuthController.register()...");
        return auth.register(credential);
    }

    @PostMapping("/test")
    public ResponseEntity<?> test() {
        log.info("AuthController.test()...");
        return ResponseEntity.ok("Test successful");
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

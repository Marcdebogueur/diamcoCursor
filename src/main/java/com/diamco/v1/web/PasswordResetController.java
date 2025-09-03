package com.diamco.v1.web;

import com.diamco.v1.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService resetService;

    // Demander le code
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        resetService.sendResetCode(email);
        return ResponseEntity.ok(Map.of("message", "Code envoyé sur votre email"));
    }

    // Vérifier le code
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = resetService.verifyCode(email, code);
        if (!valid) return ResponseEntity.badRequest().body(Map.of("message", "Code invalide ou expiré"));
        return ResponseEntity.ok(Map.of("message", "Code valide"));
    }

    // Réinitialiser le mot de passe
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        resetService.resetPassword(email, newPassword);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }
}

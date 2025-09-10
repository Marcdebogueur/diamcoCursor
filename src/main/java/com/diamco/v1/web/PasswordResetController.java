package com.diamco.v1.web;

import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.services.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        try {
            resetService.sendResetCode(email);
            return ResponseEntity.ok(
                    ApiResponse.success(HttpStatus.OK.value(), "Code envoyé sur votre email", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }

    // Vérifier le code
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = resetService.verifyCode(email, code);
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Code invalide ou expiré")
            );
        }
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Code valide", null)
        );
    }

    // Réinitialiser le mot de passe
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            resetService.resetPassword(email, newPassword);
            return ResponseEntity.ok(
                    ApiResponse.success(HttpStatus.OK.value(), "Mot de passe modifié avec succès", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }
}

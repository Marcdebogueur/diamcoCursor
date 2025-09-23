package com.diamco.v1.web;

import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.payload.ResponseApi;
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




   
    // ========================
    // CONTROLLER : forgotPassword
    // ========================
    @PostMapping("/forgot")
    public ResponseEntity<ResponseApi> forgotPassword(@RequestParam String email) {
        try {
            // On appelle le service qui envoie le code
            ResponseApi response = resetService.sendResetCode(email);
            System.out.println("Réponse envoyée: " + response.getMessage());

            // Retourne la réponse directement (succès ou erreur déjà gérée)
            return ResponseEntity.status(response.getStatus()).body(response);

        } catch (Exception e) {
            // Gestion d'erreurs non prévues
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseApi.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Une erreur inattendue est survenue")
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

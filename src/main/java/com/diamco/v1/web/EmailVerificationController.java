package com.diamco.v1.web;

import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.services.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * Demander un code de vérification d'email
     * L'utilisateur doit être authentifié
     */
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse> sendVerificationCode(Authentication authentication) {
        try {
            // Extraire l'email de l'utilisateur authentifié
            String email = authentication.getName();

            emailVerificationService.sendVerificationCode(email);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK.value(),
                            "Code de vérification envoyé sur votre email",
                            null
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
            );
        }
    }

    /**
     * Vérifier le code saisi par l'utilisateur
     */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse> verifyCode(
            @RequestParam String code,
            Authentication authentication) {
        try {
            // Extraire l'email de l'utilisateur authentifié
            String email = authentication.getName();

            boolean isValid = emailVerificationService.verifyCode(email, code);

            if (isValid) {
                return ResponseEntity.ok(
                        ApiResponse.success(
                                HttpStatus.OK.value(),
                                "Email vérifié avec succès ! Votre compte est maintenant activé.",
                                null
                        )
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error(
                                HttpStatus.BAD_REQUEST.value(),
                                "Code invalide ou expiré. Veuillez demander un nouveau code."
                        )
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage())
            );
        }
    }

    /**
     * Vérifier le statut de vérification de l'email
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getVerificationStatus(Authentication authentication) {
        try {
            String email = authentication.getName();
            boolean isVerified = emailVerificationService.isEmailVerified(email);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK.value(),
                            isVerified ? "Email vérifié" : "Email non vérifié",
                            new EmailVerificationStatus(isVerified)
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage())
            );
        }
    }

    // Classe interne pour la réponse du statut
    public record EmailVerificationStatus(boolean isEmailVerified) {}
}
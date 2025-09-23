package com.diamco.v1.services;

import com.diamco.v1.entities.PasswordResetToken;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.payload.ResponseApi;
import com.diamco.v1.repository.PasswordResetTokenRepository;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

   // ========================
    // SERVICE : sendResetCode
    // ========================
    public ResponseApi sendResetCode(String email) {
        System.out.println("Sending reset code to email: " + email);

        // Vérification si l'utilisateur existe
        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("Email not found: " + email);
            return ResponseApi.error(HttpStatus.BAD_REQUEST.value(), "Email non trouvé");
        }

        // Génération d'un code aléatoire à 6 chiffres
        String code = String.format("%06d", new Random().nextInt(1000000));

        // Création d'un token de réinitialisation
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setToken(code);
        token.setExpiration(LocalDateTime.now().plusMinutes(10)); // expiration après 10 minutes
        tokenRepository.save(token);

        try {
            // Préparation de l'email HTML
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = "<!DOCTYPE html>"
                    + "<html lang=\"fr\">"
                    + "<head>"
                    + "    <meta charset=\"UTF-8\">"
                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "    <title>Réinitialisation de mot de passe</title>"
                    + "    <style>"
                    + "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                    + "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }"
                    + "        .header { background-color: #0E3643; padding: 20px; color: #ffffff; text-align: center; font-size: 24px; font-weight: bold; }"
                    + "        .content { padding: 30px; line-height: 1.6; color: #000000; }"
                    + "        .code-block { background-color: #008BBB; padding: 15px; text-align: center; border-radius: 5px; font-size: 28px; font-weight: bold; letter-spacing: 5px; color: #ffffff; }"
                    + "        .footer { background-color: #0E3643; padding: 20px; text-align: center; font-size: 12px; color: #FFFFFF; }"
                    + "    </style>"
                    + "</head>"
                    + "<body>"
                    + "    <div class=\"email-container\">"
                    + "        <div class=\"header\">Diamco</div>"
                    + "        <div class=\"content\">"
                    + "            <h2>Réinitialisation de votre mot de passe</h2>"
                    + "            <p>Bonjour,</p>"
                    + "            <p>Utilisez le code ci-dessous pour réinitialiser votre mot de passe. Ce code est valide pendant <b>10 minutes</b>.</p>"
                    + "            <div class=\"code-block\">" + code + "</div>"
                    + "            <p>Si vous n'avez pas demandé de réinitialisation, ignorez cet email.</p>"
                    + "            <p>Cordialement,<br>L'équipe Diamco</p>"
                    + "        </div>"
                    + "        <div class=\"footer\">© 2025 Diamco. Tous droits réservés.</div>"
                    + "    </div>"
                    + "</body>"
                    + "</html>";

            // Configuration et envoi de l'email
            helper.setText(htmlContent, true);
            helper.setTo(email);
            helper.setSubject("Réinitialisation de votre mot de passe");
            helper.setFrom("votre_email@domaine.com", "Diamco");

            mailSender.send(mimeMessage);

            // Réponse OK avec le code (si tu veux le retourner côté backend pour debug ou tests)
            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("expiration", token.getExpiration());

            return ResponseApi.success(HttpStatus.OK.value(), "Code envoyé sur votre email", data);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseApi.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erreur lors de l'envoi de l'email");
        }
    }


    //  vérifier le code
    public boolean verifyCode(String email, String code) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndTokenAndUsedFalse(email, code);
        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken token = tokenOpt.get();
        if (token.getExpiration().isBefore(LocalDateTime.now())) return false;

        token.setUsed(true); // marque le code comme utilisé
        tokenRepository.save(token);
        return true;
    }

    //  Modifier le mot de passe
    public void resetPassword(String email, String newPassword) {
        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("Utilisateur non trouvé");

        user.setMdpHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

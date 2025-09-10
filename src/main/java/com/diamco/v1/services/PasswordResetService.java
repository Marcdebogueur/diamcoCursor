package com.diamco.v1.services;

import com.diamco.v1.entities.PasswordResetToken;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.repository.PasswordResetTokenRepository;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public void sendResetCode(String email) {
        System.out.println("Sending reset code to email: " + email);
        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email non trouvé");
        }

        String code = String.format("%04d", new Random().nextInt(10000));
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setToken(code);
        token.setExpiration(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(token);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = "<!DOCTYPE html>"
                    + "<html lang=\"fr\">"
                    + "<head>"
                    + "    <meta charset=\"UTF-8\">"
                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "    <title>Réinitialisation de mot de passe</title>"
                    + "    <style>"
                    + "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                    + "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }"
                    + "        .header { background-color: #0E3643; padding: 20px; color: #ffffff; text-align: center; font-size: 24px; font-weight: bold; }"
                    + "        .content { padding: 30px; line-height: 1.6; color: #000000; }"
                    + "        .code-block { background-color: #008BBB; padding: 15px; text-align: center; border-radius: 5px; font-size: 28px; font-weight: bold; letter-spacing: 5px; color: #ffffff; }"
                    + "        .footer { background-color: #0E3643; padding: 20px; text-align: center; font-size: 12px; color: #FFFFFF; }"
                    + "        .logo-container { text-align: center; padding: 20px 0; }"
                    + "        .logo { max-width: 150px; height: auto; }"
                    + "    </style>"
                    + "</head>"
                    + "<body>"
                    + "    <div class=\"email-container\">"
                    + "        <div class=\"header\">"
                    + "            Diamco"
                    + "        </div>"
                    + "        <div class=\"content\">"
                    + "            <div class=\"logo-container\">"
                    + "                <img src=\"./Groupe6.png\" alt=\"Logo Diamco\" class=\"logo\">"
                    + "            </div>"
                    + "            <h2>Réinitialisation de votre mot de passe</h2>"
                    + "            <p>Bonjour,</p>"
                    + "            <p>Nous avons reçu une demande de réinitialisation de mot de passe pour votre compte Diamco. Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer cet e-mail.</p>"
                    + "            <p>Utilisez le code ci-dessous pour finaliser la réinitialisation de votre mot de passe. Ce code est valide pour **10 minutes**.</p>"
                    + "            <div class=\"code-block\">"
                    + code // ✅ La variable 'code' est insérée directement ici
                    + "            </div>"
                    + "            <p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>"
                    + "            <p>Cordialement,<br>L'équipe Diamco</p>"
                    + "        </div>"
                    + "        <div class=\"footer\">"
                    + "            © 2025 Diamco. Tous droits réservés.<br>"
                    + "        </div>"
                    + "    </div>"
                    + "</body>"
                    + "</html>";

            // Mettez l'URL de votre logo ici
            htmlContent = htmlContent.replace("URL_DE_VOTRE_LOGO", "./Groupe6.png");

            helper.setText(htmlContent, true);
            helper.setTo(email);
            helper.setSubject("Réinitialisation de votre mot de passe");
            helper.setFrom("votre_email@domaine.com", "Diamco");

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
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

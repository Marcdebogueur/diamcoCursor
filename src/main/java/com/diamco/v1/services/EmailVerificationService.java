package com.diamco.v1.services;

import com.diamco.v1.entities.EmailVerificationToken;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.repository.EmailVerificationTokenRepository;
import com.diamco.v1.repository.UtilisateurRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UtilisateurRepository userRepository;
    private final JavaMailSender mailSender;

    /**
     * Envoie un code de vérification à 6 chiffres à l'utilisateur authentifié
     */
    public void sendVerificationCode(String email) {
        System.out.println("Envoi du code de vérification à l'email: " + email);

        // Vérifier que l'utilisateur existe
        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email non trouvé");
        }

        // Vérifier si l'email est déjà vérifié
        if (user.isEmailVerifie()) {
            throw new RuntimeException("Email déjà vérifié");
        }

        // Générer un code à 6 chiffres
        String code = String.format("%06d", new Random().nextInt(1000000));

        // Supprimer les anciens tokens pour cet email (optionnel)
        tokenRepository.deleteByEmail(email);

        // Créer et sauvegarder le token
        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail(email);
        token.setToken(code);
        token.setExpiration(LocalDateTime.now().plusMinutes(15)); // Expire après 15 minutes
        tokenRepository.save(token);

        // Envoyer l'email
        sendVerificationEmail(email, code, user.getPrenom(), user.getNom());
    }

    /**
     * Vérifie le code saisi par l'utilisateur
     */
    @Transactional
    public boolean verifyCode(String email, String code) {
        // Chercher le token valide
        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByEmailAndTokenAndUsedFalse(email, code);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        EmailVerificationToken token = tokenOpt.get();

        // Vérifier l'expiration
        if (token.getExpiration().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Marquer le token comme utilisé
        token.setUsed(true);
        tokenRepository.save(token);

        // Mettre à jour l'utilisateur - marquer l'email comme vérifié
        Utilisateur user = userRepository.findByEmail(email);
        if (user != null) {
            user.setEmailVerifie(true);
            userRepository.save(user);
        }

        return true;
    }

    /**
     * Envoie l'email de vérification avec un design professionnel
     */
    private void sendVerificationEmail(String email, String code, String prenom, String nom) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String nomComplet = (prenom != null ? prenom + " " : "") + (nom != null ? nom : "");
            if (nomComplet.trim().isEmpty()) {
                nomComplet = "Cher utilisateur";
            } else {
                nomComplet = "Cher " + nomComplet.trim();
            }

            String htmlContent = "<!DOCTYPE html>"
                    + "<html lang=\"fr\">"
                    + "<head>"
                    + "    <meta charset=\"UTF-8\">"
                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "    <title>Vérification de votre email</title>"
                    + "    <style>"
                    + "        body { font-family: 'Lato', 'Dubai bold', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                    + "        .email-container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }"
                    + "        .header { background-color: #0E3643; padding: 20px; color: #ffffff; text-align: center; font-size: 24px; font-weight: bold; }"
                    + "        .content { padding: 30px; line-height: 1.6; color: #333333; }"
                    + "        .code-block { background-color: #008BBB; padding: 20px; text-align: center; border-radius: 8px; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #ffffff; margin: 25px 0; }"
                    + "        .footer { background-color: #0E3643; padding: 20px; text-align: center; font-size: 12px; color: #FFFFFF; }"
                    + "        .warning { background-color: #FFF3CD; border-left: 4px solid #FFC107; padding: 15px; margin: 20px 0; color: #856404; }"
                    + "        .success-icon { color: #28A745; font-size: 48px; text-align: center; margin-bottom: 20px; }"
                    + "    </style>"
                    + "</head>"
                    + "<body>"
                    + "    <div class=\"email-container\">"
                    + "        <div class=\"header\">"
                    + "            <div>🔐 Diamco</div>"
                    + "        </div>"
                    + "        <div class=\"content\">"
                    + "            <div class=\"success-icon\">✉️</div>"
                    + "            <h2 style=\"color: #0E3643; text-align: center;\">Vérification de votre adresse email</h2>"
                    + "            <p>" + nomComplet + ",</p>"
                    + "            <p>Merci de vous être inscrit sur <strong>Diamco</strong> ! Pour finaliser votre inscription et sécuriser votre compte, nous devons vérifier votre adresse email.</p>"
                    + "            <p>Veuillez saisir le code de vérification ci-dessous dans l'application :</p>"
                    + "            <div class=\"code-block\">"
                    + code
                    + "            </div>"
                    + "            <div class=\"warning\">"
                    + "                <strong>⏰ Important :</strong> Ce code expire dans <strong>15 minutes</strong>. Si vous n'utilisez pas ce code dans les 15 prochaines minutes, vous devrez en demander un nouveau."
                    + "            </div>"
                    + "            <p>Si vous n'avez pas demandé cette vérification, vous pouvez ignorer cet email en toute sécurité.</p>"
                    + "            <p>Une fois votre email vérifié, vous pourrez accéder à toutes les fonctionnalités de votre compte Diamco.</p>"
                    + "            <p>Cordialement,<br><strong>L'équipe Diamco</strong></p>"
                    + "        </div>"
                    + "        <div class=\"footer\">"
                    + "            © 2025 Diamco. Tous droits réservés.<br>"
                    + "            Cet email a été envoyé pour vérifier votre adresse email."
                    + "        </div>"
                    + "    </div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);
            helper.setTo(email);
            helper.setSubject("🔐 Vérifiez votre adresse email - Code: " + code);
            helper.setFrom("donaldnoukimi@gmail.com", "Diamco - Vérification");

            mailSender.send(mimeMessage);
            System.out.println("Email de vérification envoyé avec succès à: " + email);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de vérification: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email de vérification");
        }
    }

    /**
     * Vérifie si l'utilisateur a un email vérifié
     */
    public boolean isEmailVerified(String email) {
        Utilisateur user = userRepository.findByEmail(email);
        return user != null && user.isEmailVerifie();
    }
}
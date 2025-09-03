package com.diamco.v1.services;

import com.diamco.v1.entities.*;
import com.diamco.v1.repository.UtilisateurRepository;
import com.diamco.v1.settings.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuth{

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository userRepository;

    public ResponseEntity<Map<String, Object>> register(Utilisateur credential) {
        Map<String, Object> response = new HashMap<>();

        // Vérification si l'utilisateur existe déjà
        if (userRepository.findByNom(credential.getNom()) != null 
            || userRepository.findByEmail(credential.getEmail()) != null) {
            response.put("status", "error");
            response.put("message", "L'utilisateur existe déjà");
            return ResponseEntity.ok(response); // 400
        }

        // Hash du mot de passe et sauvegarde
        credential.setMdpHash(passwordEncoder.encode(credential.getMdpHash()));
        Utilisateur savedUser = userRepository.save(credential);

        // ✅ Connexion automatique : mise à jour du flag et génération du token
        savedUser.setAuthentificated(true);
        userRepository.save(savedUser);
        String token = jwtUtils.generateToken(savedUser);

        // Préparation de la réponse JSON standardisée
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", savedUser);

        response.put("status", "success");
        response.put("message", "Inscription réussie et utilisateur connecté");
        response.put("data", data);

        return ResponseEntity.ok(response); // 200
    }
    

    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Test successful");
    }

    public ResponseEntity<Map<String, Object>> login(Utilisateur credential) {
        Map<String, Object> response = new HashMap<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getEmail(),
                            credential.getMdpHash()
                    )
            );

            if (authentication.isAuthenticated()) {
                Utilisateur user = userRepository.findByEmail(credential.getEmail());

                // Mise à jour du flag isAuthentificated
                user.setAuthentificated(true);
                userRepository.save(user);

                String token = jwtUtils.generateToken(user);

                // Préparer la réponse JSON standardisée
                response.put("status", "success");
                response.put("message", "Connexion réussie");
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", user); 
                response.put("data", data);

                return ResponseEntity.ok(response);
            }

            response.put("status", "error");
            response.put("message", "Identifiant ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            response.put("status", "error");
            response.put("message", "Identifiant ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @Override
    public ResponseEntity<?> logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token manquant");
        }

        String token = authHeader.substring(7);
        String email = jwtUtils.extractEmail(token);

        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable");
        }

        user.setAuthentificated(false);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Déconnexion réussie");
        response.put("isAuthentificated", user.isAuthentificated());

        return ResponseEntity.ok(response);
    }

}

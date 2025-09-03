package com.diamco.v1.services;

import com.diamco.v1.entities.*;
import com.diamco.v1.entities.dtos.RegisterRequest;
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

    @Override
    public ResponseEntity<?> register(Utilisateur credential) {
        if (userRepository.findByNom(credential.getNom()) != null || userRepository.findByEmail(credential.getEmail()) != null) {
            return ResponseEntity.badRequest().body("L'utilisateur existe déjà");
        }
        credential.setMdpHash(passwordEncoder.encode(credential.getMdpHash()));
        return ResponseEntity.ok(userRepository.save(credential));
    }

    @Override
    public ResponseEntity<?> login(Utilisateur credential) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getEmail(),
                            credential.getMdpHash()
                    )
            );

            if (authentication.isAuthenticated()) {
                Utilisateur user = userRepository.findByEmail(credential.getEmail());

                // ✅ Mise à jour du flag isAuthentificated
                user.setAuthentificated(true);
                userRepository.save(user);

                String token = jwtUtils.generateToken(user);

                Map<String, Object> authData = new HashMap<>();
                authData.put("token", token);
                /*authData.put("type", "Bearer");
                authData.put("nom", user.getNom());
                authData.put("role", user.getRole());*/

                return ResponseEntity.ok(authData);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiant ou mot de passe incorrect");

        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiant ou mot de passe incorrect");
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

package com.diamco.v1.services;

import com.diamco.v1.entities.Technicien;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.entities.dtos.*;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestionProfilTechnicienService implements IGestionProfilTechnicien {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<TechnicienProfilDTO> getProfil(String email) {
        log.info("Recherche du profil pour l'email: {}", email);

        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("Aucun utilisateur trouvé avec l'email: {}", email);
            return ResponseEntity.notFound().build();
        }

        log.info("Utilisateur trouvé: {} avec le rôle: {}", user.getEmail(), user.getRole());

        if (!(user instanceof Technicien technicien)) {
            log.error("L'utilisateur {} n'est pas un technicien. Type: {}", email, user.getClass().getSimpleName());
            return ResponseEntity.badRequest().build();
        }

        log.info("Création du DTO pour le technicien: {}", technicien.getEmail());

        TechnicienProfilDTO dto = new TechnicienProfilDTO();
        dto.setNom(technicien.getNom());
        dto.setPrenom(technicien.getPrenom());
        dto.setTelephone(technicien.getTelephone());
        dto.setAdresse(technicien.getAdresse());
        dto.setEmail(technicien.getEmail());
        dto.setSpecialite(technicien.getSpecialite());

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> updatePassword(String email, PasswordUpdateDTO dto) {
        log.info("Tentative de mise à jour du mot de passe pour: {}", email);

        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("Aucun utilisateur trouvé avec l'email: {}", email);
            return ResponseEntity.notFound().build();
        }

        if (!(user instanceof Technicien technicien)) {
            log.error("L'utilisateur {} n'est pas un technicien", email);
            return ResponseEntity.badRequest().body("Utilisateur n'est pas un technicien");
        }

        if (!passwordEncoder.matches(dto.getAncienMdp(), technicien.getMdpHash())) {
            log.error("Ancien mot de passe incorrect pour: {}", email);
            return ResponseEntity.badRequest().body("Ancien mot de passe incorrect !");
        }

        technicien.setMdpHash(passwordEncoder.encode(dto.getNouveauMdp()));
        userRepository.save(technicien);

        log.info("Mot de passe mis à jour avec succès pour: {}", email);
        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès !"));
    }
}
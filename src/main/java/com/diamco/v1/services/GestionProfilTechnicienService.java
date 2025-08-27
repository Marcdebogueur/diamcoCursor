package com.diamco.v1.services;

import com.diamco.v1.entities.Technicien;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.entities.dtos.*;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GestionProfilTechnicienService implements IGestionProfilTechnicien {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<TechnicienProfilDTO> getProfil(String email) {
        Utilisateur user = userRepository.findByEmail(email);
        if (!(user instanceof Technicien technicien)) {
            return ResponseEntity.badRequest().build();
        }

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
        Utilisateur user = userRepository.findByEmail(email);
        if (!(user instanceof Technicien technicien)) {
            return ResponseEntity.badRequest().body("Utilisateur n’est pas un technicien");
        }

        if (!passwordEncoder.matches(dto.getAncienMdp(), technicien.getMdpHash())) {
            return ResponseEntity.badRequest().body("Ancien mot de passe incorrect !");
        }

        technicien.setMdpHash(passwordEncoder.encode(dto.getNouveauMdp()));
        userRepository.save(technicien);
        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès !"));
    }


}

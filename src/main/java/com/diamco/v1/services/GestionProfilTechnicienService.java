package com.diamco.v1.services;

import com.diamco.v1.entities.Technicien;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
import com.diamco.v1.entities.dtos.TechnicienProfilDTO;
import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestionProfilTechnicienService implements IGestionProfilTechnicien {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse> getProfil(String email) {
        log.info("Recherche du profil pour l'email: {}", email);

        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("Aucun utilisateur trouvé avec l'email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Utilisateur non trouvé")
            );
        }

        if (!(user instanceof Technicien technicien)) {
            log.error("L'utilisateur {} n'est pas un technicien", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Utilisateur n'est pas un technicien")
            );
        }

        TechnicienProfilDTO dto = new TechnicienProfilDTO();
        dto.setNom(technicien.getNom());
        dto.setPrenom(technicien.getPrenom());
        dto.setTelephone(technicien.getTelephone());
        dto.setAdresse(technicien.getAdresse());
        dto.setEmail(technicien.getEmail());
        dto.setSpecialite(technicien.getSpecialite());

        log.info("Profil récupéré avec succès pour: {}", technicien.getEmail());

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Profil récupéré avec succès", dto)
        );
    }

    @Override
    public ResponseEntity<ApiResponse> updatePassword(String email, PasswordUpdateDTO dto) {
        log.info("Tentative de mise à jour du mot de passe pour: {}", email);

        Utilisateur user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("Utilisateur introuvable: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Utilisateur non trouvé")
            );
        }

        if (!(user instanceof Technicien technicien)) {
            log.error("L'utilisateur {} n'est pas un technicien", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Utilisateur n'est pas un technicien")
            );
        }

        if (!passwordEncoder.matches(dto.getAncienMdp(), technicien.getMdpHash())) {
            log.error("Ancien mot de passe incorrect pour: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Ancien mot de passe incorrect")
            );
        }

        technicien.setMdpHash(passwordEncoder.encode(dto.getNouveauMdp()));
        userRepository.save(technicien);

        log.info("Mot de passe mis à jour avec succès pour: {}", email);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Mot de passe mis à jour avec succès", null)
        );
    }
}

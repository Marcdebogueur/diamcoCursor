package com.diamco.v1.services;

import com.diamco.v1.entities.Client;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.entities.dtos.ClientProfilDTO;
import com.diamco.v1.entities.dtos.ClientUpdateDTO;
import com.diamco.v1.entities.dtos.PasswordUpdateDTO;
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
public class GestionProfilClientService implements IGestionProfilClient {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ApiResponse> getProfil(String email) {
        Utilisateur user = userRepository.findByEmail(email);

        if (user == null || !(user instanceof Client client)) {
            log.error("Utilisateur non trouvé ou n'est pas un client: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Client non trouvé")
            );
        }

        ClientProfilDTO dto = new ClientProfilDTO();
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setTelephone(client.getTelephone());
        dto.setAdresse(client.getAdresse());
        dto.setEmail(client.getEmail());

        log.info("Profil client récupéré: {}", email);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Profil récupéré avec succès", dto)
        );
    }

    @Override
    public ResponseEntity<ApiResponse> updateProfil(String email, ClientUpdateDTO dto) {
        Utilisateur user = userRepository.findByEmail(email);

        if (user == null || !(user instanceof Client client)) {
            log.error("Utilisateur non trouvé ou n'est pas un client: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Client non trouvé")
            );
        }

        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setAdresse(dto.getAdresse());

        userRepository.save(client);

        log.info("Profil client mis à jour: {}", email);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Profil mis à jour avec succès", null)
        );
    }

    @Override
    public ResponseEntity<ApiResponse> updatePassword(String email, PasswordUpdateDTO dto) {
        Utilisateur user = userRepository.findByEmail(email);

        if (user == null || !(user instanceof Client client)) {
            log.error("Utilisateur non trouvé ou n'est pas un client: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Client non trouvé")
            );
        }

        if (!passwordEncoder.matches(dto.getAncienMdp(), client.getMdpHash())) {
            log.error("Ancien mot de passe incorrect pour: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Ancien mot de passe incorrect")
            );
        }

        client.setMdpHash(passwordEncoder.encode(dto.getNouveauMdp()));
        userRepository.save(client);

        log.info("Mot de passe client mis à jour avec succès: {}", email);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Mot de passe mis à jour avec succès", null)
        );
    }
}

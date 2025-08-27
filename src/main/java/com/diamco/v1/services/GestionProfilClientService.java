package com.diamco.v1.services;

import com.diamco.v1.entities.Client;
import com.diamco.v1.entities.dtos.*;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GestionProfilClientService implements IGestionProfilClient {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ClientProfilDTO> getProfil(String email) {
        Client client = (Client) userRepository.findByEmail(email);
        if (client == null) return ResponseEntity.notFound().build();

        ClientProfilDTO dto = new ClientProfilDTO();
        dto.setNom(client.getNom());
        dto.setPrenom(client.getPrenom());
        dto.setTelephone(client.getTelephone());
        dto.setAdresse(client.getAdresse());
        dto.setEmail(client.getEmail());

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> updateProfil(String email, ClientUpdateDTO dto) {
        Client client = (Client) userRepository.findByEmail(email);
        if (client == null) return ResponseEntity.notFound().build();

        client.setNom(dto.getNom());
        client.setPrenom(dto.getPrenom());
        client.setAdresse(dto.getAdresse());

        userRepository.save(client);
        return ResponseEntity.ok("Profil mis à jour avec succès !");
    }

    @Override
    public ResponseEntity<?> updatePassword(String email, PasswordUpdateDTO dto) {
        Client client = (Client) userRepository.findByEmail(email);
        if (client == null) return ResponseEntity.notFound().build();

        if (!passwordEncoder.matches(dto.getAncienMdp(), client.getMdpHash())) {
            return ResponseEntity.badRequest().body("Ancien mot de passe incorrect !");
        }

        client.setMdpHash(passwordEncoder.encode(dto.getNouveauMdp()));
        userRepository.save(client);
        return ResponseEntity.ok("Mot de passe mis à jour avec succès !");
    }
}

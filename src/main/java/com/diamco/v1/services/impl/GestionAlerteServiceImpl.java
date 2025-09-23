package com.diamco.v1.services.impl;

import com.diamco.v1.entities.Alerte;
import com.diamco.v1.entities.Client;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.entities.dtos.alertes.AlerteDTO;
import com.diamco.v1.entities.dtos.alertes.CreateAlerteDTO;
import com.diamco.v1.entities.enums.StatutAlerte;
import com.diamco.v1.payload.ApiResponse;
import com.diamco.v1.repository.AlerteRepository;
import com.diamco.v1.repository.UtilisateurRepository;
import com.diamco.v1.services.IGestionAlerteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GestionAlerteServiceImpl implements IGestionAlerteService {

    private final UtilisateurRepository userRepository;
    private final AlerteRepository alerteRepository;

    @Value("${alertes.dir:src/main/resources/static/assets/alertes}")
    private String alertesDir;

    private static final AtomicInteger compteur = new AtomicInteger(1);

    @Override
    public ResponseEntity<ApiResponse> create(CreateAlerteDTO dto, Authentication authentication) {
        String email = authentication.getName();
        log.info("Création alerte pour {}", email);

        Utilisateur user = userRepository.findByEmail(email);
        if (!(user instanceof Client client)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Seuls les clients peuvent créer une alerte"));
        }

        Alerte alerte = new Alerte();
        alerte.setTitre(dto.titre());
        alerte.setMessage(dto.message());
        alerte.setClient(client);
        alerte.setDateSoumission(Instant.now());
        alerte.setStatut(StatutAlerte.NOUVELLE);

        // Gestion de la photo
        if (dto.photo() != null && !dto.photo().isEmpty()) {
            try {
                String photoPath = savePhoto(dto.photo());
                alerte.setPhotoPath(photoPath);
            } catch (IOException e) {
                log.error("Erreur lors du stockage de la photo", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erreur stockage photo: " + e.getMessage()));
            }
        }

        Alerte saved = alerteRepository.save(alerte);

        AlerteDTO response = new AlerteDTO(
                saved.getId(), saved.getTitre(), saved.getMessage(),
                saved.getPhotoPath(), saved.getDateSoumission(),
                saved.getDateResolution(), saved.getStatut()
        );

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Alerte créée avec succès", response));
    }

    private String savePhoto(MultipartFile photo) throws IOException {
        // Validation du fichier
        if (photo.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        // Vérifier le type de fichier
        String contentType = photo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Le fichier doit être une image");
        }

        // Vérifier la taille (max 5MB par exemple)
        if (photo.getSize() > 5 * 1024 * 1024) {
            throw new IOException("Le fichier est trop volumineux (max 5MB)");
        }

        String originalFilename = photo.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        // Générer un nom de fichier unique
        String fileName = "alerteDiamco_" + compteur.getAndIncrement() + extension;

        // Créer le chemin complet
        Path uploadPath = Paths.get(alertesDir);

        // Créer le dossier s'il n'existe pas
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Dossier créé: {}", uploadPath.toAbsolutePath());
        }

        // Chemin complet du fichier
        Path filePath = uploadPath.resolve(fileName);

        // Copier le fichier
        Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Photo sauvegardée: {}", filePath.toAbsolutePath());

        // Retourner le chemin relatif pour l'accès web
        return "/assets/alertes/" + fileName;
    }

    @Override
    public ResponseEntity<ApiResponse> list(Authentication authentication) {
        String email = authentication.getName();
        Utilisateur user = userRepository.findByEmail(email);
        if (!(user instanceof Client client)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Seuls les clients peuvent voir leurs alertes"));
        }

        List<AlerteDTO> alertes = alerteRepository.findByClient(client)
                .stream()
                .map(a -> new AlerteDTO(
                        a.getId(), a.getTitre(), a.getMessage(),
                        a.getPhotoPath(), a.getDateSoumission(),
                        a.getDateResolution(), a.getStatut()
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Alertes récupérées avec succès", alertes));
    }
}
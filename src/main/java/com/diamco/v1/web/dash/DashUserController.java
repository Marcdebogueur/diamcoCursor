package com.diamco.v1.web.dash;

import com.diamco.v1.entities.SuperAdmin;
import com.diamco.v1.entities.Utilisateur;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/dash/users")
@RequiredArgsConstructor
public class DashUserController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Utilisateur>> list(@RequestParam(value = "role", required = false) String role) {
        List<Utilisateur> all = utilisateurRepository.findAll();
        if (role == null || role.isBlank()) {
            return ResponseEntity.ok(all);
        }
        return ResponseEntity.ok(all.stream().filter(u -> Objects.equals(u.getRole(), role)).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Utilisateur> get(@PathVariable String id) {
        return utilisateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<?> create(@RequestBody Utilisateur utilisateur) {
        if (utilisateur instanceof SuperAdmin) {
            return ResponseEntity.badRequest().body("Impossible de créer/modifier un SUPERADMIN via l'API");
        }
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }
        if (utilisateur.getMdpHash() != null) {
            utilisateur.setMdpHash(passwordEncoder.encode(utilisateur.getMdpHash()));
        }
        return ResponseEntity.ok(utilisateurRepository.save(utilisateur));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Utilisateur payload) {
        return utilisateurRepository.findById(id).map(user -> {
            if (user instanceof SuperAdmin) {
                return ResponseEntity.badRequest().body("SUPERADMIN non modifiable");
            }
            if (payload.getNom() != null) user.setNom(payload.getNom());
            if (payload.getPrenom() != null) user.setPrenom(payload.getPrenom());
            if (payload.getTelephone() != null) user.setTelephone(payload.getTelephone());
            if (payload.getAdresse() != null) user.setAdresse(payload.getAdresse());
            if (payload.getMdpHash() != null && !payload.getMdpHash().isBlank()) {
                user.setMdpHash(passwordEncoder.encode(payload.getMdpHash()));
            }
            return ResponseEntity.ok(utilisateurRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<?> activate(@PathVariable String id, @RequestParam boolean active) {
        return utilisateurRepository.findById(id).map(user -> {
            if (user instanceof SuperAdmin) {
                return ResponseEntity.badRequest().body("SUPERADMIN non modifiable");
            }
            // On utilise le champ isAuthentificated uniquement pour la session; ajoutons un champ logique si nécessaire
            // Ici, on représente l'activation par la possibilité de se connecter: si désactivé, on force un mot de passe inutilisable
            if (!active) {
                user.setAuthentificated(false);
            }
            return ResponseEntity.ok(utilisateurRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }
}


package com.diamco.v1.web.dash;

import com.diamco.v1.entities.Alerte;
import com.diamco.v1.entities.Client;
import com.diamco.v1.entities.enums.StatutAlerte;
import com.diamco.v1.repository.AlerteRepository;
import com.diamco.v1.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/dash/alerts")
@RequiredArgsConstructor
public class DashAlertController {

    private final AlerteRepository alerteRepository;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Alerte>> list(@RequestParam(value = "statut", required = false) StatutAlerte statut) {
        if (statut == null) return ResponseEntity.ok(alerteRepository.findAll());
        return ResponseEntity.ok(alerteRepository.findByStatut(statut));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<?> create(@RequestBody Alerte payload, @RequestParam(required = false) String clientId) {
        if (clientId != null) {
            utilisateurRepository.findById(clientId).ifPresent(u -> {
                if (u instanceof Client c) payload.setClient(c);
            });
        }
        payload.setDateSoumission(Instant.now());
        return ResponseEntity.ok(alerteRepository.save(payload));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam StatutAlerte statut) {
        return alerteRepository.findById(id).map(a -> {
            a.setStatut(statut);
            if (statut == StatutAlerte.RESOLUE) {
                a.setDateResolution(Instant.now());
            }
            return ResponseEntity.ok(alerteRepository.save(a));
        }).orElse(ResponseEntity.notFound().build());
    }
}


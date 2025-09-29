package com.diamco.v1.web.dash;

import com.diamco.v1.entities.Dispositif;
import com.diamco.v1.entities.enums.StatutDispositif;
import com.diamco.v1.repository.DispositifRepository;
import com.diamco.v1.repository.ReleveCapteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/dash/dispositifs")
@RequiredArgsConstructor
public class DashDispositifController {

    private final DispositifRepository dispositifRepository;
    private final ReleveCapteurRepository releveCapteurRepository;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<Dispositif>> list(@RequestParam(value = "statut", required = false) StatutDispositif statut) {
        if (statut == null) return ResponseEntity.ok(dispositifRepository.findAll());
        return ResponseEntity.ok(dispositifRepository.findByStatut(statut));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Dispositif> get(@PathVariable String id) {
        return dispositifRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<Dispositif> create(@RequestBody Dispositif payload) {
        return ResponseEntity.ok(dispositifRepository.save(payload));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Dispositif payload) {
        return dispositifRepository.findById(id).map(d -> {
            if (payload.getNumeroSerie() != null) d.setNumeroSerie(payload.getNumeroSerie());
            if (payload.getZone() != null) d.setZone(payload.getZone());
            if (payload.getDateInstallation() != null) d.setDateInstallation(payload.getDateInstallation());
            if (payload.getStatut() != null) d.setStatut(payload.getStatut());
            if (payload.getTechnicienReferent() != null) d.setTechnicienReferent(payload.getTechnicienReferent());
            if (payload.getProprietaire() != null) d.setProprietaire(payload.getProprietaire());
            if (payload.getTypeFiltre() != null) d.setTypeFiltre(payload.getTypeFiltre());
            return ResponseEntity.ok(dispositifRepository.save(d));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/releves")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public ResponseEntity<?> getReleves(
            @PathVariable String id,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end) {
        return ResponseEntity.ok(releveCapteurRepository.findByDispositifBetween(id, start, end));
    }
}


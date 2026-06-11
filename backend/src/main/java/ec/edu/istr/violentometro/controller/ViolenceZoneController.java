package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.model.ViolenceZone;
import ec.edu.istr.violentometro.service.ViolenceZoneService;
import ec.edu.istr.violentometro.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/violence-zones")
@RequiredArgsConstructor
public class ViolenceZoneController {

    private final ViolenceZoneService zoneService;
    private final SecurityUtils       securityUtils;

    // Listar zonas del instituto autenticado
    @GetMapping
    public ResponseEntity<List<ViolenceZone>> getByInstituto() {
        Integer idInstituto = securityUtils.getIdInstituto();
        return ResponseEntity.ok(zoneService.findByInstituto(idInstituto));
    }

    // Listar plantillas globales (para referencia o copia manual)
    @GetMapping("/plantillas")
    public ResponseEntity<List<ViolenceZone>> getPlantillas() {
        return ResponseEntity.ok(zoneService.findPlantillas());
    }

    // Crear zona propia
    @PostMapping
    public ResponseEntity<ViolenceZone> create(@RequestBody ViolenceZone zone) {
        Integer idInstituto = securityUtils.getIdInstituto();
        return new ResponseEntity<>(zoneService.create(zone, idInstituto), HttpStatus.CREATED);
    }

    // Actualizar zona propia
    @PutMapping("/{id}")
    public ResponseEntity<ViolenceZone> update(
            @PathVariable Integer id,
            @RequestBody ViolenceZone zone) {
        Integer idInstituto = securityUtils.getIdInstituto();
        return ResponseEntity.ok(zoneService.update(id, zone, idInstituto));
    }

    // Eliminar zona propia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Integer idInstituto = securityUtils.getIdInstituto();
        zoneService.delete(id, idInstituto);
        return ResponseEntity.noContent().build();
    }
}
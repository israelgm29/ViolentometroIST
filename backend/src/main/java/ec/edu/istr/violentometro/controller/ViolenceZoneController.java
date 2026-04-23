package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.ViolenceZoneDTO;
import ec.edu.istr.violentometro.service.ViolenceZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/violence-zones")
@RequiredArgsConstructor
class ViolenceZoneController {

    private final ViolenceZoneService violenceZoneService;

    @GetMapping
    public ResponseEntity<List<ViolenceZoneDTO>> getAll() {
        List<ViolenceZoneDTO> violenceZones = violenceZoneService.findAll();
        return ResponseEntity.ok(violenceZones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViolenceZoneDTO> getById(@PathVariable("id") Integer id) {
        // El Service lanza EntityNotFoundException si no existe (mapeado a 404)
        return ResponseEntity.ok(violenceZoneService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ViolenceZoneDTO> create(@RequestBody @Valid ViolenceZoneDTO violenceZoneDTO) {
        ViolenceZoneDTO savedViolenceZone = violenceZoneService.save(violenceZoneDTO);
        return new ResponseEntity<>(savedViolenceZone, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViolenceZoneDTO> update(@PathVariable("id") Integer id, @RequestBody @Valid ViolenceZoneDTO violenceZoneDTO) {
        // El Service lanza EntityNotFoundException si no existe (mapeado a 404)
        ViolenceZoneDTO updatedViolenceZone = violenceZoneService.updateOne(id, violenceZoneDTO);
        return ResponseEntity.ok(updatedViolenceZone);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // El Service lanza EntityNotFoundException si no existe (mapeado a 404)
        violenceZoneService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
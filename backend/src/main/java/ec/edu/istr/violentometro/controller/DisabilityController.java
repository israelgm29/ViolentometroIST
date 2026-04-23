package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.MasterCatalogDTO;
import ec.edu.istr.violentometro.service.DisabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/disabilities")
@RequiredArgsConstructor
public class DisabilityController {
    private final DisabilityService disabilityService;

    @GetMapping
    public ResponseEntity<List<MasterCatalogDTO>> getAll() {
        return ResponseEntity.ok(disabilityService.findAll());
    }

    @PostMapping
    public ResponseEntity<MasterCatalogDTO> create(@RequestBody MasterCatalogDTO dto) {
        return new ResponseEntity<>(disabilityService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasterCatalogDTO> update(@PathVariable Integer id, @RequestBody MasterCatalogDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(disabilityService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        disabilityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
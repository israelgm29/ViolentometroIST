package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.MasterCatalogDTO;
import ec.edu.istr.violentometro.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<List<MasterCatalogDTO>> getAll() {
        return ResponseEntity.ok(regionService.findAll());
    }

    @PostMapping
    public ResponseEntity<MasterCatalogDTO> create(@RequestBody MasterCatalogDTO dto) {
        return new ResponseEntity<>(regionService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasterCatalogDTO> update(@PathVariable Integer id, @RequestBody MasterCatalogDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(regionService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        regionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

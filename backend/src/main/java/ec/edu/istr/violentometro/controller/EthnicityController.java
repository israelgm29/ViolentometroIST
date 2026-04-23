package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.MasterCatalogDTO;
import ec.edu.istr.violentometro.service.EthnicityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ethnicities")
@RequiredArgsConstructor
public class EthnicityController {

    private final EthnicityService ethnicityService;

    @GetMapping
    public ResponseEntity<List<MasterCatalogDTO>> getAll() {
        return ResponseEntity.ok(ethnicityService.findAll());
    }

    @PostMapping
    public ResponseEntity<MasterCatalogDTO> create(@RequestBody MasterCatalogDTO dto) {
        return new ResponseEntity<>(ethnicityService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasterCatalogDTO> update(@PathVariable Integer id, @RequestBody MasterCatalogDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(ethnicityService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ethnicityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.MasterCatalogDTO;
import ec.edu.istr.violentometro.service.GenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genders")
@RequiredArgsConstructor
class GenderController {

    private final GenderService genderService;

    @GetMapping
    public ResponseEntity<List<MasterCatalogDTO>> getAll() {
        return ResponseEntity.ok(genderService.findAll());
    }

    @PostMapping
    public ResponseEntity<MasterCatalogDTO> create(@RequestBody MasterCatalogDTO dto) {
        return new ResponseEntity<>(genderService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MasterCatalogDTO> update(@PathVariable Integer id, @RequestBody MasterCatalogDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(genderService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        genderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

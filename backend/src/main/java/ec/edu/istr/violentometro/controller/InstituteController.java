package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.InstituteDTO;
import ec.edu.istr.violentometro.service.InstituteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/institutes")
@RequiredArgsConstructor // Inyección limpia con Lombok
class InstituteController {

    private final InstituteService instituteService;

    @GetMapping
    public ResponseEntity<List<InstituteDTO>> getAll() {
        // Usa el DTO de salida
        List<InstituteDTO> institutes = instituteService.findAll();
        return ResponseEntity.ok(institutes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstituteDTO> getById(@PathVariable("id") Integer id) {
        // El Service lanza EntityNotFoundException si no existe (mapeado a 404)
        return ResponseEntity.ok(instituteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InstituteDTO> create(@RequestBody @Valid InstituteDTO instituteDTO) {
        // Usa DTO de entrada y salida
        InstituteDTO savedInstitute = instituteService.save(instituteDTO);
        return new ResponseEntity<>(savedInstitute, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<InstituteDTO> update(@PathVariable("id") Integer id, @RequestBody @Valid InstituteDTO instituteDTO) {
        // El Service maneja la lógica de actualización segura y lanza 404 si no existe
        InstituteDTO updatedInstitute = instituteService.updateOne(id, instituteDTO);
        return ResponseEntity.ok(updatedInstitute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // El Service maneja la lógica de validación y lanza 404 si no existe
        instituteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
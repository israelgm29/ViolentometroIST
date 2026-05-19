package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.InstituteDTO;
import ec.edu.istr.violentometro.service.InstituteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/institutes")
@RequiredArgsConstructor
class InstituteController {

    private final InstituteService instituteService;

    @GetMapping
    public ResponseEntity<List<InstituteDTO>> getAll() {
        return ResponseEntity.ok(instituteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstituteDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(instituteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InstituteDTO> create(@RequestBody @Valid InstituteDTO instituteDTO) {
        return new ResponseEntity<>(instituteService.save(instituteDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstituteDTO> update(@PathVariable Integer id,
                                               @RequestBody @Valid InstituteDTO instituteDTO) {
        return ResponseEntity.ok(instituteService.updateOne(id, instituteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        instituteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Logo endpoints ────────────────────────────────────────────────

    /**
     * POST /api/v1/institutes/{id}/logo
     * Sube o reemplaza el logo del instituto.
     * Acepta multipart/form-data con campo "file".
     */
    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadLogo(@PathVariable Integer id,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }

        // Límite de 1MB
        if (file.getSize() > 1_048_576) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        instituteService.updateLogo(id, file.getBytes(), contentType);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/institutes/{id}/logo
     * Devuelve los bytes del logo para mostrar en el frontend o usar en PDF.
     */
    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> getLogo(@PathVariable Integer id) {
        byte[] logo = instituteService.getLogo(id);
        if (logo == null || logo.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String contentType = instituteService.getLogoContentType(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        contentType != null ? contentType : MediaType.IMAGE_PNG_VALUE)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(logo);
    }

    /**
     * DELETE /api/v1/institutes/{id}/logo
     * Elimina el logo del instituto.
     */
    @DeleteMapping("/{id}/logo")
    public ResponseEntity<Void> deleteLogo(@PathVariable Integer id) {
        instituteService.updateLogo(id, null, null);
        return ResponseEntity.noContent().build();
    }
}
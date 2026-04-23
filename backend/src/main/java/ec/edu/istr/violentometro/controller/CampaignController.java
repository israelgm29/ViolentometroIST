package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.CampaignCategoryDTO;
import ec.edu.istr.violentometro.dto.CampaignDTO;
import ec.edu.istr.violentometro.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    // Público — solo activas
    @GetMapping("/active")
    public ResponseEntity<List<CampaignDTO>> getActive() {
        return ResponseEntity.ok(campaignService.findActive());
    }

    // Admin — todas
    @GetMapping
    public ResponseEntity<List<CampaignDTO>> getAll() {
        return ResponseEntity.ok(campaignService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(campaignService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CampaignDTO> create(@RequestBody CampaignDTO dto) {
        return new ResponseEntity<>(campaignService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignDTO> update(@PathVariable Integer id, @RequestBody CampaignDTO dto) {
        return ResponseEntity.ok(campaignService.update(id, dto));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable Integer id) {
        campaignService.toggleStatus(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Categorías
    @GetMapping("/categories")
    public ResponseEntity<List<CampaignCategoryDTO>> getCategories() {
        return ResponseEntity.ok(campaignService.findAllCategories());
    }

    @GetMapping("/categories/active")
    public ResponseEntity<List<CampaignCategoryDTO>> getActiveCategories() {
        return ResponseEntity.ok(campaignService.findActiveCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<CampaignCategoryDTO> createCategory(@RequestBody CampaignCategoryDTO dto) {
        return new ResponseEntity<>(campaignService.saveCategory(dto), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CampaignCategoryDTO> updateCategory(@PathVariable Integer id, @RequestBody CampaignCategoryDTO dto) {
        return ResponseEntity.ok(campaignService.updateCategory(id, dto));
    }
}

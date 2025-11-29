package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.model.ViolenceZone;
import ec.edu.istr.violentometro.service.ViolenceZoneService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/violentometro")
class ViolenceZoneController {
    private final ViolenceZoneService violenceZoneService;

    ViolenceZoneController(ViolenceZoneService violenceZoneService) {
        this.violenceZoneService = violenceZoneService;
    }


    @GetMapping("/violence")
    public ResponseEntity<List<ViolenceZone>> getAllQuestions() throws Exception {
        List<ViolenceZone> violenceZones = violenceZoneService.findAll();
        return ResponseEntity.ok(violenceZones);
    }

    @PostMapping("/violence")
    public ResponseEntity<ViolenceZone> addQuestion(@RequestBody ViolenceZone violenceZone) throws Exception {
        ViolenceZone savedViolenceZone = violenceZoneService.save(violenceZone);
        return ResponseEntity.status(201).body(savedViolenceZone);
    }

    @GetMapping("/violence/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Integer id) throws Exception {
        Optional<ViolenceZone> violenceZone = violenceZoneService.findById(id);
        if (violenceZone.isPresent()) {
            return ResponseEntity.ok(violenceZone.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/violence/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Integer id, @RequestBody ViolenceZone violenceZone) throws  Exception {
        try {
            ViolenceZone updatedViolenceZone = violenceZoneService.updateOne(violenceZone, id);
            return ResponseEntity.ok(updatedViolenceZone);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/violence/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        try {
            violenceZoneService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

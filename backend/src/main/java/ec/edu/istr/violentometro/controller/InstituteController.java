package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.model.Institute;

import ec.edu.istr.violentometro.service.InstituteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/violentometro")
class InstituteController {

    private final InstituteService instituteService;

    public InstituteController(InstituteService instituteService) {
        this.instituteService = instituteService;
    }

    @GetMapping("/institute")
    public ResponseEntity<List<Institute>> getAllInstitute() throws Exception {
        List<Institute> institutes = instituteService.findAll();
        return ResponseEntity.ok(institutes);
    }

    @PostMapping("/institute")
    public ResponseEntity<Institute> addInstitute(@RequestBody Institute institute) throws Exception {
        Institute savedInstitute = instituteService.save(institute);
        return ResponseEntity.status(201).body(savedInstitute);
    }

    @GetMapping("/institute/{id}")
    public ResponseEntity<?> getInstituteById(@PathVariable Integer id) throws Exception {
        Optional<Institute> institute = instituteService.findById(id);
        if (institute.isPresent()) {
            return ResponseEntity.ok(institute.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/institute/{id} ")
    public ResponseEntity<?> updateInstitute(@PathVariable Integer id, @RequestBody Institute institute) throws Exception {
        try {
            Institute updatedInstitute = instituteService.updateOne(institute, id);
            return ResponseEntity.ok(updatedInstitute);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/institute/{id}")
    public ResponseEntity<Void> deleteInstitute(@PathVariable Integer id) {
        try {
            instituteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}

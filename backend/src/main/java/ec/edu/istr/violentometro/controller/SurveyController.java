package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.ActiveSurveyDTO;
import ec.edu.istr.violentometro.dto.SurveyDTO;
import ec.edu.istr.violentometro.service.SurveyService;
import ec.edu.istr.violentometro.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService   surveyService;
    private final SecurityUtils   securityUtils;      // ← extrae instituto del JWT

    @GetMapping
    public ResponseEntity<List<SurveyDTO>> getAll() {
        return ResponseEntity.ok(surveyService.findAll(securityUtils.getIdInstituto()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(surveyService.findById(id, securityUtils.getIdInstituto()));
    }

    @PostMapping("/full")
    public ResponseEntity<SurveyDTO> createFull(@Valid @RequestBody SurveyDTO dto) {
        SurveyDTO saved = surveyService.createFullSurvey(dto, securityUtils.getIdInstituto());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/full/{id}")
    public ResponseEntity<SurveyDTO> updateFull(
            @PathVariable Integer id,
            @Valid @RequestBody SurveyDTO dto) {
        return ResponseEntity.ok(surveyService.updateFullSurvey(id, dto, securityUtils.getIdInstituto()));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Integer id) {
        surveyService.activateOnlyThis(id, securityUtils.getIdInstituto());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        surveyService.deleteSurvey(id, securityUtils.getIdInstituto());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<ActiveSurveyDTO> getActive() {
        return ResponseEntity.ok(surveyService.findActive(securityUtils.getIdInstituto()));
    }
}
package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.ActiveSurveyDTO;
import ec.edu.istr.violentometro.dto.SurveyDTO;
import ec.edu.istr.violentometro.service.SurveyService;
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

    private final SurveyService surveyService;

    @GetMapping
    public ResponseEntity<List<SurveyDTO>> getAll() {
        List<SurveyDTO> surveys = surveyService.findAll();
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(surveyService.findById(id));
    }

    @PostMapping("/full")
    public ResponseEntity<SurveyDTO> createFull(@Valid @RequestBody SurveyDTO dto) {
        SurveyDTO savedSurvey = surveyService.createFullSurvey(dto);
        return new ResponseEntity<>(savedSurvey, HttpStatus.CREATED);
    }

    @PutMapping("/full/{id}")
    public ResponseEntity<SurveyDTO> updateFull(
            @PathVariable Integer id,
            @Valid @RequestBody SurveyDTO dto) {
        SurveyDTO updatedSurvey = surveyService.updateFullSurvey(id, dto);
        return ResponseEntity.ok(updatedSurvey);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Integer id) {
        surveyService.activateOnlyThis(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<ActiveSurveyDTO> getActive() {
        return ResponseEntity.ok(surveyService.findActive());
    }
}
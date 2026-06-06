package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.UserAnswerDTO;
import ec.edu.istr.violentometro.service.UserAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-answers")
@RequiredArgsConstructor
class UserAnswerController {

    private final UserAnswerService userAnswerService;

    @GetMapping
    public ResponseEntity<List<UserAnswerDTO>> getAll() {
        return ResponseEntity.ok(userAnswerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(userAnswerService.findById(id));
    }

    @GetMapping("/user/dni/{encryptedDni}")
    public ResponseEntity<List<UserAnswerDTO>> getByDni(@PathVariable String encryptedDni) {
        return ResponseEntity.ok(userAnswerService.findByDni(encryptedDni));
    }

    @GetMapping("/user/dni/{encryptedDni}/survey/{surveyId}")
    public ResponseEntity<List<UserAnswerDTO>> getByDniAndSurvey(
            @PathVariable String encryptedDni,
            @PathVariable Integer surveyId) {
        return ResponseEntity.ok(
                userAnswerService.findByDniAndSurvey(encryptedDni, surveyId));
    }

    /**
     * GET /api/v1/user-answers/can-answer?userId=1&surveyId=2
     *
     * El frontend consulta este endpoint ANTES de mostrar el cuestionario.
     * Respuesta: { canAnswer: true } o { canAnswer: false, message: "..." }
     */
    @GetMapping("/can-answer")
    public ResponseEntity<UserAnswerService.CanAnswerTodayDTO> canAnswerToday(
            @RequestParam Integer userId,
            @RequestParam Integer surveyId) {
        return ResponseEntity.ok(
                userAnswerService.canAnswerToday(userId, surveyId));
    }

    @PostMapping
    public ResponseEntity<UserAnswerDTO> create(
            @RequestBody @Valid UserAnswerDTO userAnswerDTO) {
        return new ResponseEntity<>(
                userAnswerService.save(userAnswerDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> update(
            @PathVariable Integer id,
            @RequestBody @Valid UserAnswerDTO userAnswerDTO) {
        return ResponseEntity.ok(userAnswerService.updateOne(id, userAnswerDTO));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userAnswerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/user-answers/user/dni/{encryptedDni}/survey/{surveyId}/today
     * Devuelve solo las respuestas del día de hoy — usado por loadQuestionsAndResume.
     */
    @GetMapping("/user/dni/{encryptedDni}/survey/{surveyId}/today")
    public ResponseEntity<List<UserAnswerDTO>> getByDniAndSurveyToday(
            @PathVariable String encryptedDni,
            @PathVariable Integer surveyId) {
        // Spring decodea automáticamente el %2B → + cuando viene encodeado
        return ResponseEntity.ok(
                userAnswerService.findByDniAndSurveyToday(encryptedDni, surveyId));
    }
}
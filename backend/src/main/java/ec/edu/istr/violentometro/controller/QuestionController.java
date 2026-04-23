package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.QuestionDTO;
import ec.edu.istr.violentometro.dto.QuestionZoneDTO;
import ec.edu.istr.violentometro.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
class QuestionController {

    private final QuestionService questionService;

    /**
     * Endpoint para obtener todas las preguntas, con opción de incluir la Zona.
     * * @param include Parámetro opcional para poblar entidades relacionadas (e.g., "zone")
     * @return Lista de QuestionDTO (por defecto) o QuestionPopulatedDTO (si se incluye 'zone')
     */
    @GetMapping
    public ResponseEntity<?> getAllQuestions(@RequestParam(name="include",required = false) String include) {

        if (include != null && include.contains("zone")) {
            // Utilizamos el DTO con la Zona completa
            List<QuestionZoneDTO> questionsWithZone = questionService.findAllWhitZone();
            return ResponseEntity.ok(questionsWithZone);
        } else {
            // Por defecto, utilizamos el DTO base (solo ID)
            List<QuestionDTO> baseQuestions = questionService.findAll();
            return ResponseEntity.ok(baseQuestions);
        }
    }

    @PostMapping
    public ResponseEntity<QuestionDTO> addQuestion(@RequestBody @Validated QuestionDTO questionDTO) {
        QuestionDTO savedQuestion = questionService.save(questionDTO);
        return new ResponseEntity<>(savedQuestion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable("id") Integer id) {

        QuestionDTO questionDTO = questionService.findById(id);
        return ResponseEntity.ok(questionDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable("id") Integer id, @RequestBody @Validated QuestionDTO questionDTO) {

        QuestionDTO updatedQuestion = questionService.update(id, questionDTO);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Integer id) {

        questionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
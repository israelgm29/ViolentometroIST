package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.QuestionDTO;
import ec.edu.istr.violentometro.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions") // Nombre plural, ¡perfecto!
@RequiredArgsConstructor
class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {

        return ResponseEntity.ok(questionService.findAll());
    }

    @PostMapping
    public ResponseEntity<QuestionDTO> addQuestion(@RequestBody @Validated QuestionDTO questionDTO) {
        QuestionDTO savedQuestion = questionService.save(questionDTO);
        return new ResponseEntity<>(savedQuestion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Integer id) {

        QuestionDTO questionDTO = questionService.findById(id);
        return ResponseEntity.ok(questionDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Integer id, @RequestBody @Validated QuestionDTO questionDTO) {

        QuestionDTO updatedQuestion = questionService.update(id, questionDTO);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {

        questionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.repository.QuestionRepository;
import ec.edu.istr.violentometro.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/violentometro")
class QuestionController {

    private final QuestionService questionService;

    QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }


    @GetMapping("/question")
    public ResponseEntity<List<Question>> getAllQuestions() throws Exception {
        List<Question> questions = questionService.findAll();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/question")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question) throws Exception {
        Question savedQuestion = questionService.save(question);
        return ResponseEntity.status(201).body(savedQuestion);

    }

    @GetMapping("/question/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Integer id) throws Exception {
        Optional<Question> question = questionService.findById(id);
        if (question.isPresent()) {
            return ResponseEntity.ok(question.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/question/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        try {
            Question updatedQuestion = questionService.updateOne(question, id);
            return ResponseEntity.ok(updatedQuestion);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/question/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        try {
            questionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

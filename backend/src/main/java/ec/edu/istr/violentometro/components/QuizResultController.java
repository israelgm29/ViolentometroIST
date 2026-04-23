package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.QuizResultRequest;
import ec.edu.istr.violentometro.service.QuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quiz-results")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class QuizResultController {

    private final QuizResultService quizResultService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody QuizResultRequest dto) {
        // Usamos el método del servicio que creamos
        quizResultService.saveFinalResult(dto);
        return ResponseEntity.ok().build();
    }
}
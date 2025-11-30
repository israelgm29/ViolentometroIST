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
@RequiredArgsConstructor // Inyección limpia
class UserAnswerController {

    private final UserAnswerService userAnswerService;

    @GetMapping
    public ResponseEntity<List<UserAnswerDTO>> getAll() {
        return ResponseEntity.ok(userAnswerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> getById(@PathVariable Integer id) {
        // El Service lanza 404 si no existe
        return ResponseEntity.ok(userAnswerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserAnswerDTO> create(@RequestBody @Valid UserAnswerDTO userAnswerDTO) {
        UserAnswerDTO savedAnswer = userAnswerService.save(userAnswerDTO);
        return new ResponseEntity<>(savedAnswer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAnswerDTO> update(@PathVariable Integer id, @RequestBody @Valid UserAnswerDTO userAnswerDTO) {
        // El Service maneja la lógica de actualización y lanza 404 si no existe
        UserAnswerDTO updatedAnswer = userAnswerService.updateOne(id, userAnswerDTO);
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        // El Service maneja la lógica de validación y lanza 404 si no existe
        userAnswerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
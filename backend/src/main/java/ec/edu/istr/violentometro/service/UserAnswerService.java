package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.UserAnswerMapper;
import ec.edu.istr.violentometro.dto.UserAnswerDTO;
import ec.edu.istr.violentometro.model.AppUser;
import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.model.UserAnswer;
import ec.edu.istr.violentometro.repository.AppUserRepository;
import ec.edu.istr.violentometro.repository.QuestionRepository;
import ec.edu.istr.violentometro.repository.QuizResultRepository;
import ec.edu.istr.violentometro.repository.UserAnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerMapper     userAnswerMapper;
    private final AppUserRepository    appUserRepository;
    private final QuestionRepository   questionRepository;
    private final EncryptionService    encryptionService;
    private final QuizResultRepository quizResultRepository;

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ─────────────────────────────────────────────────────────────────
    // SAVE — con validación diaria
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public UserAnswerDTO save(UserAnswerDTO dto) {
        // 1. Logs de depuración (Míralos en la consola de tu IDE)
        System.out.println("Guardando respuesta para User: " + dto.getIdAppUser() + " Quest: " + dto.getIdQuestion());

        AppUser appUser   = getAppUser(dto.getIdAppUser());
        Question question = getQuestion(dto.getIdQuestion());

        // 2. Lógica de Upsert Robusta
        // Intentamos buscar una respuesta previa para evitar duplicados en la misma sesión
        UserAnswer answer = userAnswerRepository
                .findByIdAppUserIdAndIdQuestionId(appUser.getId(), question.getId())
                .orElseGet(() -> {
                    UserAnswer newAnsw = new UserAnswer();
                    newAnsw.setCreatedAt(OffsetDateTime.now()); // Solo se setea si es NUEVA
                    return newAnsw;
                });

        // 3. Seteamos los campos
        answer.setIdAppUser(appUser);
        answer.setIdQuestion(question);
        answer.setAnswer(dto.getAnswer());

        // 4. Forzamos que la fecha de actualización sea ahora (por si acaso)
        if (answer.getId() != null) {
            // Si ya existía, podrías querer actualizar la fecha o dejar la original
            // answer.setCreatedAt(OffsetDateTime.now());
        }

        try {
            UserAnswer saved = userAnswerRepository.saveAndFlush(answer); // saveAndFlush fuerza el SQL inmediato
            return userAnswerMapper.toDto(saved);
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO AL GUARDAR: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo persistir la respuesta");
        }
    }


    public CanAnswerTodayDTO canAnswerToday(Integer userId, Integer surveyId) {
        // CAMBIO CLAVE: Ahora preguntamos a la tabla de resultados finales
        boolean yaFinalizoHoy = quizResultRepository.hasFinishedToday(userId, surveyId);

        if (yaFinalizoHoy) {
            return new CanAnswerTodayDTO(false,
                    "Ya completaste este cuestionario hoy. Para proteger la integridad de los datos, podrás realizarlo nuevamente mañana.");
        }

        // Si no ha finalizado, canAnswer es true (permite retomar progreso)
        return new CanAnswerTodayDTO(true, null);
    }

    /**
     * DTO de respuesta del check diario.
     * canAnswer = true  → el estudiante puede responder
     * canAnswer = false → ya respondió hoy, message explica cuándo fue
     */
    public record CanAnswerTodayDTO(boolean canAnswer, String message) {}

    // ─────────────────────────────────────────────────────────────────
    // RESTO DE MÉTODOS — sin cambios
    // ─────────────────────────────────────────────────────────────────

    public List<UserAnswerDTO> findAll() {
        return userAnswerMapper.toDto(userAnswerRepository.findAll());
    }

    public UserAnswerDTO findById(Integer id) {
        UserAnswer userAnswer = userAnswerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta de usuario no encontrada con ID: " + id));
        return userAnswerMapper.toDto(userAnswer);
    }

    @Transactional
    public UserAnswerDTO updateOne(Integer id, UserAnswerDTO dto) {
        UserAnswer existingAnswer = userAnswerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Respuesta de usuario no encontrada con ID: " + id));

        userAnswerMapper.updateEntityFromDto(dto, existingAnswer);

        if (dto.getIdAppUser() != null &&
                !dto.getIdAppUser().equals(existingAnswer.getIdAppUser().getId())) {
            existingAnswer.setIdAppUser(getAppUser(dto.getIdAppUser()));
        }
        if (dto.getIdQuestion() != null &&
                !dto.getIdQuestion().equals(existingAnswer.getIdQuestion().getId())) {
            existingAnswer.setIdQuestion(getQuestion(dto.getIdQuestion()));
        }

        return userAnswerMapper.toDto(userAnswerRepository.save(existingAnswer));
    }

    public void deleteById(Integer id) {
        if (!userAnswerRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Respuesta de usuario no encontrada con ID: " + id);
        }
        userAnswerRepository.deleteById(id);
    }

    public List<UserAnswerDTO> findByDni(String encryptedDni) {
        String dni = encryptionService.decrypt(encryptedDni);
        return userAnswerMapper.toDto(userAnswerRepository.findByAppUserDni(dni));
    }

    public List<UserAnswerDTO> findByDniAndSurvey(String encryptedDni, Integer surveyId) {
        String dni = encryptionService.decrypt(encryptedDni);
        return userAnswerMapper.toDto(
                userAnswerRepository.findByAppUserDniAndSurveyId(dni, surveyId));
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPERS privados
    // ─────────────────────────────────────────────────────────────────

    private AppUser getAppUser(Integer userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con ID " + userId + " no encontrado."));
    }

    private Question getQuestion(Integer questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pregunta con ID " + questionId + " no encontrada."));
    }

    /**
     * Respuestas del usuario en un survey SOLO del día de hoy.
     * Usar este método en loadQuestionsAndResume() para evitar
     * cargar respuestas de días anteriores.
     */
    public List<UserAnswerDTO> findByDniAndSurveyToday(String encryptedDni, Integer surveyId) {
        String dni = encryptionService.decrypt(encryptedDni);
        return userAnswerMapper.toDto(
                userAnswerRepository.findByAppUserDniAndSurveyIdToday(dni, surveyId));
    }
}
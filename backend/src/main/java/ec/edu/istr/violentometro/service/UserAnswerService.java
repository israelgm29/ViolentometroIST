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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerMapper     userAnswerMapper;
    private final AppUserRepository    appUserRepository;
    private final QuestionRepository   questionRepository;
    private final QuizResultRepository quizResultRepository;
    // FIX: EncryptionService eliminado — el frontend ya no encripta sin HTTPS

    public record CanAnswerTodayDTO(boolean canAnswer, String message) {}

    // ─────────────────────────────────────────────────────────────────
    // SAVE
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public UserAnswerDTO save(UserAnswerDTO dto) {
        System.out.println("Guardando respuesta para User: " + dto.getIdAppUser() + " Quest: " + dto.getIdQuestion());

        AppUser appUser   = getAppUser(dto.getIdAppUser());
        Question question = getQuestion(dto.getIdQuestion());

        UserAnswer answer = userAnswerRepository
                .findByIdAppUserIdAndIdQuestionId(appUser.getId(), question.getId())
                .orElseGet(() -> {
                    UserAnswer newAnsw = new UserAnswer();
                    newAnsw.setCreatedAt(OffsetDateTime.now());
                    return newAnsw;
                });

        answer.setIdAppUser(appUser);
        answer.setIdQuestion(question);
        answer.setAnswer(dto.getAnswer());

        try {
            UserAnswer saved = userAnswerRepository.saveAndFlush(answer);
            return userAnswerMapper.toDto(saved);
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO AL GUARDAR: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo persistir la respuesta");
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // CAN ANSWER TODAY
    // ─────────────────────────────────────────────────────────────────

    public CanAnswerTodayDTO canAnswerToday(Integer userId, Integer surveyId) {
        boolean yaFinalizoHoy = quizResultRepository.hasFinishedToday(userId, surveyId);
        if (yaFinalizoHoy) {
            return new CanAnswerTodayDTO(false,
                    "Ya completaste este cuestionario hoy. Para proteger la integridad de los datos, podrás realizarlo nuevamente mañana.");
        }
        return new CanAnswerTodayDTO(true, null);
    }

    // ─────────────────────────────────────────────────────────────────
    // QUERIES
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

    // FIX: sin decrypt — recibe DNI plano directamente
    public List<UserAnswerDTO> findByDni(String dni) {
        return userAnswerMapper.toDto(userAnswerRepository.findByAppUserDni(dni));
    }

    public List<UserAnswerDTO> findByDniAndSurvey(String dni, Integer surveyId) {
        return userAnswerMapper.toDto(
                userAnswerRepository.findByAppUserDniAndSurveyId(dni, surveyId));
    }

    // FIX: sin decrypt — recibe DNI plano directamente
    public List<UserAnswerDTO> findByDniAndSurveyToday(String dni, Integer surveyId) {
        return userAnswerMapper.toDto(
                userAnswerRepository.findByAppUserDniAndSurveyIdToday(dni, surveyId));
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPERS
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
}
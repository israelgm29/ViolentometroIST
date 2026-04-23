package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.SurveyMapper;
import ec.edu.istr.violentometro.dto.ActiveSurveyDTO;
import ec.edu.istr.violentometro.dto.SurveyDTO;
import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.model.Survey;
import ec.edu.istr.violentometro.repository.QuestionRepository;
import ec.edu.istr.violentometro.repository.SurveyRepository;
import ec.edu.istr.violentometro.repository.ViolenceZoneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final ViolenceZoneRepository zoneRepository;
    private final SurveyMapper surveyMapper;

    // ========== LISTAR TODOS ==========
    public List<SurveyDTO> findAll() {
        return surveyRepository.findAll().stream()
                .map(survey -> surveyMapper.toDto(survey, null))
                .collect(Collectors.toList());
    }

    // ========== OBTENER POR ID ==========
    public SurveyDTO findById(Integer id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuestionario no encontrado con ID: " + id));

        List<Question> questions = questionRepository.findBySurveyIdOrderByQuestionNumberAsc(id);
        return surveyMapper.toDto(survey, questions);
    }

    // ========== CREAR NUEVO ==========
    @Transactional
    public SurveyDTO createFullSurvey(SurveyDTO dto) {
        Survey survey = surveyMapper.toEntity(dto);
        Survey savedSurvey = surveyRepository.save(survey);

        if (dto.getQuestions() != null) {
            List<Question> questions = surveyMapper.toQuestionEntities(dto.getQuestions(), savedSurvey);
            questionRepository.saveAll(questions);
            return surveyMapper.toDto(savedSurvey, questions);
        }

        return surveyMapper.toDto(savedSurvey, null);
    }

    // ========== ACTUALIZAR (OPCIÓN 2 - INTELIGENTE) ==========
    @Transactional
    public SurveyDTO updateFullSurvey(Integer id, SurveyDTO dto) {
        // 1. Buscar survey existente
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuestionario no encontrado con ID: " + id));

        // 2. Actualizar campos básicos del survey
        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());
        surveyRepository.save(survey);

        // 3. Obtener preguntas existentes
        List<Question> existingQuestions = questionRepository.findBySurveyIdOrderByQuestionNumberAsc(id);

        // 4. Procesar preguntas del DTO
        List<Question> updatedQuestions = new ArrayList<>();
        List<Integer> processedIds = new ArrayList<>();

        for (SurveyDTO.QuestionDTO qDto : dto.getQuestions()) {
            Question question;

            if (qDto.getId() != null) {
                // ACTUALIZAR pregunta existente
                question = existingQuestions.stream()
                        .filter(q -> q.getId().equals(qDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Pregunta no encontrada: " + qDto.getId()));

                processedIds.add(qDto.getId());
            } else {
                // CREAR nueva pregunta
                question = new Question();
                question.setSurvey(survey);
                question.setStatus(true);
            }

            // Actualizar datos de la pregunta
            question.setQuestion(qDto.getQuestion());
            question.setQuestionNumber(qDto.getQuestionNumber());
            question.setIdZone(zoneRepository.findById(qDto.getIdZone())
                    .orElseThrow(() -> new RuntimeException("Zona no encontrada: " + qDto.getIdZone())));

            updatedQuestions.add(question);
        }

        // 5. ELIMINAR preguntas que ya no están en el DTO
        List<Question> questionsToDelete = existingQuestions.stream()
                .filter(q -> !processedIds.contains(q.getId()))
                .collect(Collectors.toList());

        if (!questionsToDelete.isEmpty()) {
            questionRepository.deleteAll(questionsToDelete);
        }

        // 6. Guardar todas las preguntas (nuevas y actualizadas)
        List<Question> savedQuestions = questionRepository.saveAll(updatedQuestions);

        // 7. Retornar DTO completo
        return surveyMapper.toDto(survey, savedQuestions);
    }

    // ========== ACTIVAR ==========
    @Transactional
    public void activateOnlyThis(Integer id) {
        surveyRepository.deactivateAllSurveys();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuestionario no encontrado con ID: " + id));
        survey.setIsActive(true);
        surveyRepository.save(survey);
    }

    // ========== ELIMINAR ==========
    @Transactional
    public void deleteSurvey(Integer id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuestionario no encontrado con ID: " + id));

        // Validación: no eliminar cuestionario activo
        if (survey.getIsActive()) {
            throw new RuntimeException("No se puede eliminar el cuestionario activo. Primero active otro cuestionario.");
        }

        // Nota: Si tienes UserResponse, agrega validación aquí
        // boolean hasResponses = userResponseRepository.existsBySurveyId(id);
        // if (hasResponses) {
        //     throw new RuntimeException("No se puede eliminar este cuestionario porque ya tiene respuestas de usuarios.");
        // }

        // Eliminar preguntas primero (si no tienes CASCADE en BD)
        questionRepository.deleteBySurveyId(id);

        // Eliminar el survey
        surveyRepository.deleteById(id);
    }

    public ActiveSurveyDTO findActive() {
        Survey survey = surveyRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("No hay ningún cuestionario activo."));

        List<Question> questions = questionRepository
                .findBySurveyIdOrderByQuestionNumberAsc(survey.getId());

        return surveyMapper.toActiveDto(survey, questions);
    }
}
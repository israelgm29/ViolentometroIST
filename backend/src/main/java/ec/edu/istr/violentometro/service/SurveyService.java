package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.SurveyMapper;
import ec.edu.istr.violentometro.dto.ActiveSurveyDTO;
import ec.edu.istr.violentometro.dto.SurveyDTO;
import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.model.Survey;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import ec.edu.istr.violentometro.repository.QuestionRepository;
import ec.edu.istr.violentometro.repository.SurveyRepository;
import ec.edu.istr.violentometro.repository.ViolenceZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository    surveyRepository;
    private final QuestionRepository  questionRepository;
    private final ViolenceZoneRepository zoneRepository;
    private final InstituteRepository instituteRepository;
    private final SurveyMapper        surveyMapper;

    // ── Listar cuestionarios del instituto ────────────────────────
    public List<SurveyDTO> findAll(Integer idInstituto) {
        return surveyRepository.findAllByInstitute_Id(idInstituto).stream()
                .map(survey -> surveyMapper.toDto(survey, null))
                .collect(Collectors.toList());
    }

    // ── Obtener por ID (validando pertenencia) ────────────────────
    public SurveyDTO findById(Integer id, Integer idInstituto) {
        Survey survey = getSurveyValidado(id, idInstituto);
        List<Question> questions = questionRepository.findBySurveyIdOrderByQuestionNumberAsc(id);
        return surveyMapper.toDto(survey, questions);
    }

    // ── Crear cuestionario asignando el instituto ─────────────────
    @Transactional
    public SurveyDTO createFullSurvey(SurveyDTO dto, Integer idInstituto) {
        Institute institute = instituteRepository.findById(idInstituto)
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado: " + idInstituto));

        Survey survey = surveyMapper.toEntity(dto);
        survey.setInstitute(institute);                     // ← asignar instituto
        Survey savedSurvey = surveyRepository.save(survey);

        if (dto.getQuestions() != null) {
            List<Question> questions = surveyMapper.toQuestionEntities(dto.getQuestions(), savedSurvey);
            questionRepository.saveAll(questions);
            return surveyMapper.toDto(savedSurvey, questions);
        }
        return surveyMapper.toDto(savedSurvey, null);
    }

    // ── Actualizar cuestionario (solo del propio instituto) ───────
    @Transactional
    public SurveyDTO updateFullSurvey(Integer id, SurveyDTO dto, Integer idInstituto) {
        Survey survey = getSurveyValidado(id, idInstituto);

        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());
        surveyRepository.save(survey);

        List<Question> existingQuestions = questionRepository.findBySurveyIdOrderByQuestionNumberAsc(id);
        List<Question> updatedQuestions  = new ArrayList<>();
        List<Integer>  processedIds      = new ArrayList<>();

        for (SurveyDTO.QuestionDTO qDto : dto.getQuestions()) {
            Question question;
            if (qDto.getId() != null) {
                question = existingQuestions.stream()
                        .filter(q -> q.getId().equals(qDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Pregunta no encontrada: " + qDto.getId()));
                processedIds.add(qDto.getId());
            } else {
                question = new Question();
                question.setSurvey(survey);
                question.setStatus(true);
            }
            question.setQuestion(qDto.getQuestion());
            question.setQuestionNumber(qDto.getQuestionNumber());
            question.setIdZone(zoneRepository.findById(qDto.getIdZone())
                    .orElseThrow(() -> new RuntimeException("Zona no encontrada: " + qDto.getIdZone())));
            updatedQuestions.add(question);
        }

        List<Question> toDelete = existingQuestions.stream()
                .filter(q -> !processedIds.contains(q.getId()))
                .collect(Collectors.toList());
        if (!toDelete.isEmpty()) questionRepository.deleteAll(toDelete);

        List<Question> savedQuestions = questionRepository.saveAll(updatedQuestions);
        return surveyMapper.toDto(survey, savedQuestions);
    }

    // ── Activar (solo desactiva los del mismo instituto) ──────────
    @Transactional
    public void activateOnlyThis(Integer id, Integer idInstituto) {
        surveyRepository.deactivateAllByInstituto(idInstituto);     // ← solo su instituto
        Survey survey = getSurveyValidado(id, idInstituto);
        survey.setIsActive(true);
        surveyRepository.save(survey);
    }

    // ── Eliminar (solo del propio instituto) ──────────────────────
    @Transactional
    public void deleteSurvey(Integer id, Integer idInstituto) {
        Survey survey = getSurveyValidado(id, idInstituto);

        if (survey.getIsActive()) {
            throw new RuntimeException("No se puede eliminar el cuestionario activo.");
        }
        questionRepository.deleteBySurveyId(id);
        surveyRepository.deleteById(id);
    }

    // ── Obtener cuestionario activo del instituto ─────────────────
    public ActiveSurveyDTO findActive(Integer idInstituto) {
        Survey survey = surveyRepository.findByIsActiveTrueAndInstitute_Id(idInstituto)
                .orElseThrow(() -> new RuntimeException("No hay cuestionario activo para este instituto."));
        List<Question> questions = questionRepository.findBySurveyIdOrderByQuestionNumberAsc(survey.getId());
        return surveyMapper.toActiveDto(survey, questions);
    }

    // ── Helper: buscar survey y validar que pertenezca al instituto ──
    private Survey getSurveyValidado(Integer id, Integer idInstituto) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuestionario no encontrado: " + id));
        if (!survey.getInstitute().getId().equals(idInstituto)) {
            throw new SecurityException("No tienes permiso para acceder a este cuestionario.");
        }
        return survey;
    }
}
package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.ActiveSurveyDTO;
import ec.edu.istr.violentometro.dto.SurveyDTO;
import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.model.Survey;
import ec.edu.istr.violentometro.repository.ViolenceZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyMapper {

    private final ViolenceZoneRepository zoneRepository;

    public SurveyDTO toDto(Survey entity, List<Question> questions) {
        if (entity == null) return null;
        SurveyDTO dto = new SurveyDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());

        // ── NUEVO: exponer idInstituto en la respuesta ─────────────
        if (entity.getInstitute() != null) {
            dto.setIdInstituto(entity.getInstitute().getId());
        }
        // ───────────────────────────────────────────────────────────

        if (questions != null) {
            dto.setQuestions(questions.stream()
                    .map(this::toQuestionDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private SurveyDTO.QuestionDTO toQuestionDto(Question q) {
        if (q == null) return null;
        SurveyDTO.QuestionDTO qDto = new SurveyDTO.QuestionDTO();
        qDto.setId(q.getId());
        qDto.setQuestion(q.getQuestion());
        qDto.setQuestionNumber(q.getQuestionNumber());
        qDto.setIdZone(q.getIdZone() != null ? q.getIdZone().getId() : null);
        return qDto;
    }

    public Survey toEntity(SurveyDTO dto) {
        if (dto == null) return null;
        Survey entity = new Survey();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setIsActive(false);
        // Nota: institute NO se mapea aquí — se asigna en SurveyService
        // usando el idInstituto extraído del JWT. Esto evita que el cliente
        // pueda enviar un instituto arbitrario en el body.
        return entity;
    }

    public List<Question> toQuestionEntities(List<SurveyDTO.QuestionDTO> dtos, Survey survey) {
        return dtos.stream().map(d -> {
            Question q = new Question();
            q.setQuestion(d.getQuestion());
            q.setQuestionNumber(d.getQuestionNumber());
            q.setSurvey(survey);
            q.setStatus(true);
            q.setIdZone(zoneRepository.findById(d.getIdZone()).orElse(null));
            return q;
        }).collect(Collectors.toList());
    }

    public ActiveSurveyDTO toActiveDto(Survey survey, List<Question> questions) {
        ActiveSurveyDTO dto = new ActiveSurveyDTO();
        dto.setId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());

        if (questions != null) {
            dto.setQuestions(questions.stream()
                    .map(q -> {
                        ActiveSurveyDTO.QuestionDTO qDto = new ActiveSurveyDTO.QuestionDTO();
                        qDto.setId(q.getId());
                        qDto.setQuestion(q.getQuestion());
                        qDto.setQuestionNumber(q.getQuestionNumber());
                        qDto.setStatus(q.getStatus());

                        if (q.getIdZone() != null) {
                            ActiveSurveyDTO.ZoneDTO zDto = new ActiveSurveyDTO.ZoneDTO();
                            zDto.setId(q.getIdZone().getId());
                            zDto.setName(q.getIdZone().getName());
                            zDto.setDescription(q.getIdZone().getDescription());
                            zDto.setColor(q.getIdZone().getColor());
                            zDto.setSeverity(q.getIdZone().getSeverity());
                            zDto.setStatus(q.getIdZone().getStatus());
                            zDto.setResultTitle(q.getIdZone().getResultTitle());
                            zDto.setResultMessage(q.getIdZone().getResultMessage());
                            zDto.setRecommendations(q.getIdZone().getRecommendationList());
                            qDto.setZone(zDto);
                        }
                        return qDto;
                    })
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
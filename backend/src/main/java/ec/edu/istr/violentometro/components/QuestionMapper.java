package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.QuestionDTO;
import ec.edu.istr.violentometro.dto.QuestionZoneDTO;
import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.model.ViolenceZone;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    private  final ViolenceZoneMapper zoneMapper;

    public QuestionMapper(ViolenceZoneMapper zoneMapper) {
        this.zoneMapper = zoneMapper;
    }

    public QuestionDTO toDto(Question entity) {
        if (entity == null) return null;
        QuestionDTO dto = new QuestionDTO();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setQuestionNumber(entity.getQuestionNumber());
        dto.setStatus(entity.getStatus());

        if (entity.getIdZone() != null) {
            dto.setIdZone(entity.getIdZone().getId());
        }
        return dto;
    }

    public QuestionZoneDTO toDtoWithZone(Question entity) {
        if (entity == null) return null;
        QuestionZoneDTO dto = new QuestionZoneDTO();

        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setQuestionNumber(entity.getQuestionNumber());
        dto.setStatus(entity.getStatus());
        if (entity.getIdZone() != null) {
            dto.setZone(zoneMapper.toDto(entity.getIdZone()));
        }
        return dto;
    }

    public List<QuestionZoneDTO> toDtoWithZone(List<Question> entities) {
        return entities.stream().map(this::toDtoWithZone).collect(Collectors.toList());
    }

    public List<QuestionDTO> toDto(List<Question> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Question toEntity(QuestionDTO dto) {
        if (dto == null) return null;
        Question entity = new Question();

        entity.setQuestion(dto.getQuestion());
        entity.setQuestionNumber(dto.getQuestionNumber());
        entity.setStatus(dto.getStatus());

        if (dto.getIdZone() != null) {
            ViolenceZone zone = new ViolenceZone();
            zone.setId(dto.getIdZone());
            entity.setIdZone(zone);
        }

        return entity;
    }
}
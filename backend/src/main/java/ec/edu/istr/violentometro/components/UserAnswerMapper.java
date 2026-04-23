package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.UserAnswerDTO;
import ec.edu.istr.violentometro.model.UserAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAnswerMapper {

    // Entidad a DTO de Respuesta
    public UserAnswerDTO toDto(UserAnswer entity) {
        if (entity == null) return null;
        UserAnswerDTO dto = new UserAnswerDTO();
        dto.setIdAnswer(entity.getId());
        dto.setAnswer(entity.getAnswer());

        if (entity.getIdAppUser() != null) {
            dto.setIdAppUser(entity.getIdAppUser().getId());
        }
        if (entity.getIdQuestion() != null) {
            dto.setIdQuestion(entity.getIdQuestion().getId());
        }
        return dto;
    }

    public List<UserAnswerDTO> toDto(List<UserAnswer> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    // DTO a Entidad (para Creación) - El servicio se encarga de asignar los objetos AppUser y Question
    public UserAnswer toEntity(UserAnswerDTO dto) {
        if (dto == null) return null;
        UserAnswer entity = new UserAnswer();
        entity.setAnswer(dto.getAnswer());
        return entity;
    }

    public void updateEntityFromDto(UserAnswerDTO dto, UserAnswer entity) {

        if (dto.getAnswer() != null) {
            entity.setAnswer(dto.getAnswer());
        }
    }
}
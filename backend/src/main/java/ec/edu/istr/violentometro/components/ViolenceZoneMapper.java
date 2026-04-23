package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.ViolenceZoneDTO;
import ec.edu.istr.violentometro.model.ViolenceZone;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ViolenceZoneMapper {

    // ─── Entidad → DTO ────────────────────────────────────────────────────────

    public ViolenceZoneDTO toDto(ViolenceZone entity) {
        if (entity == null) return null;
        ViolenceZoneDTO dto = new ViolenceZoneDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setColor(entity.getColor());
        dto.setSeverity(entity.getSeverity());
        dto.setStatus(entity.getStatus());
        // Campos de resultado del quiz
        dto.setResultTitle(entity.getResultTitle());
        dto.setResultMessage(entity.getResultMessage());
        dto.setRecommendations(entity.getRecommendationList());
        return dto;
    }

    public List<ViolenceZoneDTO> toDto(List<ViolenceZone> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    // ─── DTO → Entidad (creación) ─────────────────────────────────────────────

    public ViolenceZone toEntity(ViolenceZoneDTO dto) {
        if (dto == null) return null;
        ViolenceZone entity = new ViolenceZone();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setColor(dto.getColor());
        entity.setSeverity(dto.getSeverity());
        entity.setStatus(dto.getStatus());
        // Campos de resultado del quiz
        entity.setResultTitle(dto.getResultTitle());
        entity.setResultMessage(dto.getResultMessage());
        entity.setRecommendationList(dto.getRecommendations());
        return entity;
    }

    // ─── DTO → Entidad (actualización) ───────────────────────────────────────

    public void updateEntityFromDto(ViolenceZoneDTO dto, ViolenceZone entity) {
        if (dto.getName() != null)        entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getColor() != null)       entity.setColor(dto.getColor());
        if (dto.getSeverity() != null)    entity.setSeverity(dto.getSeverity());
        if (dto.getStatus() != null)      entity.setStatus(dto.getStatus());
        // Campos de resultado del quiz — se actualizan si vienen en el DTO
        if (dto.getResultTitle() != null)   entity.setResultTitle(dto.getResultTitle());
        if (dto.getResultMessage() != null) entity.setResultMessage(dto.getResultMessage());
        if (dto.getRecommendations() != null) entity.setRecommendationList(dto.getRecommendations());
    }
}
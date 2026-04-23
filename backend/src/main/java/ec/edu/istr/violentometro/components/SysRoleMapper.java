package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.SysRoleDTO;
import ec.edu.istr.violentometro.model.SysRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SysRoleMapper {

    // Entidad a DTO de Respuesta
    public SysRoleDTO toDto(SysRole entity) {
        if (entity == null) return null;
        SysRoleDTO dto = new SysRoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public List<SysRoleDTO> toDto(List<SysRole> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    // DTO a Entidad (para Creación)
    public SysRole toEntity(SysRoleDTO dto) {
        if (dto == null) return null;
        SysRole entity = new SysRole();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        // ID se genera en la base de datos
        return entity;
    }

    public void updateEntityFromDto(SysRoleDTO dto, SysRole entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}
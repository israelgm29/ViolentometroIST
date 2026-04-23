package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.SysUserCreateDTO;
import ec.edu.istr.violentometro.dto.SysUserDTO;
import ec.edu.istr.violentometro.dto.SysUserResponseDTO;
import ec.edu.istr.violentometro.model.SysUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SysUserMapper {

    // Conversión de Entidad a DTO de Respuesta normal con id (NO incluye password)
    public SysUserDTO toDto(SysUser entity) {
        if (entity == null) return null;
        SysUserDTO dto = new SysUserDTO();
        dto.setId(entity.getId());
        dto.setFirstname(entity.getFirstname());
        dto.setSecondname(entity.getSecondname());
        dto.setFirstLastname(entity.getFirstLastname());
        dto.setSecondLastname(entity.getSecondLastname());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());
        dto.setDni(entity.getDni());
        dto.setStatus(entity.getStatus());
        dto.setEmail(entity.getEmail());


        if (entity.getIdRole() != null) dto.setIdRole(entity.getIdRole().getId());
        if (entity.getIdInstitute() != null) dto.setIdInstitute(entity.getIdInstitute().getId());

        return dto;
    }

    // Conversión de Entidad a DTO de Respuesta (NO incluye password)
    public SysUserResponseDTO toResponseDto(SysUser entity) {
        if (entity == null) return null;

        SysUserResponseDTO dto = new SysUserResponseDTO();
        dto.setId(entity.getId());
        dto.setFirstname(entity.getFirstname());
        dto.setSecondname(entity.getSecondname());
        dto.setFirstLastname(entity.getFirstLastname());
        dto.setSecondLastname(entity.getSecondLastname());
        dto.setDni(entity.getDni());
        dto.setPhone(entity.getPhone());
        dto.setAddress(entity.getAddress());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setEmail(entity.getEmail());

        // Mapeamos los objetos Simple (ID + Nombre)
        if (entity.getIdRole() != null) {
            dto.setRole(new SysUserResponseDTO.RoleInfo(
                    entity.getIdRole().getId(),
                    entity.getIdRole().getName()
            ));
        }

        if (entity.getIdInstitute() != null) {
            dto.setInstitute(new SysUserResponseDTO.InstituteInfo(
                    entity.getIdInstitute().getId(),
                    entity.getIdInstitute().getName()
            ));
        }

        return dto;
    }

    public List<SysUserDTO> toDto(List<SysUser> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SysUserResponseDTO> toResponseDtoList(List<SysUser> entities) {
        return entities.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    // Conversión de DTO de Creación a Entidad
    public SysUser toEntity(SysUserCreateDTO dto) {
        if (dto == null) return null;
        SysUser entity = new SysUser();
        // ID no se copia para creación
        entity.setFirstname(dto.getFirstname());
        entity.setSecondname(dto.getSecondname());
        entity.setFirstLastname(dto.getFirstLastname());
        entity.setSecondLastname(dto.getSecondLastname());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setDni(dto.getDni());
        entity.setPassword(dto.getPassword());
        entity.setEmail(dto.getEmail());

        return entity;
    }

    public void updateEntityFromDto(SysUserDTO dto, SysUser entity) {

        if (dto.getFirstname() != null) {
            entity.setFirstname(dto.getFirstname());
        }
        if (dto.getSecondname() != null) {
            entity.setSecondname(dto.getSecondname());
        }
        if (dto.getFirstLastname() != null) {
            entity.setFirstLastname(dto.getFirstLastname());
        }
        if (dto.getSecondLastname() != null) {
            entity.setSecondLastname(dto.getSecondLastname());
        }
        if (dto.getPhone() != null) {
            entity.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getDni() != null) {
            entity.setDni(dto.getDni());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }


    }
}
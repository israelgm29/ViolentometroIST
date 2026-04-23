package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.InstituteDTO;
import ec.edu.istr.violentometro.model.Institute;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstituteMapper {

    // Entidad a DTO de Respuesta
    public InstituteDTO toDto(Institute entity) {
        if (entity == null) return null;
        InstituteDTO dto = new InstituteDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setShortName(entity.getShortName());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setProvince(entity.getProvince());
        dto.setCountry(entity.getCountry());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setWebUrl(entity.getWebUrl());
        dto.setStatus(entity.getStatus());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    public List<InstituteDTO> toDto(List<Institute> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    // DTO a Entidad (para Creación)
    public Institute toEntity(InstituteDTO dto) {
        if (dto == null) return null;
        Institute entity = new Institute();
        // ID se genera en la base de datos, no se mapea.
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setShortName(dto.getShortName());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setProvince(dto.getProvince());
        entity.setCountry(dto.getCountry());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setWebUrl(dto.getWebUrl());
        return entity;
    }


    public void updateEntityFromDto(InstituteDTO dto, Institute entity) {
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getShortName() != null) entity.setShortName(dto.getShortName());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getCity() != null) entity.setCity(dto.getCity());
        if (dto.getProvince() != null) entity.setProvince(dto.getProvince());
        if (dto.getCountry() != null) entity.setCountry(dto.getCountry());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getWebUrl() != null) entity.setWebUrl(dto.getWebUrl());
    }
}
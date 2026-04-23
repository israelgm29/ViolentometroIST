package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.AppUserRequestDTO;
import ec.edu.istr.violentometro.dto.AppUserResponseDTO;
import ec.edu.istr.violentometro.model.AppUser;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUserResponseDTO toDto(AppUser entity) {
        if (entity == null) return null;

        AppUserResponseDTO dto = new AppUserResponseDTO();
        dto.setId(entity.getId());
        dto.setDni(entity.getDni());
        dto.setBirthdate(entity.getBirthdate());
        dto.setStatus(entity.getStatus());

        // Mapeo de objetos internos estáticos
        if (entity.getIdGender() != null) {
            dto.setGender(new AppUserResponseDTO.GenderInfo(
                    entity.getIdGender().getId(),
                    entity.getIdGender().getName()
            ));
        }

        if (entity.getIdInstitute() != null) {
            dto.setInstitute(new AppUserResponseDTO.InstituteInfo(
                    entity.getIdInstitute().getId(),
                    entity.getIdInstitute().getName()
            ));
        }

        if (entity.getIdRegion() != null) {
            dto.setRegion(new AppUserResponseDTO.RegionInfo(
                    entity.getIdRegion().getId(),
                    entity.getIdRegion().getName()
            ));
        }

        if (entity.getIdDisability() != null) {
            dto.setDisability(new AppUserResponseDTO.DisabilityInfo(
                    entity.getIdDisability().getId(),
                    entity.getIdDisability().getName()
            ));
        }

        if (entity.getIdEthnicity() != null) {
            dto.setEthnicity(new AppUserResponseDTO.EthnicityInfo(
                    entity.getIdEthnicity().getId(),
                    entity.getIdEthnicity().getName()
            ));
        }

        return dto;
    }

    public AppUser toEntity(AppUserRequestDTO dto) {
        if (dto == null) return null;
        AppUser entity = new AppUser();
        entity.setDni(dto.getDni());
        entity.setBirthdate(dto.getBirthdate());
        // Las relaciones se resuelven en el Service
        return entity;
    }
}
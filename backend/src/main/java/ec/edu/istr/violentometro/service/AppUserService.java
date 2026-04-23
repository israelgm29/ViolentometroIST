package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.AppUserMapper;
import ec.edu.istr.violentometro.dto.AppUserRequestDTO;
import ec.edu.istr.violentometro.dto.AppUserResponseDTO;
import ec.edu.istr.violentometro.model.AppUser;
import ec.edu.istr.violentometro.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final InstituteRepository instituteRepository;
    private final GenderRepository genderRepository;
    private final RegionRepository regionRepository;
    private final DisabilityRepository disabilityRepository;
    private final EthnicityRepository ethnicityRepository;
    private final AppUserMapper appUserMapper;

    public List<AppUserResponseDTO> findAll() {
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toDto)
                .collect(Collectors.toList());
    }

    public AppUserResponseDTO findByDni(String dni) {
        AppUser appUser = appUserRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con DNI: " + dni));
        return appUserMapper.toDto(appUser);
    }

    @Transactional
    public AppUserResponseDTO create(AppUserRequestDTO requestDTO) {
        // Verificamos si ya existe el DNI para evitar duplicados
        if (appUserRepository.findByDni(requestDTO.getDni()).isPresent()) {
            throw new RuntimeException("El DNI ya se encuentra registrado");
        }

        AppUser entity = appUserMapper.toEntity(requestDTO);

        // Resolvemos todas las relaciones antes de guardar
        this.resolveRelationships(entity, requestDTO);

        AppUser savedUser = appUserRepository.save(entity);
        return appUserMapper.toDto(savedUser);
    }

    @Transactional
    public AppUserResponseDTO update(String dni, AppUserRequestDTO requestDTO) {
        AppUser existingUser = appUserRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con DNI: " + dni));

        // Actualizamos campos básicos
        existingUser.setDni(requestDTO.getDni());
        existingUser.setBirthdate(requestDTO.getBirthdate());

        // Resolvemos relaciones actualizadas
        this.resolveRelationships(existingUser, requestDTO);

        return appUserMapper.toDto(appUserRepository.save(existingUser));
    }


    private void resolveRelationships(AppUser entity, AppUserRequestDTO dto) {

        if (dto.getIdGender() != null) {
            entity.setIdGender(genderRepository.findById(dto.getIdGender())
                    .orElseThrow(() -> new EntityNotFoundException("Género no encontrado")));
        }

        if (dto.getIdInstitute() != null) {
            entity.setIdInstitute(instituteRepository.findById(dto.getIdInstitute())
                    .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado")));
        }

        if (dto.getIdRegion() != null) {
            entity.setIdRegion(regionRepository.findById(dto.getIdRegion())
                    .orElseThrow(() -> new EntityNotFoundException("Región no encontrada")));
        }

        if (dto.getIdDisability() != null) {
            entity.setIdDisability(disabilityRepository.findById(dto.getIdDisability())
                    .orElseThrow(() -> new EntityNotFoundException("Discapacidad no encontrada")));
        }

        if (dto.getIdEthnicity() != null) {
            entity.setIdEthnicity(ethnicityRepository.findById(dto.getIdEthnicity())
                    .orElseThrow(() -> new EntityNotFoundException("Etnia no encontrada")));
        }
    }

    public AppUserResponseDTO updateStatus(String dni, Boolean status) {
        AppUser existingUser = appUserRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Usuario de sistema no encontrado: " + dni));

        existingUser.setStatus(status);
        AppUser savedUser = appUserRepository.save(existingUser);

        return appUserMapper.toDto(savedUser);
    }

    public void deleteById(Integer id) {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
        appUserRepository.deleteById(id);
    }

}
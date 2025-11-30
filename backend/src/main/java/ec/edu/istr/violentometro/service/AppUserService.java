package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.AppUserMapper;
import ec.edu.istr.violentometro.dto.AppUserDTO;
import ec.edu.istr.violentometro.model.AppUser;
import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.repository.AppUserRepository;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok genera el constructor automáticamente
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final InstituteRepository instituteRepository;
    private final AppUserMapper appUserMapper;

    public List<AppUserDTO> findAll() {
        return appUserRepository.findAll().stream()
                .map(appUserMapper::toDto)
                .collect(Collectors.toList());
    }

    public AppUserDTO findByDni(String dni) {
        AppUser appUser = appUserRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con DNI: " + dni));
        return appUserMapper.toDto(appUser);
    }

    @Transactional // Importante para asegurar integridad de datos
    public AppUserDTO create(AppUserDTO appUserDTO) {
        Institute institute = instituteRepository.findById(appUserDTO.getIdInstitute())
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado"));

        AppUser appUser = appUserMapper.toEntity(appUserDTO);
        appUser.setIdInstitute(institute);

        AppUser savedUser = appUserRepository.save(appUser);
        return appUserMapper.toDto(savedUser);
    }

    @Transactional
    public AppUserDTO update(String dni, AppUserDTO appUserDTO) {
        AppUser existingAppUser = appUserRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + dni));

        // Actualizar campos
        existingAppUser.setDni(appUserDTO.getDni());

        // Si cambió el instituto, buscarlo y actualizarlo
        if (appUserDTO.getIdInstitute() != null) {
            Institute institute = instituteRepository.findById(appUserDTO.getIdInstitute())
                    .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado"));
            existingAppUser.setIdInstitute(institute);
        }

        return appUserMapper.toDto(appUserRepository.save(existingAppUser));
    }

    public void deleteById(Integer id) {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
        appUserRepository.deleteById(id);
    }
}
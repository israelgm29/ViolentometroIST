package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.SysUserMapper;
import ec.edu.istr.violentometro.dto.SysUserCreateDTO;
import ec.edu.istr.violentometro.dto.SysUserDTO;
import ec.edu.istr.violentometro.dto.SysUserResponseDTO;
import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.model.SysRole;
import ec.edu.istr.violentometro.model.SysUser;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import ec.edu.istr.violentometro.repository.RoleRepository;
import ec.edu.istr.violentometro.repository.SysUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserRepository sysUserRepository;
    private final RoleRepository roleRepository;
    private final InstituteRepository instituteRepository;
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;



    public List<SysUserDTO> findAll() {
        return sysUserMapper.toDto(sysUserRepository.findAll());
    }

    public List<SysUserResponseDTO> findAllResponse(){
        return sysUserMapper.toResponseDtoList(sysUserRepository.findAll());
    }

    public SysUserDTO findById(Integer id) {
        SysUser user = sysUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario de sistema no encontrado: " + id));
        return sysUserMapper.toDto(user);
    }

    @Transactional
    public SysUserDTO save(SysUserCreateDTO dto) {

        SysRole role = roleRepository.findById(dto.getIdRole())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + dto.getIdRole()));

        Institute institute = instituteRepository.findById(dto.getIdInstitute())
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado: " + dto.getIdInstitute()));


        SysUser newUser = sysUserMapper.toEntity(dto);

        // Encriptar la contraseña antes de guardar
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 3. Asignar Entidades de relación
        newUser.setIdRole(role);
        newUser.setIdInstitute(institute);

        // 4. Guardar
        return sysUserMapper.toDto(sysUserRepository.save(newUser));
    }

    @Transactional
    public SysUserDTO update(Integer id, SysUserDTO dto) {
        SysUser existingUser = sysUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario de sistema no encontrado: " + id));

        // Actualizar campos básicos
        sysUserMapper.updateEntityFromDto(dto, existingUser);

        dto.setIdRole(existingUser.getIdRole().getId());
        dto.setIdInstitute(existingUser.getIdInstitute().getId());

        // Actualizar relaciones si es necesario
        if (dto.getIdRole() != null && !dto.getIdRole().equals(existingUser.getIdRole().getId())) {
            SysRole role = roleRepository.findById(dto.getIdRole())
                    .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + dto.getIdRole()));
            existingUser.setIdRole(role);
        }
        if( dto.getIdInstitute() != null && !dto.getIdInstitute().equals(existingUser.getIdInstitute().getId())) {
            Institute institute = instituteRepository.findById(dto.getIdInstitute())
                    .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado: " + dto.getIdInstitute()));
            existingUser.setIdInstitute(institute);
        }

        return sysUserMapper.toDto(sysUserRepository.save(existingUser));
    }

    public void deleteById(Integer id) {
        if (!sysUserRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario de sistema no encontrado: " + id);
        }
        sysUserRepository.deleteById(id);
    }

    public SysUserResponseDTO updateStatus(Integer id, Boolean status) {
        SysUser existingUser = sysUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario de sistema no encontrado: " + id));

        existingUser.setStatus(status);
        SysUser savedUser = sysUserRepository.save(existingUser);

        return sysUserMapper.toResponseDto(savedUser);
    }

    @Transactional
    public void resetPassword(Integer userId, String newPassword) {
        SysUser user = sysUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            user.setPassword(passwordEncoder.encode(newPassword));

        sysUserRepository.save(user);
    }
}
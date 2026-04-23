package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.ProfileMapper;
import ec.edu.istr.violentometro.dto.ChangePasswordDTO;
import ec.edu.istr.violentometro.dto.ProfileResponseDTO;
import ec.edu.istr.violentometro.dto.UpdateProfileDTO;
import ec.edu.istr.violentometro.model.SysUser;
import ec.edu.istr.violentometro.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder   passwordEncoder;
    private final ProfileMapper profileMapper;  // ← inyectado

    public ProfileResponseDTO getProfile(SysUser user) {
        return profileMapper.toDTO(user);
    }

    public ProfileResponseDTO updateProfile(SysUser user, UpdateProfileDTO dto) {
        user.setFirstname(dto.getFirstname());
        user.setSecondname(dto.getSecondname());
        user.setFirstLastname(dto.getFirstLastname());
        user.setSecondLastname(dto.getSecondLastname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        SysUser saved = sysUserRepository.save(user);
        return profileMapper.toDTO(saved);  // ← usa el mapper
    }

    public void changePassword(SysUser user, ChangePasswordDTO dto) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        sysUserRepository.save(user);
    }
}

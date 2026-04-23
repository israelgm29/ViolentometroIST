package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.ProfileResponseDTO;
import ec.edu.istr.violentometro.model.SysUser;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponseDTO toDTO(SysUser user) {
        return ProfileResponseDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .secondname(user.getSecondname())
                .firstLastname(user.getFirstLastname())
                .secondLastname(user.getSecondLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .dni(user.getDni())
                .role(user.getIdRole().getName())
                .status(user.getStatus())
                .build();
    }
}

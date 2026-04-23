package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.SysUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, Integer> {
    Optional<SysUser> findByEmail(@NotBlank(message = "El email es requerido") @Email(message = "Email inválido") String email);
}

package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByDni(String dni);
}

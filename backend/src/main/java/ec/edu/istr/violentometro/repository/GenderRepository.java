package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenderRepository extends JpaRepository<Gender, Integer> {
    Optional<Gender> findByNameIgnoreCase(String name);
}

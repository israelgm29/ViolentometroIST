package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Institute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstituteRepository extends JpaRepository<Institute, Integer> {
    Optional<Institute> findByCode(String code);
}

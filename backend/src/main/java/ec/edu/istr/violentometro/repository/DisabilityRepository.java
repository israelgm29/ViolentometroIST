package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Disability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisabilityRepository extends JpaRepository<Disability, Integer> {
    Optional<Disability> findByNameIgnoreCase(String name);
}

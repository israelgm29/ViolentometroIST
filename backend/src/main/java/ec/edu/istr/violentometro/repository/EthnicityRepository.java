package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Ethnicity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EthnicityRepository extends JpaRepository<Ethnicity, Integer> {
    Optional<Ethnicity> findByNameIgnoreCase(String name);
}

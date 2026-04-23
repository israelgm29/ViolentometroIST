package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {
    Optional<Region> findByNameIgnoreCase(String name);
}

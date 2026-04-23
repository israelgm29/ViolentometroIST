package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<SysRole, Integer> {
}

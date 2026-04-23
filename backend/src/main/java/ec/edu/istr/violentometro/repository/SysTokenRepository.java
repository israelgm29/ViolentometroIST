package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.SysToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysTokenRepository extends JpaRepository<SysToken, Integer> {

    // Busca el token activo (no expirado ni revocado)
    Optional<SysToken> findByTokenAndExpiredFalseAndRevokedFalse(String token);

    // Todos los tokens válidos de un usuario (para revocarlos al hacer login)
    @Query("""
        SELECT t FROM SysToken t
        WHERE t.sysUser.id = :userId
        AND t.expired = false
        AND t.revoked = false
    """)
    List<SysToken> findAllValidTokensByUser(Integer userId);
}
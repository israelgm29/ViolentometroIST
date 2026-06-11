package ec.edu.istr.violentometro.util;

import ec.edu.istr.violentometro.model.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper de seguridad: extrae datos del usuario autenticado
 * desde el SecurityContext (ya resuelto por JwtAuthFilter).
 */
@Component
public class SecurityUtils {

    /**
     * Devuelve el id_instituto del usuario autenticado.
     * Lanza IllegalStateException si no tiene instituto asignado.
     */
    public Integer getIdInstituto() {
        SysUser user = getAuthenticatedUser();
        if (user.getIdInstitute() == null) {
            throw new IllegalStateException("El usuario no tiene instituto asignado.");
        }
        return user.getIdInstitute().getId();
    }

    /**
     * Devuelve el SysUser completo del SecurityContext.
     */
    public SysUser getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }
        return (SysUser) auth.getPrincipal();
    }
}
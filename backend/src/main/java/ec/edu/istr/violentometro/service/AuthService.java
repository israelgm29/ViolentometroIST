package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.LoginRequestDTO;
import ec.edu.istr.violentometro.dto.LoginResponseDTO;
import ec.edu.istr.violentometro.model.SysToken;
import ec.edu.istr.violentometro.model.SysUser;
import ec.edu.istr.violentometro.repository.SysTokenRepository;
import ec.edu.istr.violentometro.repository.SysUserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserRepository     sysUserRepository;
    private final SysTokenRepository    sysTokenRepository;
    private final JwtService            jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response) {

        // 1. Autentica
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Busca el usuario
        SysUser user = sysUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 3. Revoca todos los tokens anteriores del usuario
        revokeAllUserTokens(user);

        // 4. Genera nuevo token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getIdRole().getName());
        claims.put("userId", user.getId());
        String jwt = jwtService.generateToken(user, claims);

        // 5. Guarda el nuevo token en BD
        saveToken(user, jwt);

        // 6. Envía en cookie HttpOnly
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true en producción
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000));
        response.addCookie(cookie);

        return LoginResponseDTO.builder()
                .id(user.getId())
                .username(user.getFirstname())
                .email(user.getEmail())
                .role(user.getIdRole().getName())
                .build();
    }

    public void logout(HttpServletResponse response) {
        // La cookie se invalida en el cliente
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    // ── Métodos privados ─────────────────────────

    private void saveToken(SysUser user, String jwt) {
        SysToken token = SysToken.builder()
                .token(jwt)
                .sysUser(user)
                .expired(false)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        sysTokenRepository.save(token);
    }

    private void revokeAllUserTokens(SysUser user) {
        List<SysToken> validTokens = sysTokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) return;

        validTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        sysTokenRepository.saveAll(validTokens);
    }
}
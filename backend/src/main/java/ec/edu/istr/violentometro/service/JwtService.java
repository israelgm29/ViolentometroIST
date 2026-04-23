package ec.edu.istr.violentometro.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ── Generar token ────────────────────────────
    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .claims(extraClaims)                        // 0.12+: claims() en lugar de setClaims()
                .subject(userDetails.getUsername())         // 0.12+: subject() en lugar de setSubject()
                .issuedAt(new Date())                       // 0.12+: issuedAt()
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // 0.12+: expiration()
                .signWith(getSignKey())                     // 0.12+: no necesita especificar algoritmo
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, new HashMap<>());
    }

    // ── Validar token ────────────────────────────
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ── Extraer claims ───────────────────────────
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    // ── Internos ─────────────────────────────────
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()                                // 0.12+: parser() en lugar de parserBuilder()
                .verifyWith(getSignKey())                   // 0.12+: verifyWith() en lugar de setSigningKey()
                .build()
                .parseSignedClaims(token)                  // 0.12+: parseSignedClaims() en lugar de parseClaimsJws()
                .getPayload();                             // 0.12+: getPayload() en lugar de getBody()
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
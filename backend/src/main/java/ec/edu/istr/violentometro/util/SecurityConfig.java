package ec.edu.istr.violentometro.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS debe ir primero
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Desactivar CSRF para API Stateless
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Configurar permisos de rutas
                .authorizeHttpRequests(auth -> auth
                        // Permitir todas las peticiones OPTIONS (Preflight de CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ============================================
                        // RUTAS PÚBLICAS (sin autenticación)
                        // ============================================
                        .requestMatchers(HttpMethod.GET, "/api/v1/user-answers/user/dni/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user-answers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/genders/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/regions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/ethnicities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/disabilities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user-answers/user/dni/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/campaigns/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/campaigns/categories/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user-answers/can-answer").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/institutes").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/quiz-results").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/statistics/**").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/api/v1/institutes/*/logo").permitAll()
                        .requestMatchers("/api/v1/reports/**").permitAll()

                        // Rutas públicas de autenticación
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/logout"
                        ).permitAll()

                        // ============================================
                        // RUTAS SEMI-PÚBLICAS (cualquier usuario autenticado)
                        // ============================================
                        .requestMatchers(
                                "/api/v1/surveys/**",
                                "/api/v1/questions/**",
                                "/api/v1/violence-zones/**"
                        ).authenticated()

                        // ============================================
                        // RUTAS POR ROL ESPECÍFICO
                        // ============================================

                        // SOLO ADMIN (gestión completa del sistema)
                        .requestMatchers("/api/v1/sys-users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/settings/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/institutes/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/violence-zones/**").hasRole("ADMIN")

                        // ADMIN + ANALYST + WELFARE (bienestar puede ver ciertas cosas)
                        .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**")
                        .hasAnyRole("ADMIN", "WELFARE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/campaigns/**")
                        .hasAnyRole("ADMIN", "WELFARE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/**")
                        .hasAnyRole("ADMIN", "WELFARE")

                        // ADMIN + WELFARE (ANALYST NO tiene acceso)
                        .requestMatchers(HttpMethod.GET, "/api/v1/surveys/**")
                        .hasAnyRole("ADMIN", "WELFARE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/questions/**")
                        .hasAnyRole("ADMIN", "WELFARE")

                        // CRUD completo solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/surveys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/surveys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/surveys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/questions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/questions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/questions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/campaigns/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/campaigns/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/campaigns/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/institutes/*/logo").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/institutes/*/logo").hasAnyRole("ADMIN")

                        // Gestión de usuarios (app-users)
                        // GET: todos pueden ver (ADMIN, ANALYST, WELFARE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/app-users/**")
                        .hasAnyRole("ADMIN", "ANALYST", "WELFARE")
                        // POST (crear): ADMIN, ANALYST, WELFARE
                        .requestMatchers(HttpMethod.POST, "/api/v1/app-users/**")
                        .hasAnyRole("ADMIN", "ANALYST", "WELFARE")
                        // PUT (editar): solo ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/app-users/**")
                        .hasRole("ADMIN")
                        // DELETE: solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/app-users/**")
                        .hasRole("ADMIN")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // 4. Gestión de sesión (Sin estado por JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Proveedor de autenticación y filtro JWT
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origen de tu Angular
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost",
                "http://localhost:80"
        ));

        // Métodos permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Cabeceras permitidas
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));

        // Permitir credenciales (cookies)
        config.setAllowCredentials(true);

        // Tiempo que el navegador guarda la respuesta del Preflight (30 min)
        config.setMaxAge(1800L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
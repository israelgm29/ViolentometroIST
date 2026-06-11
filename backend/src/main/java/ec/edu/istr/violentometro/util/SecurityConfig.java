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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // ── CORS Preflight ──────────────────────────────────────
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ── Auth ────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()

                        // ── App-users (flujo público de encuesta, sin sesión) ───
                        // Los app-users NO se loguean; operan solo con su cédula.
                        // GET:   buscar por DNI para validar/mostrar perfil
                        // POST:  registrar nuevo usuario
                        // PUT:   completar/actualizar perfil antes de la encuesta
                        // PATCH: actualizar estado
                        // DELETE: solo ADMIN (va en la sección de roles más abajo)
                        .requestMatchers(HttpMethod.GET,   "/api/v1/app-users/**").permitAll()
                        .requestMatchers(HttpMethod.POST,  "/api/v1/app-users").permitAll()
                        .requestMatchers(HttpMethod.PUT,   "/api/v1/app-users/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/app-users/**").permitAll()
                        // Busca esta sección y asegúrate de que sea permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/questions/**").permitAll()


                        // ── Encuesta (flujo público) ────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/v1/user-answers").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/v1/user-answers/user/dni/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/v1/user-answers/can-answer").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/quiz-results").permitAll()

                        // ── Catálogos públicos ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/v1/genders/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/regions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/ethnicities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/disabilities/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/statistics/**").permitAll()

                        // Institutes: solo lista e imagen son públicos
                        .requestMatchers(HttpMethod.GET, "/api/v1/institutes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/institutes/*/logo").permitAll()

                        // Campaigns: solo activas son públicas
                        .requestMatchers(HttpMethod.GET, "/api/v1/campaigns/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/campaigns/categories/active").permitAll()

                        // Reports: público (lectura)
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports/**").permitAll()

                        // Surveys: solo la activa es pública (flujo cuestionario)
                        .requestMatchers(HttpMethod.GET, "/api/v1/surveys/active").permitAll()

                        // Violence-zones: público para mostrar zonas en el cuestionario
                        .requestMatchers(HttpMethod.GET, "/api/v1/violence-zones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/violence-zones/**").permitAll()

                        // ── Roles: ADMIN ────────────────────────────────────────
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/app-users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/sys-users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/sys-users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/sys-users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/sys-users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/settings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/settings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/settings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/settings/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/violence-zones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/violence-zones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/violence-zones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/violence-zones/**").hasRole("ADMIN")

                        // Institutes CRUD (fuera de los públicos declarados arriba)
                        .requestMatchers(HttpMethod.POST,   "/api/v1/institutes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/institutes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/institutes/**").hasRole("ADMIN")

                        // ── Roles: ADMIN + WELFARE ──────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**").hasAnyRole("ADMIN", "WELFARE")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/campaigns/**").hasAnyRole("ADMIN", "WELFARE")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/campaigns/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/campaigns/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/campaigns/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/surveys/**").hasAnyRole("ADMIN", "WELFARE")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/surveys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/surveys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/surveys/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,    "/api/v1/questions/**").hasAnyRole("ADMIN", "WELFARE")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/questions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/questions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/questions/**").hasRole("ADMIN")

                        // ── Cualquier otra ruta requiere autenticación ──────────
                        .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. Permitir cualquier origen para Apps Móviles (no tienen dominio fijo)
        config.setAllowedOriginPatterns(List.of("*"));

        // 2. Permitir los métodos necesarios
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 3. ¡IMPORTANTE! Añade "X-Client-Type" a las cabeceras permitidas
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "X-Client-Type" // <-- Añade esta
        ));

        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
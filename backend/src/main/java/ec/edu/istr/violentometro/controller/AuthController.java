package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.LoginRequestDTO;
import ec.edu.istr.violentometro.dto.LoginResponseDTO;
import ec.edu.istr.violentometro.model.SysUser;
import ec.edu.istr.violentometro.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }

    // sesion activa
    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(@AuthenticationPrincipal SysUser user) {
        return ResponseEntity.ok(
                LoginResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getFirstname() + " " + user.getFirstLastname())
                        .email(user.getEmail())
                        .role(user.getIdRole().getName())
                        .build()
        );
    }
}
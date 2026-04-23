package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.ChangePasswordDTO;
import ec.edu.istr.violentometro.dto.ProfileResponseDTO;
import ec.edu.istr.violentometro.dto.UpdateProfileDTO;
import ec.edu.istr.violentometro.model.SysUser;
import ec.edu.istr.violentometro.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponseDTO> getProfile(
            @AuthenticationPrincipal SysUser user) {
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    @PutMapping
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @AuthenticationPrincipal SysUser user,
            @RequestBody @Valid UpdateProfileDTO dto) {
        return ResponseEntity.ok(profileService.updateProfile(user, dto));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal SysUser user,
            @RequestBody @Valid ChangePasswordDTO dto) {
        profileService.changePassword(user, dto);
        return ResponseEntity.ok().build();
    }
}
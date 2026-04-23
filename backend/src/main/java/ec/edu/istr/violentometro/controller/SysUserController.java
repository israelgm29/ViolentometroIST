package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.PasswordResetDTO;
import ec.edu.istr.violentometro.dto.SysUserCreateDTO;
import ec.edu.istr.violentometro.dto.SysUserDTO;
import ec.edu.istr.violentometro.dto.SysUserResponseDTO;
import ec.edu.istr.violentometro.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sys-users")
@RequiredArgsConstructor
class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping
    public ResponseEntity<List<SysUserResponseDTO>> getAll() {
        List<SysUserResponseDTO> users = sysUserService.findAllResponse();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SysUserDTO> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(sysUserService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SysUserDTO> create(@RequestBody @Valid SysUserCreateDTO sysUserCreateDTO) {
        SysUserDTO savedUser = sysUserService.save(sysUserCreateDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SysUserDTO> update(@PathVariable("id") Integer id, @RequestBody @Valid SysUserDTO sysUserDTO) {
        SysUserDTO updatedUser = sysUserService.update(id, sysUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SysUserResponseDTO> updateStatus(@PathVariable("id") Integer id, @RequestParam("status") Boolean status) {
        SysUserResponseDTO updatedUser = sysUserService.updateStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Integer id,
            @RequestBody PasswordResetDTO passwordResetDTO) {

        sysUserService.resetPassword(id, passwordResetDTO.getNewPassword());

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        sysUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
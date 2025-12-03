package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.AppUserDTO;
import ec.edu.istr.violentometro.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app-users") // Plural y kebab-case
@RequiredArgsConstructor
class AppUserController {

    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers() {
        return ResponseEntity.ok(appUserService.findAll());
    }

    @GetMapping("/{dni}")
    public ResponseEntity<AppUserDTO> getAppUserByDni(@PathVariable String dni) {
        return ResponseEntity.ok(appUserService.findByDni(dni));
    }

    @PostMapping
    public ResponseEntity<AppUserDTO> addAppUser(@RequestBody @Validated AppUserDTO appUserRequest) {
        // @Valid activará las validaciones del DTO
        return new ResponseEntity<>(appUserService.create(appUserRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{dni}")
    public ResponseEntity<AppUserDTO> updateAppUser(@PathVariable String dni, @RequestBody @Validated AppUserDTO appUserRequest) {
        return ResponseEntity.ok(appUserService.update(dni, appUserRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Integer id) {
        appUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
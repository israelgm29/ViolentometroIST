package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.AppUserRequestDTO;
import ec.edu.istr.violentometro.dto.AppUserResponseDTO;
import ec.edu.istr.violentometro.service.AppUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app-users")
@AllArgsConstructor
class AppUserController {
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<List<AppUserResponseDTO>> getAllAppUsers() {
        return ResponseEntity.ok(appUserService.findAll());
    }

    @GetMapping("/{dni}")
    public ResponseEntity<AppUserResponseDTO> getAppUserByDni(@PathVariable String dni) {
        AppUserResponseDTO appUser = appUserService.findByDni(dni);
        return ResponseEntity.ok(appUser);
    }

    @PostMapping
    public ResponseEntity<AppUserResponseDTO> addAppUser(@RequestBody AppUserRequestDTO request) {
        return new ResponseEntity<>(appUserService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{dni}")
    public ResponseEntity<AppUserResponseDTO> update(@PathVariable("dni") String dni, @RequestBody @Valid AppUserRequestDTO appUserRequestDTO) {
        AppUserResponseDTO updatedUser = appUserService.update(dni, appUserRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @PatchMapping("/{dni}/status")
    public ResponseEntity<AppUserResponseDTO> updateStatus(@PathVariable("dni") String dni, @RequestParam("status") Boolean status) {
        AppUserResponseDTO updatedUser = appUserService.updateStatus(dni, status);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppUserByDni(@PathVariable Integer id) {
        appUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

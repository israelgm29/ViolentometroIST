package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.AppUserDTO;
import ec.edu.istr.violentometro.model.AppUser;
import ec.edu.istr.violentometro.service.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/violentometro")
class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/app_user")
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers() throws Exception {
        List<AppUser> appUsers = appUserService.findAll();

        List<AppUserDTO> appUserDTOs = appUsers.stream()
                .map(this::convertToDtoWithBeanUtils)
                .collect(Collectors.toList());

        return ResponseEntity.ok(appUserDTOs);
    }

    @GetMapping("/app_user/{dni}")
    public ResponseEntity<AppUserDTO> getAppUserByDni(@PathVariable String dni) throws Exception {
        return appUserService.findByDni(dni)
                .map(this::convertToDtoWithBeanUtils)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/app_user")
    public ResponseEntity<AppUserDTO> addAppUser(@RequestBody AppUserDTO appUserRequest) throws Exception {
        AppUser savedAppUser = appUserService.createFromDto(appUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDtoWithBeanUtils(savedAppUser));
    }

    @PutMapping("/app_user/{id}")
    public ResponseEntity<AppUserDTO> updateAppUser(@PathVariable Integer id, @RequestBody AppUserDTO appUserRequest) throws Exception {
        try {
            AppUser updatedAppUser = appUserService.updateOneFromDto(id, appUserRequest);
            return ResponseEntity.ok(convertToDtoWithBeanUtils(updatedAppUser));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/app_user/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Integer id) {
        try {
            appUserService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private AppUserDTO convertToDtoWithBeanUtils(AppUser appUser) {
        AppUserDTO dto = new AppUserDTO();
        BeanUtils.copyProperties(appUser, dto);
        if (appUser.getIdInstitute() != null) {
            dto.setIdInstitute(appUser.getIdInstitute().getId());
        } else {
            dto.setIdInstitute(null);
        }
        return dto;
    }
}
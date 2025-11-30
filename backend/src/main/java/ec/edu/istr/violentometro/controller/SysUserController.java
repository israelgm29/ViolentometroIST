package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.SysUserDTO;
import ec.edu.istr.violentometro.dto.SysUserCreateDTO;
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
    public ResponseEntity<List<SysUserDTO>> getAll() {
        List<SysUserDTO> users = sysUserService.findAll();
        // El ResponseEntity.noContent() solo se usa si la lista está vacía, pero devolver [] (OK) es común
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SysUserDTO> getById(@PathVariable Integer id) {
        // Si no existe, el Service lanza EntityNotFoundException (capturada por GlobalExceptionHandler)
        return ResponseEntity.ok(sysUserService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SysUserDTO> create(@RequestBody @Valid SysUserCreateDTO sysUserCreateDTO) {
        SysUserDTO savedUser = sysUserService.save(sysUserCreateDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Añadimos la operación de actualización
    @PutMapping("/{id}")
    public ResponseEntity<SysUserDTO> update(@PathVariable Integer id, @RequestBody @Valid SysUserDTO sysUserDTO) {
        SysUserDTO updatedUser = sysUserService.update(id, sysUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Añadimos la operación de borrado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sysUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
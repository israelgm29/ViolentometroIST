package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.SysRoleDTO;
import ec.edu.istr.violentometro.service.SysRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor // Inyección limpia con Lombok
class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping
    public ResponseEntity<List<SysRoleDTO>> getAll() {
        List<SysRoleDTO> roles = sysRoleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SysRoleDTO> getById(@PathVariable Integer id) {
        // El Service lanza EntityNotFoundException si no existe (mapeado a 404)
        return ResponseEntity.ok(sysRoleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SysRoleDTO> create(@RequestBody @Valid SysRoleDTO sysRoleDTO) {
        SysRoleDTO savedRole = sysRoleService.save(sysRoleDTO);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SysRoleDTO> update(@PathVariable Integer id, @RequestBody @Valid SysRoleDTO sysRoleDTO) {
        // El Service maneja la lógica de actualización segura y lanza 404 si no existe
        SysRoleDTO updatedRole = sysRoleService.updateOne(id, sysRoleDTO);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        // El Service maneja la lógica de validación y lanza 404 si no existe
        sysRoleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
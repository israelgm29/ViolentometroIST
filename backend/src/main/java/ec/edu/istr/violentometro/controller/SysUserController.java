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
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SysUserDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(sysUserService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SysUserDTO> create(@RequestBody @Valid SysUserCreateDTO sysUserCreateDTO) {
        SysUserDTO savedUser = sysUserService.save(sysUserCreateDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SysUserDTO> update(@PathVariable Integer id, @RequestBody @Valid SysUserDTO sysUserDTO) {
        SysUserDTO updatedUser = sysUserService.update(id, sysUserDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sysUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
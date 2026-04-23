package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.SysRoleMapper;
import ec.edu.istr.violentometro.dto.SysRoleDTO;
import ec.edu.istr.violentometro.model.SysRole;
import ec.edu.istr.violentometro.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final RoleRepository roleRepository;
    private final SysRoleMapper sysRoleMapper;

    public SysRoleDTO save(SysRoleDTO dto) {
        SysRole newRole = sysRoleMapper.toEntity(dto);
        return sysRoleMapper.toDto(roleRepository.save(newRole));
    }

    public List<SysRoleDTO> findAll() {
        return sysRoleMapper.toDto(roleRepository.findAll());
    }

    public SysRoleDTO findById(Integer id) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol de sistema no encontrado con ID: " + id));
        return sysRoleMapper.toDto(role);
    }

    @Transactional
    public SysRoleDTO updateOne(Integer id, SysRoleDTO dto) {
        // Obtener la entidad existente (lanza 404 si no existe)
        SysRole existingSysRole = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol de sistema no encontrado con ID: " + id));

        // Solo actualiza campos non-null
        sysRoleMapper.updateEntityFromDto(dto, existingSysRole);

        // Guardar
        return sysRoleMapper.toDto(roleRepository.save(existingSysRole));
    }

    public void deleteById(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol de sistema no encontrado con ID: " + id);
        }
        roleRepository.deleteById(id);
    }
}
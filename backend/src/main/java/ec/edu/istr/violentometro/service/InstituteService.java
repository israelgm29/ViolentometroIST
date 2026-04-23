package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.InstituteMapper;
import ec.edu.istr.violentometro.dto.InstituteDTO;
import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstituteService {

    private final InstituteRepository instituteRepository;
    private final InstituteMapper instituteMapper;

    public InstituteDTO save(InstituteDTO dto) {
        Institute newInstitute = instituteMapper.toEntity(dto);
        return instituteMapper.toDto(instituteRepository.save(newInstitute));
    }

    public List<InstituteDTO> findAll() {
        return instituteMapper.toDto(instituteRepository.findAll());
    }

    public InstituteDTO findById(Integer id) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado con ID: " + id));
        return instituteMapper.toDto(institute);
    }

    @Transactional
    public InstituteDTO updateOne(Integer id, InstituteDTO dto) {
        // 1. Obtener la entidad existente (lanza 404 si no existe)
        Institute existingInstitute = instituteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado con ID: " + id));

        // 2. Mapeo seguro: Solo actualiza campos non-null
        instituteMapper.updateEntityFromDto(dto, existingInstitute);

        // 3. Guardar y devolver DTO
        return instituteMapper.toDto(instituteRepository.save(existingInstitute));
    }

    public void deleteById(Integer id) {
        if (!instituteRepository.existsById(id)) {
            throw new EntityNotFoundException("Instituto no encontrado con ID: " + id);
        }
        instituteRepository.deleteById(id);
    }
}
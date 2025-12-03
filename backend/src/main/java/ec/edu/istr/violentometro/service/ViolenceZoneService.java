package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.ViolenceZoneMapper;
import ec.edu.istr.violentometro.dto.ViolenceZoneDTO;
import ec.edu.istr.violentometro.model.ViolenceZone;
import ec.edu.istr.violentometro.repository.ViolenceZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViolenceZoneService {

    private final ViolenceZoneRepository violenceZoneRepository;
    private final ViolenceZoneMapper violenceZoneMapper;

    public ViolenceZoneDTO save(ViolenceZoneDTO dto) {
        ViolenceZone newZone = violenceZoneMapper.toEntity(dto);
        return violenceZoneMapper.toDto(violenceZoneRepository.save(newZone));
    }

    public List<ViolenceZoneDTO> findAll() {
        return violenceZoneMapper.toDto(violenceZoneRepository.findAll());
    }

    public ViolenceZoneDTO findById(Integer id) {
        ViolenceZone zone = violenceZoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zona de violencia no encontrada con ID: " + id));
        return violenceZoneMapper.toDto(zone);
    }

    @Transactional
    public ViolenceZoneDTO updateOne(Integer id, ViolenceZoneDTO dto) {
        // Obtener la entidad existente
        ViolenceZone existingZone = violenceZoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zona de violencia no encontrada con ID: " + id));

        // Actualiza campos non-null
        violenceZoneMapper.updateEntityFromDto(dto, existingZone);

        // Guardar
        return violenceZoneMapper.toDto(violenceZoneRepository.save(existingZone));
    }

    public void deleteById(Integer id) {
        if (!violenceZoneRepository.existsById(id)) {
            throw new EntityNotFoundException("Zona de violencia no encontrada con ID: " + id);
        }
        violenceZoneRepository.deleteById(id);
    }
}
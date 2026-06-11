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

    private final InstituteRepository  instituteRepository;
    private final InstituteMapper      instituteMapper;
    private final ViolenceZoneService  violenceZoneService;   // ← NUEVO

    // ── Crear instituto + copiar zonas plantilla automáticamente ──
    @Transactional
    public InstituteDTO save(InstituteDTO dto) {
        Institute newInstitute = instituteMapper.toEntity(dto);
        Institute saved = instituteRepository.save(newInstitute);

        // ← NUEVO: copia automática de zonas plantilla
        violenceZoneService.copiarPlantillasAInstituto(saved.getId());

        return instituteMapper.toDto(saved);
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
        Institute existingInstitute = instituteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado con ID: " + id));
        instituteMapper.updateEntityFromDto(dto, existingInstitute);
        return instituteMapper.toDto(instituteRepository.save(existingInstitute));
    }

    public void deleteById(Integer id) {
        if (!instituteRepository.existsById(id)) {
            throw new EntityNotFoundException("Instituto no encontrado con ID: " + id);
        }
        instituteRepository.deleteById(id);
    }

    public void updateLogo(Integer id, byte[] logoBytes, String contentType) {
        Institute institute = instituteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado: " + id));
        institute.setLogo(logoBytes);
        institute.setLogoContentType(contentType);
        instituteRepository.save(institute);
    }

    public byte[] getLogo(Integer id) {
        return instituteRepository.findById(id).map(Institute::getLogo).orElse(null);
    }

    public String getLogoContentType(Integer id) {
        return instituteRepository.findById(id).map(Institute::getLogoContentType).orElse(null);
    }
}
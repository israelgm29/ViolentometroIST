package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.model.ViolenceZone;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import ec.edu.istr.violentometro.repository.ViolenceZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViolenceZoneService {

    private final ViolenceZoneRepository zoneRepository;
    private final InstituteRepository    instituteRepository;

    // ── Listar zonas de un instituto ─────────────────────────────
    public List<ViolenceZone> findByInstituto(Integer idInstituto) {
        return zoneRepository.findByInstitute_IdAndIsTemplateFalse(idInstituto);
    }

    // ── Listar plantillas globales ────────────────────────────────
    public List<ViolenceZone> findPlantillas() {
        return zoneRepository.findByIsTemplateTrueAndInstituteIsNull();
    }

    // ── Crear zona propia de un instituto ────────────────────────
    @Transactional
    public ViolenceZone create(ViolenceZone zone, Integer idInstituto) {
        Institute institute = instituteRepository.findById(idInstituto)
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado: " + idInstituto));
        zone.setInstitute(institute);
        zone.setIsTemplate(false);
        return zoneRepository.save(zone);
    }

    // ── Actualizar zona (solo si pertenece al instituto) ─────────
    @Transactional
    public ViolenceZone update(Integer idZone, ViolenceZone datos, Integer idInstituto) {
        ViolenceZone zone = zoneRepository.findById(idZone)
                .orElseThrow(() -> new EntityNotFoundException("Zona no encontrada: " + idZone));

        validarPertenencia(zone, idInstituto);

        zone.setName(datos.getName());
        zone.setDescription(datos.getDescription());
        zone.setColor(datos.getColor());
        zone.setSeverity(datos.getSeverity());
        zone.setStatus(datos.getStatus());
        zone.setResultTitle(datos.getResultTitle());
        zone.setResultMessage(datos.getResultMessage());
        zone.setRecommendations(datos.getRecommendations());

        return zoneRepository.save(zone);
    }

    // ── Eliminar zona (solo si pertenece al instituto) ───────────
    @Transactional
    public void delete(Integer idZone, Integer idInstituto) {
        ViolenceZone zone = zoneRepository.findById(idZone)
                .orElseThrow(() -> new EntityNotFoundException("Zona no encontrada: " + idZone));
        validarPertenencia(zone, idInstituto);
        zoneRepository.delete(zone);
    }

    // ── Copiar plantillas globales a un instituto nuevo ───────────
    @Transactional
    public void copiarPlantillasAInstituto(Integer idInstituto) {
        // Si ya tiene zonas, no copiar de nuevo
        if (zoneRepository.existsByInstitute_Id(idInstituto)) return;

        Institute institute = instituteRepository.findById(idInstituto)
                .orElseThrow(() -> new EntityNotFoundException("Instituto no encontrado: " + idInstituto));

        List<ViolenceZone> plantillas = zoneRepository.findByIsTemplateTrueAndInstituteIsNull();

        List<ViolenceZone> copias = plantillas.stream().map(p -> {
            ViolenceZone copia = new ViolenceZone();
            copia.setName(p.getName());
            copia.setDescription(p.getDescription());
            copia.setColor(p.getColor());
            copia.setSeverity(p.getSeverity());
            copia.setStatus(p.getStatus());
            copia.setResultTitle(p.getResultTitle());
            copia.setResultMessage(p.getResultMessage());
            copia.setRecommendations(p.getRecommendations());
            copia.setInstitute(institute);
            copia.setIsTemplate(false);
            return copia;
        }).toList();

        zoneRepository.saveAll(copias);
    }

    // ── Validación de pertenencia ─────────────────────────────────
    private void validarPertenencia(ViolenceZone zone, Integer idInstituto) {
        if (zone.getInstitute() == null || !zone.getInstitute().getId().equals(idInstituto)) {
            throw new SecurityException("No tienes permiso para modificar esta zona.");
        }
    }
}
package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.ViolenceZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViolenceZoneRepository extends JpaRepository<ViolenceZone, Integer> {

    // ── NUEVO: obtener plantillas globales para copiar ─────────────
    List<ViolenceZone> findByIsTemplateTrueAndInstituteIsNull();

    // ── NUEVO: obtener zonas propias de un instituto ───────────────
    List<ViolenceZone> findByInstitute_IdAndIsTemplateFalse(Integer idInstituto);

    // ── NUEVO: verificar si un instituto ya tiene zonas propias ───
    boolean existsByInstitute_Id(Integer idInstituto);

}

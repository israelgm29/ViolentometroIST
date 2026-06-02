package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {

    Optional<Survey> findByIsActiveTrue();

    // ── NUEVO: listar cuestionarios de un instituto ───────────────
    List<Survey> findAllByInstitute_Id(Integer idInstituto);

    // ── NUEVO: obtener el cuestionario activo de un instituto ─────
    Optional<Survey> findByIsActiveTrueAndInstitute_Id(Integer idInstituto);

    // ── NUEVO: desactivar solo los cuestionarios de un instituto ──
    @Modifying
    @Query("UPDATE Survey s SET s.isActive = false WHERE s.institute.id = :idInstituto")
    void deactivateAllByInstituto(@Param("idInstituto") Integer idInstituto);

    // Mantener para compatibilidad si se usa en otro lugar
    @Modifying
    @Query("UPDATE Survey s SET s.isActive = false")
    void deactivateAllSurveys();


}

package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.dto.DemographicReportDTO;
import ec.edu.istr.violentometro.dto.StatisticsDTO;
import ec.edu.istr.violentometro.dto.VulnerabilityReportDTO;
import ec.edu.istr.violentometro.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface QuizResultRepository extends JpaRepository<QuizResult, Integer> {

    @Query("SELECT COUNT(qr) > 0 FROM QuizResult qr " +
            "WHERE qr.idAppUser.id = :userId " +
            "AND qr.idSurvey.id = :surveyId " +
            "AND CAST(qr.createdAt AS date) = CAST(CURRENT_TIMESTAMP AS date)")
    boolean hasFinishedToday(@Param("userId") Integer userId, @Param("surveyId") Integer surveyId);

    @Query("SELECT MAX(qr.createdAt) FROM QuizResult qr " +
            "WHERE qr.idAppUser.id = :userId AND qr.idSurvey.id = :surveyId")
    Optional<java.time.OffsetDateTime> findLastCompletionDate(@Param("userId") Integer userId, @Param("surveyId") Integer surveyId);

    // ─── TENDENCIAS ────────────────────────────────────────────────────────────

    /**
     * Participaciones reales por día (una fila por usuario/sesión).
     * Reemplaza getAlertsTrend de UserAnswer.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "CAST(qr.createdAt AS date) || '', COUNT(qr.id)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "GROUP BY CAST(qr.createdAt AS date) " +
            "ORDER BY CAST(qr.createdAt AS date) ASC")
    List<StatisticsDTO> getParticipationTrend(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Casos críticos por día (risk_level = 'critical').
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "CAST(qr.createdAt AS date) || '', COUNT(qr.id)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "AND LOWER(qr.riskLevel) = 'critical' " +
            "GROUP BY CAST(qr.createdAt AS date) " +
            "ORDER BY CAST(qr.createdAt AS date) ASC")
    List<StatisticsDTO> getCriticalTrend(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Puntaje promedio por día.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "CAST(qr.createdAt AS date) || '', CAST(AVG(qr.totalScore) AS long)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "GROUP BY CAST(qr.createdAt AS date) " +
            "ORDER BY CAST(qr.createdAt AS date) ASC")
    List<StatisticsDTO> getAvgScoreTrend(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Distribución de niveles de riesgo por día.
     * Retorna filas con label = "FECHA|NIVEL" para que el service las procese.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "CAST(qr.createdAt AS date) || '|' || qr.riskLevel, COUNT(qr.id)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "GROUP BY CAST(qr.createdAt AS date), qr.riskLevel " +
            "ORDER BY CAST(qr.createdAt AS date) ASC")
    List<StatisticsDTO> getRiskLevelTrend(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    // ─── KPIs (reemplazan queries de UserAnswer) ───────────────────────────────

    /**
     * Total de participaciones únicas en el período.
     * Reemplaza getTotalUniqueVictims.
     */
    @Query("SELECT COUNT(DISTINCT qr.idAppUser.id) FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId")
    Long getTotalParticipants(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Conteo de casos críticos únicos.
     * Reemplaza countUsersInCriticalRisk.
     */
    @Query("SELECT COUNT(DISTINCT qr.idAppUser.id) FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "AND LOWER(qr.riskLevel) = 'critical'")
    Long countCriticalParticipants(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Vulnerabilidad por institución.
     * Reemplaza getVulnerabilityTableReport.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.VulnerabilityReportDTO(" +
            "i.name, " +
            "COUNT(DISTINCT CASE WHEN LOWER(qr.riskLevel) = 'critical' THEN qr.idAppUser.id END), " +
            "COUNT(DISTINCT CASE WHEN LOWER(qr.riskLevel) IN ('high', 'medium') THEN qr.idAppUser.id END), " +
            "COUNT(DISTINCT qr.idAppUser.id)) " +
            "FROM QuizResult qr " +
            "JOIN qr.idAppUser u " +
            "JOIN u.idInstitute i " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "GROUP BY i.name " +
            "ORDER BY COUNT(DISTINCT qr.idAppUser.id) DESC")
    List<VulnerabilityReportDTO> getVulnerabilityByInstitution(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Zona dominante más frecuente en el período.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "qr.dominantZone.name, COUNT(qr.id)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "AND qr.dominantZone IS NOT NULL " +
            "GROUP BY qr.dominantZone.name " +
            "ORDER BY COUNT(qr.id) DESC")
    List<StatisticsDTO> getDominantZones(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);


    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(qr.riskLevel, COUNT(qr.id)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND (:surveyId IS NULL OR qr.idSurvey.id = :surveyId) " +
            "GROUP BY qr.riskLevel")
    List<StatisticsDTO> countByRiskLevel(@Param("start") OffsetDateTime start,
                                         @Param("end") OffsetDateTime end,
                                         @Param("surveyId") Integer surveyId);

    // ─── AÑADIR en QuizResultRepository.java ─────────────────────────────────────
// Estas queries cuentan estudiantes únicos por categoría demográfica + nivel de riesgo
// directamente desde quiz_result, sin tocar user_answer.

    /**
     * Estudiantes únicos por GÉNERO + nivel de riesgo.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.DemographicReportDTO(" +
            "u.idGender.name, qr.riskLevel, COUNT(DISTINCT u.id), null) " +
            "FROM QuizResult qr " +
            "JOIN qr.idAppUser u " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "AND u.idGender IS NOT NULL " +
            "GROUP BY u.idGender.name, qr.riskLevel " +
            "ORDER BY u.idGender.name ASC, qr.riskLevel ASC")
    List<DemographicReportDTO> getGenderByRiskLevel(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Estudiantes únicos por ETNIA + nivel de riesgo.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.DemographicReportDTO(" +
            "u.idEthnicity.name, qr.riskLevel, COUNT(DISTINCT u.id), null) " +
            "FROM QuizResult qr " +
            "JOIN qr.idAppUser u " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "AND u.idEthnicity IS NOT NULL " +
            "GROUP BY u.idEthnicity.name, qr.riskLevel " +
            "ORDER BY u.idEthnicity.name ASC, qr.riskLevel ASC")
    List<DemographicReportDTO> getEthnicityByRiskLevel(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Estudiantes únicos por DISCAPACIDAD + nivel de riesgo.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.DemographicReportDTO(" +
            "u.idDisability.name, qr.riskLevel, COUNT(DISTINCT u.id), null) " +
            "FROM QuizResult qr " +
            "JOIN qr.idAppUser u " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "AND u.idDisability IS NOT NULL " +
            "GROUP BY u.idDisability.name, qr.riskLevel " +
            "ORDER BY u.idDisability.name ASC, qr.riskLevel ASC")
    List<DemographicReportDTO> getDisabilityByRiskLevel(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Todas las sesiones de un estudiante por DNI, ordenadas por fecha ASC.
     * Retorna proyección plana para construir el historial.
     */
    @Query("SELECT qr FROM QuizResult qr " +
            "JOIN FETCH qr.idAppUser u " +
            "LEFT JOIN FETCH qr.dominantZone z " +
            "WHERE u.dni = :dni " +
            "AND qr.createdAt BETWEEN :start AND :end " +
            "AND (:surveyId IS NULL OR qr.idSurvey.id = :surveyId) " +
            "ORDER BY qr.createdAt ASC")
    List<QuizResult> findSessionsByDni(
            @Param("dni") String dni,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);


    /**
     * Puntaje promedio en un período.
     */
    @Query("SELECT AVG(qr.totalScore) FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId")
    Double getAvgScore(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Conteo de sesiones por nivel de riesgo en un período.
     * Retorna StatisticsDTO con label=riskLevel, count=sesiones
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "qr.riskLevel, COUNT(qr.id)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "GROUP BY qr.riskLevel")
    List<StatisticsDTO> countSessionsByRiskLevel(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Todas las sesiones del período con datos del estudiante.
     * Para el reporte de participación individual.
     */
    @Query("SELECT qr FROM QuizResult qr " +
            "JOIN FETCH qr.idAppUser u " +
            "LEFT JOIN FETCH u.idGender " +
            "LEFT JOIN FETCH u.idEthnicity " +
            "LEFT JOIN FETCH u.idInstitute " +
            "LEFT JOIN FETCH qr.dominantZone " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "ORDER BY qr.createdAt ASC")
    List<QuizResult> findAllSessionsInPeriod(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);

    /**
     * Resumen diario: conteo de sesiones y puntaje promedio por día.
     */
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "CAST(qr.createdAt AS date) || '', COUNT(qr.id)) " +
            "FROM QuizResult qr " +
            "WHERE qr.createdAt BETWEEN :start AND :end " +
            "AND qr.idSurvey.id = :surveyId " +
            "GROUP BY CAST(qr.createdAt AS date) " +
            "ORDER BY CAST(qr.createdAt AS date) ASC")
    List<StatisticsDTO> getDailySessionCount(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("surveyId") Integer surveyId);


}
package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.dto.AlertSignalDTO;
import ec.edu.istr.violentometro.dto.CriticalCaseDTO;
import ec.edu.istr.violentometro.dto.ReportDetailDTO;
import ec.edu.istr.violentometro.dto.StatisticsDTO;
import ec.edu.istr.violentometro.dto.VulnerabilityReportDTO;
import ec.edu.istr.violentometro.dto.ZoneStatisticsDTO;
import ec.edu.istr.violentometro.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {

    Optional<UserAnswer> findByIdAppUserIdAndIdQuestionId(Integer appUserId, Integer questionId);

    @Query("SELECT ua FROM UserAnswer ua WHERE ua.idAppUser.dni = :dni ORDER BY ua.createdAt ASC")
    List<UserAnswer> findByAppUserDni(@Param("dni") String dni);

    @Query("SELECT ua FROM UserAnswer ua WHERE ua.idAppUser.dni = :dni AND ua.idQuestion.survey.id = :surveyId ORDER BY ua.createdAt ASC")
    List<UserAnswer> findByAppUserDniAndSurveyId(@Param("dni") String dni, @Param("surveyId") Integer surveyId);

    // 1. Zonas de Violencia
    @Query("SELECT new ec.edu.istr.violentometro.dto.ZoneStatisticsDTO(" +
            "z.name, z.color, COUNT(ua.id), " +
            "ROUND(CAST(COUNT(ua.id) * 100.0 / (SELECT COUNT(ua2.id) FROM UserAnswer ua2 WHERE ua2.answer = true AND ua2.createdAt BETWEEN :startDate AND :endDate) AS double), 2)) " +
            "FROM UserAnswer ua JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY z.name, z.color, z.severity ORDER BY z.severity ASC")
    List<ZoneStatisticsDTO> getCountByViolenceZone(OffsetDateTime startDate, OffsetDateTime endDate);

    // 2. Etnias
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(e.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idEthnicity e " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY e.name")
    List<StatisticsDTO> getCountByEthnicity(OffsetDateTime startDate, OffsetDateTime endDate);

    // 3. Regiones
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(r.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idRegion r " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY r.name")
    List<StatisticsDTO> getCountByRegion(OffsetDateTime startDate, OffsetDateTime endDate);

    // 4. Discapacidad
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "COALESCE(d.name, 'Sin Discapacidad'), COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u LEFT JOIN u.idDisability d " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY d.name")
    List<StatisticsDTO> getCountByDisability(OffsetDateTime startDate, OffsetDateTime endDate);

    // 5. Género
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(g.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idGender g " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY g.name")
    List<StatisticsDTO> getCountVictimsByGender(OffsetDateTime startDate, OffsetDateTime endDate);

    // 6. Total víctimas únicas
    @Query("SELECT COUNT(DISTINCT ua.idAppUser.id) FROM UserAnswer ua " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate")
    Long getTotalUniqueVictims(OffsetDateTime startDate, OffsetDateTime endDate);


    // 8. Reporte de vulnerabilidad
    @Query("SELECT new ec.edu.istr.violentometro.dto.VulnerabilityReportDTO(" +
            "i.name, " +
            "COUNT(DISTINCT CASE WHEN z.severity = 3 THEN u.id END), " +
            "COUNT(DISTINCT CASE WHEN z.severity = 2 THEN u.id END), " +
            "COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idInstitute i " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY i.name")
    List<VulnerabilityReportDTO> getVulnerabilityTableReport(OffsetDateTime startDate, OffsetDateTime endDate);

    // 9. Riesgo crítico
    @Query("SELECT COUNT(DISTINCT u.id) FROM UserAnswer ua " +
            "JOIN ua.idQuestion q JOIN q.idZone z JOIN ua.idAppUser u " +
            "WHERE ua.answer = true AND z.severity = 3 AND ua.createdAt BETWEEN :startDate AND :endDate")
    Long countUsersInCriticalRisk(OffsetDateTime startDate, OffsetDateTime endDate);

    // 10. Incidencia por pregunta
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "q.question, z.name, COUNT(ua.id), " +
            "CAST(COUNT(ua.id) * 100.0 / (SELECT COUNT(ua2.id) FROM UserAnswer ua2 WHERE ua2.answer = true AND ua2.createdAt BETWEEN :start AND :end) AS double), " +
            "z.color) " +
            "FROM UserAnswer ua JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "GROUP BY q.question, z.name, z.color ORDER BY COUNT(ua.id) DESC")
    List<ReportDetailDTO> getQuestionIncidenceReport(OffsetDateTime start, OffsetDateTime end);

    // 11. Top preguntas violentas
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(q.question, COUNT(ua.id)) " +
            "FROM UserAnswer ua JOIN ua.idQuestion q " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "GROUP BY q.question ORDER BY COUNT(ua.id) DESC")
    List<StatisticsDTO> getTopViolentQuestions(OffsetDateTime start, OffsetDateTime end, Pageable pageable);

    // 12. Tendencia de alertas
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(CAST(ua.createdAt AS date) || '', COUNT(ua.id)) " +
            "FROM UserAnswer ua " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "GROUP BY CAST(ua.createdAt AS date) ORDER BY CAST(ua.createdAt AS date) ASC")
    List<StatisticsDTO> getAlertsTrend(OffsetDateTime start, OffsetDateTime end);

    // 13. Detalle por género
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "g.name, z.name, COUNT(DISTINCT u.id), 0.0, z.color) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idGender g " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "GROUP BY g.name, z.name, z.color")
    List<ReportDetailDTO> getGenderDetailedReport(OffsetDateTime start, OffsetDateTime end);

    // 14. Detalle por etnia
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "e.name, z.name, COUNT(DISTINCT u.id), 0.0, z.color) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idEthnicity e " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "GROUP BY e.name, z.name, z.color")
    List<ReportDetailDTO> getEthnicityDetailedReport(OffsetDateTime start, OffsetDateTime end);

    // 15. Detalle por discapacidad
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "COALESCE(d.name, 'Sin Discapacidad'), z.name, COUNT(DISTINCT u.id), 0.0, z.color) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u LEFT JOIN u.idDisability d " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "GROUP BY d.name, z.name, z.color")
    List<ReportDetailDTO> getDisabilityDetailedReport(OffsetDateTime start, OffsetDateTime end);


    // ═══════════════════════════════════════════════════════════════════════════
    // QUERIES FILTRADAS POR SURVEY
    // ═══════════════════════════════════════════════════════════════════════════

    // 1. Zonas — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.ZoneStatisticsDTO(" +
            "z.name, z.color, COUNT(ua.id), " +
            "ROUND(CAST(COUNT(ua.id) * 100.0 / (SELECT COUNT(ua2.id) FROM UserAnswer ua2 WHERE ua2.answer = true AND ua2.createdAt BETWEEN :startDate AND :endDate AND ua2.idQuestion.survey.id = :surveyId) AS double), 2)) " +
            "FROM UserAnswer ua JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY z.name, z.color, z.severity ORDER BY z.severity ASC")
    List<ZoneStatisticsDTO> getCountByViolenceZoneBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 2. Etnias — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(e.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idEthnicity e " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND ua.idQuestion.survey.id = :surveyId " +
            "GROUP BY e.name")
    List<StatisticsDTO> getCountByEthniciityBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 3. Regiones — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(r.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idRegion r " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND ua.idQuestion.survey.id = :surveyId " +
            "GROUP BY r.name")
    List<StatisticsDTO> getCountByRegionBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 4. Discapacidad — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(" +
            "COALESCE(d.name, 'Sin Discapacidad'), COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u LEFT JOIN u.idDisability d " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND ua.idQuestion.survey.id = :surveyId " +
            "GROUP BY d.name")
    List<StatisticsDTO> getCountByDisabilityBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 5. Género — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(g.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idGender g " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND ua.idQuestion.survey.id = :surveyId " +
            "GROUP BY g.name")
    List<StatisticsDTO> getCountVictimsByGenderBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 6. Total víctimas únicas — filtrado por survey
    @Query("SELECT COUNT(DISTINCT ua.idAppUser.id) FROM UserAnswer ua " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND ua.idQuestion.survey.id = :surveyId")
    Long getTotalUniqueVictimsBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 7. Reporte de vulnerabilidad — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.VulnerabilityReportDTO(" +
            "i.name, " +
            "COUNT(DISTINCT CASE WHEN z.severity = 3 THEN u.id END), " +
            "COUNT(DISTINCT CASE WHEN z.severity = 2 THEN u.id END), " +
            "COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idInstitute i " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY i.name")
    List<VulnerabilityReportDTO> getVulnerabilityTableReportBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 8. Riesgo crítico — filtrado por survey
    @Query("SELECT COUNT(DISTINCT u.id) FROM UserAnswer ua " +
            "JOIN ua.idQuestion q JOIN q.idZone z JOIN ua.idAppUser u " +
            "WHERE ua.answer = true AND z.severity = 3 " +
            "AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND q.survey.id = :surveyId")
    Long countUsersInCriticalRiskBySurvey(OffsetDateTime startDate, OffsetDateTime endDate, @Param("surveyId") Integer surveyId);

    // 9. Incidencia por pregunta — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "q.question, z.name, COUNT(ua.id), " +
            "CAST(COUNT(ua.id) * 100.0 / (SELECT COUNT(ua2.id) FROM UserAnswer ua2 WHERE ua2.answer = true AND ua2.createdAt BETWEEN :start AND :end AND ua2.idQuestion.survey.id = :surveyId) AS double), " +
            "z.color) " +
            "FROM UserAnswer ua JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY q.question, z.name, z.color ORDER BY COUNT(ua.id) DESC")
    List<ReportDetailDTO> getQuestionIncidenceReportBySurvey(OffsetDateTime start, OffsetDateTime end, @Param("surveyId") Integer surveyId);

    // 10. Top preguntas violentas — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(q.question, COUNT(ua.id)) " +
            "FROM UserAnswer ua JOIN ua.idQuestion q " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY q.question ORDER BY COUNT(ua.id) DESC")
    List<StatisticsDTO> getTopViolentQuestionsBySurvey(OffsetDateTime start, OffsetDateTime end, @Param("surveyId") Integer surveyId, Pageable pageable);

    // 11. Tendencia de alertas — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(CAST(ua.createdAt AS date) || '', COUNT(ua.id)) " +
            "FROM UserAnswer ua " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "AND ua.idQuestion.survey.id = :surveyId " +
            "GROUP BY CAST(ua.createdAt AS date) ORDER BY CAST(ua.createdAt AS date) ASC")
    List<StatisticsDTO> getAlertsTrendBySurvey(OffsetDateTime start, OffsetDateTime end, @Param("surveyId") Integer surveyId);

    // 12. Detalle por género — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "g.name, z.name, COUNT(DISTINCT u.id), 0.0, z.color) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idGender g " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY g.name, z.name, z.color")
    List<ReportDetailDTO> getGenderDetailedReportBySurvey(OffsetDateTime start, OffsetDateTime end, @Param("surveyId") Integer surveyId);

    // 13. Detalle por etnia — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "e.name, z.name, COUNT(DISTINCT u.id), 0.0, z.color) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u JOIN u.idEthnicity e " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY e.name, z.name, z.color")
    List<ReportDetailDTO> getEthnicityDetailedReportBySurvey(OffsetDateTime start, OffsetDateTime end, @Param("surveyId") Integer surveyId);

    // 14. Detalle por discapacidad — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.ReportDetailDTO(" +
            "COALESCE(d.name, 'Sin Discapacidad'), z.name, COUNT(DISTINCT u.id), 0.0, z.color) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u LEFT JOIN u.idDisability d " +
            "JOIN ua.idQuestion q JOIN q.idZone z " +
            "WHERE ua.answer = true AND ua.createdAt BETWEEN :start AND :end " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY d.name, z.name, z.color")
    List<ReportDetailDTO> getDisabilityDetailedReportBySurvey(OffsetDateTime start, OffsetDateTime end, @Param("surveyId") Integer surveyId);

    // ═══════════════════════════════════════════════════════════════════════════
    // CASOS CRÍTICOS — severity = 3
    // ═══════════════════════════════════════════════════════════════════════════

    // Casos críticos — sin filtro de survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.CriticalCaseDTO(" +
            "u.id, u.dni, g.name, e.name, i.name, " +
            "MIN(ua.createdAt), COUNT(ua.id)) " +
            "FROM UserAnswer ua " +
            "JOIN ua.idAppUser u " +
            "JOIN u.idGender g " +
            "JOIN u.idEthnicity e " +
            "JOIN u.idInstitute i " +
            "JOIN ua.idQuestion q " +
            "JOIN q.idZone z " +
            "WHERE ua.answer = true AND z.severity = 3 " +
            "AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY u.id, u.dni, g.name, e.name, i.name " +
            "ORDER BY COUNT(ua.id) DESC")
    List<CriticalCaseDTO> getCriticalCases(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    // Casos críticos — filtrado por survey
    @Query("SELECT new ec.edu.istr.violentometro.dto.CriticalCaseDTO(" +
            "u.id, u.dni, g.name, e.name, i.name, " +
            "MIN(ua.createdAt), COUNT(ua.id)) " +
            "FROM UserAnswer ua " +
            "JOIN ua.idAppUser u " +
            "JOIN u.idGender g " +
            "JOIN u.idEthnicity e " +
            "JOIN u.idInstitute i " +
            "JOIN ua.idQuestion q " +
            "JOIN q.idZone z " +
            "WHERE ua.answer = true AND z.severity = 3 " +
            "AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND q.survey.id = :surveyId " +
            "GROUP BY u.id, u.dni, g.name, e.name, i.name " +
            "ORDER BY COUNT(ua.id) DESC")
    List<CriticalCaseDTO> getCriticalCasesBySurvey(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("surveyId") Integer surveyId);

    // Señales de alerta de un usuario — con surveyId opcional
    @Query("SELECT new ec.edu.istr.violentometro.dto.AlertSignalDTO(" +
            "q.id, q.question, z.name) " +
            "FROM UserAnswer ua " +
            "JOIN ua.idQuestion q " +
            "JOIN q.idZone z " +
            "WHERE ua.answer = true AND z.severity = 3 " +
            "AND ua.idAppUser.id = :userId " +
            "AND ua.createdAt BETWEEN :startDate AND :endDate " +
            "AND (:surveyId IS NULL OR q.survey.id = :surveyId) " +
            "ORDER BY q.questionNumber ASC")
    List<AlertSignalDTO> getAlertSignalsByUser(
            @Param("userId") Integer userId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("surveyId") Integer surveyId);


    @Query("SELECT ua FROM UserAnswer ua " +
            "WHERE ua.idAppUser.dni = :dni " +
            "AND ua.idQuestion.survey.id = :surveyId " +
            "AND CAST(ua.createdAt AS date) = CAST(CURRENT_TIMESTAMP AS date) " +
            "ORDER BY ua.createdAt ASC")
    List<UserAnswer> findByAppUserDniAndSurveyIdToday(
            @Param("dni") String dni,
            @Param("surveyId") Integer surveyId);

    // Conteos por Etnia
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(u.idEthnicity.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u " +
            "WHERE ua.createdAt BETWEEN :start AND :end " +
            "AND (:surveyId IS NULL OR ua.idQuestion.survey.id = :surveyId) " +
            "GROUP BY u.idEthnicity.name")
    List<StatisticsDTO> countByEthnicity(@Param("start") OffsetDateTime start,
                                         @Param("end") OffsetDateTime end,
                                         @Param("surveyId") Integer surveyId);

    // Conteos por Género
    @Query("SELECT new ec.edu.istr.violentometro.dto.StatisticsDTO(u.idGender.name, COUNT(DISTINCT u.id)) " +
            "FROM UserAnswer ua JOIN ua.idAppUser u " +
            "WHERE ua.createdAt BETWEEN :start AND :end " +
            "AND (:surveyId IS NULL OR ua.idQuestion.survey.id = :surveyId) " +
            "GROUP BY u.idGender.name")
    List<StatisticsDTO> countByGender(@Param("start") OffsetDateTime start,
                                      @Param("end") OffsetDateTime end,
                                      @Param("surveyId") Integer surveyId);
}
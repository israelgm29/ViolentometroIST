package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.*;
import ec.edu.istr.violentometro.repository.QuizResultRepository;
import ec.edu.istr.violentometro.repository.SurveyRepository;
import ec.edu.istr.violentometro.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserAnswerRepository userAnswerRepository;
    private final SurveyRepository surveyRepository;
    private final QuizResultRepository quizResultRepository;

    // ─────────────────────────────────────────────────────────────────────
    // Dashboard principal
    // ─────────────────────────────────────────────────────────────────────

    public DashboardResponseDTO getFullDashboardData(OffsetDateTime start, OffsetDateTime end, Integer surveyId) {

        Long critical = surveyId != null
                ? userAnswerRepository.countUsersInCriticalRiskBySurvey(start, end, surveyId)
                : userAnswerRepository.countUsersInCriticalRisk(start, end);

        String level = "ESTABLE";
        if (critical > 5) level = "CRÍTICO";
        else if (critical > 0) level = "ALERTA";

        if (surveyId != null) {
            return DashboardResponseDTO.builder()
                    .zones(userAnswerRepository.getCountByViolenceZoneBySurvey(start, end, surveyId))
                    .ethnics(userAnswerRepository.getCountByEthniciityBySurvey(start, end, surveyId))
                    .regions(userAnswerRepository.getCountByRegionBySurvey(start, end, surveyId))
                    .disabilities(userAnswerRepository.getCountByDisabilityBySurvey(start, end, surveyId))
                    .genders(userAnswerRepository.getCountVictimsByGenderBySurvey(start, end, surveyId))
                    .totalVictims(userAnswerRepository.getTotalUniqueVictimsBySurvey(start, end, surveyId))
                    .vulnerabilityTable(userAnswerRepository.getVulnerabilityTableReportBySurvey(start, end, surveyId))
                    .criticalRiskCount(critical)
                    .alertLevel(level)
                    .topQuestions(userAnswerRepository.getTopViolentQuestionsBySurvey(start, end, surveyId, PageRequest.of(0, 5)))
                    .alertsTrend(userAnswerRepository.getAlertsTrendBySurvey(start, end, surveyId))
                    .build();
        }

        return DashboardResponseDTO.builder()
                .zones(userAnswerRepository.getCountByViolenceZone(start, end))
                .ethnics(userAnswerRepository.getCountByEthnicity(start, end))
                .regions(userAnswerRepository.getCountByRegion(start, end))
                .disabilities(userAnswerRepository.getCountByDisability(start, end))
                .genders(userAnswerRepository.getCountVictimsByGender(start, end))
                .totalVictims(userAnswerRepository.getTotalUniqueVictims(start, end))
                .vulnerabilityTable(userAnswerRepository.getVulnerabilityTableReport(start, end))
                .criticalRiskCount(critical)
                .alertLevel(level)
                .topQuestions(userAnswerRepository.getTopViolentQuestions(start, end, PageRequest.of(0, 5)))
                .alertsTrend(userAnswerRepository.getAlertsTrend(start, end))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Reportes detallados
    // ─────────────────────────────────────────────────────────────────────

    public List<ReportDetailDTO> getDetailedReport(String type, OffsetDateTime start, OffsetDateTime end, Integer surveyId) {
        List<ReportDetailDTO> reports;

        switch (type.toLowerCase()) {
            case "preguntas":
                return surveyId != null
                        ? userAnswerRepository.getQuestionIncidenceReportBySurvey(start, end, surveyId)
                        : userAnswerRepository.getQuestionIncidenceReport(start, end);
            case "genero":
                reports = surveyId != null
                        ? userAnswerRepository.getGenderDetailedReportBySurvey(start, end, surveyId)
                        : userAnswerRepository.getGenderDetailedReport(start, end);
                break;
            case "etnia":
                reports = surveyId != null
                        ? userAnswerRepository.getEthnicityDetailedReportBySurvey(start, end, surveyId)
                        : userAnswerRepository.getEthnicityDetailedReport(start, end);
                break;
            case "discapacidad":
                reports = surveyId != null
                        ? userAnswerRepository.getDisabilityDetailedReportBySurvey(start, end, surveyId)
                        : userAnswerRepository.getDisabilityDetailedReport(start, end);
                break;
            default:
                throw new IllegalArgumentException("Tipo de reporte no válido: " + type);
        }

        if (reports != null && !reports.isEmpty()) {
            long total = reports.stream().mapToLong(ReportDetailDTO::getValue).sum();
            reports.forEach(r -> {
                if (total > 0) r.setPercentage((double) r.getValue() * 100 / total);
            });
            reports.sort(Comparator.comparing(ReportDetailDTO::getLabel)
                    .thenComparing(Comparator.comparing(ReportDetailDTO::getValue).reversed()));
        }

        return reports;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Reporte de casos críticos
    // ─────────────────────────────────────────────────────────────────────

    public CriticalCasesReportDTO getCriticalCasesReport(OffsetDateTime start, OffsetDateTime end, Integer surveyId) {

        // 1. Obtener todos los usuarios con al menos una señal crítica (severity=3)
        List<CriticalCaseDTO> cases = surveyId != null
                ? userAnswerRepository.getCriticalCasesBySurvey(start, end, surveyId)
                : userAnswerRepository.getCriticalCases(start, end);

        // 2. Enriquecer cada caso con el detalle de sus señales activadas
        cases.forEach(c -> {
            var signals = surveyId != null
                    ? userAnswerRepository.getAlertSignalsByUser(c.getUserId(), start, end, surveyId)
                    : userAnswerRepository.getAlertSignalsByUser(c.getUserId(), start, end, null);
            c.setAlertSignals(signals);
        });

        // 3. Título del survey
        String title = surveyId != null
                ? surveyRepository.findById(surveyId)
                  .map(s -> s.getTitle())
                  .orElse("Cuestionario #" + surveyId)
                : "Todos los cuestionarios";

        return new CriticalCasesReportDTO(surveyId, title, OffsetDateTime.now(), cases.size(), cases);
    }

    // ─── AÑADIR en StatisticsService.java ────────────────────────────────────────
    public TrendResponseDTO getTrends(OffsetDateTime start, OffsetDateTime end, Integer surveyId) {

        List<StatisticsDTO> participation = quizResultRepository.getParticipationTrend(start, end, surveyId);
        List<StatisticsDTO> critical = quizResultRepository.getCriticalTrend(start, end, surveyId);
        List<StatisticsDTO> avgScore = quizResultRepository.getAvgScoreTrend(start, end, surveyId);
        List<StatisticsDTO> riskRaw = quizResultRepository.getRiskLevelTrend(start, end, surveyId);

        // KPIs rápidos
        Long totalParticipants = quizResultRepository.getTotalParticipants(start, end, surveyId);
        Long totalCritical = quizResultRepository.countCriticalParticipants(start, end, surveyId);
        Double avg = participation.isEmpty() ? 0.0
                : quizResultRepository.getAvgScoreTrend(start, end, surveyId)
                  .stream().mapToLong(StatisticsDTO::getCount).average().orElse(0.0);

        // Construir series por nivel de riesgo
        // riskRaw tiene labels como "2026-04-08|critical" → separamos fecha y nivel
        List<String> levels = List.of("critical", "high", "medium", "low");

        // Recopilar todas las fechas únicas ordenadas
        List<String> allDates = riskRaw.stream()
                .map(r -> r.getLabel().split("\\|")[0])
                .distinct()
                .sorted()
                .toList();

        // Para cada nivel construir su serie de datos
        List<TrendResponseDTO.RiskLevelSeriesDTO> riskSeries = levels.stream()
                .map(level -> {
                    List<Long> data = allDates.stream().map(date -> {
                        String key = date + "|" + level;
                        return riskRaw.stream()
                                .filter(r -> r.getLabel().equalsIgnoreCase(key))
                                .mapToLong(StatisticsDTO::getCount)
                                .sum();
                    }).toList();

                    return TrendResponseDTO.RiskLevelSeriesDTO.builder()
                            .name(level)
                            .data(data)
                            .dates(allDates)
                            .build();
                })
                .filter(s -> s.getData().stream().anyMatch(v -> v > 0)) // omitir niveles sin datos
                .toList();

        return TrendResponseDTO.builder()
                .participationTrend(participation)
                .criticalTrend(critical)
                .avgScoreTrend(avgScore)
                .riskLevelSeries(riskSeries)
                .totalParticipants(totalParticipants)
                .totalCritical(totalCritical)
                .avgScore(avg)
                .build();
    }

    // Helper para el label del periodo
    private String formatPeriod(OffsetDateTime start, OffsetDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("Del %s al %s", start.format(fmt), end.format(fmt));
    }

}



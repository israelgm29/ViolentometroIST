package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.PeriodComparisonReportDTO;
import ec.edu.istr.violentometro.dto.StatisticsDTO;
import ec.edu.istr.violentometro.pdf.reports.PeriodComparisonReportExcel;
import ec.edu.istr.violentometro.repository.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PeriodComparisonReportService {

    private final QuizResultRepository quizResultRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateExcel(String start1, String end1,
                                String start2, String end2,
                                Integer surveyId) {
        PeriodComparisonReportDTO report = buildReport(start1, end1, start2, end2, surveyId);
        return PeriodComparisonReportExcel.generate(report);
    }

    private PeriodComparisonReportDTO buildReport(String start1, String end1,
                                                  String start2, String end2,
                                                  Integer surveyId) {
        OffsetDateTime s1 = OffsetDateTime.parse(start1);
        OffsetDateTime e1 = OffsetDateTime.parse(end1);
        OffsetDateTime s2 = OffsetDateTime.parse(start2);
        OffsetDateTime e2 = OffsetDateTime.parse(end2);

        // ── Período 1 ──────────────────────────────────────────────
        Long   p1Participants = quizResultRepository.getTotalParticipants(s1, e1, surveyId);
        Long   p1Critical     = quizResultRepository.countCriticalParticipants(s1, e1, surveyId);
        Double p1AvgScore     = quizResultRepository.getAvgScore(s1, e1, surveyId);
        List<StatisticsDTO> p1Levels = quizResultRepository.countSessionsByRiskLevel(s1, e1, surveyId);

        // ── Período 2 ──────────────────────────────────────────────
        Long   p2Participants = quizResultRepository.getTotalParticipants(s2, e2, surveyId);
        Long   p2Critical     = quizResultRepository.countCriticalParticipants(s2, e2, surveyId);
        Double p2AvgScore     = quizResultRepository.getAvgScore(s2, e2, surveyId);
        List<StatisticsDTO> p2Levels = quizResultRepository.countSessionsByRiskLevel(s2, e2, surveyId);

        // ── Tasas de criticidad ────────────────────────────────────
        double p1Rate = rate(p1Critical, p1Participants);
        double p2Rate = rate(p2Critical, p2Participants);

        // ── Variaciones ────────────────────────────────────────────
        Double participationChange = pctChange(p1Participants, p2Participants);
        Double criticalChange      = pctChange(p1Critical,     p2Critical);
        Double avgScoreChange      = pctChange(p1AvgScore,     p2AvgScore);
        Double criticalRateChange  = p2Rate - p1Rate;

        // ── Conclusión general ─────────────────────────────────────
        String conclusion = buildConclusion(criticalChange, avgScoreChange, criticalRateChange);

        return PeriodComparisonReportDTO.builder()
                .surveyTitle("Cuestionario " + surveyId)
                .generatedAt(OffsetDateTime.now().format(DT_FMT))
                .period1Label(s1.format(DATE_FMT) + " → " + e1.format(DATE_FMT))
                .period2Label(s2.format(DATE_FMT) + " → " + e2.format(DATE_FMT))
                .period1Participants(p1Participants)
                .period2Participants(p2Participants)
                .participationChange(participationChange)
                .period1Critical(p1Critical)
                .period2Critical(p2Critical)
                .criticalChange(criticalChange)
                .period1AvgScore(p1AvgScore)
                .period2AvgScore(p2AvgScore)
                .avgScoreChange(avgScoreChange)
                .period1CriticalRate(p1Rate)
                .period2CriticalRate(p2Rate)
                .criticalRateChange(criticalRateChange)
                // Niveles período 1
                .period1LevelCritical(getLevel(p1Levels, "critical"))
                .period1LevelHigh(getLevel(p1Levels, "high"))
                .period1LevelMedium(getLevel(p1Levels, "medium"))
                .period1LevelLow(getLevel(p1Levels, "low"))
                // Niveles período 2
                .period2LevelCritical(getLevel(p2Levels, "critical"))
                .period2LevelHigh(getLevel(p2Levels, "high"))
                .period2LevelMedium(getLevel(p2Levels, "medium"))
                .period2LevelLow(getLevel(p2Levels, "low"))
                .conclusion(conclusion)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private double rate(Long critical, Long total) {
        if (total == null || total == 0) return 0;
        return (critical != null ? critical : 0) * 100.0 / total;
    }

    private Double pctChange(Number v1, Number v2) {
        if (v1 == null || v2 == null) return null;
        double d1 = v1.doubleValue();
        if (d1 == 0) return v2.doubleValue() == 0 ? 0.0 : 100.0;
        return ((v2.doubleValue() - d1) / d1) * 100;
    }

    private Long getLevel(List<StatisticsDTO> levels, String level) {
        return levels.stream()
                .filter(s -> level.equalsIgnoreCase(s.getLabel()))
                .mapToLong(StatisticsDTO::getCount)
                .sum();
    }

    private String buildConclusion(Double criticalChange,
                                   Double avgScoreChange,
                                   Double criticalRateChange) {
        int improvements = 0;
        int deteriorations = 0;

        if (criticalChange != null) {
            if (criticalChange < -5)  improvements++;
            if (criticalChange > 5)   deteriorations++;
        }
        if (avgScoreChange != null) {
            if (avgScoreChange < -5)  improvements++;
            if (avgScoreChange > 5)   deteriorations++;
        }
        if (criticalRateChange != null) {
            if (criticalRateChange < -2) improvements++;
            if (criticalRateChange > 2)  deteriorations++;
        }

        if (improvements > deteriorations) return "MEJORA";
        if (deteriorations > improvements) return "DETERIORO";
        return "ESTABLE";
    }
}
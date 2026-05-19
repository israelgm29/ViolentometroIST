package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.ParticipationReportDTO;
import ec.edu.istr.violentometro.dto.ParticipationReportDTO.DailySummaryDTO;
import ec.edu.istr.violentometro.dto.ParticipationReportDTO.SessionDetailDTO;
import ec.edu.istr.violentometro.model.QuizResult;
import ec.edu.istr.violentometro.pdf.reports.ParticipationReportExcel;
import ec.edu.istr.violentometro.repository.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationReportService {

    private final QuizResultRepository quizResultRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateExcel(String start, String end, Integer surveyId) {
        ParticipationReportDTO report = buildReport(start, end, surveyId);
        return ParticipationReportExcel.generate(report);
    }

    private ParticipationReportDTO buildReport(String start, String end, Integer surveyId) {
        OffsetDateTime s = OffsetDateTime.parse(start);
        OffsetDateTime e = OffsetDateTime.parse(end);

        List<QuizResult> sessions = quizResultRepository.findAllSessionsInPeriod(s, e, surveyId);

        // ── KPIs ─────────────────────────────────────────────────────
        long totalSessions     = sessions.size();
        long totalParticipants = sessions.stream()
                .map(r -> r.getIdAppUser().getId()).distinct().count();
        long totalCritical = sessions.stream()
                .filter(r -> "critical".equalsIgnoreCase(r.getRiskLevel())).count();

        // Días únicos
        long uniqueDays = sessions.stream()
                .map(r -> r.getCreatedAt().toLocalDate())
                .distinct().count();
        double avgPerDay = uniqueDays > 0 ? (double) totalSessions / uniqueDays : 0;

        // ── Resumen diario ────────────────────────────────────────────
        Map<String, List<QuizResult>> byDay = sessions.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate().format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<DailySummaryDTO> dailySummary = byDay.entrySet().stream()
                .map(entry -> {
                    List<QuizResult> daySessions = entry.getValue();
                    double avgScore = daySessions.stream()
                            .filter(r -> r.getTotalScore() != null)
                            .mapToInt(QuizResult::getTotalScore)
                            .average().orElse(0);

                    return DailySummaryDTO.builder()
                            .date(entry.getKey())
                            .totalSessions((long) daySessions.size())
                            .critical(count(daySessions, "critical"))
                            .high(count(daySessions, "high"))
                            .medium(count(daySessions, "medium"))
                            .low(count(daySessions, "low"))
                            .avgScore(avgScore)
                            .build();
                })
                .collect(Collectors.toList());

        // ── Detalle individual ────────────────────────────────────────
        List<SessionDetailDTO> details = sessions.stream()
                .map(r -> SessionDetailDTO.builder()
                        .date(r.getCreatedAt().format(DATE_FMT))
                        .time(r.getCreatedAt().format(TIME_FMT))
                        .dni(r.getIdAppUser().getDni())
                        .gender(r.getIdAppUser().getIdGender() != null
                                ? r.getIdAppUser().getIdGender().getName() : "—")
                        .ethnicity(r.getIdAppUser().getIdEthnicity() != null
                                ? r.getIdAppUser().getIdEthnicity().getName() : "—")
                        .institute(r.getIdAppUser().getIdInstitute() != null
                                ? r.getIdAppUser().getIdInstitute().getName() : "—")
                        .riskLevel(r.getRiskLevel())
                        .score(r.getTotalScore())
                        .dominantZone(r.getDominantZone() != null
                                ? r.getDominantZone().getName() : "—")
                        .build())
                .collect(Collectors.toList());

        return ParticipationReportDTO.builder()
                .surveyTitle("Cuestionario " + surveyId)
                .period(s.format(DATE_FMT) + " — " + e.format(DATE_FMT))
                .generatedAt(OffsetDateTime.now().format(DT_FMT))
                .totalSessions(totalSessions)
                .totalParticipants(totalParticipants)
                .totalCritical(totalCritical)
                .avgSessionsPerDay(avgPerDay)
                .dailySummary(dailySummary)
                .sessionDetails(details)
                .build();
    }

    private long count(List<QuizResult> sessions, String level) {
        return sessions.stream()
                .filter(r -> level.equalsIgnoreCase(r.getRiskLevel()))
                .count();
    }
}
package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.StudentTrackingReportDTO;
import ec.edu.istr.violentometro.dto.StudentTrackingReportDTO.SessionDTO;
import ec.edu.istr.violentometro.model.AppUser;
import ec.edu.istr.violentometro.model.QuizResult;
import ec.edu.istr.violentometro.pdf.reports.StudentTrackingReportExcel;
import ec.edu.istr.violentometro.repository.AppUserRepository;
import ec.edu.istr.violentometro.repository.QuizResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentTrackingReportService {

    private final AppUserRepository appUserRepository;
    private final QuizResultRepository quizResultRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Excel ─────────────────────────────────────────────────────────

    public byte[] generateExcel(String dni, String start, String end, Integer surveyId) {
        StudentTrackingReportDTO report = buildReport(dni, start, end, surveyId);
        return StudentTrackingReportExcel.generate(report);
    }

    // ── Construcción del DTO ──────────────────────────────────────────

    private StudentTrackingReportDTO buildReport(String dni, String start,
                                                 String end, Integer surveyId) {
        // Buscar estudiante
        AppUser user = appUserRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado: " + dni));

        OffsetDateTime s = OffsetDateTime.parse(start);
        OffsetDateTime e = OffsetDateTime.parse(end);

        // Sesiones ordenadas ASC
        List<QuizResult> sessions = quizResultRepository
                .findSessionsByDni(dni, s, e, surveyId);

        if (sessions.isEmpty()) {
            return buildEmptyReport(user);
        }

        // Construir historial con tendencia
        List<SessionDTO> sessionDTOs = buildSessionDTOs(sessions);

        // KPIs de resumen
        QuizResult last = sessions.get(sessions.size() - 1);
        QuizResult first = sessions.get(0);

        double avgScore = sessions.stream()
                .filter(r -> r.getTotalScore() != null)
                .mapToInt(QuizResult::getTotalScore)
                .average().orElse(0);

        String trend = calculateOverallTrend(sessions);

        return StudentTrackingReportDTO.builder()
                .dni(user.getDni())
                .gender(user.getIdGender() != null ? user.getIdGender().getName() : "—")
                .ethnicity(user.getIdEthnicity() != null ? user.getIdEthnicity().getName() : "—")
                .disability(user.getIdDisability() != null ? user.getIdDisability().getName() : "—")
                .region(user.getIdRegion() != null ? user.getIdRegion().getName() : "—")
                .institute(user.getIdInstitute() != null ? user.getIdInstitute().getName() : "—")
                .ageApprox(calculateAge(user))
                .totalSessions(sessions.size())
                .firstSession(first.getCreatedAt().format(DATE_FMT))
                .lastSession(last.getCreatedAt().format(DATE_FMT))
                .currentRiskLevel(last.getRiskLevel())
                .avgScore(avgScore)
                .trend(trend)
                .sessions(sessionDTOs)
                .build();
    }

    private List<SessionDTO> buildSessionDTOs(List<QuizResult> sessions) {
        List<SessionDTO> dtos = new ArrayList<>();
        for (int i = 0; i < sessions.size(); i++) {
            QuizResult curr = sessions.get(i);
            String trend = "—";

            if (i > 0) {
                QuizResult prev = sessions.get(i - 1);
                trend = calculateSessionTrend(prev.getTotalScore(), curr.getTotalScore());
            }

            dtos.add(SessionDTO.builder()
                    .sessionNumber(i + 1)
                    .date(curr.getCreatedAt().format(DATE_FMT))
                    .riskLevel(curr.getRiskLevel())
                    .score(curr.getTotalScore())
                    .dominantZone(curr.getDominantZone() != null
                            ? curr.getDominantZone().getName() : "—")
                    .trend(trend)
                    .build());
        }
        return dtos;
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private String calculateSessionTrend(Integer prevScore, Integer currScore) {
        if (prevScore == null || currScore == null) return "—";
        if (currScore < prevScore) return "MEJORANDO";
        if (currScore > prevScore) return "EMPEORANDO";
        return "ESTABLE";
    }

    private String calculateOverallTrend(List<QuizResult> sessions) {
        if (sessions.size() < 2) return "ESTABLE";
        int first = sessions.get(0).getTotalScore() != null ? sessions.get(0).getTotalScore() : 0;
        int last = sessions.get(sessions.size() - 1).getTotalScore() != null
                ? sessions.get(sessions.size() - 1).getTotalScore() : 0;
        if (last < first) return "MEJORANDO";
        if (last > first) return "EMPEORANDO";
        return "ESTABLE";
    }

    private Integer calculateAge(AppUser user) {
        if (user.getBirthdate() == null) return null;
        return Period.between(user.getBirthdate(), java.time.LocalDate.now()).getYears();
    }

    private StudentTrackingReportDTO buildEmptyReport(AppUser user) {
        return StudentTrackingReportDTO.builder()
                .dni(user.getDni())
                .gender(user.getIdGender() != null ? user.getIdGender().getName() : "—")
                .ethnicity(user.getIdEthnicity() != null ? user.getIdEthnicity().getName() : "—")
                .disability(user.getIdDisability() != null ? user.getIdDisability().getName() : "—")
                .region(user.getIdRegion() != null ? user.getIdRegion().getName() : "—")
                .institute(user.getIdInstitute() != null ? user.getIdInstitute().getName() : "—")
                .ageApprox(calculateAge(user))
                .totalSessions(0)
                .firstSession("Sin sesiones")
                .lastSession("Sin sesiones")
                .currentRiskLevel("—")
                .avgScore(0.0)
                .trend("ESTABLE")
                .sessions(List.of())
                .build();
    }
}
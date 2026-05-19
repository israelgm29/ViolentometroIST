package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.DemographicReportDTO;
import ec.edu.istr.violentometro.dto.GeneralReportDTO;
import ec.edu.istr.violentometro.dto.VulnerabilityReportDTO;
import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.pdf.reports.GeneralReportPdf;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import ec.edu.istr.violentometro.repository.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneralReportService {

    private final QuizResultRepository quizResultRepository;
    private final InstituteRepository  instituteRepository;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generatePdf(String start, String end, Integer surveyId, byte[] ignored) {
        GeneralReportDTO report   = fetchReport(start, end, surveyId);
        byte[]           logo     = loadInstituteLogo();
        return GeneralReportPdf.generate(report, logo);
    }

    private byte[] loadInstituteLogo() {
        return instituteRepository.findAll().stream()
                .filter(i -> i.getLogo() != null && i.getLogo().length > 0)
                .map(Institute::getLogo)
                .findFirst()
                .orElse(null);
    }

    private GeneralReportDTO fetchReport(String start, String end, Integer surveyId) {
        OffsetDateTime s = OffsetDateTime.parse(start);
        OffsetDateTime e = OffsetDateTime.parse(end);

        List<VulnerabilityReportDTO> vulnerability =
                quizResultRepository.getVulnerabilityByInstitution(s, e, surveyId);

        Long totalParticipants = quizResultRepository.getTotalParticipants(s, e, surveyId);
        Long totalCritical     = quizResultRepository.countCriticalParticipants(s, e, surveyId);

        List<DemographicReportDTO> gender     = withPercentages(quizResultRepository.getGenderByRiskLevel(s, e, surveyId));
        List<DemographicReportDTO> ethnic     = withPercentages(quizResultRepository.getEthnicityByRiskLevel(s, e, surveyId));
        List<DemographicReportDTO> disability = withPercentages(quizResultRepository.getDisabilityByRiskLevel(s, e, surveyId));

        return GeneralReportDTO.builder()
                .surveyTitle("Reporte Institucional")
                .institution("Violentómetro ISTR")
                .period(s.format(DATE_FMT) + " — " + e.format(DATE_FMT))
                .generatedAt(OffsetDateTime.now().format(DATE_FMT))
                .totalParticipants(totalParticipants)
                .totalCritical(totalCritical)
                .totalInstitutions(vulnerability.size())
                .vulnerabilityTable(vulnerability)
                .genderDetail(gender)
                .ethnicDetail(ethnic)
                .disabilityDetail(disability)
                .build();
    }

    private List<DemographicReportDTO> withPercentages(List<DemographicReportDTO> data) {
        if (data == null || data.isEmpty()) return List.of();
        long total = data.stream().mapToLong(DemographicReportDTO::getValue).sum();
        data.forEach(d -> { if (total > 0) d.setPercentage((d.getValue() * 100.0) / total); });
        return data;
    }
}
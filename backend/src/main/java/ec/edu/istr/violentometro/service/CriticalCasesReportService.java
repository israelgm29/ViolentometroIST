package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.CriticalCasesReportDTO;
import ec.edu.istr.violentometro.pdf.reports.CriticalCasesReportExcel;
import ec.edu.istr.violentometro.pdf.reports.CriticalCasesReportPdf;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * Orquesta la generación de reportes de casos críticos.
 * Solo responsabilidad: pedir datos a StatisticsService
 * y delegar la generación al report correspondiente.
 */
@Service
public class CriticalCasesReportService {

    private final StatisticsService statisticsService;

    public CriticalCasesReportService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public byte[] generatePdf(String start, String end,
                              Integer surveyId, byte[] logoBytes) {
        CriticalCasesReportDTO report = fetchReport(start, end, surveyId);
        return CriticalCasesReportPdf.generate(report, logoBytes);
    }

    public byte[] generateExcel(String start, String end, Integer surveyId) {
        CriticalCasesReportDTO report = fetchReport(start, end, surveyId);
        return CriticalCasesReportExcel.generate(report);
    }

    private CriticalCasesReportDTO fetchReport(String start, String end, Integer surveyId) {
        return statisticsService.getCriticalCasesReport(
                OffsetDateTime.parse(start),
                OffsetDateTime.parse(end),
                surveyId
        );
    }
}
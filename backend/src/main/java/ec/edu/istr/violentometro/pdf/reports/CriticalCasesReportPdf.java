package ec.edu.istr.violentometro.pdf.reports;

import ec.edu.istr.violentometro.dto.CriticalCaseDTO;
import ec.edu.istr.violentometro.dto.CriticalCasesReportDTO;
import ec.edu.istr.violentometro.pdf.PdfFactory;
import ec.edu.istr.violentometro.pdf.PdfReportBuilder;
import ec.edu.istr.violentometro.pdf.components.KpiSection;
import ec.edu.istr.violentometro.pdf.template.PdfStyles;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Genera el PDF del reporte de casos críticos.
 * Solo conoce la estructura del PDF — no consulta datos ni conoce Spring.
 */
public class CriticalCasesReportPdf {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Constructor privado — solo métodos estáticos
    private CriticalCasesReportPdf() {}

    // ═════════════════════════════════════════════════════════════════
    // ENTRADA PÚBLICA
    // ═════════════════════════════════════════════════════════════════

    public static byte[] generate(CriticalCasesReportDTO report, byte[] logoBytes) {
        return new PdfReportBuilder(PdfFactory.create("Violentómetro ISTR"))
                .cover(
                        "REPORTE DE CASOS CRÍTICOS",
                        report.getSurveyTitle(),
                        buildPeriodLabel(report),
                        logoBytes
                )
                .header("REPORTE DE CASOS CRÍTICOS", report.getSurveyTitle())
                .kpis(buildKpis(report))
                .table(buildHeaders(), buildRows(report.getCases()))
                .build();
    }

    // ═════════════════════════════════════════════════════════════════
    // BUILDERS PRIVADOS
    // ═════════════════════════════════════════════════════════════════

    private static List<String> buildHeaders() {
        return List.of(
                "#", "DNI / Víctima", "Género",
                "Institución", "Fecha", "Nivel", "Señales de Alerta"
        );
    }

    private static List<List<String>> buildRows(List<CriticalCaseDTO> cases) {
        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < cases.size(); i++) {
            CriticalCaseDTO c = cases.get(i);
            rows.add(List.of(
                    String.valueOf(i + 1),
                    safe(c.getVictimDni()),
                    safe(c.getGender()),
                    safe(c.getInstitution()),
                    formatDate(c.getRegisteredAt()),
                    safe(c.getRiskLevel()),
                    formatSignals(c)
            ));
        }
        return rows;
    }

    private static List<KpiSection.Kpi> buildKpis(CriticalCasesReportDTO report) {
        long critical = report.getCases().stream()
                .filter(c -> "CRÍTICO".equals(c.getRiskLevel())).count();
        long high = report.getCases().stream()
                .filter(c -> "ALTO".equals(c.getRiskLevel())).count();
        int avg = report.getCases().isEmpty() ? 0
                : (int) report.getCases().stream()
                        .mapToInt(CriticalCaseDTO::getRiskScore)
                        .average().orElse(0);
        return List.of(
                new KpiSection.Kpi("Total Críticos",   String.valueOf(report.getTotalCritical()), PdfStyles.ACCENT),
                new KpiSection.Kpi("Nivel Crítico",    String.valueOf(critical),                  PdfStyles.CRITICAL),
                new KpiSection.Kpi("Nivel Alto",       String.valueOf(high),                      PdfStyles.HIGH),
                new KpiSection.Kpi("Puntaje Promedio", String.valueOf(avg),                       PdfStyles.MODERATE)
        );
    }

    // ═════════════════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════════════════

    private static String safe(String value) {
        return (value != null && !value.isBlank()) ? value : "—";
    }

    private static String formatDate(OffsetDateTime date) {
        return date != null ? date.format(DATE_FMT) : "—";
    }

    private static String formatSignals(CriticalCaseDTO c) {
        if (c.getAlertSignals() == null || c.getAlertSignals().isEmpty()) return "—";
        return c.getAlertSignals().stream()
                .map(s -> "• " + safe(s.getQuestionText()))
                .collect(Collectors.joining("\n"));
    }

    private static String buildPeriodLabel(CriticalCasesReportDTO report) {
        if (report.getGeneratedAt() == null) return "—";
        return "Hasta " + report.getGeneratedAt().format(DATE_FMT);
    }
}
package ec.edu.istr.violentometro.pdf.reports;

import ec.edu.istr.violentometro.dto.DemographicReportDTO;
import ec.edu.istr.violentometro.dto.GeneralReportDTO;
import ec.edu.istr.violentometro.dto.VulnerabilityReportDTO;
import ec.edu.istr.violentometro.pdf.PdfFactory;
import ec.edu.istr.violentometro.pdf.PdfReportBuilder;
import ec.edu.istr.violentometro.pdf.components.GenericTableSection;
import ec.edu.istr.violentometro.pdf.components.KpiSection;
import ec.edu.istr.violentometro.pdf.template.PdfStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneralReportPdf {

    private static final String DEMOGRAPHIC_NOTE =
            "Un mismo estudiante puede aparecer en múltiples niveles de riesgo " +
                    "si respondió el cuestionario en diferentes fechas del período. " +
                    "Cada fila representa sesiones registradas, no estudiantes únicos.";

    private static final Map<String, String> RISK_LABELS = Map.of(
            "critical", "CRÍTICO",
            "high",     "ALTO",
            "moderate",   "MODERADO",
            "low",      "BAJO"
    );

    private GeneralReportPdf() {}

    public static byte[] generate(GeneralReportDTO report, byte[] logoBytes) {
        PdfReportBuilder builder = new PdfReportBuilder(
                PdfFactory.create(report.getInstitution()));

        // ── Portada + KPIs + Vulnerabilidad ─────────────────────────
        builder.cover("REPORTE GENERAL INSTITUCIONAL",
                        report.getSurveyTitle(),
                        report.getPeriod(),
                        logoBytes)
                .header("REPORTE GENERAL INSTITUCIONAL", report.getSurveyTitle())
                .kpis(buildKpis(report))
                .spacer();

        GenericTableSection.draw(
                builder.getDoc(),
                "Vulnerabilidad por Institución",
                buildVulnerabilityHeaders(),
                buildVulnerabilityRows(report.getVulnerabilityTable())
                // sin nota — la vulnerabilidad sí muestra únicos
        );

        // ── Género ───────────────────────────────────────────────────
        builder.pageBreak()
                .header("ANÁLISIS POR GÉNERO", report.getPeriod());

        GenericTableSection.draw(
                builder.getDoc(),
                "Sesiones por Género y Nivel de Riesgo",
                buildDemoHeaders("Género"),
                buildDemoRows(report.getGenderDetail()),
                DEMOGRAPHIC_NOTE
        );

        // ── Etnia ────────────────────────────────────────────────────
        builder.pageBreak()
                .header("ANÁLISIS POR ETNIA", report.getPeriod());

        GenericTableSection.draw(
                builder.getDoc(),
                "Sesiones por Etnia y Nivel de Riesgo",
                buildDemoHeaders("Etnia"),
                buildDemoRows(report.getEthnicDetail()),
                DEMOGRAPHIC_NOTE
        );

        // ── Discapacidad ─────────────────────────────────────────────
        builder.pageBreak()
                .header("ANÁLISIS POR DISCAPACIDAD", report.getPeriod());

        GenericTableSection.draw(
                builder.getDoc(),
                "Sesiones por Discapacidad y Nivel de Riesgo",
                buildDemoHeaders("Tipo de Discapacidad"),
                buildDemoRows(report.getDisabilityDetail()),
                DEMOGRAPHIC_NOTE
        );

        return builder.build();
    }

    // ── KPIs ─────────────────────────────────────────────────────────

    private static List<KpiSection.Kpi> buildKpis(GeneralReportDTO r) {
        String rate = r.getTotalParticipants() != null && r.getTotalParticipants() > 0
                ? String.format("%.1f%%", (r.getTotalCritical() * 100.0) / r.getTotalParticipants())
                : "0%";
        return List.of(
                new KpiSection.Kpi("Total Participantes",
                        String.valueOf(r.getTotalParticipants()), PdfStyles.PRIMARY),
                new KpiSection.Kpi("Casos Críticos",
                        String.valueOf(r.getTotalCritical()), PdfStyles.CRITICAL),
                new KpiSection.Kpi("Instituciones",
                        String.valueOf(r.getTotalInstitutions()), PdfStyles.ACCENT),
                new KpiSection.Kpi("Tasa Criticidad", rate, PdfStyles.HIGH)
        );
    }

    // ── Vulnerabilidad ────────────────────────────────────────────────

    private static List<String> buildVulnerabilityHeaders() {
        return List.of("#", "Institución", "Riesgo Crítico",
                "Riesgo Moderado", "Total", "% Criticidad");
    }

    private static List<List<String>> buildVulnerabilityRows(
            List<VulnerabilityReportDTO> data) {
        List<List<String>> rows = new ArrayList<>();
        if (data == null) return rows;
        for (int i = 0; i < data.size(); i++) {
            VulnerabilityReportDTO v = data.get(i);
            long total = v.getTotalVictims();
            long high  = v.getHighRiskCount();
            String rate = total > 0
                    ? String.format("%.1f%%", (high * 100.0) / total) : "0%";
            rows.add(List.of(
                    String.valueOf(i + 1),
                    safe(v.getInstitutionName()),
                    String.valueOf(v.getHighRiskCount()),
                    String.valueOf(v.getModerateRiskCount()),
                    String.valueOf(v.getTotalVictims()),
                    rate
            ));
        }
        return rows;
    }

    // ── Demográfico ───────────────────────────────────────────────────

    private static List<String> buildDemoHeaders(String category) {
        return List.of("#", category, "Nivel de Riesgo", "Sesiones", "% del Período");
    }

    private static List<List<String>> buildDemoRows(List<DemographicReportDTO> data) {
        List<List<String>> rows = new ArrayList<>();
        if (data == null) return rows;
        for (int i = 0; i < data.size(); i++) {
            DemographicReportDTO d = data.get(i);
            rows.add(List.of(
                    String.valueOf(i + 1),
                    safe(d.getLabel()),
                    RISK_LABELS.getOrDefault(
                            d.getGroup() != null ? d.getGroup().toLowerCase() : "",
                            safe(d.getGroup())),
                    String.valueOf(d.getValue()),
                    d.getPercentage() != null
                            ? String.format("%.1f%%", d.getPercentage()) : "—"
            ));
        }
        return rows;
    }

    private static String safe(String v) {
        return (v != null && !v.isBlank()) ? v : "—";
    }
}
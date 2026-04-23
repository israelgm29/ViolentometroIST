package ec.edu.istr.violentometro.pdf.reports;

import ec.edu.istr.violentometro.dto.CriticalCaseDTO;
import ec.edu.istr.violentometro.dto.CriticalCasesReportDTO;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Genera el Excel del reporte de casos críticos.
 * Solo conoce la estructura del Excel — no consulta datos ni conoce Spring.
 */
public class CriticalCasesReportExcel {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private CriticalCasesReportExcel() {}

    // ═════════════════════════════════════════════════════════════════
    // ENTRADA PÚBLICA
    // ═════════════════════════════════════════════════════════════════

    public static byte[] generate(CriticalCasesReportDTO report) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = wb.createSheet("Casos Críticos");

            // ── Estilos ───────────────────────────────────────────────
            Styles st = new Styles(wb);

            int row = 0;

            // ── Encabezado institucional ──────────────────────────────
            row = writeTitle(sheet, row, st.title,
                    "INSTITUTO SUPERIOR TECNOLÓGICO RIOBAMBA — ISTR");
            row = writeTitle(sheet, row, st.subtitle,
                    "Sistema de Detección de Violencia — Violentómetro");
            row++;

            // ── Metadatos ─────────────────────────────────────────────
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Reporte",       "Casos Críticos");
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Cuestionario",  report.getSurveyTitle());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Generado",      OffsetDateTime.now().format(DT_FMT));
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Clasificación", "CONFIDENCIAL");
            row++;

            // ── KPIs ──────────────────────────────────────────────────
            row = writeKpis(sheet, row, report, st);
            row++;

            // ── Headers de tabla ──────────────────────────────────────
            String[] headers = {
                    "#", "DNI / Víctima", "Género", "Institución",
                    "Fecha Registro", "Nivel de Riesgo", "Puntaje", "Señales de Alerta"
            };
            int headerRowIdx = row;
            Row headerRow = sheet.createRow(row++);
            headerRow.setHeightInPoints(22);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(st.header);
            }

            // ── Filas de datos ────────────────────────────────────────
            var cases = report.getCases();
            for (int i = 0; i < cases.size(); i++) {
                CriticalCaseDTO c = cases.get(i);
                Row dataRow = sheet.createRow(row++);
                dataRow.setHeightInPoints(18);
                XSSFCellStyle base = i % 2 == 1 ? st.dataAlt : st.data;

                setCell(dataRow, 0, String.valueOf(i + 1),            base);
                setCell(dataRow, 1, safe(c.getVictimDni()),           base);
                setCell(dataRow, 2, safe(c.getGender()),              base);
                setCell(dataRow, 3, safe(c.getInstitution()),         base);
                setCell(dataRow, 4, formatDate(c.getRegisteredAt()),  base);

                // Celda de nivel con color de riesgo
                Cell riskCell = dataRow.createCell(5);
                riskCell.setCellValue(safe(c.getRiskLevel()));
                riskCell.setCellStyle(st.riskStyle(c.getRiskLevel()));

                setCell(dataRow, 6, String.valueOf(c.getRiskScore()), base);
                setCell(dataRow, 7, formatSignals(c),                 base);
            }

            // ── Anchos de columna ─────────────────────────────────────
            int[] widths = {6, 18, 14, 24, 16, 16, 10, 60};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }

            // ── AutoFilter y freeze ───────────────────────────────────
            sheet.setAutoFilter(new CellRangeAddress(
                    headerRowIdx, headerRowIdx, 0, headers.length - 1));
            sheet.createFreezePane(0, headerRowIdx + 1);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de casos críticos", e);
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // WRITERS
    // ═════════════════════════════════════════════════════════════════

    private static int writeTitle(XSSFSheet sheet, int rowIdx,
                                  XSSFCellStyle style, String text) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(20);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 7));
        return rowIdx + 1;
    }

    private static int writeMeta(XSSFSheet sheet, int rowIdx,
                                 XSSFCellStyle labelStyle, XSSFCellStyle valueStyle,
                                 String label, String value) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(16);
        Cell lc = row.createCell(0); lc.setCellValue(label); lc.setCellStyle(labelStyle);
        Cell vc = row.createCell(1); vc.setCellValue(value); vc.setCellStyle(valueStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 7));
        return rowIdx + 1;
    }

    private static int writeKpis(XSSFSheet sheet, int rowIdx,
                                 CriticalCasesReportDTO report, Styles st) {

        long critical = report.getCases().stream()
                .filter(c -> "CRÍTICO".equals(c.getRiskLevel())).count();
        long high = report.getCases().stream()
                .filter(c -> "ALTO".equals(c.getRiskLevel())).count();
        int avg = report.getCases().isEmpty() ? 0
                : (int) report.getCases().stream()
                        .mapToInt(CriticalCaseDTO::getRiskScore)
                        .average().orElse(0);

        String[] labels = {"Total Críticos", "Nivel Crítico", "Nivel Alto", "Puntaje Promedio"};
        String[] values = {
                String.valueOf(report.getTotalCritical()),
                String.valueOf(critical),
                String.valueOf(high),
                String.valueOf(avg)
        };
        int[] cols = {0, 2, 4, 6};

        // Valores
        Row valRow = sheet.createRow(rowIdx);
        valRow.setHeightInPoints(28);
        for (int i = 0; i < 4; i++) {
            Cell vc = valRow.createCell(cols[i]);
            vc.setCellValue(values[i]);
            vc.setCellStyle(st.kpiValue);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, cols[i], cols[i] + 1));
        }

        // Labels
        Row labRow = sheet.createRow(rowIdx + 1);
        labRow.setHeightInPoints(14);
        for (int i = 0; i < 4; i++) {
            Cell lc = labRow.createCell(cols[i]);
            lc.setCellValue(labels[i]);
            lc.setCellStyle(st.kpiLabel);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx + 1, rowIdx + 1, cols[i], cols[i] + 1));
        }

        return rowIdx + 2;
    }

    private static void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "—");
        cell.setCellStyle(style);
    }

    // ═════════════════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════════════════

    private static String safe(String v) {
        return (v != null && !v.isBlank()) ? v : "—";
    }

    private static String formatDate(OffsetDateTime d) {
        return d != null ? d.format(DATE_FMT) : "—";
    }

    private static String formatSignals(CriticalCaseDTO c) {
        if (c.getAlertSignals() == null || c.getAlertSignals().isEmpty()) return "—";
        return c.getAlertSignals().stream()
                .map(s -> "• " + safe(s.getQuestionText()))
                .collect(Collectors.joining("\n"));
    }

    private static XSSFColor rgb(int r, int g, int b) {
        return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
    }

    // ═════════════════════════════════════════════════════════════════
    // ESTILOS — clase interna para no contaminar la API pública
    // ═════════════════════════════════════════════════════════════════

    private static class Styles {

        final XSSFCellStyle title;
        final XSSFCellStyle subtitle;
        final XSSFCellStyle metaLabel;
        final XSSFCellStyle metaValue;
        final XSSFCellStyle kpiValue;
        final XSSFCellStyle kpiLabel;
        final XSSFCellStyle header;
        final XSSFCellStyle data;
        final XSSFCellStyle dataAlt;

        private final XSSFCellStyle riskCritical;
        private final XSSFCellStyle riskHigh;
        private final XSSFCellStyle riskModerate;

        Styles(XSSFWorkbook wb) {
            title       = buildTitle(wb);
            subtitle    = buildSubtitle(wb);
            metaLabel   = buildMetaLabel(wb);
            metaValue   = buildMetaValue(wb);
            kpiValue    = buildKpiValue(wb);
            kpiLabel    = buildKpiLabel(wb);
            header      = buildHeader(wb);
            data        = buildData(wb, false);
            dataAlt     = buildData(wb, true);
            riskCritical = buildRisk(wb, rgb(220, 38,  38));
            riskHigh     = buildRisk(wb, rgb(234, 88,  12));
            riskModerate = buildRisk(wb, rgb(202, 138,  4));
        }

        XSSFCellStyle riskStyle(String level) {
            return switch (level != null ? level : "") {
                case "CRÍTICO"  -> riskCritical;
                case "ALTO"     -> riskHigh;
                default         -> riskModerate;
            };
        }

        private static XSSFCellStyle buildTitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 14);
            f.setColor(rgb(45, 55, 72));
            s.setFont(f);
            s.setAlignment(HorizontalAlignment.CENTER);
            return s;
        }

        private static XSSFCellStyle buildSubtitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 10);
            f.setColor(rgb(100, 116, 139));
            s.setFont(f);
            s.setAlignment(HorizontalAlignment.CENTER);
            return s;
        }

        private static XSSFCellStyle buildMetaLabel(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(rgb(100, 116, 139));
            s.setFont(f);
            s.setFillForegroundColor(rgb(248, 250, 252));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildMetaValue(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 9);
            s.setFont(f);
            s.setFillForegroundColor(rgb(248, 250, 252));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildKpiValue(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 16);
            f.setColor(rgb(109, 40, 217));
            s.setFont(f);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setFillForegroundColor(rgb(237, 233, 254));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildKpiLabel(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 8);
            f.setColor(rgb(100, 116, 139));
            s.setFont(f);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setFillForegroundColor(rgb(241, 245, 249));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildHeader(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(rgb(255, 255, 255));
            s.setFont(f);
            s.setFillForegroundColor(rgb(45, 55, 72));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.LEFT);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            s.setWrapText(true);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildData(XSSFWorkbook wb, boolean alt) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 9);
            s.setFont(f);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            s.setWrapText(true);
            if (alt) {
                s.setFillForegroundColor(rgb(248, 250, 252));
                s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildRisk(XSSFWorkbook wb, XSSFColor color) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(color);
            s.setFont(f);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            applyBorder(s);
            return s;
        }

        private static void applyBorder(XSSFCellStyle s) {
            XSSFColor border = rgb(226, 232, 240);
            s.setBorderBottom(BorderStyle.THIN); s.setBottomBorderColor(border);
            s.setBorderTop(BorderStyle.THIN);    s.setTopBorderColor(border);
            s.setBorderLeft(BorderStyle.THIN);   s.setLeftBorderColor(border);
            s.setBorderRight(BorderStyle.THIN);  s.setRightBorderColor(border);
        }
    }
}
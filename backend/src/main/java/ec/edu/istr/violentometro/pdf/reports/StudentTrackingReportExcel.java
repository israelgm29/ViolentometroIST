package ec.edu.istr.violentometro.pdf.reports;

import ec.edu.istr.violentometro.dto.StudentTrackingReportDTO;
import ec.edu.istr.violentometro.dto.StudentTrackingReportDTO.SessionDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Genera el Excel de seguimiento por estudiante.
 */
public class StudentTrackingReportExcel {

    private StudentTrackingReportExcel() {
    }

    public static byte[] generate(StudentTrackingReportDTO report) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = wb.createSheet("Seguimiento");
            Styles st = new Styles(wb);
            int row = 0;

            // ── Título ────────────────────────────────────────────────
            row = writeTitle(sheet, row, st.title,
                    "REPORTE DE SEGUIMIENTO — ESTUDIANTE");
            row = writeTitle(sheet, row, st.subtitle,
                    "Instituto Superior Tecnológico Riobamba — Violentómetro");
            row++;

            // ── Datos del estudiante ──────────────────────────────────
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "DNI", report.getDni());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Género", report.getGender());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Etnia", report.getEthnicity());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Discapacidad", report.getDisability());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Región", report.getRegion());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue, "Instituto", report.getInstitute());
            row++;

            // ── KPIs de resumen ───────────────────────────────────────
            row = writeKpis(sheet, row, report, st);
            row++;

            // ── Tabla de historial ────────────────────────────────────
            row = writeSessionsTable(sheet, row, report.getSessions(), st);

            // ── Anchos ────────────────────────────────────────────────
            int[] widths = {6, 18, 18, 14, 14, 20, 14};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }

            sheet.createFreezePane(0, 3);
            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de seguimiento", e);
        }
    }

    // ── Secciones ─────────────────────────────────────────────────────

    private static int writeTitle(XSSFSheet sheet, int rowIdx,
                                  XSSFCellStyle style, String text) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(20);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 6));
        return rowIdx + 1;
    }

    private static int writeMeta(XSSFSheet sheet, int rowIdx,
                                 XSSFCellStyle labelStyle, XSSFCellStyle valueStyle,
                                 String label, String value) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(16);
        Cell lc = row.createCell(0);
        lc.setCellValue(label);
        lc.setCellStyle(labelStyle);
        Cell vc = row.createCell(1);
        vc.setCellValue(safe(value));
        vc.setCellStyle(valueStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 6));
        return rowIdx + 1;
    }

    private static int writeKpis(XSSFSheet sheet, int rowIdx,
                                 StudentTrackingReportDTO report, Styles st) {
        String[] labels = {"Total Sesiones", "Primera Sesión", "Última Sesión",
                "Nivel Actual", "Puntaje Promedio", "Tendencia"};
        String[] values = {
                String.valueOf(report.getTotalSessions()),
                safe(report.getFirstSession()),
                safe(report.getLastSession()),
                safe(report.getCurrentRiskLevel()),
                report.getAvgScore() != null
                        ? String.format("%.1f", report.getAvgScore()) : "—",
                safe(report.getTrend())
        };

        // Valores
        Row valRow = sheet.createRow(rowIdx);
        valRow.setHeightInPoints(26);
        for (int i = 0; i < 6; i++) {
            XSSFCellStyle style = i == 3
                    ? st.riskStyle(report.getCurrentRiskLevel())
                    : i == 5 ? st.trendStyle(report.getTrend())
                      : st.kpiValue;
            Cell vc = valRow.createCell(i);
            vc.setCellValue(values[i]);
            vc.setCellStyle(style);
        }

        // Labels
        Row labRow = sheet.createRow(rowIdx + 1);
        labRow.setHeightInPoints(13);
        for (int i = 0; i < 6; i++) {
            Cell lc = labRow.createCell(i);
            lc.setCellValue(labels[i]);
            lc.setCellStyle(st.kpiLabel);
        }

        return rowIdx + 2;
    }

    private static int writeSessionsTable(XSSFSheet sheet, int rowIdx,
                                          List<SessionDTO> sessions, Styles st) {
        // Título sección
        Row secRow = sheet.createRow(rowIdx++);
        Cell secCell = secRow.createCell(0);
        secCell.setCellValue("HISTORIAL DE SESIONES");
        secCell.setCellStyle(st.sectionTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 6));

        // Headers
        String[] headers = {"#", "Fecha", "Nivel de Riesgo", "Puntaje", "Zona Dominante", "Tendencia vs anterior"};
        Row headerRow = sheet.createRow(rowIdx++);
        headerRow.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(st.header);
        }

        // Filas
        for (int i = 0; i < sessions.size(); i++) {
            SessionDTO s = sessions.get(i);
            Row row = sheet.createRow(rowIdx++);
            row.setHeightInPoints(16);
            XSSFCellStyle base = i % 2 == 0 ? st.data : st.dataAlt;

            setCell(row, 0, String.valueOf(s.getSessionNumber()), base);
            setCell(row, 1, safe(s.getDate()), base);

            Cell riskCell = row.createCell(2);
            riskCell.setCellValue(safe(s.getRiskLevel()));
            riskCell.setCellStyle(st.riskStyle(s.getRiskLevel()));

            setCell(row, 3, s.getScore() != null
                    ? String.valueOf(s.getScore()) : "—", base);
            setCell(row, 4, safe(s.getDominantZone()), base);

            Cell trendCell = row.createCell(5);
            trendCell.setCellValue(safe(s.getTrend()));
            trendCell.setCellStyle(st.trendStyle(s.getTrend()));
        }

        return rowIdx;
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private static void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "—");
        cell.setCellStyle(style);
    }

    private static String safe(String v) {
        return (v != null && !v.isBlank()) ? v : "—";
    }

    private static XSSFColor rgb(int r, int g, int b) {
        return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
    }

    // ── Estilos ───────────────────────────────────────────────────────

    private static class Styles {
        final XSSFCellStyle title, subtitle, metaLabel, metaValue;
        final XSSFCellStyle kpiValue, kpiLabel, sectionTitle;
        final XSSFCellStyle header, data, dataAlt;
        private final XSSFCellStyle riskCritical, riskHigh, riskModerate, riskLow;
        private final XSSFCellStyle trendUp, trendDown, trendStable;

        Styles(XSSFWorkbook wb) {
            title = buildTitle(wb);
            subtitle = buildSubtitle(wb);
            metaLabel = buildMetaLabel(wb);
            metaValue = buildMetaValue(wb);
            kpiValue = buildKpiValue(wb);
            kpiLabel = buildKpiLabel(wb);
            sectionTitle = buildSectionTitle(wb);
            header = buildHeader(wb);
            data = buildData(wb, false);
            dataAlt = buildData(wb, true);
            riskCritical = buildRisk(wb, rgb(220, 38, 38), rgb(254, 242, 242));
            riskHigh = buildRisk(wb, rgb(234, 88, 12), rgb(255, 247, 237));
            riskModerate = buildRisk(wb, rgb(202, 138, 4), rgb(254, 252, 232));
            riskLow = buildRisk(wb, rgb(22, 163, 74), rgb(240, 253, 244));
            trendUp = buildTrend(wb, rgb(22, 163, 74));
            trendDown = buildTrend(wb, rgb(220, 38, 38));
            trendStable = buildTrend(wb, rgb(100, 116, 139));
        }

        XSSFCellStyle riskStyle(String level) {
            if (level == null) return riskLow;
            return switch (level.toLowerCase()) {
                case "critical", "crítico" -> riskCritical;
                case "high", "alto" -> riskHigh;
                case "medium", "moderado" -> riskModerate;
                default -> riskLow;
            };
        }

        XSSFCellStyle trendStyle(String trend) {
            if (trend == null) return trendStable;
            return switch (trend) {
                case "MEJORANDO" -> trendUp;
                case "EMPEORANDO" -> trendDown;
                default -> trendStable;
            };
        }

        private static XSSFCellStyle buildTitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 14);
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
            f.setBold(true);
            f.setFontHeightInPoints((short) 9);
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
            f.setBold(true);
            f.setFontHeightInPoints((short) 14);
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

        private static XSSFCellStyle buildSectionTitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 10);
            f.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));
            s.setFont(f);
            s.setFillForegroundColor(rgb(45, 55, 72));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.LEFT);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildHeader(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 9);
            f.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));
            s.setFont(f);
            s.setFillForegroundColor(rgb(109, 40, 217));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.LEFT);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildData(XSSFWorkbook wb, boolean alt) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 9);
            s.setFont(f);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            if (alt) {
                s.setFillForegroundColor(rgb(248, 250, 252));
                s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildRisk(XSSFWorkbook wb,
                                               XSSFColor textColor, XSSFColor bgColor) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 9);
            f.setColor(textColor);
            s.setFont(f);
            s.setFillForegroundColor(bgColor);
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            applyBorder(s);
            return s;
        }

        private static XSSFCellStyle buildTrend(XSSFWorkbook wb, XSSFColor color) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true);
            f.setFontHeightInPoints((short) 9);
            f.setColor(color);
            s.setFont(f);
            s.setAlignment(HorizontalAlignment.CENTER);
            applyBorder(s);
            return s;
        }

        private static void applyBorder(XSSFCellStyle s) {
            XSSFColor border = rgb(226, 232, 240);
            s.setBorderBottom(BorderStyle.THIN);
            s.setBottomBorderColor(border);
            s.setBorderTop(BorderStyle.THIN);
            s.setTopBorderColor(border);
            s.setBorderLeft(BorderStyle.THIN);
            s.setLeftBorderColor(border);
            s.setBorderRight(BorderStyle.THIN);
            s.setRightBorderColor(border);
        }
    }
}
package ec.edu.istr.violentometro.pdf.reports;

import ec.edu.istr.violentometro.dto.ParticipationReportDTO;
import ec.edu.istr.violentometro.dto.ParticipationReportDTO.DailySummaryDTO;
import ec.edu.istr.violentometro.dto.ParticipationReportDTO.SessionDetailDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ParticipationReportExcel {

    private ParticipationReportExcel() {}

    public static byte[] generate(ParticipationReportDTO report) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Styles st = new Styles(wb);

            // ── Hoja 1: Resumen diario ────────────────────────────────
            buildDailySummarySheet(wb.createSheet("Resumen Diario"), report, st);

            // ── Hoja 2: Detalle individual ────────────────────────────
            buildDetailSheet(wb.createSheet("Detalle Individual"), report, st);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de participación", e);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // HOJA 1 — RESUMEN DIARIO
    // ═══════════════════════════════════════════════════════════════

    private static void buildDailySummarySheet(XSSFSheet sheet,
                                               ParticipationReportDTO report,
                                               Styles st) {
        int row = 0;

        // Título
        row = writeTitle(sheet, row, st.title, "PARTICIPACIÓN POR FECHA — RESUMEN DIARIO", 7);
        row = writeTitle(sheet, row, st.subtitle,
                report.getSurveyTitle() + "  |  " + report.getPeriod(), 7);
        row++;

        // KPIs
        row = writeKpis(sheet, row, report, st);
        row++;

        // Headers
        String[] headers = {"Fecha", "Total Sesiones", "Crítico", "Alto", "Moderado", "Bajo", "Puntaje Prom."};
        Row hRow = sheet.createRow(row++);
        hRow.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            setCell(hRow, i, headers[i], st.header);
        }

        // Datos
        List<DailySummaryDTO> summary = report.getDailySummary();
        for (int i = 0; i < summary.size(); i++) {
            DailySummaryDTO d = summary.get(i);
            Row dataRow = sheet.createRow(row++);
            dataRow.setHeightInPoints(16);
            XSSFCellStyle base = i % 2 == 0 ? st.data : st.dataAlt;

            setCell(dataRow, 0, d.getDate(),                          st.dateCell);
            setCell(dataRow, 1, str(d.getTotalSessions()),            base);
            setCell(dataRow, 2, str(d.getCritical()),   st.riskCritical);
            setCell(dataRow, 3, str(d.getHigh()),       st.riskHigh);
            setCell(dataRow, 4, str(d.getMedium()),     st.riskMedium);
            setCell(dataRow, 5, str(d.getLow()),        st.riskLow);
            setCell(dataRow, 6, d.getAvgScore() != null
                    ? String.format("%.1f", d.getAvgScore()) : "—",   base);
        }

        // Totals row
        Row totalRow = sheet.createRow(row);
        totalRow.setHeightInPoints(18);
        setCell(totalRow, 0, "TOTAL", st.totalLabel);
        setCell(totalRow, 1, str(report.getTotalSessions()), st.totalValue);
        setCell(totalRow, 2, str(report.getTotalCritical()), st.totalValue);
        for (int i = 3; i < 7; i++) setCell(totalRow, i, "", st.totalValue);

        // Anchos
        int[] widths = {14, 16, 12, 12, 12, 12, 15};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);

        sheet.setAutoFilter(new CellRangeAddress(4, 4, 0, 6));
        sheet.createFreezePane(0, 5);
    }

    // ═══════════════════════════════════════════════════════════════
    // HOJA 2 — DETALLE INDIVIDUAL
    // ═══════════════════════════════════════════════════════════════

    private static void buildDetailSheet(XSSFSheet sheet,
                                         ParticipationReportDTO report,
                                         Styles st) {
        int row = 0;

        row = writeTitle(sheet, row, st.title, "PARTICIPACIÓN POR FECHA — DETALLE INDIVIDUAL", 8);
        row = writeTitle(sheet, row, st.subtitle,
                report.getSurveyTitle() + "  |  " + report.getPeriod(), 8);
        row++;

        // Headers
        String[] headers = {"Fecha", "Hora", "DNI", "Género", "Etnia",
                "Instituto", "Nivel de Riesgo", "Puntaje", "Zona Dominante"};
        Row hRow = sheet.createRow(row++);
        hRow.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) setCell(hRow, i, headers[i], st.header);

        // Datos
        List<SessionDetailDTO> details = report.getSessionDetails();
        for (int i = 0; i < details.size(); i++) {
            SessionDetailDTO d = details.get(i);
            Row dataRow = sheet.createRow(row++);
            dataRow.setHeightInPoints(16);
            XSSFCellStyle base = i % 2 == 0 ? st.data : st.dataAlt;

            setCell(dataRow, 0, d.getDate(),       st.dateCell);
            setCell(dataRow, 1, d.getTime(),       base);
            setCell(dataRow, 2, safe(d.getDni()),  base);
            setCell(dataRow, 3, safe(d.getGender()), base);
            setCell(dataRow, 4, safe(d.getEthnicity()), base);
            setCell(dataRow, 5, safe(d.getInstitute()), base);

            Cell riskCell = dataRow.createCell(6);
            riskCell.setCellValue(safe(d.getRiskLevel()));
            riskCell.setCellStyle(riskStyle(d.getRiskLevel(), st));

            setCell(dataRow, 7, d.getScore() != null
                    ? String.valueOf(d.getScore()) : "—", base);
            setCell(dataRow, 8, safe(d.getDominantZone()), base);
        }

        // Anchos
        int[] widths = {12, 8, 14, 12, 14, 30, 14, 10, 20};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);

        sheet.setAutoFilter(new CellRangeAddress(3, 3, 0, 8));
        sheet.createFreezePane(0, 4);
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private static int writeTitle(XSSFSheet sheet, int rowIdx,
                                  XSSFCellStyle style, String text, int cols) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(20);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, cols - 1));
        return rowIdx + 1;
    }

    private static int writeKpis(XSSFSheet sheet, int rowIdx,
                                 ParticipationReportDTO r, Styles st) {
        String[] labels = {"Total Sesiones", "Participantes Únicos", "Casos Críticos", "Prom. Sesiones/Día"};
        String[] values = {
                str(r.getTotalSessions()),
                str(r.getTotalParticipants()),
                str(r.getTotalCritical()),
                r.getAvgSessionsPerDay() != null
                        ? String.format("%.1f", r.getAvgSessionsPerDay()) : "0"
        };

        Row valRow = sheet.createRow(rowIdx);
        valRow.setHeightInPoints(26);
        Row labRow = sheet.createRow(rowIdx + 1);
        labRow.setHeightInPoints(13);

        for (int i = 0; i < 4; i++) {
            Cell vc = valRow.createCell(i * 2);
            vc.setCellValue(values[i]);
            vc.setCellStyle(st.kpiValue);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, i * 2, i * 2 + 1));

            Cell lc = labRow.createCell(i * 2);
            lc.setCellValue(labels[i]);
            lc.setCellStyle(st.kpiLabel);
            sheet.addMergedRegion(new CellRangeAddress(rowIdx + 1, rowIdx + 1, i * 2, i * 2 + 1));
        }
        return rowIdx + 2;
    }

    private static XSSFCellStyle riskStyle(String level, Styles st) {
        if (level == null) return st.riskLow;
        return switch (level.toLowerCase()) {
            case "critical", "crítico" -> st.riskCritical;
            case "high",    "alto"     -> st.riskHigh;
            case "medium",  "moderado" -> st.riskMedium;
            default                    -> st.riskLow;
        };
    }

    private static void setCell(Row row, int col, String value, XSSFCellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "—");
        cell.setCellStyle(style);
    }

    private static String str(Long v)    { return v != null ? String.valueOf(v) : "0"; }
    private static String safe(String v) { return v != null && !v.isBlank() ? v : "—"; }

    private static XSSFColor rgb(int r, int g, int b) {
        return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
    }

    // ── Estilos ───────────────────────────────────────────────────────

    private static class Styles {
        final XSSFCellStyle title, subtitle;
        final XSSFCellStyle kpiValue, kpiLabel;
        final XSSFCellStyle header, data, dataAlt, dateCell;
        final XSSFCellStyle riskCritical, riskHigh, riskMedium, riskLow;
        final XSSFCellStyle totalLabel, totalValue;

        Styles(XSSFWorkbook wb) {
            title        = buildTitle(wb);
            subtitle     = buildSubtitle(wb);
            kpiValue     = buildKpiValue(wb);
            kpiLabel     = buildKpiLabel(wb);
            header       = buildHeader(wb);
            data         = buildData(wb, false);
            dataAlt      = buildData(wb, true);
            dateCell     = buildDateCell(wb);
            riskCritical = buildRisk(wb, rgb(220, 38,  38), rgb(254, 242, 242));
            riskHigh     = buildRisk(wb, rgb(234, 88,  12), rgb(255, 247, 237));
            riskMedium   = buildRisk(wb, rgb(202, 138,  4), rgb(254, 252, 232));
            riskLow      = buildRisk(wb, rgb(22,  163, 74), rgb(240, 253, 244));
            totalLabel   = buildTotalLabel(wb);
            totalValue   = buildTotalValue(wb);
        }

        private static XSSFCellStyle buildTitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 13);
            f.setColor(rgb(45, 55, 72));
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
            return s;
        }

        private static XSSFCellStyle buildSubtitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 9); f.setColor(rgb(100, 116, 139));
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
            return s;
        }

        private static XSSFCellStyle buildKpiValue(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 16);
            f.setColor(rgb(109, 40, 217));
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
            s.setFillForegroundColor(rgb(237, 233, 254));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildKpiLabel(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 8); f.setColor(rgb(100, 116, 139));
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
            s.setFillForegroundColor(rgb(241, 245, 249));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildHeader(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
            s.setFont(f);
            s.setFillForegroundColor(rgb(45, 55, 72));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildData(XSSFWorkbook wb, boolean alt) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont(); f.setFontHeightInPoints((short) 9);
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
            if (alt) {
                s.setFillForegroundColor(rgb(248, 250, 252));
                s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildDateCell(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(rgb(45, 55, 72));
            s.setFont(f);
            s.setFillForegroundColor(rgb(241, 245, 249));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildRisk(XSSFWorkbook wb,
                                               XSSFColor text, XSSFColor bg) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(text);
            s.setFont(f);
            s.setFillForegroundColor(bg);
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildTotalLabel(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
            s.setFont(f);
            s.setFillForegroundColor(rgb(30, 41, 59));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildTotalValue(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(rgb(109, 40, 217));
            s.setFont(f);
            s.setFillForegroundColor(rgb(237, 233, 254));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            applyBorder(s); return s;
        }

        private static void applyBorder(XSSFCellStyle s) {
            XSSFColor b = rgb(226, 232, 240);
            s.setBorderBottom(BorderStyle.THIN); s.setBottomBorderColor(b);
            s.setBorderTop(BorderStyle.THIN);    s.setTopBorderColor(b);
            s.setBorderLeft(BorderStyle.THIN);   s.setLeftBorderColor(b);
            s.setBorderRight(BorderStyle.THIN);  s.setRightBorderColor(b);
        }
    }
}
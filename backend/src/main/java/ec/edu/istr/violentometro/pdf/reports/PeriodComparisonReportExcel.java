package ec.edu.istr.violentometro.pdf.reports;

import ec.edu.istr.violentometro.dto.PeriodComparisonReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;

public class PeriodComparisonReportExcel {

    private PeriodComparisonReportExcel() {}

    public static byte[] generate(PeriodComparisonReportDTO report) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = wb.createSheet("Comparativo");
            Styles st = new Styles(wb);
            int row = 0;

            // ── Título ────────────────────────────────────────────────
            row = writeTitle(sheet, row, st.title,
                    "REPORTE COMPARATIVO DE PERÍODOS");
            row = writeTitle(sheet, row, st.subtitle,
                    "Instituto Superior Tecnológico Riobamba — Violentómetro");
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue,
                    "Cuestionario", report.getSurveyTitle());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue,
                    "Generado", report.getGeneratedAt());
            row = writeMeta(sheet, row, st.metaLabel, st.metaValue,
                    "Conclusión", report.getConclusion());
            row++;

            // ── Encabezados de períodos ───────────────────────────────
            row = writePeriodHeaders(sheet, row, report, st);
            row++;

            // ── Tabla comparativa principal ───────────────────────────
            row = writeComparisonTable(sheet, row, report, st);
            row++;

            // ── Desglose por nivel de riesgo ──────────────────────────
            row = writeRiskBreakdown(sheet, row, report, st);

            // ── Anchos de columna ─────────────────────────────────────
            sheet.setColumnWidth(0, 35 * 256); // Métrica
            sheet.setColumnWidth(1, 20 * 256); // Período 1
            sheet.setColumnWidth(2, 20 * 256); // Período 2
            sheet.setColumnWidth(3, 18 * 256); // Variación
            sheet.setColumnWidth(4, 18 * 256); // Evaluación

            sheet.createFreezePane(0, 2);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel comparativo", e);
        }
    }

    // ── Secciones ─────────────────────────────────────────────────────

    private static int writePeriodHeaders(XSSFSheet sheet, int rowIdx,
                                          PeriodComparisonReportDTO report, Styles st) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(22);

        setCell(row, 0, "MÉTRICA",             st.header);
        setCell(row, 1, report.getPeriod1Label(), st.headerP1);
        setCell(row, 2, report.getPeriod2Label(), st.headerP2);
        setCell(row, 3, "VARIACIÓN",           st.header);
        setCell(row, 4, "EVALUACIÓN",          st.header);

        return rowIdx + 1;
    }

    private static int writeComparisonTable(XSSFSheet sheet, int rowIdx,
                                            PeriodComparisonReportDTO r, Styles st) {
        // Sección title
        Row secRow = sheet.createRow(rowIdx++);
        Cell secCell = secRow.createCell(0);
        secCell.setCellValue("INDICADORES PRINCIPALES");
        secCell.setCellStyle(st.sectionTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 4));

        // Filas de métricas
        rowIdx = writeMetricRow(sheet, rowIdx, st, "Participaciones totales",
                str(r.getPeriod1Participants()), str(r.getPeriod2Participants()),
                formatChange(r.getParticipationChange()),
                evaluate(r.getParticipationChange(), true), r.getParticipationChange());

        rowIdx = writeMetricRow(sheet, rowIdx, st, "Casos críticos",
                str(r.getPeriod1Critical()), str(r.getPeriod2Critical()),
                formatChange(r.getCriticalChange()),
                evaluate(r.getCriticalChange(), false), r.getCriticalChange());

        rowIdx = writeMetricRow(sheet, rowIdx, st, "Puntaje promedio",
                fmtScore(r.getPeriod1AvgScore()), fmtScore(r.getPeriod2AvgScore()),
                formatChange(r.getAvgScoreChange()),
                evaluate(r.getAvgScoreChange(), false), r.getAvgScoreChange());

        rowIdx = writeMetricRow(sheet, rowIdx, st, "Tasa de criticidad",
                fmtPct(r.getPeriod1CriticalRate()), fmtPct(r.getPeriod2CriticalRate()),
                formatChange(r.getCriticalRateChange()),
                evaluate(r.getCriticalRateChange(), false), r.getCriticalRateChange());

        return rowIdx;
    }

    private static int writeMetricRow(XSSFSheet sheet, int rowIdx, Styles st,
                                      String metric, String v1, String v2,
                                      String change, String eval, Double changeVal) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(18);

        setCell(row, 0, metric, st.metricLabel);
        setCell(row, 1, v1,     st.dataP1);
        setCell(row, 2, v2,     st.dataP2);

        Cell changeCell = row.createCell(3);
        changeCell.setCellValue(change);
        changeCell.setCellStyle(changeVal != null && changeVal < 0
                ? st.changeNeg : st.changePos);

        Cell evalCell = row.createCell(4);
        evalCell.setCellValue(eval);
        evalCell.setCellStyle(eval.contains("✓") ? st.evalGood : st.evalBad);

        return rowIdx + 1;
    }

    private static int writeRiskBreakdown(XSSFSheet sheet, int rowIdx,
                                          PeriodComparisonReportDTO r, Styles st) {
        Row secRow = sheet.createRow(rowIdx++);
        Cell secCell = secRow.createCell(0);
        secCell.setCellValue("DESGLOSE POR NIVEL DE RIESGO (sesiones)");
        secCell.setCellStyle(st.sectionTitle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx - 1, rowIdx - 1, 0, 4));

        String[][] levels = {
                {"Crítico",  str(r.getPeriod1LevelCritical()), str(r.getPeriod2LevelCritical())},
                {"Alto",     str(r.getPeriod1LevelHigh()),     str(r.getPeriod2LevelHigh())},
                {"Moderado", str(r.getPeriod1LevelMedium()),   str(r.getPeriod2LevelMedium())},
                {"Bajo",     str(r.getPeriod1LevelLow()),      str(r.getPeriod2LevelLow())}
        };

        for (int i = 0; i < levels.length; i++) {
            Row row = sheet.createRow(rowIdx++);
            row.setHeightInPoints(16);
            XSSFCellStyle base = i % 2 == 0 ? st.data : st.dataAlt;
            setCell(row, 0, levels[i][0], st.metricLabel);
            setCell(row, 1, levels[i][1], base);
            setCell(row, 2, levels[i][2], base);

            // Variación numérica
            try {
                long v1 = Long.parseLong(levels[i][1]);
                long v2 = Long.parseLong(levels[i][2]);
                long diff = v2 - v1;
                Cell diffCell = row.createCell(3);
                diffCell.setCellValue((diff >= 0 ? "+" : "") + diff);
                diffCell.setCellStyle(diff > 0 ? st.changePos : diff < 0 ? st.changeNeg : base);
            } catch (Exception ignored) {
                setCell(row, 3, "—", base);
            }
            setCell(row, 4, "", base);
        }

        return rowIdx;
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private static int writeTitle(XSSFSheet sheet, int rowIdx,
                                  XSSFCellStyle style, String text) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(20);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 4));
        return rowIdx + 1;
    }

    private static int writeMeta(XSSFSheet sheet, int rowIdx,
                                 XSSFCellStyle labelStyle, XSSFCellStyle valueStyle,
                                 String label, String value) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(16);
        Cell lc = row.createCell(0); lc.setCellValue(label); lc.setCellStyle(labelStyle);
        Cell vc = row.createCell(1); vc.setCellValue(safe(value)); vc.setCellStyle(valueStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 1, 4));
        return rowIdx + 1;
    }

    private static void setCell(Row row, int col, String value, XSSFCellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "—");
        cell.setCellStyle(style);
    }

    private static String str(Long v)    { return v != null ? String.valueOf(v) : "0"; }
    private static String safe(String v) { return v != null && !v.isBlank() ? v : "—"; }

    private static String fmtScore(Double v) {
        return v != null ? String.format("%.1f", v) : "—";
    }

    private static String fmtPct(Double v) {
        return v != null ? String.format("%.1f%%", v) : "—";
    }

    private static String formatChange(Double change) {
        if (change == null) return "—";
        return (change >= 0 ? "+" : "") + String.format("%.1f%%", change);
    }

    /**
     * higherIsBetter=true  → aumento es bueno (participaciones)
     * higherIsBetter=false → aumento es malo  (críticos, puntaje, tasa)
     */
    private static String evaluate(Double change, boolean higherIsBetter) {
        if (change == null) return "—";
        if (Math.abs(change) < 1) return "ESTABLE";
        boolean improved = higherIsBetter ? change > 0 : change < 0;
        return improved ? "Mejora ✓" : "Atención ✗";
    }

    private static XSSFColor rgb(int r, int g, int b) {
        return new XSSFColor(new byte[]{(byte) r, (byte) g, (byte) b}, null);
    }

    // ── Estilos ───────────────────────────────────────────────────────

    private static class Styles {
        final XSSFCellStyle title, subtitle, metaLabel, metaValue;
        final XSSFCellStyle header, headerP1, headerP2;
        final XSSFCellStyle sectionTitle, metricLabel;
        final XSSFCellStyle data, dataAlt, dataP1, dataP2;
        final XSSFCellStyle changePos, changeNeg;
        final XSSFCellStyle evalGood, evalBad;

        Styles(XSSFWorkbook wb) {
            title        = buildTitle(wb);
            subtitle     = buildSubtitle(wb);
            metaLabel    = buildMetaLabel(wb);
            metaValue    = buildMetaValue(wb);
            header       = buildHeader(wb, rgb(45, 55, 72));
            headerP1     = buildHeader(wb, rgb(109, 40, 217));
            headerP2     = buildHeader(wb, rgb(5, 150, 105));
            sectionTitle = buildSectionTitle(wb);
            metricLabel  = buildMetricLabel(wb);
            data         = buildData(wb, false);
            dataAlt      = buildData(wb, true);
            dataP1       = buildDataColored(wb, rgb(237, 233, 254));
            dataP2       = buildDataColored(wb, rgb(209, 250, 229));
            changePos    = buildChange(wb, rgb(5, 150, 105));
            changeNeg    = buildChange(wb, rgb(220, 38, 38));
            evalGood     = buildEval(wb, rgb(5, 150, 105), rgb(209, 250, 229));
            evalBad      = buildEval(wb, rgb(220, 38, 38), rgb(254, 242, 242));
        }

        private static XSSFCellStyle buildTitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 14);
            f.setColor(rgb(45, 55, 72));
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
            return s;
        }

        private static XSSFCellStyle buildSubtitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 10); f.setColor(rgb(100, 116, 139));
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
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
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildMetaValue(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont(); f.setFontHeightInPoints((short) 9);
            s.setFont(f);
            s.setFillForegroundColor(rgb(248, 250, 252));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildHeader(XSSFWorkbook wb, XSSFColor bg) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
            s.setFont(f);
            s.setFillForegroundColor(bg);
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildSectionTitle(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
            s.setFont(f);
            s.setFillForegroundColor(rgb(30, 41, 59));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildMetricLabel(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 9); f.setBold(true);
            f.setColor(rgb(45, 55, 72));
            s.setFont(f);
            s.setFillForegroundColor(rgb(241, 245, 249));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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

        private static XSSFCellStyle buildDataColored(XSSFWorkbook wb, XSSFColor bg) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setFontHeightInPoints((short) 10); f.setBold(true);
            f.setColor(rgb(30, 41, 59));
            s.setFont(f);
            s.setFillForegroundColor(bg);
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildChange(XSSFWorkbook wb, XSSFColor color) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(color);
            s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER);
            applyBorder(s); return s;
        }

        private static XSSFCellStyle buildEval(XSSFWorkbook wb,
                                               XSSFColor textColor, XSSFColor bgColor) {
            XSSFCellStyle s = wb.createCellStyle();
            XSSFFont f = wb.createFont();
            f.setBold(true); f.setFontHeightInPoints((short) 9);
            f.setColor(textColor);
            s.setFont(f);
            s.setFillForegroundColor(bgColor);
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            applyBorder(s); return s;
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
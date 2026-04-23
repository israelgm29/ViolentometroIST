package ec.edu.istr.violentometro.pdf.components;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.kernel.colors.DeviceRgb;

import ec.edu.istr.violentometro.pdf.template.PdfLayoutConfig;
import ec.edu.istr.violentometro.pdf.template.PdfStyles;

import java.util.List;

public class TableSection {

    // Índice de la columna de nivel de riesgo — centralizado para fácil cambio
    private static final int RISK_LEVEL_COL = 4;
    private static final int SIGNALS_COL    = 6;

    public static void draw(
            Document doc,
            List<String> headers,
            List<List<String>> rows
    ) {
        if (headers == null || rows == null) return;

        // ── Título de sección ─────────────────────────────────────────
        doc.add(new Paragraph("DETALLE DE CASOS CRÍTICOS")
                .setBold()
                .setFontSize(11)
                .setFontColor(PdfStyles.PRIMARY)
                .setBorderLeft(new SolidBorder(PdfStyles.ACCENT, 3))
                .setPaddingLeft(6)
                .setMarginBottom(6));

        // ── Anchos proporcionales (landscape A4 = ~841pt útil) ────────
        float[] columnWidths = {3, 12, 10, 10, 10, 6, 49};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        // ✅ NO usar setKeepTogether(true) en tablas grandes — causa páginas en blanco
        table.setKeepWithNext(false);

        // ── Cabecera ──────────────────────────────────────────────────
        for (String header : headers) {
            table.addHeaderCell(
                    new Cell()
                            .add(new Paragraph(header)
                                    .setBold()
                                    .setFontSize(8)
                                    .setFontColor(new DeviceRgb(255, 255, 255)))
                            .setBackgroundColor(PdfStyles.PRIMARY)
                            .setTextAlignment(TextAlignment.LEFT)
                            .setPadding(6)
                            .setBorder(Border.NO_BORDER)
            );
        }

        // ── Filas ─────────────────────────────────────────────────────
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            boolean isAlt = i % 2 == 0;

            for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
                String value = (colIndex < row.size() && row.get(colIndex) != null)
                        ? row.get(colIndex) : "—";

                Cell cell;

                if (colIndex == RISK_LEVEL_COL) {
                    // ✅ Badge de nivel de riesgo con color dinámico
                    cell = buildRiskBadgeCell(value, isAlt);
                } else {
                    cell = buildStandardCell(value, colIndex, isAlt);
                }

                table.addCell(cell);
            }
        }

        doc.add(table);
        doc.add(new Paragraph(" ").setMarginBottom(PdfLayoutConfig.SECTION_SPACING));
    }

    // ── Badge de riesgo ───────────────────────────────────────────────
    private static Cell buildRiskBadgeCell(String level, boolean isAlt) {

        // Tabla interna de 1 celda centrada para simular el badge
        Table badge = new Table(1);
        badge.setWidth(UnitValue.createPointValue(52));
        badge.setHorizontalAlignment(HorizontalAlignment.CENTER);

        badge.addCell(
                new Cell()
                        .add(new Paragraph(level)
                                .setFontSize(7)
                                .setBold()
                                .setFontColor(PdfStyles.getRiskColor(level))
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMultipliedLeading(1f))
                        .setBackgroundColor(PdfStyles.getRiskBg(level))
                        .setBorder(new SolidBorder(PdfStyles.getRiskColor(level), 0.5f))
                        .setBorderRadius(new BorderRadius(3))
                        .setPaddingTop(2)
                        .setPaddingBottom(2)
                        .setPaddingLeft(6)
                        .setPaddingRight(6)
        );

        Cell cell = new Cell()
                .add(badge)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(4)
                .setBorder(new SolidBorder(PdfStyles.BORDER, 0.3f));

        if (isAlt) cell.setBackgroundColor(PdfStyles.BG_LIGHT);
        return cell;
    }

    // ── Celda estándar ────────────────────────────────────────────────
    private static Cell buildStandardCell(String value, int colIndex, boolean isAlt) {

        float fontSize = (colIndex == SIGNALS_COL) ? 6.5f : 7.5f;

        Paragraph p = new Paragraph(value)
                .setFontSize(fontSize)
                .setFontColor(PdfStyles.TEXT)
                .setMultipliedLeading(1.2f);

        Cell cell = new Cell()
                .add(p)
                .setPadding(5)
                .setBorder(new SolidBorder(PdfStyles.BORDER, 0.3f))
                .setKeepTogether(true);   // ✅ keepTogether por celda, no por tabla

        if (isAlt) cell.setBackgroundColor(PdfStyles.BG_LIGHT);

        // Alineaciones
        if (colIndex == 0 || colIndex == 5) cell.setTextAlignment(TextAlignment.CENTER);

        return cell;
    }
}
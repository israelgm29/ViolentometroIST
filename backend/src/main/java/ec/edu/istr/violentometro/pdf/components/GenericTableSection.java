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

public class GenericTableSection {

    private GenericTableSection() {}

    /**
     * Sin nota al pie — para tablas que no necesitan aclaración.
     */
    public static void draw(Document doc,
                            String sectionTitle,
                            List<String> headers,
                            List<List<String>> rows) {
        draw(doc, sectionTitle, headers, rows, null);
    }

    /**
     * Con nota al pie opcional.
     * Si footerNote != null se muestra debajo de la tabla en texto muted pequeño.
     */
    public static void draw(Document doc,
                            String sectionTitle,
                            List<String> headers,
                            List<List<String>> rows,
                            String footerNote) {

        if (headers == null || rows == null) return;

        // ── Título de sección ──────────────────────────────────────
        doc.add(new Paragraph(sectionTitle.toUpperCase())
                .setBold()
                .setFontSize(11)
                .setFontColor(PdfStyles.PRIMARY)
                .setBorderLeft(new SolidBorder(PdfStyles.ACCENT, 3))
                .setPaddingLeft(6)
                .setMarginBottom(6));

        // ── Columnas proporcionales ────────────────────────────────
        float[] widths = buildColumnWidths(headers.size());
        Table table = new Table(UnitValue.createPercentArray(widths));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setKeepWithNext(false);
        table.setSkipLastFooter(true);
        table.setSkipFirstHeader(false);

        // ── Cabecera ───────────────────────────────────────────────
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

        // ── Filas ──────────────────────────────────────────────────
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            boolean isAlt = i % 2 == 0;

            for (int col = 0; col < headers.size(); col++) {
                String value = (col < row.size() && row.get(col) != null)
                        ? row.get(col) : "—";

                Cell cell = new Cell()
                        .add(new Paragraph(value)
                                .setFontSize(8)
                                .setFontColor(PdfStyles.TEXT)
                                .setMultipliedLeading(1.2f))
                        .setPadding(5)
                        .setBorder(new SolidBorder(PdfStyles.BORDER, 0.3f))
                        .setKeepTogether(true);

                if (isAlt) cell.setBackgroundColor(PdfStyles.BG_LIGHT);
                if (col == 0) cell.setTextAlignment(TextAlignment.CENTER);

                table.addCell(cell);
            }
        }

        doc.add(table);

        // ── Nota al pie (opcional) ─────────────────────────────────
        if (footerNote != null && !footerNote.isBlank()) {
            doc.add(new Paragraph("* " + footerNote)
                    .setFontSize(7)
                    .setFontColor(PdfStyles.MUTED)
                    .setItalic()
                    .setMarginTop(4)
                    .setMarginBottom(PdfLayoutConfig.SECTION_SPACING)
                    .setBorderLeft(new SolidBorder(PdfStyles.MUTED, 1))
                    .setPaddingLeft(6));
        } else {
            doc.add(new Paragraph(" ")
                    .setMarginBottom(PdfLayoutConfig.SECTION_SPACING));
        }
    }

    private static float[] buildColumnWidths(int colCount) {
        return switch (colCount) {
            case 3  -> new float[]{3, 60, 37};
            case 4  -> new float[]{3, 45, 30, 22};
            case 5  -> new float[]{3, 35, 25, 15, 22};
            case 6  -> new float[]{3, 35, 20, 14, 14, 14};
            default -> {
                float[] equal = new float[colCount];
                equal[0] = 5;
                float rest = 95f / (colCount - 1);
                for (int i = 1; i < colCount; i++) equal[i] = rest;
                yield equal;
            }
        };
    }
}
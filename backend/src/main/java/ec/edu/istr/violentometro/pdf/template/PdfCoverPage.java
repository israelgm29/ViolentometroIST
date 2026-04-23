package ec.edu.istr.violentometro.pdf.template;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfCoverPage {

    private static final DateTimeFormatter DT_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter D_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void draw(
            Document doc,
            String reportTitle,
            String surveyTitle,
            String period,
            byte[] logoBytes
    ) {
        // ── 1. Banda superior accent ───────────────────────────────
        Div topBand = new Div()
                .setBackgroundColor(PdfStyles.ACCENT)
                .setHeight(14)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(-PdfLayoutConfig.MARGIN_TOP)   // salir del margen
                .setMarginLeft(-PdfLayoutConfig.MARGIN_LEFT)
                .setMarginRight(-PdfLayoutConfig.MARGIN_RIGHT);
        doc.add(topBand);

        // ── 2. Bloque institucional: logo + nombre ─────────────────
        Table instTable = new Table(new float[]{20, 80});
        instTable.setWidth(UnitValue.createPercentValue(100));
        instTable.setMarginTop(20);
        instTable.setBorder(Border.NO_BORDER);

        // Celda logo
        Cell logoCell = new Cell().setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        if (logoBytes != null) {
            try {
                Image logo = new Image(ImageDataFactory.create(logoBytes));
                logo.setWidth(70);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                logoCell.add(logo);
            } catch (Exception ignored) {
                logoCell.add(new Paragraph("ISTR")
                        .setBold().setFontSize(18)
                        .setFontColor(PdfStyles.ACCENT));
            }
        } else {
            logoCell.add(new Paragraph("ISTR")
                    .setBold().setFontSize(22)
                    .setFontColor(PdfStyles.ACCENT)
                    .setTextAlignment(TextAlignment.CENTER));
        }
        instTable.addCell(logoCell);

        // Celda nombre institucional
        Cell nameCell = new Cell().setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPaddingLeft(20);

        nameCell.add(new Paragraph("REPÚBLICA DEL ECUADOR")
                .setFontSize(8)
                .setFontColor(PdfStyles.MUTED)
                .setCharacterSpacing(1.5f));

        nameCell.add(new Paragraph("Instituto Superior Tecnológico Riobamba")
                .setBold()
                .setFontSize(16)
                .setFontColor(PdfStyles.PRIMARY)
                .setMarginTop(4));

        nameCell.add(new Paragraph("Sistema de Detección de Violencia — Violentómetro")
                .setFontSize(9)
                .setFontColor(PdfStyles.MUTED)
                .setMarginTop(2));

        instTable.addCell(nameCell);
        doc.add(instTable);

        // ── 3. Línea divisoria con color ──────────────────────────
        doc.add(new Paragraph(" ").setMarginBottom(16));
        doc.add(new LineSeparator(new SolidLine(1.5f))
                .setStrokeColor(PdfStyles.ACCENT)
                .setMarginBottom(24));

        // ── 4. Título principal del reporte ───────────────────────
        doc.add(new Paragraph(reportTitle)
                .setBold()
                .setFontSize(26)
                .setFontColor(PdfStyles.PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setCharacterSpacing(1f)
                .setMarginBottom(6));

        doc.add(new Paragraph("Instituto Superior Tecnológico Riobamba — ISTR")
                .setFontSize(11)
                .setFontColor(PdfStyles.MUTED)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(32));

        // ── 5. Tabla de metadatos ─────────────────────────────────
        Table metaTable = new Table(new float[]{30, 70});
        metaTable.setWidth(UnitValue.createPercentValue(60));
        metaTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
        metaTable.setBorder(Border.NO_BORDER);

        addMetaRow(metaTable, "Período",       period);
        addMetaRow(metaTable, "Cuestionario",  surveyTitle);
        addMetaRow(metaTable, "Generado",
                LocalDateTime.now().format(DT_FORMATTER));
        addMetaRow(metaTable, "Clasificación", "CONFIDENCIAL");

        doc.add(metaTable);

        // ── 6. Espaciado flexible hasta el pie ────────────────────
        doc.add(new Paragraph("\n\n\n\n"));

        // ── 7. Línea decorativa final ─────────────────────────────
        doc.add(new LineSeparator(new SolidLine(0.5f))
                .setStrokeColor(PdfStyles.BORDER)
                .setMarginBottom(8));

        // ── 8. Pie de portada ─────────────────────────────────────
        Table footerTable = new Table(new float[]{50, 50});
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.setBorder(Border.NO_BORDER);

        Cell leftFoot = new Cell().setBorder(Border.NO_BORDER);
        leftFoot.add(new Paragraph("Violentómetro ISTR © " + LocalDateTime.now().getYear())
                .setFontSize(7)
                .setFontColor(PdfStyles.MUTED));

        Cell rightFoot = new Cell().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        rightFoot.add(new Paragraph("DOCUMENTO CONFIDENCIAL — Uso exclusivo institucional")
                .setFontSize(7)
                .setFontColor(PdfStyles.MUTED)
                .setBold());

        footerTable.addCell(leftFoot);
        footerTable.addCell(rightFoot);
        doc.add(footerTable);

        // ── 9. Banda inferior primary ─────────────────────────────
        Div bottomBand = new Div()
                .setBackgroundColor(PdfStyles.PRIMARY)
                .setHeight(10)
                .setWidth(UnitValue.createPercentValue(100));
        doc.add(bottomBand);
    }

    // ── Helper: fila de metadato (label | valor) ──────────────────
    private static void addMetaRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label)
                        .setBold()
                        .setFontSize(9)
                        .setFontColor(PdfStyles.MUTED))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(PdfStyles.BORDER, 0.3f))
                .setPaddingTop(5)
                .setPaddingBottom(5);

        Cell valueCell = new Cell()
                .add(new Paragraph(value != null ? value : "—")
                        .setFontSize(9)
                        .setFontColor(PdfStyles.TEXT))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(PdfStyles.BORDER, 0.3f))
                .setPaddingTop(5)
                .setPaddingBottom(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
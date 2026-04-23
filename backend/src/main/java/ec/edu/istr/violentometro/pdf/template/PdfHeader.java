package ec.edu.istr.violentometro.pdf.template;

import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfHeader {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");   // ✅ formato legible

    public static void draw(Document doc, String title, String subtitle) {

        // Barra superior accent
        doc.add(new Div()
                .setBackgroundColor(PdfStyles.ACCENT)
                .setHeight(5)
                .setWidth(UnitValue.createPercentValue(100)));

        Table table = new Table(new float[]{70, 30});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginTop(8);

        // Izquierda: título + subtítulo
        Cell left = new Cell().setBorder(Border.NO_BORDER);
        left.add(new Paragraph(title)
                .setBold()
                .setFontSize(PdfLayoutConfig.TITLE_SIZE)
                .setFontColor(PdfStyles.PRIMARY));
        left.add(new Paragraph(subtitle)
                .setFontSize(PdfLayoutConfig.SUBTITLE_SIZE)
                .setFontColor(PdfStyles.MUTED));
        table.addCell(left);

        // Derecha: sistema + fecha formateada ✅
        Cell right = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        right.add(new Paragraph("Violentómetro ISTR")
                .setBold()
                .setFontSize(9)
                .setFontColor(PdfStyles.PRIMARY));
        right.add(new Paragraph(LocalDateTime.now().format(FORMATTER))
                .setFontSize(8)
                .setFontColor(PdfStyles.MUTED));
        table.addCell(right);

        doc.add(table);

        doc.add(new LineSeparator(new SolidLine(0.5f))
                .setMarginTop(6)
                .setMarginBottom(10));
    }
}
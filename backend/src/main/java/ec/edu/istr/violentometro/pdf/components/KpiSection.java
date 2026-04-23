package ec.edu.istr.violentometro.pdf.components;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.kernel.colors.Color;

import ec.edu.istr.violentometro.pdf.template.PdfLayoutConfig;
import ec.edu.istr.violentometro.pdf.template.PdfStyles;

import java.util.List;

public class KpiSection {

    // 🧠 Modelo interno reutilizable
    public record Kpi(String label, String value, Color color) {
    }

    public static void draw(Document doc, List<Kpi> kpis) {

        if (kpis == null || kpis.isEmpty()) return;

        // Tabla de KPIs (cada KPI = 1 columna)
        Table table = new Table(kpis.size());
        table.setWidth(UnitValue.createPercentValue(100));


        for (Kpi kpi : kpis) {

            Cell cell = new Cell()
                    .setPadding(8)
                    .setBorder(new SolidBorder(PdfStyles.BORDER, 0.5f))
                    .setBorderRadius(new BorderRadius(6))
                    .setBackgroundColor(PdfStyles.BG_LIGHT)
                    .setBorderLeft(new SolidBorder(kpi.color(), 4));

            // 🔹 Valor grande
            cell.add(new Paragraph(kpi.value())
                    .setFontSize(18)
                    .setBold()
                    .setFontColor(kpi.color())
                    .setTextAlignment(TextAlignment.CENTER));

            // 🔹 Label pequeño
            cell.add(new Paragraph(kpi.label())
                    .setFontSize(PdfLayoutConfig.SMALL_TEXT)
                    .setFontColor(PdfStyles.MUTED)
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(cell);
        }

        doc.add(new Paragraph("RESUMEN EJECUTIVO")
                .setBold()
                .setFontColor(PdfStyles.PRIMARY)
                .setMarginBottom(5));

        doc.add(table);

        // Espacio después
        doc.add(new Paragraph(" ")
                .setMarginBottom(PdfLayoutConfig.SECTION_SPACING));
    }
}
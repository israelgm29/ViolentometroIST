package ec.edu.istr.violentometro.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.properties.AreaBreakType;

import ec.edu.istr.violentometro.pdf.PdfFactory.PdfFactoryResult;
import ec.edu.istr.violentometro.pdf.components.KpiSection;
import ec.edu.istr.violentometro.pdf.components.TableSection;
import ec.edu.istr.violentometro.pdf.template.PdfCoverPage;
import ec.edu.istr.violentometro.pdf.template.PdfFooter;
import ec.edu.istr.violentometro.pdf.template.PdfHeader;

import lombok.Getter;

import java.util.List;

public class PdfReportBuilder {

    @Getter
    private final com.itextpdf.layout.Document doc;
    private final PdfContext                   context;
    private final PdfFooter                    footer;

    public PdfReportBuilder(PdfFactoryResult result) {
        this.doc     = result.context().doc();
        this.context = result.context();
        this.footer  = result.footer();
    }

    /**
     * Portada en A4 vertical.
     * Agrega explícitamente una página A4 vertical como primera página,
     * luego salta a la segunda página que hereda A4 horizontal del Document.
     */
    public PdfReportBuilder cover(
            String reportTitle,
            String surveyTitle,
            String period,
            byte[] logoBytes) {

        // Insertar primera página en A4 vertical explícitamente
        context.pdf().addNewPage(1, PageSize.A4);

        // Dibujar portada en esa primera página
        PdfCoverPage.draw(doc, reportTitle, surveyTitle, period, logoBytes);

        // Saltar a la siguiente página — el Document usará su tamaño base (A4 horizontal)
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        return this;
    }

    public PdfReportBuilder header(String title, String subtitle) {
        PdfHeader.draw(doc, title, subtitle);
        return this;
    }

    public PdfReportBuilder kpis(List<KpiSection.Kpi> kpis) {
        KpiSection.draw(doc, kpis);
        return this;
    }

    public PdfReportBuilder table(List<String> headers, List<List<String>> rows) {
        TableSection.draw(doc, headers, rows);
        return this;
    }

    public PdfReportBuilder spacer() {
        doc.add(new com.itextpdf.layout.element.Paragraph(" "));
        return this;
    }

    /**
     * Salto de página — el Document ya tiene A4 horizontal como base,
     * por lo que todas las páginas nuevas serán horizontal automáticamente.
     */
    public PdfReportBuilder pageBreak() {
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        return this;
    }

    public byte[] build() {
        footer.writeTotal(context.pdf());
        doc.close();
        return context.out().toByteArray();
    }
}
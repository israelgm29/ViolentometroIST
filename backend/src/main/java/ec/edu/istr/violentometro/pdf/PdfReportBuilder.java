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

import java.util.List;

public class PdfReportBuilder {

    private final com.itextpdf.layout.Document doc;
    private final PdfContext                   context;
    private final PdfFooter                    footer;

    public PdfReportBuilder(PdfFactoryResult result) {
        this.doc     = result.context().doc();
        this.context = result.context();
        this.footer  = result.footer();
    }

    /**
     * Portada institucional completa.
     * Siempre debe ser la primera llamada en la cadena.
     */
    public PdfReportBuilder cover(
            String reportTitle,
            String surveyTitle,
            String period,
            byte[] logoBytes) {
        PdfCoverPage.draw(doc, reportTitle, surveyTitle, period, logoBytes);
        context.pdf().addNewPage(PageSize.A4.rotate());
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

    public PdfReportBuilder pageBreak() {
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        return this;
    }

    public byte[] build() {
        footer.writeTotal(context.pdf());   // ✅ ANTES de close()
        doc.close();
        return context.out().toByteArray();
    }
}
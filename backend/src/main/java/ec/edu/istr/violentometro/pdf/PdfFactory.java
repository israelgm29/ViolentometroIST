package ec.edu.istr.violentometro.pdf;

import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import ec.edu.istr.violentometro.pdf.template.PdfFooter;
import ec.edu.istr.violentometro.pdf.template.PdfWatermark;

import java.io.ByteArrayOutputStream;

public class PdfFactory {

    /**
     * Crea el contexto del PDF con footer y watermark registrados.
     *  Retorna PdfFactoryResult que incluye el footer para poder
     *    llamar writeTotal() antes de cerrar el documento.
     */
    public static PdfFactoryResult create(String institution) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter   writer = new PdfWriter(out);
        PdfDocument pdf    = new PdfDocument(writer);

        // Footer: guardamos la referencia para writeTotal() al final
        PdfFooter footer = new PdfFooter(institution);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, footer);

        // Watermark
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PdfWatermark("CONFIDENCIAL"));

        // Landscape A4
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(
                20,   // top
                20,   // right  — dejamos espacio para el footer
                35,   // bottom — ✅ más margen para que el footer no solape contenido
                20    // left
        );

        PdfContext context = new PdfContext(doc, pdf, out);
        return new PdfFactoryResult(context, footer);
    }

    /**
     * Resultado de la fábrica: contexto + footer con referencia al placeholder.
     */
    public record PdfFactoryResult(PdfContext context, PdfFooter footer) {}
}
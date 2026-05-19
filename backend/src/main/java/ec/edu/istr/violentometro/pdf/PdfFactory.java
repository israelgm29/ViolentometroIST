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

    public static PdfFactoryResult create(String institution) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter   writer = new PdfWriter(out);
        PdfDocument pdf    = new PdfDocument(writer);

        PdfFooter footer = new PdfFooter(institution);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, footer);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PdfWatermark("CONFIDENCIAL"));

        // ✅ Crear documento con A4 HORIZONTAL como tamaño base.
        // La portada agrega su propia página A4 vertical explícitamente
        // al inicio, antes de que el Document use su tamaño por defecto.
        Document doc = new Document(pdf, PageSize.A4.rotate());
        doc.setMargins(20, 20, 35, 20);

        PdfContext context = new PdfContext(doc, pdf, out);
        return new PdfFactoryResult(context, footer);
    }

    public record PdfFactoryResult(PdfContext context, PdfFooter footer) {}
}
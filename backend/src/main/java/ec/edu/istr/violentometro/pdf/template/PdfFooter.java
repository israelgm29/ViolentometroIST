package ec.edu.istr.violentometro.pdf.template;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

/**
 * Footer con número de página correcto usando el patrón "placeholder + second pass".
 *
 * ─ Durante la generación: escribe "Página N de " y deja un PdfFormXObject vacío
 *   como placeholder para el total.
 * ─ Al llamar a writeTotal() al final: rellena todos los placeholders con el
 *   número real de páginas.
 *
 * Uso:
 *   PdfFooter footer = new PdfFooter(institution);
 *   pdf.addEventHandler(PdfDocumentEvent.END_PAGE, footer);
 *   // ... generar contenido ...
 *   footer.writeTotal(pdf);   // ← llamar ANTES de doc.close()
 */
public class PdfFooter implements IEventHandler {

    private final String institution;

    // Placeholder XObject — se rellena en writeTotal()
    private PdfFormXObject totalPlaceholder;

    public PdfFooter(String institution) {
        this.institution = institution;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf  = docEvent.getDocument();
        PdfPage page     = docEvent.getPage();
        int pageNumber   = pdf.getPageNumber(page);

        // Inicializar placeholder solo una vez
        if (totalPlaceholder == null) {
            totalPlaceholder = new PdfFormXObject(
                    new com.itextpdf.kernel.geom.Rectangle(0, 0, 20, 10)
            );
        }

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        Canvas canvas = new Canvas(pdfCanvas, page.getPageSize());

        float y     = PdfLayoutConfig.MARGIN_BOTTOM - 8;
        float pageW = page.getPageSize().getWidth();

        // Izquierda: confidencialidad
        canvas.showTextAligned(
                new Paragraph("DOCUMENTO CONFIDENCIAL — Uso exclusivo institucional")
                        .setFontSize(PdfLayoutConfig.SMALL_TEXT)
                        .setFontColor(PdfStyles.MUTED),
                PdfLayoutConfig.MARGIN_LEFT, y,
                TextAlignment.LEFT
        );

        // Centro: institución
        canvas.showTextAligned(
                new Paragraph(institution)
                        .setFontSize(PdfLayoutConfig.SMALL_TEXT)
                        .setFontColor(PdfStyles.MUTED),
                pageW / 2, y,
                TextAlignment.CENTER
        );

        // Derecha: "Página N de [placeholder]"
        // ✅ Escribimos el número actual y luego el placeholder para el total
        float rightX = pageW - PdfLayoutConfig.MARGIN_RIGHT;

        canvas.showTextAligned(
                new Paragraph("Página " + pageNumber + " de ")
                        .setFontSize(PdfLayoutConfig.SMALL_TEXT)
                        .setFontColor(PdfStyles.TEXT),
                rightX - 22, y,
                TextAlignment.RIGHT
        );

        // Insertar el XObject placeholder donde irá el total
        pdfCanvas.addXObjectAt(totalPlaceholder, rightX - 20, y - 2);

        canvas.close();
    }

    /**
     * Rellena el placeholder con el total real de páginas.
     * Llamar ANTES de doc.close(), después de haber generado todo el contenido.
     */
    public void writeTotal(PdfDocument pdf) {
        if (totalPlaceholder == null) return;

        int total = pdf.getNumberOfPages();

        Canvas canvas = new Canvas(
                new PdfCanvas(totalPlaceholder, pdf),
                totalPlaceholder.getBBox().toRectangle()
        );

        canvas.showTextAligned(
                new Paragraph(String.valueOf(total))
                        .setFontSize(PdfLayoutConfig.SMALL_TEXT)
                        .setFontColor(PdfStyles.TEXT),
                0, 2,
                TextAlignment.LEFT
        );

        canvas.close();
    }
}
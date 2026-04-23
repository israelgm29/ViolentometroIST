package ec.edu.istr.violentometro.pdf.template;

import com.itextpdf.kernel.events.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.DeviceRgb;

public class PdfWatermark implements IEventHandler {

    private final String text;

    public PdfWatermark(String text) {
        this.text = text;
    }

    @Override
    public void handleEvent(Event event) {

        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();

        PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), docEvent.getDocument());

        Canvas modelCanvas = new Canvas(canvas, page.getPageSize());

        Paragraph p = new Paragraph(text)
                .setFontSize(60)
                .setFontColor(new DeviceRgb(200, 200, 200))
                .setBold();

        modelCanvas.showTextAligned(
                p,
                page.getPageSize().getWidth() / 2,
                page.getPageSize().getHeight() / 2,
                docEvent.getDocument().getPageNumber(page),
                TextAlignment.CENTER,
                com.itextpdf.layout.properties.VerticalAlignment.MIDDLE,
                (float) Math.toRadians(45)
        );

        modelCanvas.close();
    }
}
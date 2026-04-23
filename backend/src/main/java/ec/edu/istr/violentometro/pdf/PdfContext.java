package ec.edu.istr.violentometro.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;

import java.io.ByteArrayOutputStream;

public record PdfContext(
        Document doc,
        PdfDocument pdf,
        ByteArrayOutputStream out
) {}
package ec.edu.istr.violentometro.pdf.template;

public class PdfLayoutConfig {

    public final float marginTop;
    public final float marginBottom;
    // 📏 Márgenes (A4 en puntos)
    public static final float MARGIN_TOP = 20;
    public static final float MARGIN_BOTTOM = 20;
    public static final float MARGIN_LEFT = 20;
    public static final float MARGIN_RIGHT = 20;

    // 📐 Espaciados
    public static final float SECTION_SPACING = 12;
    public static final float ELEMENT_SPACING = 6;

    // 📊 Tamaños
    public static final float TITLE_SIZE = 16;
    public static final float SUBTITLE_SIZE = 10;
    public static final float TEXT_SIZE = 9;
    public static final float SMALL_TEXT = 8;


    public PdfLayoutConfig(float marginTop, float marginBottom) {
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    public static PdfLayoutConfig defaultConfig() {
        return new PdfLayoutConfig(20, 20);
    }
}
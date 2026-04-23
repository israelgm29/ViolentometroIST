package ec.edu.istr.violentometro.pdf.template;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;

public class PdfStyles {

    // 🎨 COLORES CORPORATIVOS
    public static final Color PRIMARY   = new DeviceRgb(45, 55, 72);
    public static final Color ACCENT    = new DeviceRgb(109, 40, 217);

    public static final Color CRITICAL  = new DeviceRgb(220, 38, 38);
    public static final Color HIGH      = new DeviceRgb(234, 88, 12);
    public static final Color MODERATE  = new DeviceRgb(202, 138, 4);

    public static final Color TEXT      = new DeviceRgb(15, 23, 42);
    public static final Color MUTED     = new DeviceRgb(100, 116, 139);
    public static final Color BORDER    = new DeviceRgb(226, 232, 240);

    public static final Color BG_LIGHT  = new DeviceRgb(248, 250, 252);

    private PdfStyles() {
        // evitar instancias
    }

    public static Color getRiskColor(String level) {
        return switch (level) {
            case "CRÍTICO" -> CRITICAL;
            case "ALTO" -> HIGH;
            case "MODERADO" -> MODERATE;
            default -> MUTED;
        };
    }

    public static Color getRiskBg(String level) {
        return switch (level) {
            case "CRÍTICO" -> new DeviceRgb(254, 242, 242);
            case "ALTO" -> new DeviceRgb(255, 247, 237);
            case "MODERADO" -> new DeviceRgb(254, 252, 232);
            default -> BG_LIGHT;
        };
    }
}

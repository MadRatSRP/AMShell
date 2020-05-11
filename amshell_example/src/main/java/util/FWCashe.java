package util;



import com.silentknight.amshell.javax.microedition.lcdui.Font;
import java.util.Vector;

/**
 * Класс кэша ширины букв шрифта
 */
public class FWCashe {

    /**
     * Получить кэш
     * @param font шрифт, из которого получаем кэш ширины букв
     * @return экземпляр класса
     */
    public static FWCashe getCache(Font font) {
        Vector fwc = new Vector();
        Vector fwc_f = new Vector();
        for (int i = 0; i < fwc.size(); i++) {
            if (fwc_f.elementAt(i).equals(font)) {
                return (FWCashe) fwc.elementAt(i);
            }
        }
        fwc_f.addElement(font);
        FWCashe f = new FWCashe(font);
        fwc.addElement(f);
        return f;
    }
    
    protected Font font;
    protected byte[][] caches;

    protected FWCashe(Font font) {
        this.font = font;
        caches = new byte[256][];
        for (int i = 0; i < 256; i++) {
            caches[i] = null;
        }
    }

    public int charWidth(char ch) {
        int hi = (ch >> 8) & 0xFF;
        int lo = (ch) & 0xFF;
        if (caches[hi] == null) {
            caches[hi] = new byte[256];
            for (int i = 0; i < 256; i++) {
                caches[hi][i] = -1;
            }
        }
        if (caches[hi][lo] == -1) {
            caches[hi][lo] = (byte) font.charWidth(ch);
        }
        return caches[hi][lo];
    }

    public int stringWidth(String s) {
        int width = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            width += charWidth(s.charAt(i));
        }
        return width;
    }
}

package ui;

import main.P;
import java.util.Calendar;
import javax.microedition.lcdui.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aNNiMON
 */
public class UI {

    /** Шрифт заголовков и софт-бара */
    private static final Font smallFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    /** Высота софт-бара */
    private static final int softBarHeight = smallFont.getHeight() + 2;

    /**
     * Отрисовка софт-бара
     * @param g контекст графики
     * @param left надпись на левом софте
     * @param right надпись на правом софте
     */
    public static void drawSoftBar(Graphics g, String left, String right) {
        final int w = g.getClipWidth();
        final int h = g.getClipHeight() - 1;
        final int w2 = w / 2;
        drawHGradient(g, P.coldn, P.colup, 0, h - softBarHeight - 1, w2, softBarHeight + 2);
        drawHGradient(g, P.colup, P.coldn, w2, h - softBarHeight - 1, w2, softBarHeight + 2);
        g.setColor(0);
        g.drawLine(0, h - softBarHeight - 2, w, h - softBarHeight - 2);
        g.setFont(smallFont);
        g.setColor(0xFFFFFF);
        g.drawString(left, 3, h, Graphics.LEFT | Graphics.BOTTOM);
        g.drawString(right, w - 3, h, Graphics.RIGHT | Graphics.BOTTOM);
        g.setColor(P.backgrnd);
        g.drawString(getTime(), w2, h, Graphics.HCENTER | Graphics.BOTTOM);
    }

    /**
     * Отрисовка заголовка
     * @param g контекст графики
     * @param title надпись заголовка
     */
    public static void drawTitle(Graphics g, String title) {
        final int w = g.getClipWidth();
        drawVGradient(g, P.colup, P.coldn, 0, 0, w, softBarHeight);
        g.setColor(P.colup);
        g.drawLine(0, softBarHeight, w, softBarHeight);
        g.setFont(smallFont);
        g.setColor(0xFFFFFF);
        g.drawString(title, w / 2, softBarHeight, Graphics.HCENTER | Graphics.BOTTOM);
    }

    /**
     * Отрисовка прогресса
     * @param g контекст графики
     * @param cur текущее состояние
     * @param all всего
     * @param str комментарий
     */
    public static void drawProgressBar(Graphics g, int cur, int all, String str) {
        int w = g.getClipWidth();
        int h = g.getClipHeight();
        final int w2 = w / 2;
        if (all != 0) {
            final int barWidth = (cur * w) / all;
            final int barY = h / 2 + h / 16;
            drawRect(g, P.colup, P.coldn, 0, barY, barWidth, h / 16);
            String value = String.valueOf((cur * 100) / all) + "%";
            g.setColor(P.fmtextnc);
            g.drawString(value, w2, barY, Graphics.TOP | Graphics.HCENTER);
            g.setColor(P.fmtextcur);
            g.drawString(value, w2 + 1, barY + 1, Graphics.TOP | Graphics.HCENTER);
        }
        if (str != null || str.length() != 0) {
            g.setColor(P.fmtextcur);
            g.drawString(str, w2 + 1, h / 2 + 1, Graphics.BOTTOM | Graphics.HCENTER);
        }
    }

    /**
     * Получить высоту шрифта. 
     * @return высота soft- и title- бара
     */
    public static int getSoftBarHeight() {
        return softBarHeight;
    }
    
    /**
     * Получить время 
     * @return строка с временем вида: HH:MM
     */
    private static String getTime()    {
        StringBuffer sb = new StringBuffer();
        Calendar cal = Calendar.getInstance();
        int tmp = cal.get(Calendar.HOUR_OF_DAY);
        if(tmp <= 9) sb.append('0');
        sb.append(tmp);
        sb.append(':');
        //Минута
        tmp = cal.get(Calendar.MINUTE);
        if(tmp <= 9) sb.append('0');
        sb.append(tmp);
        return sb.toString();
    }

    /**
     * Ортисовка непрозрачного градиента
     * @param g контекст графики
     * @param color1 верхний цвет градиента
     * @param color2 нижний цвет градиента
     * @param x1 X
     * @param y1 Y
     * @param w ширина
     * @param h высота
     */
    public static void drawRect(Graphics g, int color1, int color2, int x1, int y1, int w, int h) {
        // Если цвета одинаковы, то градиент нам не нужен
        if(color1 == color2) {
            g.setColor(color1);
            g.fillRect(x1, y1, w, h);
            return;
        } else {
            drawVGradient(g, color1, color2, x1, y1, w, h);
        }
    }

    /**
     * Отрисовка вертикального градиента
     * @param graphics контекст графики
     * @param rgb1 верхний цвет градиента
     * @param rgb2 нижний цвет градиента
     * @param xn X
     * @param yn Y
     * @param wdh ширина
     * @param hgt высота
     */
    private static void drawVGradient(Graphics graphics, int rgb1, int rgb2, int xn, int yn, int wdh, int hgt) {
        // Первый цвет
        int r1 = acolor(rgb1, 'r');
        int g1 = acolor(rgb1, 'g');
        int b1 = acolor(rgb1, 'b');
        // Разница между вторым и первым цветом
        int deltaR = acolor(rgb2, 'r') - r1;
        int deltaG = acolor(rgb2, 'g') - g1;
        int deltaB = acolor(rgb2, 'b') - b1;
        try {
            wdh += xn;
            hgt += yn;
            final int deltaHeight = hgt - yn - 1;
            for (int i = yn; i < hgt; i++) {
                int r = r1 + ((i - hgt + 1) * deltaR) / deltaHeight + deltaR;
                int g = g1 + ((i - hgt + 1) * deltaG) / deltaHeight + deltaG;
                int b = b1 + ((i - hgt + 1) * deltaB) / deltaHeight + deltaB;
                graphics.setColor(r, g, b);
                graphics.drawLine(xn, i, wdh - 1, i);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Отрисовка горизонтального градиента
     * @param graphics контекст графики
     * @param rgb1 верхний цвет градиента
     * @param rgb2 нижний цвет градиента
     * @param xn X
     * @param yn Y
     * @param wdh ширина
     * @param hgt высота
     */
    private static void drawHGradient(Graphics graphics, int rgb1, int rgb2, int xn, int yn, int wdh, int hgt) {
        int r, g, b;
        r = g = b = 0;
        // Первый цвет
        int r1 = acolor(rgb1, 'r');
        int g1 = acolor(rgb1, 'g');
        int b1 = acolor(rgb1, 'b');
        // Разница между вторым и первым цветом
        int deltaR = acolor(rgb2, 'r') - r1;
        int deltaG = acolor(rgb2, 'g') - g1;
        int deltaB = acolor(rgb2, 'b') - b1;
        try {
            wdh += xn;
            hgt += yn;
            final int deltaWidth = wdh - xn - 1;
            for (int i = xn; i < wdh; i++) {
                r = r1 + ((i - wdh + 1) * deltaR) / deltaWidth + deltaR;
                g = g1 + ((i - wdh + 1) * deltaG) / deltaWidth + deltaG;
                b = b1 + ((i - wdh + 1) * deltaB) / deltaWidth + deltaB;
                graphics.setColor(r, g, b);
                graphics.drawLine(i, yn, i, hgt - 1);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Получить цвет нужного канала в пикселе
     * @param argb пиксель
     * @param c символьный параметр - название канала (a, r, g, b)
     * @return 
     */
    private static int acolor(int argb, char c) {
        if(c=='r') return ((argb >> 16) & 0xff);
        else if(c=='g') return ((argb >> 8) & 0xff);
        else if(c=='b') return (argb & 0xff);
        else if(c=='a') return ((argb >> 24) & 0xff);
        else return argb;
    }
}

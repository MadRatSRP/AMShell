package midedit;

import javax.microedition.lcdui.*;
import java.io.*;

/**
 *
 * @author user
 */
public class PrintSmallFont {

    private static int[] nMax = {100000, 1000, 100, 10, 1, 0};
    private static int[] denom = {1, 10, 100, 1000, 10000, 100000};
    
    private static Image[][] digImages;
    private static Image[][] notesImages;
    
    private static final int digLen = 4;
    
    private static final int noteLen = 11;
    private static final int noteLenY = 5;

    private static final int NUM_CHAR = 12;

    private static final int NUM_COLOR = 2;

    private static final int NUM_NOTES = 12;

    private static final int NUM_NOTES_LINE = 6;
    
    
    /**
     *
     * @param g
     * @param num
     * @param dot
     * @param x
     * @param y
     * @param color
     * @return
     */
    public static int print(Graphics g, int num, int dot, int x, int y, int color) {
        int xLen = 0;
        int nt = Math.abs(num);
        int dig;
        Image[] imgs = digImages[color];

        while (dot > 0 && nt / denom[dot] >= nMax[dot]) {
            dot--;
            nt /= 10;
        }

        if (dot > 0) {
            for (int i = 0; i < dot; ++i, nt /= 10, xLen += digLen) {
                dig = nt % 10;
                g.drawImage(imgs[dig], x - xLen, y, Graphics.RIGHT | Graphics.BOTTOM);
            }

            g.drawImage(imgs[10], x - xLen, y, Graphics.RIGHT | Graphics.BOTTOM);
            xLen += digLen;
        }
        do {
            dig = nt % 10;
            g.drawImage(imgs[dig], x - xLen, y, Graphics.RIGHT | Graphics.BOTTOM);
            nt /= 10;
            xLen += digLen;
        } while (nt != 0);

        if (num < 0) {
            dig = 11;
            g.drawImage(imgs[dig], x - xLen, y, Graphics.RIGHT | Graphics.BOTTOM);
            xLen += digLen;
        }

        return xLen;
    }

    /**
     *
     * @param g
     * @param n
     * @param x
     * @param y
     * @param color
     * @return
     */
    public static int printNote(Graphics g, int n, int x, int y, int color) {
        g.drawImage(notesImages[color][n], x, y, Graphics.RIGHT | Graphics.BOTTOM);

        return noteLen;
    }
    
    

    static {
        try {
            Image numbers0 = Image.createImage("/img/nums0m.png");
            Image numbers1 = Image.createImage("/img/nums1m.png");
            Image notes0 = Image.createImage("/img/notes0.png");
            Image notes1 = Image.createImage("/img/notes1.png");
            digImages = new Image[NUM_COLOR][];
            digImages[0] = new Image[NUM_CHAR];
            digImages[1] = new Image[NUM_CHAR];

            notesImages = new Image[NUM_COLOR][];
            notesImages[0] = new Image[NUM_NOTES];
            notesImages[1] = new Image[NUM_NOTES];


            int len = 0;
            for (int i = 0; i < NUM_CHAR; ++i, len += digLen) {
                digImages[0][i] = Image.createImage(4, 5);
                digImages[0][i].getGraphics().
                        drawImage(numbers0, -len, 0, Graphics.LEFT | Graphics.TOP);
            }

            len = 0;
            for (int i = 0; i < NUM_CHAR; ++i, len += digLen) {
                digImages[1][i] = Image.createImage(4, 5);
                digImages[1][i].getGraphics().
                        drawImage(numbers1, -len, 0, Graphics.LEFT | Graphics.TOP);
            }

            len = 0;
            int lenY = 0;
            for (int i = 0; i < NUM_NOTES;) {
                notesImages[0][i] = Image.createImage(noteLen, noteLenY);
                notesImages[0][i].getGraphics().
                        drawImage(notes0, -len, -lenY, Graphics.LEFT | Graphics.TOP);
                len += noteLen;
                if (++i == NUM_NOTES_LINE) {
                    lenY = noteLenY;
                    len = 0;
                }
            }

            len = 0;
            lenY = 0;
            for (int i = 0; i < NUM_NOTES;) {
                notesImages[1][i] = Image.createImage(noteLen, noteLenY);
                notesImages[1][i].getGraphics().
                        drawImage(notes1, -len, -lenY, Graphics.LEFT | Graphics.TOP);
                len += noteLen;
                if (++i == NUM_NOTES_LINE) {
                    lenY = noteLenY;
                    len = 0;
                }

            }

        } catch (IOException ex) {
        }
    }
}

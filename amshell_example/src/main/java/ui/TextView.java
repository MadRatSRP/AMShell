package ui;

/*
 * aNNiMON 2011
 * For more info visit http://annimon.com/
 */

/*
 * пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ MultiLineText пїЅпїЅ пїЅ.пїЅ. пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
 * www.mobilab.ru
 */

import util.StringEncoder;
import main.P;
import main.Main;
import main.Rms;
import main.Key;
import main.L;
import java.io.DataInputStream;
import java.util.Vector;
import com.silentknight.amshell.javax.microedition.lcdui.*;

public class TextView extends Canvas {

    private Font defaultFont;
    private Displayable dspl;
    private int fontHeight, defaultFontHeight;
    private String text; //пїЅпїЅпїЅпїЅпїЅ
    private String title; //пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
    
    private int w, h; //пїЅпїЅпїЅпїЅпїЅпїЅпїЅ

    private int workingHeight;    //пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ;
    private int yBase;         //пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
    private int scrollStep;         //пїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
    private int allTextHeight; //пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
    private Vector strLines;

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private TextView() {
        setFullScreenMode(true);
        w = getWidth();
        h = getHeight();
        fontHeight = UI.getSoftBarHeight()+2;
        defaultFont = Font.getDefaultFont();
        scrollStep = defaultFontHeight = defaultFont.getHeight()+2;
        workingHeight = h - fontHeight*2 - 2;
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     * @param dspl пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     * @param textFile пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     * @param multilang пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅ (пїЅпїЅпїЅпїЅпїЅ. пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ _en, _ru)
     * @param title пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ 
     */
    public TextView(Displayable dspl, String textFile, boolean multilang, String title) {
        this();
        this.title = title;
        this.dspl = dspl;
        String path = "/lang/"+textFile;
        if(multilang) path = path + "_"+Rms.languageApp;
        text = getText(path);
        setParameters();
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param text пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     * @param title пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param dspl пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     */
    public TextView(String text, String title, Displayable dspl) {
        this();
        this.title = title;
        this.dspl = dspl;
        this.text = text;
        setParameters();
    }

    public void sizeChanged(int w, int h) {
        this.w = w;
        this.h = h;
        workingHeight = h - fontHeight*2 - 2;
        setParameters();
    }


    public void paint(Graphics g) {
        g.setColor(P.backgrnd);
        g.fillRect(0, 0, w, h);
        g.drawImage(P.bgKey, w, h - UI.getSoftBarHeight() - 2, Graphics.RIGHT | Graphics.BOTTOM);
        UI.drawTitle(g, title);
        g.setColor(P.fmtextnc);
        g.setFont(defaultFont);
        g.setClip(0, fontHeight+1, w, workingHeight);
        int y1 = yBase;
        for (int i = 0; i < strLines.size(); i++) {
            if ((y1 + defaultFontHeight) > 0) {
                g.drawString(strLines.elementAt(i).toString(), 1, fontHeight + 5 + y1, Graphics.LEFT | Graphics.TOP);
            }
            y1 = y1 + defaultFontHeight;
            if (y1 > workingHeight) {
                break;
            }
        }
        g.setClip(0, 0, w, h);
        UI.drawSoftBar(g, "", L.str[L.back]);
    }
    
    public void keyPressed(int key) {
        int ga = getGameAction(key);
        if(ga==UP) MoveUp();
        else if(ga==DOWN) MoveDown();
        else if(ga==LEFT) PageUp();
        else if(ga==RIGHT) PageDown();
        else if(ga==FIRE || key==Key.rightSoftKey) Main.dsp.setCurrent(dspl);
        else if(key==Key.leftSoftKey) {
            System.gc();
        }
        repaint();
    }
    
    public void keyRepeated(int key) {
        keyPressed(key);
    }
    
    public void pointerPressed(int pix, int piy) {
        int q = UI.getSoftBarHeight();
        if(pix>w-2*q && piy>h-q) Main.dsp.setCurrent(dspl);
        repaint();
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private void setParameters() {
        //пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
        strLines = new Vector();
        String[] arr = splitString(text, "\n");
        for(int i = 0; i<arr.length; i++) {
            String substr = arr[i];
            int i0 = 0, space = 0, in = 0, j = 0, jw = 0;   //пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
            int imax = substr.length();   //пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
            boolean isexit = true;
            yBase = 0;
            while (isexit) {
                space = substr.indexOf(" ", i0 + 1);
                if (space <= i0) {
                    space = imax;
                    isexit = false;
                }

                j = defaultFont.stringWidth(substr.substring(i0, space));
                if (jw + j < w) {//пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
                    jw = jw + j;
                    i0 = space;
                } else {//пїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
                    strLines.addElement(substr.substring(in, i0));
                    in = i0 + 1;
                    jw = j;
                    if (j > w) {//пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
                        space = i0;
                        while (jw > w) {
                            j = 0;
                            while (j < w) {
                                space++;
                                j = defaultFont.stringWidth(substr.substring(in, space));
                            }
                            space = space - 1;
                            j = defaultFont.stringWidth(substr.substring(in, space));
                            strLines.addElement(substr.substring(in, space));
                            jw = jw - j;
                            i0 = space;
                            in = space;
                        }
                        jw = 0;
                    } else {
                        i0 = space;
                    }
                }
            }
            strLines.addElement(substr.substring(in, imax));
        }
        allTextHeight = strLines.size() * defaultFontHeight;
    }

    private void MoveDown() {
        if (allTextHeight > workingHeight) {
            yBase = yBase - scrollStep;
            if (workingHeight - yBase > allTextHeight) {
                yBase = workingHeight - allTextHeight;
            }
        }
    }

    private void MoveUp() {
        if (allTextHeight > workingHeight) {
            yBase = yBase + scrollStep;
            if (yBase > 0) {
                yBase = 0;
            }
        }

    }

    private void PageUp() {
        if (allTextHeight > workingHeight) {
            yBase = yBase + workingHeight;
            if (yBase > 0) {
                yBase = 0;
            }
        }

    }

    private void PageDown() {
        if (allTextHeight > workingHeight) {
            yBase = yBase - workingHeight;
            if (workingHeight - yBase > allTextHeight) {
                yBase = workingHeight - allTextHeight;
            }
        }
    }

    private String[] splitString(String str, String delim) {
        if (str.length() == 0 || delim.length() == 0) return new String[]{str};

        Vector v = new Vector();
        int pos = 0;
        int newpos = str.indexOf(delim, 0);

        while (newpos != -1) {
            v.addElement(str.substring(pos, newpos));
            pos = newpos + delim.length();
            newpos = str.indexOf(delim, pos);
        }
        v.addElement(str.substring(pos));

        String[] s = new String[v.size()];
        v.copyInto(s);
        return s;
    }

    private String getText(String path) {
        DataInputStream dis = new DataInputStream(getClass().getResourceAsStream(path));
        StringBuffer strBuff = new StringBuffer();
        int ch = 0;
        try {
            while ((ch = dis.read()) != -1) {
                strBuff.append(StringEncoder.decodeCharCP1251((byte)ch));
            }
            dis.close();
        } catch (Exception e) {e.printStackTrace();}
        return strBuff.toString();
    }
}

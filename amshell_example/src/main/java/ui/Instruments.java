package ui;

import main.P;
import main.Main;
import main.Key;
import main.L;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import midedit.Constants;

/**
 * пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
 * @author aNNiMON
 */
public class Instruments extends Canvas {
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ.
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ/пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private static final int GROUPS_OF_INSTRUMENTS = 16;
    
    /** 
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ.
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ/пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private static final int INSTRUMENTS_IN_GROUP = 8;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private static final byte MENU_MODE = 0;
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private int w, h;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private String title;
    
    /** пїЅпїЅпїЅпїЅ */
    private String[] menu;
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ */
    private int cu = 0;
    
    /** пїЅпїЅпїЅпїЅпїЅ (пїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ) */
    private byte mode;
    
    private int iconsSize = 2;
    
    private int FileY, CursorY, stFh, startPrintFile;
    private int FILE_HEIGHT;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ double-пїЅпїЅпїЅпїЅпїЅ пїЅ touchscreen */
    private long time;
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ mode
     */
    public Instruments() {
        setFullScreenMode(true);
        w = getWidth();
        h = getHeight();
        
        mode = MENU_MODE;
        
        FILE_HEIGHT = P.medPlain.getHeight() + 2;
        stFh = P.medPlain.getHeight() + P.medPlain.getHeight() / 2;
        cu = CursorY = FileY = startPrintFile = 0;
        updateItems();
    }
    
    private void updateItems() {
        cu = CursorY = FileY = startPrintFile = 0;
        if(mode == MENU_MODE) {
            title = L.str[L.instruments];
            menu = new String[GROUPS_OF_INSTRUMENTS + 1];
            for (int i = 0; i < menu.length; i++) {
                menu[i] = Constants.getInstrName(129 + i);
            }
            // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
            menu[menu.length - 1] = Constants.getInstrName(0);
        } else {
            menu = new String[INSTRUMENTS_IN_GROUP];
            System.arraycopy(L.instr, (mode-1)*INSTRUMENTS_IN_GROUP+1, menu, 0, INSTRUMENTS_IN_GROUP);
        }
        repaint();
    }


    public void sizeChanged(int w, int h) {
        this.w = getWidth();
        this.h = getHeight();
        super.sizeChanged(w, h);
    }
    
    public void paint(Graphics g) {
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
        g.setColor(P.backgrnd);
        g.fillRect(0, 0, w, h);
        
        g.drawImage(P.bgKey, w, h - UI.getSoftBarHeight() - 2, Graphics.RIGHT | Graphics.BOTTOM);
        
        UI.drawTitle(g, title);
        if(mode == MENU_MODE) {
            UI.drawSoftBar(g, "", L.str[L.cancel]);
        } else {
            UI.drawSoftBar(g, L.str[L.play], L.str[L.back]);
        }

        g.setFont(P.medBold);
        
        g.setColor(P.fmback1);
        g.translate(0, stFh);
        // пїЅпїЅпїЅпїЅпїЅпїЅ
        g.setColor(P.obv);
        g.fillRect(-1, CursorY, w, FILE_HEIGHT-1);
        g.setColor(P.fmbord);
        g.drawRect(-1, CursorY-1, w, FILE_HEIGHT);
        
        FileY = 0;
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
        for (int i = startPrintFile; i < menu.length; i++) {
            g.setColor(P.fmtextnc);
            final String s = menu[i];
            if (FileY == CursorY) {
                g.setColor(P.fmbord);
                g.drawString(s, iconsSize + 4, FileY + iconsSize / 4, Graphics.TOP | Graphics.LEFT);
                g.setColor(P.fmtextcur);
            }

            // g.drawImage(icons[id], iconsSize + 2, FileY + 1, 24);
            
            // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
            g.drawString(s, iconsSize + 3, FileY + iconsSize / 4, Graphics.TOP | Graphics.LEFT);
            FileY += FILE_HEIGHT;
            if(FileY > (h - (stFh*2) - FILE_HEIGHT)) break;
        }
        g.setColor(0x00);
        g.fillRect(-1, FileY+1, w+1, 2);
        g.fillRect(-1, -2, w+1, 2);
        g.translate(0, -stFh);
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param play пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param selected ID пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     */
    private void selectItem(boolean play, int selected) {
        final String v = menu[selected];
        if(mode == MENU_MODE) {
            if(selected == menu.length - 1) {
                // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
                Main.midlet.compositionForm.selectInstrument(false, v);
            } else {
                mode = (byte) (selected + 1);
                title = v;
                updateItems();
            }
        } else {
            Main.midlet.compositionForm.selectInstrument(play, v);
        }
    }

    public void keyPressed (int key) {
        int ga = getGameAction(key);
        
        if(ga==UP || ga==LEFT) {cursorUp();}
        else if(ga==DOWN || ga==RIGHT) {cursorDown();}
        else if (ga==FIRE) selectItem(false, cu);
        else if (key==Key.leftSoftKey) selectItem(true, cu);
        else if (key==Key.rightSoftKey) rightSoft();
        repaint();
    }
    
    public void keyRepeated (int key) {
        keyPressed(key);
    }

    private void rightSoft() {
        if(mode == MENU_MODE) {
            Main.dsp.setCurrent(Main.midlet.compositionForm);
        } else {
            mode = MENU_MODE;
        }
        updateItems();
    }
    
    private void cursorDown() {
        if (menu.length > 0) {
            final int maxHeight = h - 2 * stFh;
            if (cu == menu.length - 1) {
                CursorY = 0;
                cu = 0;
                startPrintFile = 0;
            } else if (cu > (maxHeight / FILE_HEIGHT - 3) ) {
                if (CursorY >= (maxHeight - 2 * FILE_HEIGHT)) {
                    startPrintFile++;
                    cu++;
                } else {
                    cu++;
                    CursorY += FILE_HEIGHT;
                }
            } else {
                CursorY += FILE_HEIGHT;
                cu++;
            }
        }
    }

    private void cursorUp() {
        if (menu.length > 0) {
            if (cu == 0) {
                cu = menu.length - 1;
                final int maxHeight = h - 2 * stFh;
                if (menu.length > ( (maxHeight - FILE_HEIGHT) / FILE_HEIGHT)) {
                    startPrintFile = (menu.length - ( maxHeight / FILE_HEIGHT));
                    CursorY = (cu - startPrintFile) * FILE_HEIGHT;
                } else {
                    CursorY = cu * FILE_HEIGHT;
                }
            } else if ((CursorY == 0) && (cu > 0)) {
                startPrintFile--;
                cu--;
            } else if (CursorY > 0) {
                CursorY -= FILE_HEIGHT;
                cu--;
            }
        }
    }
    
    public void pointerPressed(int pix, int piy) {
        int q = UI.getSoftBarHeight();
        if(pix<2*q && piy>h-q) selectItem(true, cu);
        else if(pix>w-2*q && piy>h-q) rightSoft();
        else {
            int cu1 = cu;
            piy -= 2*P.medPlain.getHeight();
            if(piy>0 && piy<menu.length*FILE_HEIGHT) {
                cu = piy/FILE_HEIGHT;
                CursorY = cu * FILE_HEIGHT;
            }
            if(System.currentTimeMillis()-time<700 && cu1==cu) selectItem(false, cu);
            time = System.currentTimeMillis();
        }
        repaint();
    }

}

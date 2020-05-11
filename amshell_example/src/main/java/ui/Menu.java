package ui;

import main.P;
import main.Main;
import main.Key;
import main.L;
import java.io.IOException;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import midedit.CompositionForm;
import midedit.MixerModel;

/**
 * пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
 * @author aNNiMON
 */
public class Menu extends Canvas {
    
    /** пїЅпїЅпїЅ-пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ */
    private static final int MAX_MENU_ICONS = 9;
    /** ID пїЅпїЅпїЅпїЅпїЅпїЅ */
    private static final int CREATE_NEW = 0;
    private static final int RESUME = 1;
    private static final int OPEN = 2;
    private static final int SAVE = 3;
    private static final int SAVE_AS = 4;
    private static final int RMS_FS = 5;
    private static final int OPTIONS = 6;
    private static final int ABOUT = 7;
    private static final int EXIT = 8;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private int w, h;
    
    /** пїЅпїЅпїЅпїЅ */
    private String[] menu;
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ */
    private int cu = 0;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ */
    private Image[] icons;
    
    private final int iconsSize;
    
    private int FileY, CursorY, stFh, startPrintFile;
    private int FILE_HEIGHT;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ double-пїЅпїЅпїЅпїЅпїЅ пїЅ touchscreen */
    private long time;
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ mode
     */
    public Menu() {
        setFullScreenMode(true);
        w = getWidth();
        h = getHeight();
        readImages();
        iconsSize = icons[0].getHeight();
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
        menu = new String[] {
            L.str[L.create],
            L.str[L.open],
            (P.isRMSMode ? L.str[L.RMS] : L.str[L.file]),
            L.str[L.options],
            L.str[L.help],
            L.str[L.about],
            L.str[L.exit]
        };
        FILE_HEIGHT = iconsSize + 2;//P.medPlain.getHeight() + 2;
        stFh = P.medPlain.getHeight() + P.medPlain.getHeight() / 2;
        CursorY = FileY = startPrintFile = 0;
    }
    
    public void addNewItems() {
        menu = new String[] {
            L.str[L.resume],
            L.str[L.create],            
            L.str[L.open],
            L.str[L.save],
            L.str[L.saveAs],
            (P.isRMSMode ? L.str[L.RMS] : L.str[L.file]),
            L.str[L.options],
            L.str[L.help],
            L.str[L.about],
            L.str[L.exit]
        };
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
        
        String title = L.str[L.midedit] + ". "+Key.Platform;
        UI.drawTitle(g, title);
        UI.drawSoftBar(g, L.str[L.ok], L.str[L.exit]);

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
            
            // пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
            int id = 0;
            if(s.equals(L.str[L.create])) id = CREATE_NEW;
            else if(s.equals(L.str[L.resume])) id = RESUME;
            else if(s.equals(L.str[L.open])) id = OPEN;
            else if(s.equals(L.str[L.save])) id = SAVE;
            else if(s.equals(L.str[L.saveAs])) id = SAVE_AS;
            else if(s.equals(L.str[L.RMS]) || s.equals(L.str[L.file])) id = RMS_FS;
            else if(s.equals(L.str[L.options])) id = OPTIONS;
            else if(s.equals(L.str[L.about]) || s.equals(L.str[L.help])) id = ABOUT;
            else if(s.equals(L.str[L.exit])) id = EXIT;
            
            g.drawImage(icons[id], iconsSize + 2, FileY + 1, 24);
            
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
     * @param selected ID пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     */
    private void selectItem(int selected) { 
        String v = menu[selected];
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        if(v.equals(L.str[L.create])) {
            try {
                Main.midlet.model.newMix();
                if (Main.midlet.compositionForm != null) {
                    Main.midlet.compositionForm.releaseMem();
                }
                Main.midlet.compositionForm = null;
                System.gc();
                P.openSaveString = "newmix";
                P.isRMSMode = true;
                Main.midlet.compositionForm = new CompositionForm(Main.midlet);
                Main.dsp.setCurrent(Main.midlet.compositionForm);
                new Thread(Main.midlet.compositionForm).start();
            } catch (Exception e) {
                e.printStackTrace();
                Alert a = new Alert(L.str[L.error], e.getMessage(), null, null);
                Main.dsp.setCurrent(a);
            }
        }
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        else if(v.equals(L.str[L.resume])) {
            P.isRMSMode = true;
            Main.dsp.setCurrent(Main.midlet.compositionForm);
        }
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        else if(v.equals(L.str[L.open])) {
            Main.midlet.model.setLocalFile();
            Main.dsp.setCurrent(new FileManager(P.path, MixerModel.getLocalFile()));
        }
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        else if(v.equals(L.str[L.save])) {
            if (Main.midlet.compositionForm.isNew()) {
                saveAsFile();
                return;
            }
            String openSaveString = Main.midlet.compositionForm.getCompositionName();
            try {
                Main.midlet.model.setLocalFile();
                Main.midlet.compositionForm.saveComposition(openSaveString);
                Main.midlet.compositionForm.setTitle(openSaveString);
            } catch (Exception e) {
                Alert a = new Alert(L.str[L.error], e.getMessage(), null, null);
                Main.dsp.setCurrent(a);
            }
        }
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ
        else if(v.equals(L.str[L.saveAs])) {
            saveAsFile();
        }
        // RMS | FS
        else if(v.equals(L.str[L.RMS])) {
            menu[selected] = L.str[L.file];
            P.isRMSMode = false;
        } else if(v.equals(L.str[L.file])) {
            menu[selected] = L.str[L.RMS];
            P.isRMSMode = true;
        }
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        else if(v.equals(L.str[L.options])) {
            Main.dsp.setCurrent(new SettingsForm(this));
        }
        // пїЅпїЅпїЅпїЅпїЅпїЅ
        else if(v.equals(L.str[L.help])) {
            TextView tv = new TextView(this, "help", false, L.str[L.help]);
            Main.dsp.setCurrent(tv);
        }
        // пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        else if(v.equals(L.str[L.about])) Main.dsp.setCurrent(new TextView(this, "about", true, L.str[L.about]));
        else if(v.equals(L.str[L.exit])) Main.midlet.destroyApp(true);
    }

    public void keyPressed (int key) {
        int ga = getGameAction(key);
        
        if(ga==UP || ga==LEFT) {cursorUp();}
        else if(ga==DOWN || ga==RIGHT) {cursorDown();}
        else if (ga==FIRE || key==Key.leftSoftKey) selectItem(cu);
        else if (key==Key.rightSoftKey) rightSoft();
        repaint();
    }
    
    public void keyRepeated (int key) {
        keyPressed(key);
    }

    private void rightSoft() {
        Main.midlet.destroyApp(true);
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

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     */
    private void readImages() {
        icons = new Image[MAX_MENU_ICONS];
        for (int i = 0; i < icons.length; i++) {
            try {
                icons[i] = Image.createImage("/img/main/"+i+".png");
            } catch (IOException ioe) {
                icons[i] = Image.createImage(1, 1);
            }
        }
    }
    
    private void saveAsFile() {
        Main.midlet.model.setLocalFile();
        Main.dsp.setCurrent(new FileManager(P.path, this, MixerModel.getLocalFile()));
    }
    
    public void pointerPressed(int pix, int piy) {
        int q = UI.getSoftBarHeight();
        if(pix<2*q && piy>h-q) selectItem(cu);
        else if(pix>w-2*q && piy>h-q) rightSoft();
        else {
            int cu1 = cu;
            piy -= 2*P.medPlain.getHeight();
            if(piy>0 && piy<menu.length*FILE_HEIGHT) {
                cu = piy/FILE_HEIGHT;
                CursorY = cu * FILE_HEIGHT;
            }
            if(System.currentTimeMillis()-time<700 && cu1==cu) selectItem(cu);
            time = System.currentTimeMillis();
        }
        repaint();
    }

}

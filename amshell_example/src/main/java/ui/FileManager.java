package ui;

import java.io.IOException;
import util.FWCashe;
import main.P;
import main.Main;
import main.Key;
import main.L;
import java.util.*;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import midedit.CompositionForm;
import midedit.io.AbstractFile;
import midedit.media.PreviewPlayer;

/**
 * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
 * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
 * 
 * @author aNNiMON
 */
public class FileManager extends Canvas {

    /* пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    /** MIDI-пїЅпїЅпїЅпїЅпїЅ */
    private static final String[] TypeMid = {
        "mid", "midi"
    };
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private static final int FONT_HEIGHT = P.smBold.getHeight()+2;
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private static final int SH_HEIGHT = FONT_HEIGHT + 1;
    
    
    /** пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ */
    public String pathFile;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private int w, h;
    
    /** пїЅпїЅпїЅпїЅпїЅ, пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ*/
    private Displayable previousDisplayable;
    
    /** пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private FWCashe fontCashe;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private AbstractFile file;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private PreviewPlayer pw;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private Image folderIcon, midiIcon;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ */
    private String[] data;
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ */
    private String[] menu;
    
    /* пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private int CursorY,  startPrintFile, numFiles;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ */
    private int menuY, menuWidth, menuHeight;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅ */
    private int curFiles, curMenu;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅ */
    private boolean showMenu;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ */
    private boolean isDelete;
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @param s пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ
     * @param aFile пїЅпїЅ 
     */
    public FileManager(String s, AbstractFile aFile) {
        setFullScreenMode(true);
        w = getWidth();
        h = getHeight();
        
        pathFile = s;
        file = aFile;
        pw = new PreviewPlayer();
        
        fontCashe = FWCashe.getCache(P.smBold);
        CursorY = startPrintFile = curFiles = numFiles = 0;
        showMenu = false;
        isDelete = false;
        
        loadImages();
        // пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
        menu = new String[] {
            L.str[L.open],
            L.str[L.playStop],
            L.str[L.delete],
            L.str[L.cancel]
        };
        update();
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param s пїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ
     * @param prev пїЅпїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @param aFile пїЅпїЅ 
     */
    public FileManager(String s, Displayable prev, AbstractFile aFile) {
        this(s, aFile);
        previousDisplayable = prev;
        // пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ 
        menu = new String[] {
            L.str[L.saveInThisFolder],
            L.str[L.newFolder],
            L.str[L.delete],
            L.str[L.cancel]
        };
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @param update пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     */
    public void setCurrent(boolean update) {
        if(update) update();
        Main.dsp.setCurrent(this);
    }

    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅ */
    private void setMenu() {
        Vector vt = new Vector();
        if (menu[0].equals(L.str[L.saveInThisFolder])) {
            // пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
            vt.addElement(L.str[L.saveInThisFolder]);
            if(!P.isRMSMode && (!P.path.equals("/"))) vt.addElement(L.str[L.newFolder]);
        } else {
            if (numFiles > 0) {
                // пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
                vt.addElement(L.str[L.open]);
                if (getType(TypeMid, data[curFiles])) {
                    vt.addElement(L.str[L.playStop]);
                    //vt.addElement(L.str[L.openToBuffer]);
                }
            }
        }
        if((numFiles > 0) && !data[curFiles].endsWith("/")) vt.addElement(L.str[L.delete]);
        vt.addElement(L.str[L.cancel]);
        menu = new String[vt.size()];
        vt.copyInto(menu);
        vt = null;
        
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
        calcMenuPos();
    }
    
    /** 
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     */
    private void calcMenuPos() {
        menuHeight = FONT_HEIGHT * menu.length;
        menuWidth = 0;
        for(int i = 0; i < menu.length; i++) {
            int itemWidth = fontCashe.stringWidth(menu[i]);
            if(itemWidth > menuWidth) menuWidth = itemWidth;
        }
        menuWidth = menuWidth + 3; // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        menuY = h - menuHeight - UI.getSoftBarHeight();
    }
    
    public void sizeChanged(int w, int h) {
        this.w = getWidth();
        this.h = getHeight();
        super.sizeChanged(w, h);
    }
    
    public void paint(Graphics g) {
        g.setColor(P.backgrnd);
        g.fillRect(0, 0, w, h);
        g.drawImage(P.bgKey, w, h - UI.getSoftBarHeight() - 2, Graphics.RIGHT | Graphics.BOTTOM);
        drawFiles(g);
        if (showMenu) {
            drawMenu(g);
        }
        UI.drawTitle(g, P.isRMSMode ? L.str[L.RMS]: pathFile);
        UI.drawSoftBar(g,L.str[L.menu], L.str[L.back]);
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @param g пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private void drawFiles(Graphics g) {
        if (numFiles > 0) {
            // пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
            g.setColor(P.obv);
            g.fillRect(0, CursorY + SH_HEIGHT, w, FONT_HEIGHT);
            g.setColor(P.fmbord);
            g.drawRect(0, CursorY + FONT_HEIGHT, w, FONT_HEIGHT);
        }
        int FileY = 0;
        g.setFont(P.smBold);
        for (int i = startPrintFile; i < numFiles; i++) {
            // пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
            Image tmp = midiIcon;
            if (data[i].indexOf("/") != -1) tmp = folderIcon;
            g.drawImage(tmp, 1, FileY + SH_HEIGHT + 1, 0);
            // пїЅпїЅпїЅпїЅпїЅ 
            int col = P.fmtextnc;
            if (FileY == CursorY) col = P.fmtextcur;
            g.setColor(col);
            g.drawString(data[i], 18, FileY + SH_HEIGHT, 20);
            
            FileY += FONT_HEIGHT;
            if(FileY > (h - (SH_HEIGHT*2)-3)) break;
        }
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     * @param g пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private void drawMenu(Graphics g) {
        final int menuX = 3;
        
        g.setFont(P.smPlain);
        int colorBack, colorObv, colorText;
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
        for (int i = 0; i < menu.length; i++) {
            // пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
            if (i == curMenu) {
                colorBack = P.obv;
                colorObv = P.fmbord;
                colorText = P.fmtextnc;
            } else {
                colorBack = P.fmback1;
                colorObv = P.obv;
                colorText = P.fmbord;
            }
            int hh = menuY + i * (FONT_HEIGHT - 1);
            g.setColor(colorBack);
            g.fillRect(menuX, hh, menuWidth, FONT_HEIGHT - 1);
            g.setColor(colorObv);
            g.drawRect(menuX, hh, menuWidth, FONT_HEIGHT - 1);
            g.setColor(colorText);
            g.drawString(menu[i], menuX + 3, hh + 1, 20);
        }
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param selected ID пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     */
    private void selectItem(int selected) {
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
        final String cur = menu[selected];
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
        final String fileSelected = (numFiles > 0) ? data[curFiles] : "";
        
        curMenu = 0;
        showMenu = false;
        
        if (cur.equals(L.str[L.open])) {
            // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
            nextDir(fileSelected);
            P.path = pathFile;
        }
        else if (cur.equals(L.str[L.playStop])) {
            // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
            if (getType(TypeMid, fileSelected)) {
                if(pw.isPlaying()) {
                    // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
                    try {
                        pw.stop();
                        pw.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    String openPath = fileSelected;
                    if(!P.isRMSMode) openPath = file.getPrefix() + pathFile + fileSelected;
                    try {
                        pw.play(openPath);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        else if (cur.equals(L.str[L.ok])) {
            System.out.println(fileSelected);
            if(!fileSelected.endsWith("/")) {
                String fileToDelete = fileSelected;
                if(!P.isRMSMode) fileToDelete = pathFile + fileSelected;
                try {
                    file.delete(fileToDelete);
                    update();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            isDelete = false;
        }
        else if (cur.equals(L.str[L.delete])) {
            menu = new String[] {L.str[L.cancel], L.str[L.ok]};
            showMenu = true;
            isDelete = true;
        }
        else if (cur.equals( L.str[L.cancel])) {
            // пїЅпїЅпїЅпїЅпїЅпїЅ
            if(isDelete) {
                // пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
                isDelete = false;
                setMenu();
                return;
            }
            // пїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
            P.path = pathFile;
            if(menu[0].equals(L.str[L.saveInThisFolder])) Main.dsp.setCurrent(previousDisplayable);
            else Main.dsp.setCurrent(Main.midlet.menu);
        }
        else if (cur.equals(L.str[L.saveInThisFolder])) { 
            P.path = pathFile;

            String openSaveString = Main.midlet.compositionForm.getCompositionName();
            int indBeg = openSaveString.lastIndexOf('/');
            int indEnd = openSaveString.lastIndexOf('.');
            if (indEnd == -1) {
                indEnd = openSaveString.length();
            }
            openSaveString = openSaveString.substring(indBeg + 1, indEnd);
            if (openSaveString.length() > 64) {
                openSaveString = openSaveString.substring(0, 63);
            }
            P.openSaveString = openSaveString;
            
            Main.dsp.setCurrent(new SaveName_frm(this, pathFile));
        }
        else if (cur.equals(L.str[L.newFolder])) { 
            // пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
            NewFolderForm newFolder = new NewFolderForm(L.str[L.newFolder], this, pathFile);
            Main.dsp.setCurrent(newFolder);
        }
    }

    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private void loadImages() {
        try {
            folderIcon = Image.createImage("/img/folder.png");
            midiIcon = Image.createImage("/img/midifile.png");
        } catch (Exception ioe) {}
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     */
    private void backDir() {
        CursorY = 0;
        curFiles = 0;
        startPrintFile = 0;
        int i = pathFile.lastIndexOf('/', pathFile.length() - 2);
        if (i != -1) {
            pathFile = pathFile.substring(0, i + 1);
        } else {
            pathFile = "/";
            return;
        }
        P.path = pathFile;
        update();
    }

    /** 
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     * @param s пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ / пїЅпїЅпїЅпїЅпїЅ
     */
    private void nextDir(String s) {
        if (s.indexOf("/") != -1) {
            pathFile = pathFile + s;
            P.path = pathFile;
            update();
            curFiles = 0;
            startPrintFile = 0;
            CursorY = 0;
        } else if (getType(TypeMid, s)) {
            if(menu[0].equals(L.str[L.saveInThisFolder])) return;
            try {
                if (Main.midlet.compositionForm != null) {
                    Main.midlet.compositionForm.releaseMem();
                }
                Main.midlet.compositionForm = null;
                System.gc();

                String openSaveString = pathFile + s;
                Main.midlet.compositionForm = new CompositionForm(Main.midlet, openSaveString);
                Main.dsp.setCurrent(Main.midlet.compositionForm);
                new Thread(Main.midlet.compositionForm).start();

            } catch (Exception e) {
                /*Alert a = new Alert(Constants.getStringName(13), Constants.getStringName(13) + ":\n" + e.getMessage(), null, null);
                display.setCurrent(a);
                if (compositionForm != null) {
                    compositionForm.releaseMem();
                }
                compositionForm = null;*/
            }
        }
    }

    public void keyPressed(int i) {
        
        int ga = getGameAction(i);
        if (showMenu) {
            // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ - пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅ
            if (ga==UP) {
                curMenu--;
                if(curMenu<0) curMenu = menu.length - 1;
            }
            else if (ga==DOWN) {
                curMenu++;
                if(curMenu >= menu.length) curMenu = 0;
            }
            else if (ga==FIRE || ga==RIGHT) selectItem(curMenu);
            else if (i == Key.leftSoftKey) showMenu = !showMenu;
        } else {
            if(i == Key.leftSoftKey) {
                showMenu = true;
                curMenu = 0;
                setMenu();
            }
            else if(ga == LEFT || i == Key.rightSoftKey) {
                backDir();
            }
            else switch(ga) {
                case DOWN: cursorDown(); break;
                case UP: cursorUp(); break;
                case FIRE:
                case RIGHT:
                    if (numFiles > 0)
                        nextDir(data[curFiles]);
                    break;
            }
            switch (i) {
                case KEY_NUM3:
                    for(int qq=0; qq<10; qq++) cursorUp();
                    break;
                 case KEY_NUM9:
                    for(int qq=0; qq<10; qq++) cursorDown();
                    break;
            }
        }
        repaint();
    }

    public void pointerPressed(int pix, int piy) {
        int y = UI.getSoftBarHeight();
        if (showMenu) {
            if(pix<2*y && piy>h-y) {
                 showMenu = !showMenu;
            }
        }
        if(pix<2*y && piy>h-y) {
            showMenu = true;
            curMenu = 0;
            setMenu();
        }else if(pix>w-2*y && piy>h-y) {
            backDir();
        }
        repaint();
    }
    
    
    public void keyRepeated(int i) {
        keyPressed(i);
    }

    private void cursorDown() {
        if (data.length > 0) {
            final int maxHeight = h - 2 * SH_HEIGHT;
            if (curFiles == data.length - 1) {
                CursorY = 0;
                curFiles = 0;
                startPrintFile = 0;
            } else if (curFiles > (maxHeight / FONT_HEIGHT - 3)) {
                if (CursorY >= (maxHeight - 2 * FONT_HEIGHT)) {
                    startPrintFile++;
                    curFiles++;
                } else {
                    curFiles++;
                    CursorY += FONT_HEIGHT;
                }
            } else {
                CursorY += FONT_HEIGHT;
                curFiles++;
            }
        }
    }

    private void cursorUp() {
        if (data.length > 0) {
            if (curFiles == 0) {
                curFiles = data.length - 1;
                final int maxHeight = h - 2 * SH_HEIGHT;
                if (data.length > (maxHeight / FONT_HEIGHT)) {
                    startPrintFile = (data.length - maxHeight / FONT_HEIGHT) + 1;
                    CursorY = (data.length - 1 - startPrintFile) * FONT_HEIGHT;
                } else {
                    CursorY = (data.length - 1) * FONT_HEIGHT;
                }
            } else if ((CursorY == 0) && (curFiles > 0)) {
                startPrintFile--;
                curFiles--;
            } else if (CursorY > 0) {
                CursorY -= FONT_HEIGHT;
                curFiles--;
            }
        }
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅ-пїЅпїЅ пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param type пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ
     * @param file пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     * @return true - пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private boolean getType(String[] type, String file) {
        for (int i = 0; i < type.length; i++) {
            if (file.toLowerCase().endsWith("." + type[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     */
    private void update() {
        data = file.list(pathFile);
        numFiles = data.length;
    }
    
}

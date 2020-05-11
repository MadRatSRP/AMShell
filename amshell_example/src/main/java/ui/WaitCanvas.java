/*
 * aNNiMON 2011
 * For more info visit http://annimon.com/
 */
package ui;

import com.silentknight.amshell.javax.microedition.lcdui.*;
import main.Key;
import main.L;
import main.P;
import midedit.Waitable;

/**
 * пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
 * @author aNNiMON
 */
public class WaitCanvas extends Canvas implements Runnable {
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ-пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private static final int MAX_PERCENT = 100;
    
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private Runtime runtime;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private Waitable activity;
    
    /** пїЅпїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private Displayable nextDisplayable;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private String title;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private int w, h;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ */
    private int totalMemory;
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ (пїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ) */
    private int curPercent;
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param title пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param act пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param displayable пїЅпїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    public WaitCanvas(String title, Waitable act, Displayable displayable) {
        setFullScreenMode(true);
        w = getWidth();
        h = getHeight();
        this.title = title;
        activity = act;
        nextDisplayable = displayable;
        runtime = Runtime.getRuntime();
        totalMemory = (int) runtime.totalMemory();
    }    
    
    public void paint(Graphics g) {
        g.setColor(P.backgrnd);
        g.fillRect(0, 0, w, h);
        g.drawImage(P.bgKey, w, h - UI.getSoftBarHeight() - 2, Graphics.RIGHT | Graphics.BOTTOM);
        UI.drawTitle(g, title);
        UI.drawSoftBar(g, "", L.str[L.cancel]);
        
        // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
        final int memoryPercent = (totalMemory - (int) runtime.freeMemory()) * MAX_PERCENT / totalMemory;
        String memory = L.str[L.memory] + " " + String.valueOf(memoryPercent) + "%";
        UI.drawProgressBar(g, curPercent, MAX_PERCENT, memory);
    }
    
    public void run() {
        try {
            Thread.sleep(500);
            curPercent = 0;
            while (curPercent < MAX_PERCENT) {
                curPercent = activity.getCurPercent();
                repaint();
                Thread.sleep(500);
            }
        } catch (Exception ex) {
        }
    }
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    public void cancel() {
        activity.cancel();
        curPercent = MAX_PERCENT;
    }
    
    public void sizeChanged(int w, int h) {
        this.w = getWidth();
        this.h = getHeight();
        super.sizeChanged(w, h);
    }
    
    public void keyPressed(int key) {
        if(key == Key.rightSoftKey) {
            cancel();
            main.Main.dsp.setCurrent(nextDisplayable);
        }
    }
    
}

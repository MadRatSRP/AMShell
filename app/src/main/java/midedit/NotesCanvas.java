package midedit;

import main.Main;
import midedit.media.Composition;
import javax.microedition.lcdui.*;

/**
 *
 * @author user
 */
public class NotesCanvas extends MixerCanvas {

    /**
     *
     * @param ctrl
     * @param c 
     * @param ch
     */
    public NotesCanvas(Main ctrl, Composition c, int ch) {
        super(ctrl, c, ch);
        hMax = 127;
        hBase = 60;
        curY = 11;
    }

    /**
     *
     * @param ch
     */
    public void setChannel(int ch) {
        channel = ch;
    }

    /**
     *
     * @return
     */
    protected boolean doKEY_NUM5() {
        if (super.doKEY_NUM5() == false) {
            int dt;
            dt = super.getCurLen();
            byte n = (byte) getNoteFromLine(curY);
            int curT = getCurTime();
            composition.addNoteOn(curT, (byte) channel, n, super.getCurVol(), dt, true);
            composition.addNoteOffWithoutSearch(curT + dt, (byte) channel, n, dt);
        }
        return true;
    }

    /**
     *
     * @param g
     * @param note
     * @param shiftTime
     * @param shiftNote
     */
    protected void paintNote(Graphics g, Note note, int shiftTime, int shiftNote) {
        int x, h;
        int dx, t = 1;
        x = getXInPixel(note.noteTime + shiftTime);
        h = getLineFromNote(note.noteLine + shiftNote);
        dx = (note.noteLength * wOne) / Constants.timeConst;
        if (dx == 0) {
            dx = 2;
        }
        if (x + dx >= 0 && x < rollWidth) {

            if (h >= 0 && h < nH) {
                g.fillRect(x + t, h * hOne + 1, dx - t, hOne - 1);
            } else {
                h = (h < 0) ? -1 : nH;
                g.fillRect(x + t, h * hOne + 1, 2, hOne - 1);
            }
        }
    }

    /**
     *
     * @return
     */
    protected byte getLineFromNote(int n) {
        return (byte) ((Constants.INV_CANVAS_CONST - n) - hBase);
    }

    /**
     *
     * @param ch
     * @return
     */
    protected int getNoteFromLine(int ch) {
        return (byte) getCurNote();
    }
}

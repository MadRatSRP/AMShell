package midedit;

import midedit.media.Composition;
import main.Main;

/**
 * а“аЛаАб±б± бЂаЕаДаАаКбІаИбЂаОаВаАаНаИбЅ аБаАбЂаАаБаАаНаОаВ
 * @author user
 */
public class DrumsCanvas extends MixerCanvas {

    /** аЁаДаВаИаГ бІаЕаКб±бІаОаВб‚б… аМаЕбІаОаК аВ аБаАбЂаАаБаАаНаАб… */
    public static final int drumsShift = Constants.DRUMS_SHIFT;
    private byte[] drumsTable = null;
    private byte[] drumsTableInverse = null;

    /**
     *
     * @param ctrl
     * @param c
     */
    public DrumsCanvas(Main ctrl, Composition c) {
        super(ctrl, c, Constants.DRUMS_CHANNEL);
        prepareConstants();

        byte[] b = new byte[Constants.NCHANNEL];
        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            b[i] = (byte) (drumsShift + i);
        }
        drumsTable = b;
        newDrumsTableInverse();
    }

    /**
     *
     */
    private void prepareConstants() {
        if (nH > Constants.DRUMS_LENGTH) {
            nH = Constants.DRUMS_LENGTH;
        }
        hStep = 4;
        hMin = drumsShift;
        hMax = (drumsShift + Constants.DRUMS_LENGTH - 1);
        hBase = hMin;
        curY = 0;
    }

    /**
     *
     * @return
     */
    protected boolean doKEY_NUM5() {
        if (super.doKEY_NUM5() == false) {
            composition.addNoteOn(getCurTime(), (byte) channel,
                    (byte) getNoteFromLine(curY), getCurVol(), 0, true);
        }
        return true;
    }

    protected void doSmallDown() {
        if (hBase + curY < hMax) {
            super.doSmallDown();
        }
    }

    private void newDrumsTableInverse() {
        drumsTableInverse = new byte[Constants.N_INSTR];
        for (int i = 0; i < Constants.N_INSTR; ++i) {
            drumsTableInverse[i] = (byte) -1;
        }
        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            drumsTableInverse[drumsTable[i]] = (byte) i;
        }
    }

    /**
     *
     * @param g
     */
    protected void paintScale(javax.microedition.lcdui.Graphics g) {
        for (int hh = hBeg; hh < hBeg + rollHeight; hh += hOne) {
            g.fillRect(4, hh, wBeg - 5, 1);
        }
    }

    /**
     *
     * @param n
     * @return
     */
    protected byte getLineFromNote(int n) {
        return (byte) (n - hBase);
    }

    /**
     *
     * @param ln
     * @return
     */
    protected int getNoteFromLine(int ln) {
        return (byte) (hBase + curY);
    }
}

package midedit.media;

import midedit.Note;
import midedit.NoteLong;
import midedit.Constants;
import midedit.NoteList;
import midedit.MixerCanvas;
import midedit.UndoableAction;

/**
 *
 * @author user
 */
public class Composition {

    private String name;
    public NoteList[] tracks;
    /**
     *
     */
    public static final int DEFAULT_CHANNEL = 0;
    private int timeNoteOff;
    private int ticksPer4;
    /**
     *
     */
    public final static int NOTHING = -127;
    private int[] instruments;
    private byte[] channelVolExp;
    /**
     *
     */
    public final static byte channelVolExpMax = 64;
    private boolean[] soloList;
    private boolean isSoloMode;
    private int nom = 4;
    private int denomE = 2;
    private NoteLong meterNote = null;
    private UndoableAction undoableAction;

    /**
     *
     * @param s
     */
    private Composition(String s) {
        setName(s);

        tracks = new NoteList[Constants.NCHANNEL];
        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            tracks[i] = new NoteList();
        }


        instruments = new int[Constants.NCHANNEL];
        channelVolExp = new byte[Constants.NCHANNEL];
        soloList = new boolean[Constants.NCHANNEL];
        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            instruments[i] = NOTHING;
            channelVolExp[i] = (byte) (channelVolExpMax / 2);
            soloList[i] = false;
        }
        instruments[Constants.DRUMS_CHANNEL] = -1;

        isSoloMode = false;
        setTicksPer4(240);

        undoableAction = new UndoableAction(this);
    }

    /**
     *
     */
    public Composition() {
        this("new");
    }

    /**
     *
     */
    public void deleteNoteList() {
        tracks = null;
    }

    /**
     *
     * @return
     */
    public String[] getInstrumentsStrings() {
        int size = 0;
        for (int i = 0; i < Constants.NCHANNEL; i++) {
            if (instruments[i] != NOTHING) {
                size++;
            }
        }
        String[] ss = new String[size];
        int j = 0;
        for (int i = 0; i < Constants.NCHANNEL; i++) {
            if (instruments[i] != NOTHING) {
                ss[j++] = Constants.getInstrName(instruments[i] + 1);
            }
        }
        return ss;
    }

    /**
     *
     * @param iNum
     * @return
     */
    public byte addInstrument(int iNum) {
        if (iNum == -1) {
            if (instruments[Constants.DRUMS_CHANNEL] == NOTHING) {
                instruments[Constants.DRUMS_CHANNEL] = iNum;
                return (byte) Constants.DRUMS_CHANNEL;
            }
        } else {
            int i;
            for (i = 0; i < Constants.NCHANNEL
                    && instruments[i] != NOTHING || i == Constants.DRUMS_CHANNEL; ++i);
            if (i < Constants.NCHANNEL) {
                instruments[i] = iNum;
                return (byte) i;
            }
        }
        return -1;
    }

    /**
     *
     * @param ch
     * @param iNum
     */
    public void setInstrument(int ch, int iNum) {
        if (ch >= 0) {
            instruments[ch] = iNum;

        }
    }

    /**
     *
     * @return
     */
    public int[] getInstruments() {
        int[] t = new int[instruments.length];
        for (int i = 0; i < instruments.length; ++i) {
            t[i] = instruments[i];
        }
        return t;
    }

    /**
     *
     * @param ch
     * @return
     */
    public int getInstrument(int ch) {
        return instruments[ch];
    }

    /**
     *
     * @param ch
     * @param e
     */
    public void setVolumeExp(int ch, byte e) {
        channelVolExp[ch] = e;
    }

//    /**
//     *
//     * @param ch
//     * @return
//     */
//    public byte getVolumeExp(int ch) {
//        return channelVolExp[ch];
//    }
    /**
     *
     * @return
     */
    public byte[] getVolumeExpArr() {
        byte[] t = new byte[channelVolExp.length];
        for (int i = 0; i < channelVolExp.length; ++i) {
            t[i] = channelVolExp[i];
        }
        return t;
    }

    /**
     *
     * @return
     */
    public boolean[] getSoloList() {
        boolean[] dup = new boolean[soloList.length];
        System.arraycopy(soloList, 0, dup, 0, soloList.length);
        return dup;
    }

    /**
     *
     * @param channel
     * @param solo
     */
    public void setSolo(int channel, boolean solo) {
        soloList[channel] = solo;
    }

    /**
     *
     * @param solo
     */
    public void setSoloMode(boolean solo) {
        isSoloMode = solo;
    }

    /**
     *
     */
    public void setSoloMode() {
        boolean solo = false;
        for (int ind = 0; ind < instruments.length; ++ind) {
            if (soloList[ind]) {
                solo = true;
            }
        }

        isSoloMode = solo;
    }

    /**
     *
     * @param ch
     * @return
     */
    public boolean isPlayChan(int ch) {
        return !isSoloMode || soloList[ch];
    }

//    /**
//     *
//     * @return
//     */
//    public boolean isSoloMode() {
//        return isSoloMode;
//    }
    /**
     *
     * @param tt
     * @param ch
     * @param nn
     * @param vv
     * @param ll
     * @param log
     */
    public void addNoteOn(int tt, byte ch, byte nn, byte vv, int ll, boolean log) {
        tracks[ch].add(new Note(tt, ch, nn, vv, ll));
        if (log) {
            undoableAction.logAddNote(new Note(tt, ch, nn, vv, ll));
        }
    }

    /**
     *
     * @param tt
     * @param ch
     * @param nn
     */
    public void addNoteOff(int tt, byte ch, byte nn) {
        Note noteTmp = tracks[ch].searchBack(ch, nn);
        if (noteTmp != null) {
            noteTmp.noteLength = tt - noteTmp.noteTime;
            tracks[ch].add(new Note(tt, ch, nn, (byte) 0, noteTmp.noteLength));
        } else {
            tracks[ch].add(new Note(tt, ch, nn, (byte) 0, 0));
        }
    }

    /**
     *
     * @param tt
     * @param ch
     * @param nn
     * @param backLen
     */
    public void addNoteOffWithoutSearch(int tt, byte ch, byte nn, int backLen) {
        tracks[ch].add(new Note(tt, ch, nn, (byte) 0, backLen));
    }

    /**
     *
     * @param tt
     * @param ch
     * @param nn
     * @param vv
     */
    public void delNote(int tt, byte ch, byte nn, byte vv) {
        timeNoteOff = tracks[ch].del(tt, tt + Constants.timeConst - 1, ch, nn, vv, null);
        if (timeNoteOff == -2) {
            return;
        }
        int len;
        if (timeNoteOff == -1) {
            len = 0;
        } else {
            len = timeNoteOff - tt;
        }
        undoableAction.logDelNote(new Note(tt, ch, nn, vv, len));
    }

    /**
     *
     * @param tBeg
     * @param tEnd
     * @param ch
     * @param nn
     * @param vv
     */
    public void delNotes(int tBeg, int tEnd, byte ch, byte nn, byte vv) {
        if (ch == -1) {
            for (int i = 0; i < Constants.NCHANNEL; ++i) {
                tracks[i].del(tBeg, tEnd, ch, nn, vv, null);
            }
        } else {
            tracks[ch].del(tBeg, tEnd, ch, nn, vv, null);
        }
    }

    /**
     *
     * @param noteTime
     * @param ch
     * @return
     */
    public Note getFirstNote(int t, int ch) {
        return tracks[ch].searchNoteByTime(t);
    }

    /**
     *
     * @param time
     * @param newTemp
     * @return
     */
    public NoteLong addTemp(int time, int newTemp) {
        byte[] dat = new byte[]{(byte) 0xff, (byte) 0x51, 0x03, 3, 4, 5};
        int tmp = newTemp;
        for (int i = 5; i >= 3; --i) {
            dat[i] = (byte) (tmp & 0xff);
            tmp >>= 8;
        }
        NoteLong noteTemp = new NoteLong(time, dat);
        tracks[DEFAULT_CHANNEL].add(noteTemp);
        return noteTemp;
    }

    /**
     *
     * @param delTemp
     */
    public void delTemp(Note delTemp) {
        tracks[DEFAULT_CHANNEL].delOne(delTemp);
    }

    /**
     *
     * @param tempBPM
     * @return
     */
    public static int getMsPer4(int tempBPM) {
        return 500000 * 120 / tempBPM;
    }

    /**
     *
     * @param msPer4
     * @return
     */
    public static int getTempBeatPerMin(int msPer4) {
        return 120 * 500000 / msPer4;
    }

    /**
     *
     * @param noteTime
     */
    public void setTicksPer4(int t) {
        ticksPer4 = t;
        Constants.setTimeConst(getTime2CanvasConst());
    }

    /**
     *
     * @return
     */
    public short getTicksPer4() {
        return (short) ticksPer4;
    }

    /**
     *
     * @return
     */
    public int getTime2CanvasConst() {
        return ticksPer4 / 8;
    }

    /**
     *
     * @param ch
     * @return
     */
    public NoteList getNoteListByChannel(int ch) {
        return tracks[ch];
    }

    /**
     *
     * @param s
     */
    public void setName(String s) {
        name = s;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param percent
     * @param ch
     */
    public void setCurW(int percent, byte ch) {
        int timeNew;
        if (percent == 0) {
            timeNew = 0;
        } else {
            timeNew = this.getFirstNote(this.getLastTime() * percent / 100, ch).noteTime;
            MixerCanvas.curX = 0;
        }
        MixerCanvas.xBase = (short) (timeNew / Constants.timeConst);
    }

    /**
     *
     * @return
     */
    public int getCurWInPercent() {
        int timeMax = this.getLastTime();
        return (MixerCanvas.xBase * Constants.timeConst * 100) / (timeMax > 0 ? timeMax : 1);
    }

    /**
     *
     * @return
     */
    public int getLastTime() {
        int tCur, tMax = 0;

        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            Note noteTimeMax = this.getFirstNote(0x7fffffff, i);
            if (noteTimeMax != null) {
                tCur = noteTimeMax.noteTime + noteTimeMax.noteLength;
                if (tCur > tMax) {
                    tMax = tCur;
                }
            }
        }

        return tMax;
    }

    /**
     *
     * @return
     */
    public UndoableAction getUndoableAction() {
        return undoableAction;
    }

    /**
     *
     * @return
     */
    public int getNom() {
        return nom;
    }

    /**
     *
     * @return
     */
    public int getDenomE() {
        return denomE;
    }

    /**
     *
     * @param nomNew
     * @param deNomNew
     */
    public void setMeter(int nomNew, int deNomNew) {
        nom = nomNew;
        denomE = deNomNew;

        if (meterNote == null) {
            byte[] dat = new byte[]{(byte) 0xff, (byte) 0x58, 0x04, 0x04, 0x02, 0x18, 0x08};
            dat[3] = (byte) nomNew;
            dat[4] = (byte) deNomNew;
            NoteLong newMeter = new NoteLong(0, dat);
            tracks[DEFAULT_CHANNEL].add(newMeter);
            meterNote = newMeter;
        } else {
            meterNote.dat[3] = (byte) nomNew;
            meterNote.dat[4] = (byte) deNomNew;
        }
    }
}

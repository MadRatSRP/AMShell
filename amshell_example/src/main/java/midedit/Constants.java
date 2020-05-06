package midedit;

import java.util.Vector;
import main.L;

/**
 * аАтЂјаАаЊаАаЏаБТБаБа†аАа«аАаЏаБа†аБтЂ“
 * @author user
 */
public class Constants {

    public static String getInstrName(int index) {
        return L.instr[index];
    }

    public static void setTimeConst(int t) {
        timeConst = t;
    }
    /**
     *
     */
    public static final int INV_CANVAS_CONST = 131;
    /**
     *
     */
    public static int timeConst = 10;
    /**
     *
     */
    public final static int N_INSTR = 127,
            DRUMS_LENGTH = 61,//27__87
            DRUMS_SHIFT = 27;
    /**
     *
     */
    public final static int DRUMS_CHANNEL = 9;
    /**
     *
     */
    public static final int NCHANNEL = 16;
    /**
     *
     */
    public static final int MAX_NOTE_LENGTH = 8;		// min == 1/(2^5)
    static VectorArr instrVectorArr = new VectorArr(L.instr, 1, 128);

    static class VectorArr extends Vector {

        public VectorArr(String[] src, int beg, int length) {
            super(length);
            setSize(length);
            System.arraycopy(src, beg, super.elementData, 0, size());
        }
    }
}
package midedit;

import midedit.media.Composition;
import midedit.io.AbstractFile;
import java.io.*;
import main.P;

public class MidiFile implements Waitable {

    private String outStr;
    private String url;
    private static AbstractFile file = null;
    private int seekPos = 0;
    private int seekLen = 1;
    private int maxTime = 0;
    private boolean cancelStatus;
    private final static int sizeMax = 15000;
    private static byte[] MTHD = {0x4D, 0x54, 0x68, 0x64};
    private static byte[] MTRK = {0x4d, 0x54, 0x72, 0x6b};
    private static byte[] ENDTrk = {0x00, -0x01, 0x2f, 0x00};
    private static byte[] dat4PlayNote = {
        77, 84, 104, 100, 0, 0, 0, 6,//7
        0, 0, 0, 1, 0, 96, 77, 84,//15
        114, 107, 0, 0, 0, 38, 0, -1,//23
        88, 4, 4, 2, 24, 8, 0, -1,//31
        81, 3, 33, -14, -13, 0, -64, 26,//39//instr
        0, -112, 46, 1, 8, -103/*0x90*/, 46/*note height*/, 127,//55
        127, -112/*0x80*/, 46, 1, 16, -112, 46/*note height*/, 1,//63
        0, -1, 47, 0,};
    private static byte[] bufTmp = new byte[16];
    private static int intTmp;
    private static byte[] bMidi = new byte[sizeMax];

    public MidiFile(AbstractFile aFile) {
        file = aFile;
    }

    private boolean increaseBuffer() {
        int prevLen = bMidi.length;
        int nextLen;
        if (prevLen > 32000) {
            nextLen = prevLen + 32000;
        } else {
            nextLen = prevLen * 2;
        }

        byte[] newarr;
        try {
            newarr = new byte[nextLen];
            System.arraycopy(bMidi, 0, newarr, 0, bMidi.length);
            bMidi = newarr;
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    public void setFile() {
        file = P.isRMSMode ? MixerModel.rmsFile : MixerModel.jsr75File;
    }

    public String writeMix(Composition c, int t, boolean forPlay) throws Exception {
        return writeMixWithName(c, t, forPlay, "out.mid", -1, -1);
    }

    public String writeMixWithName(Composition c, int tBeg, boolean forPlay, String name) throws Exception {
        return writeMixWithName(c, tBeg, forPlay, name, -1, -1);
    }

    public String writeMixWithName(Composition c, int tBeg, boolean forPlay, String name, int channel, int instrument) throws Exception {
        System.gc();
        outStr = name;
        int iMidi;		// index of b
        int i;

        iMidi = 0;
        ////  begin  header -------------------------
        /// print MThd  (4)
        for (i = 0; i < MTHD.length; ++i) {
            bMidi[iMidi++] = MTHD[i];
        }

        /// print header lenght (4)
        bMidi[iMidi] = 0;
        bMidi[iMidi + 1] = 0;
        bMidi[iMidi + 2] = 0;
        bMidi[iMidi + 3] = 6;  /// always =6
        iMidi += 4;

        /// print format (2)
        bMidi[iMidi] = 0;
        bMidi[iMidi + 1] = 1;
        iMidi += 2;

        /// print num track (2)
        int numTracks = 0;
        NoteList curList;
        //// print tracks
        seekLen = 1;
        seekPos = 1;
        int lastTrack = 0;
        if (channel == -1) {
            for (i = 0; i < Constants.NCHANNEL; ++i) {
                curList = c.getNoteListByChannel(i);
                seekLen += curList.getLen();
                if (curList.getLen() > 0 && (!forPlay || c.isPlayChan(i))) {
                    numTracks++;
                    lastTrack = i;
                }
            }
        } else {
            curList = c.getNoteListByChannel(channel);
            seekLen += curList.getLen();
            if (curList.getLen() > 0 && (!forPlay || c.isPlayChan(i))) {
                numTracks++;
                lastTrack = 0;
            }
        }
        bMidi[iMidi] = 0;
        bMidi[iMidi + 1] = (byte) numTracks;
        iMidi += 2;

        /// print tick per quart (2)
        short ticks = c.getTicksPer4();
        bMidi[iMidi] = (byte) (ticks / 256);
        bMidi[iMidi + 1] = (byte) (ticks % 256);
        iMidi += 2;

        /// end header -------------------

        cancelStatus = false;
        maxTime = 0; // global var
        boolean isLastTrack = false;
        if (channel == -1) {
            for (i = 0; i < Constants.NCHANNEL; ++i) {
                curList = c.getNoteListByChannel(i);
                if (curList.getLen() > 0 && (!forPlay || c.isPlayChan(i))) {   //     track is not empty			
                    if (i == lastTrack) {
                        isLastTrack = true;
                    }
                    int numByte = writeTrack(c, i, tBeg, iMidi, forPlay, isLastTrack, instrument);
                    iMidi += numByte;
                }
            }
        } else {
            curList = c.getNoteListByChannel(channel);
            if (curList.getLen() > 0 && (!forPlay || c.isPlayChan(i))) {	//	   track is not empty			
                isLastTrack = true;
                int numByte = writeTrack(c, channel, tBeg, iMidi, forPlay, isLastTrack, instrument);
                iMidi += numByte;
            }
        }
        /// end print
        int df = 0;
        try {
            /*if(file == null)
            //file = new File();
            file = new SiemensFile();*/
            try {
                if (file.exists(outStr) >= 0) {
                    file.delete(outStr);
                }
            } catch (/*IllegalArgument*/Exception e) {
            }

            df = file.open(outStr, true);

            url = file.getURL();

            file.write(df, bMidi, 0, iMidi);
            file.close(df);
            return file.getAns();
        } catch (IOException e) {
            //throw new Exception("IO:writeMixWithName()\noteLine"+e.toString()+"\noutStr="+outStr);
            throw new Exception("Can't write " + outStr);
        } finally {
            //file.close(df);
            seekPos = seekLen;
        }

    }

    private int writeTrack(Composition c, int ch, int tBeg, int begInd, boolean forPlay, boolean isLastTrack, int instrument) throws Exception {
        int ind = begInd;
        int i;
        ///  track  --------------
        /// print MTrk (4)
        for (i = 0; i < MTRK.length; ++i) {
            bMidi[ind++] = MTRK[i];
        }

        /// print track lenght (4)
        int lenInd = ind;
        ////    write later
        ind += 4;
        int msgBegInd = ind;
        if (instrument == -1) {
            instrument = c.getInstrument(ch);
        }
        if (instrument != Composition.NOTHING && instrument != -1/*Drums*/) {
            bMidi[ind] = (byte) 0;
            bMidi[ind + 1] = (byte) (0xc0 | (0x0f & ch));
            bMidi[ind + 2] = (byte) (instrument);
            ind += 3;
        }

        //NoteList notes = noteChannel.getNoteListByChannel(ch);

        /// print notes

        byte[] volumeExpArr = c.getVolumeExpArr();

        //int iMax = bMidi.noteLength - 200;
/*		seekLen = mix.getLen()+1;
        seekPos = 1;
        cancelStatus = false;*/
        int vol;
        int kVol = 256 / c.channelVolExpMax;
        byte[] tnote;
        int tPrev = tBeg;

        if (forPlay/* && false*/) {
            int dTime = 2 * Constants.timeConst;
            tnote = getVarLenRepr(/*dTime*/0); // Delta-Time
            for (i = tnote.length - 1; i >= 0; --i, ++ind) {
                bMidi[ind] = tnote[i];
            }

            //bMidi[ind++] = (byte) (Constants.timeConst * 2) + maxTime - tPrev;
            bMidi[ind++] = (byte) (ch | ((byte) 0x90/*0x80*/));
            //bMidi[ind++] = (byte) 0x99;
            bMidi[ind++] = (byte) 0x2e;
            bMidi[ind++] = (byte) 0x01;

            tnote = getVarLenRepr(/*dTime*/dTime - 0); // Delta-Time
            for (i = tnote.length - 1; i >= 0; --i, ++ind) {
                bMidi[ind] = tnote[i];
            }
            bMidi[ind++] = (byte) (ch | ((byte) 0x80));
            //bMidi[ind++] = (byte) 0x99;
            bMidi[ind++] = (byte) 0x2e;
            bMidi[ind++] = (byte) 0x00;


            //tPrev -= dTime;
            //tPrev =0;
        }
        for (Note note = c.getFirstNote(tBeg, ch); note != null; note = note.prev) {
            if (note instanceof NoteLong) {
                NoteLong tempoNote = (NoteLong) note;
                byte[] dat = tempoNote.dat;
                if ((dat[0] == (byte) 0xff && dat[1] == (byte) 0x51)) {   //////write tempo
                    bMidi[ind++] = (byte) 0x0;
                    for (i = 0; i < dat.length; ++i, ind++) {
                        bMidi[ind] = dat[i];
                    }
                    break;
                }
            }
        }


        for (Note note = c.getFirstNote(tBeg, ch); note != null/* && ind<iMax*/; note = note.next) {
            if (cancelStatus) {
                throw new Exception("Cancel");
            }

            if (ind >= bMidi.length - 400) {
                if (increaseBuffer() == false) {
                    break;
                }
            }

            /// calc maxTime for set end Note
            if (note.noteTime > maxTime) {
                maxTime = note.noteTime;
            }

            tnote = getVarLenRepr(note.noteTime - tPrev); // Delta-Time
            if (tnote.length == 0) {
                throw new Exception("len==0");
            }

            for (i = tnote.length - 1; i >= 0; --i, ++ind) {
                bMidi[ind] = tnote[i];
            }

            if (note instanceof NoteLong) {
                NoteLong notelong = (NoteLong) note;
                for (i = 0; i < notelong.dat.length; ++i, ind++) {
                    bMidi[ind] = notelong.dat[i];
                }

            } else // simple  Note
            {

                bMidi[ind] = (byte) (note.noteChannel | ((note.noteVolume == 0) ? (byte) 0x80 : (byte) 0x90));

                //bMidi[iMidi]=(byte)0x99;		// 0x99 -- Note On
                bMidi[ind + 1] = note.noteLine;
                //bMidi[iMidi+2]=note.noteVolume;  edit 08.07.2004
                if ((note.noteVolume & 0x80) != 0) {
                    note.noteVolume = 0x7f;
                }
                vol = note.noteVolume + kVol * (volumeExpArr[note.noteChannel] - c.channelVolExpMax / 2); //*( 1<<(volumeExpArr[note.noteChannel]/2))/0x10000;
                if (vol > 127) {
                    vol = 127;
                } else if (vol < 0) {
                    vol = 0;
                }
                bMidi[ind + 2] = (byte) vol;
                ///////////
                ind += 3;
            }
            tPrev = note.noteTime;
            seekPos++;
            Thread.yield();
        }
        /// print last note
        if (forPlay && isLastTrack/* && false*/) {
            tnote = getVarLenRepr((Constants.timeConst * 2) + maxTime - tPrev); // Delta-Time
            for (i = tnote.length - 1; i >= 0; --i, ++ind) {
                bMidi[ind] = tnote[i];
            }

            //bMidi[ind++] = (byte) (Constants.timeConst * 2) + maxTime - tPrev;
            bMidi[ind++] = (byte) 0x99;
            bMidi[ind++] = (byte) 0x2e;
            bMidi[ind++] = (byte) 0x01;
        }


        /// print end track (4)
        for (i = 0; i < ENDTrk.length; ++i) {
            bMidi[ind++] = ENDTrk[i];
        }


        writeIntBigEnding(bMidi, lenInd, ind - msgBegInd, 4); ///////?????

        return ind - begInd;
    }

    private void writeIntBigEnding(byte[] dest, int begInd, int src, int numBytesToWrite) {
        int t = src;
        int ind = begInd + numBytesToWrite - 1;
        for (; ind >= begInd; --ind) {
            dest[ind] = (byte) t;
            t >>= 8;
        }
        return /*numBytesToWrite*/;
    }

    public byte[] writeNote(byte instr, byte nn) {
        byte[] b = dat4PlayNote;
        if (instr >= 0) {
            b[39] = instr;
            b[41 + 4] = (byte) (0x90);
            b[45 + 4] = (byte) (0x80);
        } else {
            b[39] = 26;
            b[45] = -103;
            b[49] = -112;
        }
        b[39 + 3 + 4] = nn;			//  offset 39 -- note number
        b[43 + 3 + 4] = nn;
        return b;
    }

    public byte[] writeTest(byte instr) {
        outStr = "test.mid";
        InputStream is = getClass().getResourceAsStream("/test.mid");
        byte b[] = new byte[148];
        try {
            is.read(b);
        } catch (IOException ex) {
        }
        b[24] = instr;
        return b;
    }

    private byte[] getVarLenRepr(int t) {
        int tt = t;
        if (tt < 0) {
            tt = 0;
        }
        int i, end = 0; // 0   1    2    3
        byte[] b = {0, -128, -128, -128};		// -128 == 0x80
        for (i = 0; i < 4; ++i) {
            b[i] |= (byte) (tt & 0x7f);
            tt >>= 7;
            if (tt == 0) {
                end = i + 1;
                break;
            }
        }
        byte[] r = new byte[end];
        for (i = 0; i < r.length; ++i) {
            r[i] = b[i];
        }
        return r;
    }

    public String getURI() {
        return url;
    }

    public Composition readMix(String path) throws Exception {
        String name = /*file.getPrefix() + */ path;
        int gcCount = 0;
        System.gc();
        Composition composition = new Composition();
        int df = 0;
        try {
            if (file.exists(name) < 0) {
                throw new IOException("File not found");
            }
            df = file.open(name, false);
            seekLen = file.length(df);
            seekPos = 0;
            cancelStatus = false;

            int d = 0x4d546864; //  MThd
            int t = readInt(df, 4);
            if (t != d) {
                throw new IOException(/*"Not Found MThd"*/"Can't read file");
            }
            t = readInt(df, 4);
            if (t != 6) {
                throw new IOException("len MThd=" + t);
            }
            int format = readInt(df, 2);
            if (format > 1) {
                throw new IOException("Format=" + format + "\nthis format don't support");
            }
            int numTrk = readInt(df, 2);
            if (format == 0 && numTrk != 1) {
                throw new IOException("ntrk=" + numTrk);
            }
            t = readInt(df, 2);
            composition.setTicksPer4(t);

            // format 1
            for (int indTrk = 0; indTrk < numTrk; ++indTrk) {
                d = 0x4d54726b; // MTrk
                t = readInt(df, 4);
                if (t != d) {
                    throw new IOException("Track not found");
                }
                t = readInt(df, 4); // noteLength of track
                boolean hasMoreNotes = true;
                int timeDelta, timeAbs = 0;
                int dPrev = 0;
                int d1 = 0;
                int d1h;
                int d2;
                int temp;
                int n;
                int datInt;
                int datInt2;

                byte channel;
                byte note;
                byte vel;
                byte instrum;

                while (hasMoreNotes) {
                    if (cancelStatus) {
                        throw new Exception("Cancel");
                    }
                    gcCount++;
                    if (gcCount % 150 == 0) {
                        if (gcCount % 1000 == 0) {
                            System.gc();
                        }
                        //mod.listener.setPercent(seekPos*100/fileLen);
                    }

                    timeDelta = readTimeDelta(df);
                    timeAbs += timeDelta;
                    d = readInt(df, 1);
                    boolean isFBN = (d & 0x80) == 0x00; // FBN = first bit null
                    if (isFBN) {
                        if (dPrev == 0) {
                            throw new IOException("File corrupted");
                        }
                        d1 = dPrev;
                        d2 = d;
                    } else {
                        d1 = d;
                        d2 = readInt(df, 1);
                    }
                    d1h = d1 & 0xf0;
                    switch (d1h) {
                        case 0x90:
                        case 0x80:
                            channel = (byte) (d1 & 0x0f);
                            note = (byte) d2; //(readInt(df,1));
                            vel = (byte) (readInt(df, 1));
                            if (d1h == 0x90 && vel != 0) {
                                if (composition.getInstrument(channel)
                                        == Composition.NOTHING) {
                                    composition.setInstrument(channel, 0);
                                }
                                composition.addNoteOn(timeAbs, channel, note,
                                        vel, 0/*Constants.timeConst*/, false);
                            } else //	if(channel != Constants.DRUMS_CHANNEL)
                            {
                                composition.addNoteOff(timeAbs, channel, note);
                            }

                            break;
                        case 0xc0:
                            channel = (byte) (d1 & 0x0f);
                            instrum = (byte) d2; //(readInt(df,1));
                            if (channel != Constants.DRUMS_CHANNEL) {
                                composition.setInstrument(channel, instrum);
                            }

                            break;
                        case 0xa0:
                            skip(df, 2 - 1);
                            break;
                        case 0xb0:
                            skip(df, 2 - 1);
                            break;
                        case 0xe0:
                            skip(df, 2 - 1);
                            break;
                        case 0xf0:
                            switch (d1) {
                                case 0xff: // meta-events
                                    switch (d2) {
                                        case 0x58: // Time Signature
                                            n = readInt(df, 1); // noteLine == 4
                                            datInt = readInt(df, 1);
                                            datInt2 = readInt(df, 1);
                                            composition.setMeter(datInt, datInt2);
//----
                                            skip(df, 2);
                                            break;
                                        case 0x51: // Set tempo
                                            skip(df, 1); // 0x03 - lenght
                                            temp = readInt(df, 3);
                                            //composition.setTemp(temp);
                                            composition.addTemp(timeAbs, temp);
                                            break;
                                        case 0x2f:
                                            skip(df, 1);
                                            hasMoreNotes = false;
                                            break;
                                        default: {
                                            n = readInt(df, 1);
                                            skip(df, n);
                                        }
                                    }
                                    break;
                                case 0xf0:
                                    do {
                                        datInt = readInt(df, 1);
                                    } while (datInt != 0xf7);
                                    break;
                                default: {
                                    n = d2; // readInt(df,1);
                                    skip(df, n);
                                }
                            }
                            break;
                        default:
                            throw new IOException("File corrupted");
                    }
                    dPrev = d1;
                    Thread.yield();
                }
            }
        } catch (IOException e) {
            throw new Exception("Can't read:\n" + e.getMessage());
        } finally {
            file.close(df);
            seekPos = seekLen;
        }
        return composition;
    }

    private int readTimeDelta(int df) throws IOException {
        int timeDelta = 0;
        byte[] buf = new byte[1];
        do {
            file.read(df, buf, 0, 1);
            timeDelta = (timeDelta << 7) + (buf[0] & (byte) 0x7f);
            seekPos++;
        } while ((buf[0] & (byte) 0x80) != 0);

        return timeDelta;
    }

    private int readInt(int df, int len) throws IOException {
        int t = 0;
        int i;
        for (i = 0; i < len; ++i) {
            file.read(df, bufTmp, 0, 1);
            t = (t << 8) | 0xff & bufTmp[0];
        }
        seekPos += len;
        return t;
    }

    private void skip(int df, int n) throws IOException {
        while (n > 0) {
            intTmp = (n > bufTmp.length) ? bufTmp.length : n;
            file.read(df, bufTmp, 0, intTmp);
            seekPos += intTmp;
            n -= intTmp;
        }
    }

    public int getCurPercent() {
        return seekPos * 100 / seekLen;
    }

    public void reset() {
        seekPos = 0;
        seekLen = 4;
        cancelStatus = false;
    }

    public void cancel() {
        cancelStatus = true;
    }
}

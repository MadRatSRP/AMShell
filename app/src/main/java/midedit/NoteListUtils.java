package midedit;

import midedit.media.Composition;

/**
 *
 * @author user
 */
public class NoteListUtils {

    private NoteList bufferList;
    private byte bufferChannel;
    private int bufticksPer4;
    /**
     *
     */
    public static final int OVERWRITE = 0;
    /**
     *
     */
    public static final int REPLACE = 1;
    /**
     *
     */
    public static final int INSERT = 2;
    /**
     *
     */
    public static final byte NOTE_MARKED = 1;
    /**
     *
     */
    public static final byte NOTE_NOT_MARKED = 0;

    /**
     *
     */
    public NoteListUtils() {
        bufferList = null;
        bufferChannel = 0;
    }

    /**
     *
     * @param composition
     * @param channel
     * @param timeMarkBeg
     * @param timeMarkEnd
     * @param markNotes
     */
    public void copy2Buffer(Composition composition, byte channel,
            int timeMarkBeg, int timeMarkEnd, boolean markNotes) {
        bufferList = new NoteList();
        bufticksPer4 = composition.getTicksPer4();


        if (markNotes == false) {
            copySelected(bufferList, composition.getNoteListByChannel(channel));
        } else {
            copy(bufferList, bufticksPer4, channel, 0,
                    composition.getNoteListByChannel(channel), bufticksPer4, channel,
                    timeMarkBeg, timeMarkEnd, null, markNotes);
        }

        bufferChannel = channel;
    }

    /**
     *
     * @param composition
     * @param channel
     * @param timeBeg
     * @param attr
     * @return
     */
    public int paste2Composition(Composition composition, byte channel,
            int timeBeg, int attr) {
        int retVal = 0;
        if (bufferList != null) {
            NoteList dest = composition.getNoteListByChannel(channel);
            int newTicksPer4 = composition.getTicksPer4();
            UndoableAction undo = composition.getUndoableAction();
            undo.prepare();

            if (attr == INSERT) {
                undo.addAction(UndoableAction.DELETE_LIST);
                undo.addAction(UndoableAction.SHIFT_LIST);
                undo.addAction(UndoableAction.ADD_LIST);
                int bufLen = bufferList.searchNoteByTime(0x7fffffff).noteTime;
                shift(dest, timeBeg,
                        (bufLen * newTicksPer4) / bufticksPer4, undo);
                retVal = bufLen;
            } else if (attr == REPLACE) {
                undo.addAction(UndoableAction.DELETE_LIST);
                undo.addAction(UndoableAction.ADD_LIST);
                int bufLen = bufferList.searchNoteByTime(0x7fffffff).noteTime;
                dest.del(timeBeg, timeBeg
                        + (bufLen * newTicksPer4) / bufticksPer4,
                        channel, (byte) -1, (byte) 127, undo);
            } else if (attr == OVERWRITE) {
                undo.addAction(UndoableAction.ADD_LIST);
            }

            copy(dest, newTicksPer4, channel, timeBeg,
                    bufferList, bufticksPer4, bufferChannel, 0, 0x7fffffff, undo, false);
        }
        return retVal;
    }

    /**
     *
     * @param dest
     * @param src
     */
    private static void copySelected(NoteList dest, NoteList src) {
        Note note = src.getFirst();
        int timeShift = -1;
        for (; note != null; note = note.next) {
            if (note.mark == NOTE_MARKED) {
                if (timeShift == -1) {
                    timeShift = note.noteTime;
                }
                dest.add(new Note(note.noteTime - timeShift, note.noteChannel, note.noteLine, note.noteVolume, note.noteLength));
            }

        }

    }

    /**
     *
     * @param dest
     * @param destTicksPer4
     * @param chDest
     * @param tDestBeg
     * @param src
     * @param srcTicksPer4
     * @param chSrc
     * @param tSrcBeg
     * @param tSrcEnd
     * @param undo
     * @param onlyMarkSrc
     */
    private static void copy(NoteList dest, int destTicksPer4, byte chDest, int tDestBeg,
            NoteList src, int srcTicksPer4, byte chSrc, int tSrcBeg, int tSrcEnd,
            UndoableAction undo, boolean onlyMarkSrc) {
        int t, len;
        byte n;
        Note srcNote;
        for (srcNote = src.searchNoteByTime(tSrcBeg);
                srcNote != null && srcNote.noteTime <= tSrcEnd; srcNote = srcNote.next) {
            if (srcNote.noteChannel == chSrc && (srcNote.noteVolume == 0 && srcNote.noteTime - srcNote.noteLength >= tSrcBeg
                    || srcNote.noteVolume != 0 && srcNote.noteTime + srcNote.noteLength <= tSrcEnd)
                    && (srcNote.noteVolume != 0 || onlyMarkSrc)) {
                if (onlyMarkSrc) {
                    srcNote.mark = NOTE_MARKED;
                } else {
                    t = tDestBeg
                            + ((srcNote.noteTime - tSrcBeg) * destTicksPer4) / srcTicksPer4;
                    n = srcNote.noteLine;
                    len = (srcNote.noteLength * destTicksPer4) / srcTicksPer4;
                    dest.add(new Note(t, chDest, n, srcNote.noteVolume, len));

                    if (len > 0) {
                        dest.add(new Note(t + len, chDest, n, (byte) 0, len));
                    }
                    if (undo != null) {
                        undo.log2AddNoteList(t, chDest, n, srcNote.noteVolume, len);
                    }
                }
            }
        }
    }

    /**
     *
     * @param list
     * @param tBeg
     * @param shiftLen
     * @param undo
     */
    public static void shift(NoteList list, int tBeg, int shiftLen, UndoableAction undo) {
        Note note = list.searchNoteByTime(0x7fffffff);
        for (; note != null && note.noteTime >= tBeg; note = note.prev) {
            if (note.noteVolume == 0 && note.noteTime - note.noteLength < tBeg) {
                Note noteTmp;
                for (noteTmp = note.prev;
                        noteTmp != null && (noteTmp.noteChannel != note.noteChannel || noteTmp.noteLine != note.noteLine);
                        noteTmp = noteTmp.prev)
     ;

                if (noteTmp != null && noteTmp.noteChannel == note.noteChannel && noteTmp.noteLine == note.noteLine) {
                    if (undo != null) {
                        undo.log2DelNoteList(noteTmp.noteTime, noteTmp.noteChannel, noteTmp.noteLine, noteTmp.noteVolume, noteTmp.noteLength);
                    }

                    noteTmp.noteLength = note.noteTime - noteTmp.noteTime + shiftLen;

                    if (noteTmp.noteTime + noteTmp.noteLength < tBeg) {
                        noteTmp.noteLength = tBeg - noteTmp.noteTime;
                    }
                    note.noteLength = noteTmp.noteLength;

                    if (undo != null) {
                        undo.log2AddNoteList(noteTmp.noteTime, noteTmp.noteChannel, noteTmp.noteLine, noteTmp.noteVolume, noteTmp.noteLength);
                    }

                }
            }
            note.noteTime += shiftLen;
            if (note.noteTime < tBeg) {
                if (undo != null && note.noteVolume != 0) {
                    undo.log2DelNoteList(note.noteTime - shiftLen, note.noteChannel, note.noteLine, note.noteVolume, note.noteLength);
                    undo.log2AddNoteList(tBeg, note.noteChannel, note.noteLine, note.noteVolume, note.noteLength);
                }

                note.noteTime = tBeg;
            }
        }
        if (undo != null) {
            undo.logShift(tBeg, shiftLen, list.getFirst().noteChannel);
        }
    }

    /**
     *
     * @param composition
     * @param channel
     * @param timeBeg
     * @param timeEnd
     */
    public void delete(Composition composition, byte channel, int timeBeg, int timeEnd) {
        NoteList list = composition.getNoteListByChannel(channel);
        UndoableAction undo = composition.getUndoableAction();
        undo.prepare();
        undo.addAction(UndoableAction.DELETE_LIST);

        list.del(timeBeg, timeEnd, (byte) -1, (byte) -1, (byte) 127, undo);
        undo.addAction(UndoableAction.SHIFT_LIST);
        undo.addAction(UndoableAction.ADD_LIST);
        shift(list, timeBeg, timeBeg - timeEnd, undo);
    }

    /**
     *
     * @param composition
     * @param channel
     */
    public static void unMarkNotes(Composition composition, byte channel) {
        NoteList list = composition.getNoteListByChannel(channel);
        for (Note note = list.getFirst(); note != null; note = note.next) {
            note.mark = NOTE_NOT_MARKED;
        }
    }

    /**
     *
     * @param composition
     * @param channel
     */
    public static void deleteSelected(Composition composition, byte channel) {
        UndoableAction undo = composition.getUndoableAction();
        undo.prepare();
        undo.addAction(UndoableAction.DELETE_LIST);
        NoteList list = composition.getNoteListByChannel(channel);
        list.delSelected(undo);

    }

    /**
     *
     * @param composition
     * @param channel
     * @param shiftTime
     * @param shiftNote
     * @param shiftVol
     */
    public static void doMove(Composition composition, byte channel, int shiftTime, int shiftNote, int shiftVol) {
        UndoableAction undo = composition.getUndoableAction();
        undo.addAction(UndoableAction.ADD_LIST);
        NoteList dest = composition.getNoteListByChannel(channel);
        int newTime;
        byte newVol;
        int tmp;
        byte newN;
        for (Note note = undo.getDeletedList().getFirst();
                note != null; note = note.next) {
            if (note.noteChannel == channel && note.noteVolume != 0) {
                newTime = note.noteTime + shiftTime;
                if (newTime < 0) {
                    newTime = 0;
                }
                newN = (byte) (note.noteLine + shiftNote);

                tmp = note.noteVolume + shiftVol;
                if (tmp < 1) {
                    tmp = 1;
                } else if (tmp > 127) {
                    tmp = 127;
                }
                newVol = (byte) tmp;

                dest.add(new Note(newTime, channel, newN, newVol, note.noteLength));

                if (note.noteLength > 0) {
                    dest.add(new Note(newTime + note.noteLength, channel, newN, (byte) 0, note.noteLength));
                }
                if (undo != null && note.noteVolume != 0) {
                    undo.log2AddNoteList(newTime, channel, newN, newVol, note.noteLength);
                }

            }
        }
    }
}

package midedit;

import midedit.media.Composition;

/**
 *
 * @author user
 */
public class UndoableAction {

    public static final byte DELETE_LIST = 1;
    public static final byte ADD_LIST = 2;
    public static final byte SHIFT_LIST = 3;
    public static final byte ADD_NOTE = 4;
    public static final byte DEL_NOTE = 5;
    
    private static final byte NUM_ACT = 3;
    
    private NoteList dellist = null;
    private NoteList addlist = null;
    
    private Note noteAddDel;

    private int indOrder = 0;
    private int shiftBeg;
    private int shiftLen;
    private byte[] order = new byte[NUM_ACT];
    private Composition composition;
    private byte channel;
    private boolean canUndo;
    private boolean canRedo;

    public UndoableAction(Composition c) {
        composition = c;
        prepare();
    }

    public void addAction(byte n) {
        order[indOrder++] = n;
        canUndo = true;
        canRedo = false;
    }

    public NoteList getDeletedList() {
        return dellist;
    }

    public void clean() {
        dellist = null;
        addlist = null;
        indOrder = 0;
    }

    public void prepare() {
        order[0] = 0;
        indOrder = 0;
        dellist = new NoteList();
        addlist = new NoteList();
    }

    public void logAddNote(Note singleNote) {
        channel = singleNote.noteChannel;
        clean();
        addAction(ADD_NOTE);
        noteAddDel = singleNote;
    }

    public void logDelNote(Note singleNote) {
        channel = singleNote.noteChannel;
        clean();
        addAction(DEL_NOTE);
        noteAddDel = singleNote;
    }
    
    public void log2AddNoteList(int t, byte ch, byte n, byte v, int len) {
        addlist.add(new Note(t, ch, n, v, len));
    }

    public void log2DelNoteList(int t, byte ch, byte n, byte v, int len) {
        dellist.add(new Note(t, ch, n, v, len));
    }

    public void logShift(int timeBeg, int len, byte ch) {
        channel = ch;
        shiftBeg = timeBeg;
        shiftLen = len;
    }

    private void doAction() {
        NoteList list;
        for (int i = indOrder - 1; i >= 0; --i) {
            switch (order[i]) {
                case ADD_NOTE:
                    composition.getNoteListByChannel(channel).delOne(noteAddDel.noteTime, noteAddDel.noteChannel,
                            noteAddDel.noteLine, noteAddDel.noteVolume);
                    if (noteAddDel.noteLength != 0) {
                        composition.getNoteListByChannel(channel).delOne(noteAddDel.noteTime + noteAddDel.noteLength,
                                noteAddDel.noteChannel, noteAddDel.noteLine, (byte) 0);
                    }
                    break;
                case DEL_NOTE:
                    composition.getNoteListByChannel(channel).add(new Note(noteAddDel.noteTime, noteAddDel.noteChannel,
                            noteAddDel.noteLine, noteAddDel.noteVolume, noteAddDel.noteLength));
                    if (noteAddDel.noteLength != 0) {
                        composition.getNoteListByChannel(channel).add(new Note(noteAddDel.noteTime + noteAddDel.noteLength,
                                noteAddDel.noteChannel, noteAddDel.noteLine, (byte) 0, noteAddDel.noteLength));
                    }
                    break;
                case DELETE_LIST:
                    Note noteLocal = dellist.getFirst();
                    if (noteLocal != null) {
                        channel = noteLocal.noteChannel;
                    }
                    list = composition.getNoteListByChannel(channel);
                    for (noteLocal = dellist.getFirst(); noteLocal != null; noteLocal = noteLocal.next) {
                        list.add(new Note(noteLocal.noteTime, noteLocal.noteChannel, noteLocal.noteLine, noteLocal.noteVolume, noteLocal.noteLength));
                        if (noteLocal.noteLength != 0) {
                            list.add(new Note(noteLocal.noteTime + noteLocal.noteLength, noteLocal.noteChannel, noteLocal.noteLine, (byte) 0, noteLocal.noteLength));
                        }
                    }
                    break;
                case ADD_LIST:
                    noteLocal = addlist.getFirst();
                    if (noteLocal != null) {
                        channel = noteLocal.noteChannel;
                    }

                    list = composition.getNoteListByChannel(channel);
                    for (noteLocal = addlist.getFirst(); noteLocal != null; noteLocal = noteLocal.next) {
                        list.delOne(noteLocal.noteTime, noteLocal.noteChannel, noteLocal.noteLine, noteLocal.noteVolume);
                        if (noteLocal.noteLength != 0) {
                            list.delOne(noteLocal.noteTime + noteLocal.noteLength, noteLocal.noteChannel, noteLocal.noteLine, (byte) 0);
                        }
                    }
                    break;
                case SHIFT_LIST:
                    NoteListUtils.shift(composition.getNoteListByChannel(channel), shiftBeg, -shiftLen, null);
                    break;
            }
        }
        reverseActOrder();
        canUndo = !canUndo;
        canRedo = !canRedo;
    }

    private void reverseActOrder() {
        byte[] newOrder = new byte[NUM_ACT];
        NoteList newAddlist = addlist, newDellist = dellist;
        for (int i = 0; i < indOrder; ++i) {
            switch (order[indOrder - 1 - i]) {
                case ADD_NOTE:
                    newOrder[i] = DEL_NOTE;
                    break;
                case DEL_NOTE:
                    newOrder[i] = ADD_NOTE;
                    break;
                case ADD_LIST:
                    newOrder[i] = DELETE_LIST;
                    newDellist = addlist;
                    break;
                case DELETE_LIST:
                    newOrder[i] = ADD_LIST;
                    newAddlist = dellist;
                    break;
                case SHIFT_LIST:
                    newOrder[i] = SHIFT_LIST;
                    shiftLen = -shiftLen;
                    break;
            }
        }
        addlist = newAddlist;
        dellist = newDellist;
        order = newOrder;
    }

    /**
     *
     */
    public void undo() {
        if (canUndo) {
            doAction();
        }
    }

    /**
     *
     */
    public void redo() {
        if (canRedo) {
            doAction();
        }
    }
}

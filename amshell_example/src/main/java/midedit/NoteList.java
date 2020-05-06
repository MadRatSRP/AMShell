package midedit;

/**
 *
 * @author user
 */
public class NoteList {

    private Note note = null, noteBeg = null;
    private Note noteTmp;
    private Note noteNew;
    private int len = 0;
    private int timeLastDelOn = -1;
    private int timeNoteOff;
    private int t;
    /**
     *
     */
    public static final int CHANNEL_LONG_NOTE = -2;

    /**
     *
     * @param noteExist
     * @return
     */
    public Note add(Note noteExist) {
        if (noteExist == null) {
            return null;
        }
        t = noteExist.noteTime;
        len++;
        if (noteBeg == null) {
            noteBeg = note = noteExist;
        } else {
            noteNew = noteExist;
            if (note == null) {
                note = noteBeg;
            }
            if (note.noteTime != t) {
                if (t < note.noteTime) {
                    for (noteTmp = note;
                            noteTmp != null && t < noteTmp.noteTime;
                            noteTmp = noteTmp.prev);
                    if (noteTmp == null) {
                        addInto(null, noteNew, noteBeg);
                        return noteNew;
                    }
                    note = noteTmp;
                } else {
                    for (noteTmp = note;
                            noteTmp.next != null && (noteTmp.next.noteTime < t);
                            noteTmp = noteTmp.next);
                    note = noteTmp;
                }
            }
            if (noteExist.noteVolume != 0) {
                for (; note.next != null && t == note.next.noteTime; note = note.next);
            } else {
                for (; note.prev != null && t == note.noteTime; note = note.prev);
            }

            addInto(note, noteNew, note.next);
            note = noteNew;
        }
        return note;
    }

    private void addInto(Note n1, Note note, Note n2) {
        note.next = n2;
        if (n2 != null) {
            n2.prev = note;
        }
        note.prev = n1;
        if (n1 != null) {
            n1.next = note;
        } else {
            noteBeg = note;
        }
    }

    /**
     *
     * @param noteTime
     * @param noteChannel
     * @param noteLine
     * @param noteVolume
     */
    public void delOne(int noteTime, byte noteChannel, byte noteLine, byte noteVolume) {
        for (note = searchNoteByTime(noteTime); note != null && note.noteTime <= noteTime; note = note.next) {
            if (noteTime == note.noteTime && noteChannel == note.noteChannel && noteLine == note.noteLine && (noteVolume != 0 ^ note.noteVolume == 0)) {
                if (note.next != null) {
                    note.next.prev = note.prev;
                }
                if (note.prev != null) {
                    note.prev.next = note.next;
                } else {
                    noteBeg = note.next;
                }

                note = (note.next != null) ? note.next : note.prev;
                len--;
                return;
            }
        }
    }

    /**
     *
     * @param delNote
     */
    public void delOne(Note delNote) {
        for (note = getFirst(); note != null && note.noteTime <= t; note = note.next) {
            if (note == delNote) {
                if (note.next != null) {
                    note.next.prev = note.prev;
                }
                if (note.prev != null) {
                    note.prev.next = note.next;
                } else {
                    noteBeg = note.next;
                }

                note = (note.next != null) ? note.next : note.prev;
                len--;
                return;
            }
        }
    }

    /**
     *
     * @param undo
     */
    public void delSelected(UndoableAction undo) {
        for (note = getFirst(); note != null; note = note.next) {
            if (note.mark == NoteListUtils.NOTE_MARKED) {
                if (note.next != null) {
                    note.next.prev = note.prev;
                }
                if (note.prev != null) {
                    note.prev.next = note.next;
                } else {
                    noteBeg = note.next;
                }

                if (undo != null && note.noteVolume != 0) {
                    undo.log2DelNoteList(note.noteTime, note.noteChannel, note.noteLine, note.noteVolume, note.noteLength);
                }

                len--;
            }
        }
    }

    /**
     *
     * @param noteTime
     * @param tMax
     * @param noteChannel
     * @param noteLine
     * @param noteVolume
     * @param undo
     * @return
     */
    public int del(int noteTime, int tMax, byte noteChannel, byte noteLine, byte noteVolume, UndoableAction undo) {
        if (noteTime == tMax) {
            tMax++;
        }
        int tOffMax = tMax;
        boolean isdel = false;
        timeNoteOff = -1;

        for (note = searchNoteByTime(noteTime); note != null && note.noteTime <= tMax; note = note.next) {
            if ((note.noteChannel == noteChannel || noteChannel == (byte) -1)
                    && (note.noteTime > noteTime || noteVolume != 0 && note.noteTime == noteTime)
                    && (note.noteTime < tMax || noteVolume == 0 && note.noteTime == tMax)
                    && (note.noteLine == noteLine || noteLine == (byte) -1)
                    && ((noteVolume != 0 && (note.noteVolume != 0 || note.noteTime - note.noteLength >= noteTime))
                    || (noteVolume == 0 && (note.noteVolume == 0 && timeLastDelOn <= note.noteTime - note.noteLength && note.noteTime - note.noteLength < noteTime)))) {
                if (note.next != null) {
                    note.next.prev = note.prev;
                }
                if (note.prev != null) {
                    note.prev.next = note.next;
                } else {
                    noteBeg = note.next;
                }

                len--;
                if (noteVolume != 0) {
                    if (tOffMax < note.noteTime + note.noteLength) {
                        tOffMax = note.noteTime + note.noteLength;
                    }
                    if (undo != null && note.noteVolume != 0) {
                        undo.log2DelNoteList(note.noteTime, note.noteChannel, note.noteLine, note.noteVolume, note.noteLength);
                    }
                }
                if (note.noteVolume == 0) {
                    timeNoteOff = note.noteTime;
                }
                isdel = true;
            }
        }

        if (noteVolume != 0 && tOffMax > tMax) {
            timeLastDelOn = noteTime;
            del(tMax, tOffMax, noteChannel, noteLine, (byte) 0, null);
        } else {
            timeLastDelOn = -1;
        }
        if (isdel == false) {
            return -2;
        }
        return timeNoteOff;
    }

    /**
     *
     * @param noteChannel
     * @param noteLine
     * @return
     */
    public Note searchBack(byte noteChannel, byte noteLine) {
        if (note == null) {
            return null;
        }
        for (noteTmp = note;
                noteTmp != null && (noteTmp.noteChannel != noteChannel || noteTmp.noteLine != noteLine);
                noteTmp = noteTmp.prev)
   ;

        return noteTmp;
    }

    /**
     *
     * @param noteTime
     * @return
     */
    public Note searchNoteByTime(int noteTime) {
        if (note == null) {
            if (noteBeg != null) {
                note = noteBeg;
            } else {
                return null;
            }
        }
        if (noteTime <= note.noteTime) {
            for (noteTmp = note;
                    noteTmp.prev != null && noteTime <= noteTmp.noteTime;
                    noteTmp = noteTmp.prev)
    ;
            if (noteTmp.noteTime < noteTime && noteTmp.next != null) {
                noteTmp = noteTmp.next;
            }
            note = noteTmp;
        } else {
            for (noteTmp = note;
                    noteTmp.next != null && noteTmp.noteTime < noteTime;
                    noteTmp = noteTmp.next)
    ;
            note = noteTmp;
        }
        return note;
    }

    /**
     *
     * @return
     */
    public int getLen() {
        return len;
    }

    /**
     *
     * @return
     */
    public Note getFirst() {
        note = noteBeg;
        return note;
    }
}

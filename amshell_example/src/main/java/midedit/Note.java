package midedit;

/**
 * Нота
 * @author user
 */
public class Note {

    /** Следующая нота */
    public Note next = null;
    /** Предыдущая нота */
    public Note prev = null;
    
    /** Время (позиция) с которой должна воспроизводится нота */
    public int noteTime;
    /** Длина ноты */
    public int noteLength;
    /** Канал ноты */
    public byte noteChannel;
    /** Номер линии */
    public byte noteLine;
    /** Громкость */
    public byte noteVolume;
    /** Выделение */
    public byte mark;

    /**
     * Конструктор
     * @param time время (позиция) с которой должна воспроизводится нота
     * @param channel канал ноты
     * @param line номер линии
     * @param volume громкость
     * @param length длина ноты
     */
    public Note(int time, byte channel, byte line, byte volume, int length) {
        noteTime = time;
        noteChannel = channel;
        noteLine = line;
        noteVolume = volume;
        noteLength = length;
        mark = NoteListUtils.NOTE_NOT_MARKED;
    }
}
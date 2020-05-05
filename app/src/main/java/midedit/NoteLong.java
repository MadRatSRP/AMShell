package midedit;

/**
 * Длина ноты
 * @author user
 */
public class NoteLong extends Note {

    /** Данные ноты */
    public byte[] dat;
    
    /**
     * Конструктор
     * @param time время (позиция) с которой должна воспроизводится нота
     * @param data данные
     */
    public NoteLong(int time, byte[] data) {
        super(time, (byte) NoteList.CHANNEL_LONG_NOTE, (byte) 0, (byte) 0, 0);
        dat = data;
    }
    
}

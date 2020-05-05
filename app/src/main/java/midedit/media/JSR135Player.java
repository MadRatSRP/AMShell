package midedit.media;

import midedit.MixerModel;
import javax.microedition.media.*;
import java.io.*;

/**
 * Класс воспроизведения мидишек
 * @author user
 */
public class JSR135Player extends AbstractPlayer implements PlayerListener {

    private Player player;
    
    /**
     * Событие при обновлении состояния плеера
     * @param player сам плеер
     * @param event событие
     * @param eventData данные события
     */
    public void playerUpdate(Player player, String event, Object eventData) {
        if (event.equals(STARTED)) {
            setIsPlaying(true);
            return;
        }
        if (event.equals(STOPPED) || event.equals(CLOSED) || event.equals(END_OF_MEDIA)) {
            setIsPlaying(false);
        }
        return;
    }

    /**
     * Воспроизведение мелодии по указанному пути
     * @param path путь к файлу
     * @throws Exception не удалось воспроизвести
     */
    public void play(String path) throws Exception {
        if (player != null) {
            player.close();
        }
        InputStream is = MixerModel.getLocalFile().getInputStreambyURL(path);
        player = Manager.createPlayer(is, "audio/midi");
        player.addPlayerListener(this);
        player.realize();
        player.start();
    }
    
    /**
     * Воспроизведение мелодии
     * @param writeTest массив байт midi-мелодии
     */
    public void playTest(byte[] writeTest) {
        ByteArrayInputStream is = new ByteArrayInputStream(writeTest);
        if (player != null) {
            player.close();
        }

        try {
            player = Manager.createPlayer(is, "audio/midi");
            player.addPlayerListener(this);
            player.realize();
            player.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Остановить плеер
     * @throws Exception
     */
    protected void stop() throws Exception {
        if (player != null) {
            player.stop();
        }
    }

    /**
     * Закрыть плеер и освободить память
     */
    public void close() {
        if (player != null) {
            player.close();
        }
        player = null;
        System.gc();
    }
    
}

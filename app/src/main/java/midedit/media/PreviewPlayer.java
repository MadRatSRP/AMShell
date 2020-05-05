package midedit.media;

import midedit.MixerModel;
import javax.microedition.media.*;
import java.io.*;

/**
 *
 * @author user
 */
public class PreviewPlayer implements PlayerListener {

    private boolean isPlaying = false;
    private Player player;
    
    public void playerUpdate(Player player, String event, Object eventData) {
        if (event.equals(STARTED)) {
            isPlaying = true;
            return;
        }
        if (event.equals(STOPPED) || event.equals(CLOSED) || event.equals(END_OF_MEDIA)) {
            isPlaying = false;
        }
    }

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

    public void stop() throws Exception {
        if (player != null) {
            player.stop();
        }
    }

    public void close() {
        if (player != null) {
            player.close();
        }
        player = null;

    }

    public boolean isPlaying() {
        return isPlaying;
    }
    
}

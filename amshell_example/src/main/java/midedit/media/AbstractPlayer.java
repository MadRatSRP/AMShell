package midedit.media;

import midedit.CompositionForm;
import javax.microedition.lcdui.*;

/**
 *
 * @author user
 */
public abstract class AbstractPlayer {

    private Displayable commandForm;
    
    public final void setCommandForm(Displayable d) {
        commandForm = d;
    }

    public abstract void play(String path) throws Exception;

    protected abstract void stop() throws Exception;

    public abstract void close();

    /**
     *
     * @param is
     */
    protected void setIsPlaying(boolean is) {
        CompositionForm.isPlaying = is;
        if (is) {
            commandForm.addCommand(CompositionForm.stop);
        } else {
            commandForm.removeCommand(CompositionForm.stop);
        }
    }

    public abstract void playTest(byte[] writeTest);
}

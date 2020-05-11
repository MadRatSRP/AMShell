package midedit;

import midedit.media.Composition;
import midedit.media.AbstractPlayer;
import midedit.media.JSR135Player;
import midedit.io.RMSFile;
import midedit.io.JSR75File;
import midedit.io.AbstractFile;
import main.Main;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import main.L;
import main.P;

/**
 *
 * @author user
 */
public class MixerModel extends Thread {

    private MidiFile mfile = null;
    /**
     * аАа«аАаЃаАа«аАТЎаАа¦аАаіаАа«аАТЛаАа«аАтЂјаАа¦аАтЂђ аАа«аАб»аАа«аАтІ™аАа¦а‘а¦аАа«аАа аАа«аАтЂЙаАа«аАТЛаАа¦аАтЂЎ
     */
    public final Display display = Main.dsp;
    private NoteListUtils buffer;
    static AbstractFile rmsFile = new RMSFile();
    static AbstractFile jsr75File;
    public AbstractPlayer crossPlayer = null;

    public MixerModel() {
        try {
            if (System.getProperty("microedition.io.file.FileConnection.version") != null) {
                jsr75File = new JSR75File();
            } else {
                jsr75File = rmsFile;
            }
            if (crossPlayer == null) {
                try {
                    Class.forName("com.silentknight.amshell.javax.microedition.media.Player");
                    crossPlayer = new JSR135Player();
                } catch (ClassNotFoundException ex1) {
                }
            }
        } catch (Exception e) {
            Alert a = new Alert(L.str[L.apiError], L.str[L.apiError] + ":\n" + e.getMessage(), null, null);
            display.setCurrent(a);
        }
        mfile = new MidiFile(getLocalFile());
        buffer = new NoteListUtils();
    }

    /**
     *
     * @return
     */
    public NoteListUtils getBuffer() {
        return buffer;
    }

    /**
     *
     * @param c
     * @param t
     * @throws Exception
     */
    public void playMix(Composition c, int t) throws Exception {
        crossPlayer.close();
        getLocalFile().close(0);
        setLocalFile();
        mfile.writeMix(c, t, true);
        String path = mfile.getURI();
        crossPlayer.play(path);
    }

    /**
     *
     * @param instrum
     * @param nn
     */
    public void playNote(byte instrum, byte nn) {
        try {
            crossPlayer.close();
            crossPlayer.playTest(mfile.writeNote(instrum, nn));
        } catch (Exception e) {
            CompositionForm.msg("MixerModel: 91: playNote " + e.getMessage());
        }
    }

    public void playTest(byte instrum) {
        crossPlayer.close();
        mfile.writeTest(instrum);
        crossPlayer.playTest(mfile.writeTest(instrum));
    }

    /**
     *
     * @param c
     * @param t
     * @param channel
     * @param instrument
     * @throws Exception
     */
    public void playTrack(Composition c, int t, int channel, int instrument) throws Exception {
        crossPlayer.close();
        getLocalFile().close(0);
        setLocalFile();
        mfile.writeMixWithName(c, t, true, "out_track.mid", channel, instrument);
        String path = mfile.getURI();
        crossPlayer.play(path);
    }

    /**
     *
     * @param c
     * @param t
     * @param channel
     * @throws Exception
     */
    public void playTrack(Composition c, int t, int channel) throws Exception {
        playTrack(c, t, channel, -1);
    }

    /**
     *
     */
    public void stopPlay() {
        if(crossPlayer != null)
            crossPlayer.close();
    }


    /**
     *
     * @param c
     * @param name
     * @return
     * @throws Exception
     */
    public String saveMix(Composition c, String name) throws Exception {
        return mfile.writeMixWithName(c, 0, false, name);
    }

    /**
     *
     */
    public void resetProgress() {
        mfile.reset();
    }

    /**
     *
     * @param name
     * @return
     * @throws Exception
     */
    public Composition openMix(String name) throws Exception {
        Composition c;
        if (name != null) {
            c = mfile.readMix(name);
            c.setName(name);
        } else {
            c = new Composition();
            c.addTemp(0, 500000);
        }
        return c;
    }

    /**
     *
     * @return
     */
    public Composition newMix() {
        Composition c = new Composition();
        return c;
    }

    /**
     *
     * @return
     */
    public Waitable getWaitableFile() {
        return mfile;
    }

    /**
     *
     */
    public void setLocalFile() {
        mfile.setFile();
    }

    /**
     *
     * @return
     */
    public static AbstractFile getLocalFile() {
        return P.isRMSMode ? rmsFile : jsr75File;
    }
}

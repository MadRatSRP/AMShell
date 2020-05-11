package midedit;


import midedit.media.Composition;
import java.util.*;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import main.L;
import main.Main;
import main.P;

/**
 *
 * @author user
 */
public class VolumeForm extends Form implements CommandListener {

    private Command playOrig = new Command(L.str[L.playOrigin], Command.ITEM, 1);
    private Gauge[] volumeGauges;
    private ChoiceGroup[] soloChecks;
    private Vector channels = null;
    private Composition composition;
    private Displayable backForm;
    private MixerModel model;
    private byte gaugeVolMax = 16;
    private byte[] dupVols = null;
    private boolean[] dupSolo = null;

    /**
     *
     * @param c
     * @param chans
     * @param m
     * @param back
     */
    public VolumeForm(Composition c, Vector chans, MixerModel m, Displayable back) {
        super(L.str[L.volume]);
        composition = c;
        channels = chans;
        model = m;
        backForm = back;
        dupVols = composition.getVolumeExpArr();
        dupSolo = composition.getSoloList();
        buildContent();
    }

    private void buildContent() {
        int size = channels.size();
        soloChecks = new ChoiceGroup[size];
        volumeGauges = new Gauge[size];
        String[] muteString = new String[1];
        muteString[0] = L.str[L.solo];
        String gaugeString = "";
        boolean[] solo = composition.getSoloList();
        for (int i = 0; i < size; ++i) {
            byte channel = ((Byte) channels.elementAt(i)).byteValue();
            soloChecks[i] = new ChoiceGroup(L.str[L.channel] + " #" + channel + ":\n" +
                    Constants.getInstrName(composition.getInstrument(channel) + 1),
                    Choice.MULTIPLE, muteString, null);
            soloChecks[i].setSelectedIndex(0, solo[channel]);
            volumeGauges[i] = new Gauge(gaugeString, true, gaugeVolMax, convVolExp2Gauge(dupVols[channel]));
            this.append(soloChecks[i]);
            this.append(volumeGauges[i]);
            this.append("     ");
        }
        this.addCommand(CompositionForm.play);
        this.addCommand(playOrig);
        this.addCommand(CompositionForm.ok);
        this.addCommand(P.comCancel);
        this.setCommandListener(this);
    }

    /**
     *
     * @param command
     * @param displayable
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command == CompositionForm.ok) {
            setVolumeFromGaugesSolo();
            Main.dsp.setCurrent(backForm);
        } else if (command == P.comCancel) {
            restoreVolumesArr();
            Main.dsp.setCurrent(backForm);
        } else if (command == CompositionForm.play) {
            setVolumeFromGaugesSolo();
            try {
                model.playMix(composition, MixerCanvas.xBase * Constants.timeConst);
                this.addCommand(CompositionForm.stop);
            } catch (Exception ex) {
            }

        } else if (command == playOrig) {
            restoreVolumesArr();
            try {
                model.playMix(composition, MixerCanvas.xBase * Constants.timeConst);
                this.addCommand(CompositionForm.stop);
            } catch (Exception ex) {
            }
        } else if (command == CompositionForm.stop) {
            model.stopPlay();
            this.removeCommand(CompositionForm.stop);
        }
    }



    private void setVolumeFromGaugesSolo() {
        for (int ind = 0; ind < channels.size(); ++ind) {
            byte channel = ((Byte) channels.elementAt(ind)).byteValue();
            composition.setVolumeExp(channel,
                    convVolGauge2Exp(volumeGauges[ind].getValue()));
            composition.setSolo(channel, soloChecks[ind].isSelected(0));
        }
        composition.setSoloMode(isSolo());

    }

    private boolean isSolo() {
        boolean solo = false;
        for (int ind = 0; ind < channels.size(); ++ind) {
            if (soloChecks[ind].isSelected(0)) {
                solo = true;
            }
        }

        return solo;
    }

    private void restoreVolumesArr() {
        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            composition.setVolumeExp(i, dupVols[i]);
            composition.setSolo(i, dupSolo[i]);
        }
        composition.setSoloMode();
    }

    private byte convVolGauge2Exp(int val) {
        return (byte) (val * Composition.channelVolExpMax / gaugeVolMax);
    }

    private int convVolExp2Gauge(byte e) {
        return e * gaugeVolMax / Composition.channelVolExpMax;
    }
}

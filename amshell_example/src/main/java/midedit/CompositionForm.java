package midedit;


import midedit.media.Composition;
import java.util.*;
import javax.microedition.lcdui.*;
import main.L;
import main.P;
import main.Main;
import ui.Instruments;
import ui.WaitCanvas;

/**
 * пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
 * @author user
 * @author aNNiMON (пїЅпїЅпїЅ)
 */
public class CompositionForm extends Form implements CommandListener, Runnable {

    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private static final byte SEEK_GAUGE_MAX = 10;
    
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    public static boolean isPlaying;
    
    
    /**
     * пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ/пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ.
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ.
     */
    private static Canvas listInstrumentsCanvas;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private Main control;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private Display display;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private MixerModel model;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private Composition composition;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ */
    private NotesCanvas notesCanvas;
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private DrumsCanvas drumsCanvas;
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private MixerCanvas curCanvas;
    
    /** пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private ChannelChoiceGroup choiСЃeInstrument;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private Gauge seekingGauge;
    
    /** пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ */
    private String fileName;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private byte seekGaugeCur = 0;
    
    /** пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅ */
    private boolean isNew = false;
    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    private boolean isAdd;

    /** пїЅпїЅпїЅпїЅпїЅпїЅпїЅ */
    public static Command play = new Command(L.str[L.play], Command.ITEM, 1);
    public static Command stop = new Command(L.str[L.stop], Command.BACK, 0);
    public static Command back = new Command(L.str[L.back], Command.BACK, 2);
    public static Command ok = new Command(L.str[L.ok], Command.ITEM, 1);
    
    private Command edit = new Command(L.str[L.edit], Command.ITEM, 1);
    private Command addInstrument = new Command(L.str[L.addInstrument], Command.ITEM, 2);
    private Command setInstrument = new Command(L.str[L.setInstrument], Command.ITEM, 3);
    private Command delInstrument = new Command(L.str[L.delInstrument], Command.ITEM, 4);
    private Command temp = new Command(L.str[L.tempoBox], Command.ITEM, 5);
    private Command volume = new Command(L.str[L.volume], Command.ITEM, 6);
    private Command meter = new Command(L.str[L.meter], Command.ITEM, 7);
    
    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param control пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @throws Exception
     */
    public CompositionForm(Main control) throws Exception {
        this(control, null);
        isNew = true;
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ 
     * @param control пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @param fName пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ
     * @throws Exception
     */
    public CompositionForm(Main control, String fName) throws Exception {
        super((fName != null) ? fName : "New");
        this.control = control;
        model = control.getModel();
        model.crossPlayer.setCommandForm(CompositionForm.this);
        fileName = fName;
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @return
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ, пїЅпїЅпїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param isNewComposition
     */
    public void setNew(boolean isNewComposition) {
        isNew = isNewComposition;
    }

    /**
     *
     */
    public void setComposForm() {
        seekingGauge.setValue((composition.getCurWInPercent() + SEEK_GAUGE_MAX / 2) / SEEK_GAUGE_MAX);
        seekGaugeCur = (byte) seekingGauge.getValue();
        display.setCurrent(this);
    }
    
    /**
     *
     */
    public void run() {
        display = main.Main.dsp;

        WaitCanvas waitCanvas = new WaitCanvas(L.str[L.opening],
                model.getWaitableFile(),
                control.getCurrentlistMenu());
        display.setCurrent(waitCanvas);
        model.resetProgress();
        new Thread(waitCanvas).start();
        try {
            composition = model.openMix(fileName);
            buildContent();
            display.callSerially(new Runnable() {

                public void run() {
                    display.setCurrent(CompositionForm.this);
                }
            });
        } catch (Exception ex) {
            Alert a = new Alert(L.str[L.error], L.str[L.openError] + "\n" + ex.toString(), null, null);//@@@
            a.setTimeout(Alert.FOREVER);
            waitCanvas.cancel();
            control.setCurrentlistMenu(a);
        }
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     */
    public void releaseMem() {
        if (composition != null) {
            composition.deleteNoteList();
        }
    }

    private void buildContent() throws Exception {
        notesCanvas = null;
        drumsCanvas = null;
        curCanvas = null;
        MixerCanvas.xBase = 0;
        MixerCanvas.curX = 0;

        String[] instrumentsStrings = composition.getInstrumentsStrings();
        if (instrumentsStrings == null) {
            throw new Exception("instruments == null");
        }

        choiСЃeInstrument = new ChannelChoiceGroup(L.str[L.menu], instrumentsStrings, this.buildChansList());
        if (choiСЃeInstrument == null) {
            throw new Exception("choiпїЅeInstrument == null");
        }
        
        this.append(choiСЃeInstrument);
        seekingGauge = new Gauge(L.str[L.seek], true, SEEK_GAUGE_MAX, 0);
        this.append(seekingGauge);
        this.addCommand(edit);
        this.addCommand(play);
        isPlaying = false;
        this.addCommand(volume);
        this.addCommand(temp);
        this.addCommand(meter);
        this.addCommand(addInstrument);
        this.addCommand(setInstrument);
        this.addCommand(delInstrument);
        this.addCommand(back);
        this.setCommandListener(this);
        P.isRMSMode = true;
    }

    private byte[] buildChansList() {
        int[] instruments = composition.getInstruments();
        int size = 0;
        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            if (instruments[i] != Composition.NOTHING) {
                size++;
            }
        }
        byte[] chans = new byte[size];
        int j = 0;
        for (int i = 0; i < Constants.NCHANNEL; ++i) {
            if (instruments[i] != Composition.NOTHING) {
                chans[j++] = (byte) i;
            }
        }
        return chans;
    }

    
    /**
     *
     * @param play пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ ok
     * @param instrName
     */
    public void selectInstrument(boolean play, String instrName) {
        int instrumNum;
        model.stopPlay();
        if(instrName.equals(Constants.getInstrName(0))) instrumNum = -1;
        else instrumNum = Constants.instrVectorArr.indexOf(instrName);
        if (play) {
            int channel = -1;
            int lengthOfChannel = 0;
            try {
                channel = choiСЃeInstrument.getSelectedChannel();
                lengthOfChannel = composition.tracks[channel].getLen();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            if (channel == Constants.DRUMS_CHANNEL || isAdd || lengthOfChannel < 3) {
                try {
                    model.playTest((byte) instrumNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    model.playTrack(composition, channel, instrumNum);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        model.playTest((byte) instrumNum);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            // ok
            if (isAdd) {
                byte channel = composition.addInstrument(instrumNum);
                if (channel != -1) {
                    choiСЃeInstrument.appendChannel(instrName, channel);
                } else {
                    Alert a = new Alert(L.str[L.listInstruments], L.str[L.impossible], null, null);
                    display.setCurrent(a);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            } else {
                try {
                    int channel = choiСЃeInstrument.getSelectedChannel();
                    if (channel != -1 && channel != Constants.DRUMS_CHANNEL) {
                        choiСЃeInstrument.setChannel(choiСЃeInstrument.getSelectedIndex(), instrName, (byte) channel);
                        composition.setInstrument(channel, instrumNum);
                    }
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
            display.setCurrent(CompositionForm.this);
        }
    }

    /**
     *
     * @param command
     * @param displayable
     */
    public void commandAction(Command command, Displayable displayable) {
        int newVal = seekingGauge.getValue();
        if (newVal != seekGaugeCur) {
            byte channel=0;
            try {
                channel = choiСЃeInstrument.getSelectedChannel();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            try {
                composition.setCurW(seekingGauge.getValue() * 100 / SEEK_GAUGE_MAX, channel);
            } catch (NullPointerException nullExc) {
                seekingGauge.setValue(0);
            }
        }
        if ((command == play) && (displayable == this)) {
            try {
                model.playMix(composition, 0);
            } catch (Exception e) {
                msg(e.getMessage());
            }
        } else if (command == P.comCancel) {
            model.stopPlay();
            display.setCurrent(CompositionForm.this);
        } else if (command == stop) {
            model.stopPlay();
        } else if (command == edit) {
            int channel;
            try {
                channel = choiСЃeInstrument.getSelectedChannel();
            } catch (IllegalAccessException ex) {
                channel = -1;
            }
            if (channel == -1) {
                Alert a = new Alert("CompositionForm", "getChannelFromInstrumName=" + channel, null, null);
                display.setCurrent(a);
                return;
            }

            if (channel == Constants.DRUMS_CHANNEL) {
                if (drumsCanvas == null) {
                    drumsCanvas = new DrumsCanvas(control, composition);
                }
                curCanvas = drumsCanvas;
            } else {
                if (notesCanvas == null) {
                    notesCanvas = new NotesCanvas(control, composition, channel);
                } else {
                    notesCanvas.setChannel(channel);
                }
                curCanvas = notesCanvas;
            }

            display.setCurrent(curCanvas);
            curCanvas.setNeedPaint();
        } else if (command == addInstrument) {
            isAdd = true;
            display.setCurrent(getListOfInstruments());
        } else if (command == setInstrument) {
            isAdd = false;
            display.setCurrent(getListOfInstruments());
        } else if (command == delInstrument) {
            byte channel;
            try {
                channel = choiСЃeInstrument.getSelectedChannel();
            } catch (IllegalAccessException ex) {
                return;
            }
            choiСЃeInstrument.deleteChannel(choiСЃeInstrument.getSelectedIndex());
            composition.setInstrument(channel, Composition.NOTHING);
            composition.delNotes(0, 0x7fffffff, channel, (byte) -1, (byte) 127);
        } else if (command == temp) {
            TempoList tempos = new TempoList(composition, model, this);
            display.setCurrent(tempos);
        } else if (command == volume) {
            VolumeForm volForm = new VolumeForm(composition, choiСЃeInstrument.getChansVector(), model, this);
            display.setCurrent(volForm);
        } else if (command == meter) {
            Form textBoxTemp = new Form(Constants.getInstrName(72));
            final TextField nomField = new TextField(L.str[L.numerator], "" + composition.getNom(), 2, TextField.NUMERIC);
            textBoxTemp.append(nomField);
            final TextField denomField = new TextField(L.str[L.denominator], "" + composition.getDenomE(), 1, TextField.NUMERIC);
            textBoxTemp.append(denomField);
            textBoxTemp.append(createStringItem(L.str[L.meterInfo], 2));
            textBoxTemp.addCommand(CompositionForm.ok);
            textBoxTemp.addCommand(P.comCancel);
            textBoxTemp.setCommandListener(new CommandListener() {

                public void commandAction(Command command, Displayable displayable) {
                    if (command == CompositionForm.ok) {
                        composition.setMeter(Integer.parseInt(nomField.getString(), 10),
                                Integer.parseInt(denomField.getString(), 10));
                    }
                    display.setCurrent(CompositionForm.this);
                }
            });
            model.display.setCurrent(textBoxTemp);

        } 
        else if (command == back) {
            control.comBack();
        }
    }
    
    /**
     *
     * @param text
     * @param appearanceMode
     * @return
     */
    private Item createStringItem(String text, int appearanceMode) {
        StringItem strItem = new StringItem(null, text, appearanceMode);
        return strItem;
    }

    /**
     *
     * @param name
     * @throws Exception
     */
    public void saveComposition(String name) throws Exception {
        this.setTitle(name);
        final String nameFinal = name;
        Thread runner = new Thread() {

            public void run() {
                WaitCanvas waitCanvas = new WaitCanvas(L.str[L.saving] + "...",
                        model.getWaitableFile(),
                        control.getCurrentlistMenu());
                display.setCurrent(waitCanvas);
                model.resetProgress();
                new Thread(waitCanvas).start();
                composition.setName(nameFinal);
                try {
                    String ans = model.saveMix(composition, nameFinal);
                    Alert a = new Alert(L.str[L.saved], ans, null, null);
                    a.setTimeout(Alert.FOREVER);
                    control.setCurrentlistMenu(a);
                } catch (Exception ex) {
                    Alert a = new Alert(L.str[L.error], L.str[L.savingError] + "\n" + ex.getMessage(), null, null);
                    a.setTimeout(Alert.FOREVER);
                    waitCanvas.cancel();
                    control.setCurrentlistMenu(a);

                }
            }
        };
        runner.start();

    }

    /**
     *
     * @return
     */
    public String getCompositionName() {
        return composition.getName();
    }

    /**
     *
     * @param s
     */
    public static void msg(String s) {
        Alert a = new Alert(L.str[L.error], L.str[L.error] + "\n" + s, null, AlertType.ERROR);
        Main.dsp.setCurrent(a);
    }
    
    
    private Canvas getListOfInstruments() {
        if (listInstrumentsCanvas == null) {
            listInstrumentsCanvas = new Instruments();
        }
        return listInstrumentsCanvas;
    }


    class ChannelChoiceGroup extends ChoiceGroup {

        private Vector instrumentsVector;

        public ChannelChoiceGroup(String label, String[] stringElements, byte[] chans) {
            super(label, Choice.EXCLUSIVE, stringElements, null);
            instrumentsVector = new Vector(Constants.NCHANNEL, 1);
            for (int i = 0; i < chans.length; ++i) {
                instrumentsVector.addElement(new Byte(chans[i]));
            }
        }

        public byte getSelectedChannel() throws IllegalAccessException{
            try{
            Byte b = (Byte) instrumentsVector.elementAt(this.getSelectedIndex());
            return b.byteValue();
            }catch (ArrayIndexOutOfBoundsException arI){
                throw new IllegalAccessException(arI.getMessage());
            }
        }

        public int appendChannel(String stringElement, byte channel) {
            if (instrumentsVector.size() < Constants.NCHANNEL) {
                instrumentsVector.addElement(new Byte(channel));
                return super.append(stringElement, null);
            }
            return -1;
        }

        public void deleteChannel(int index) {
            instrumentsVector.removeElementAt(index);
            super.delete(index);
        }

        public void setChannel(int index, String stringElement, byte channel) {
            instrumentsVector.setElementAt(new Byte(channel), index);
            super.set(index, stringElement, null);
        }

        public Vector getChansVector() {
            return instrumentsVector;
        }
    }

}
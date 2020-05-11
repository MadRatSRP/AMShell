package midedit;

import midedit.media.Composition;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import java.util.*;
import com.silentknight.amshell.javax.microedition.lcdui.List;
import main.L;
import main.P;

/**
 *
 * @author user
 */
public class TempoList extends List implements CommandListener {

    private Composition composition;
    private MixerModel model;
    private Displayable backForm;
    private Vector tempNotes;
    static private Command instempo = new Command(L.str[L.insertTempo], Command.ITEM, 1);
    static private Command deltempo = new Command(L.str[L.deleteTempo], Command.ITEM, 1);
    private int tick = 0;
    private int meterNom = 4, meterDenom = 4;
    private int editNum = -1;

    /**
     *
     * @param c 
     * @param m
     * @param back
     */
    public TempoList(Composition c, MixerModel m, Displayable back) {
        super(L.str[L.tempo], IMPLICIT);
        composition = c;
        model = m;
        backForm = back;
        tempNotes = new Vector();
        meterNom = composition.getNom();
        meterDenom = 1 << composition.getDenomE();
        tick = composition.getTicksPer4() * 4 * meterNom / meterDenom;
        System.out.println("tick = " + tick);
        this.addCommand(instempo);
        this.addCommand(deltempo);
        this.addCommand(CompositionForm.back);
        this.setCommandListener(TempoList.this);
        int tmp;
        NoteLong notelong;
        for (Note note = c.getNoteListByChannel(Composition.DEFAULT_CHANNEL).getFirst(); note != null; note = note.next) {
            if (note instanceof NoteLong) {
                notelong = (NoteLong) note;
                if (notelong.dat[0] == (byte) 0xff && notelong.dat[1] == (byte) 0x51) {
                    tmp = 0;
                    for (int i = 3; i <= 5; ++i) {
                        tmp = (tmp << 8) | 0xff & notelong.dat[i];
                    }

                    tempNotes.addElement(notelong);
                    this.append(getTempoString(note, tmp), null);
                }
            }
        }

    }

    private String getTempoString(Note note, int tempMsPer4) {
        int tempBPM;
        int mod = (composition.getTicksPer4() << 2) >> (composition.getDenomE());
        tempBPM = Composition.getTempBeatPerMin(tempMsPer4);
        return "" + note.noteTime / tick + ":" + (note.noteTime * meterNom / tick) % meterNom + ":"
                + note.noteTime % mod + " - " + tempBPM;

    }

    private void viewInsertForm(int time, int tempo) {
        Form textBoxTemp = new Form(L.str[L.insertTempo]);
        final TextField timeField = new TextField(L.str[L.time], "" + time, 4, TextField.NUMERIC);
        textBoxTemp.append(timeField);
        final TextField tempoField = new TextField(L.str[L.tempo], "" + tempo, 4, TextField.NUMERIC);
        textBoxTemp.append(tempoField);
        textBoxTemp.addCommand(CompositionForm.ok);
        textBoxTemp.addCommand(P.comCancel);
        textBoxTemp.setCommandListener(new CommandListener() {

            public void commandAction(Command command, Displayable displayable) {
                if (command == CompositionForm.ok) {
                    if (editNum >= 0) {
                        delTemp(editNum);
                    }
                    editNum = -1;

                    int tempMsPer4 = Composition.getMsPer4(Integer.parseInt(tempoField.getString(),
                            10));
                    int time = tick * Integer.parseInt(timeField.getString(), 10);
                    NoteLong noteTemp = composition.addTemp(time, tempMsPer4);
                    Note note;
                    int ind;
                    for (ind = 0; ind < tempNotes.size(); ++ind) {
                        note = (NoteLong) tempNotes.elementAt(ind);
                        if (note.noteTime > time) {
                            break;
                        }
                    }
                    tempNotes.insertElementAt(noteTemp, ind);
                    TempoList.this.insert(ind, getTempoString(noteTemp, tempMsPer4), null);

                }
                model.display.setCurrent(TempoList.this);
            }
        });
        model.display.setCurrent(textBoxTemp);

    }

    private void delTemp(int ind) {
        composition.delTemp((NoteLong) tempNotes.elementAt(ind));
        tempNotes.removeElementAt(ind);
        this.delete(ind);
    }

    /**
     *
     * @param command
     * @param displayable
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command == instempo) {
            editNum = -1;
            viewInsertForm(0, 120);

        } else if (command == deltempo) {

            if (tempNotes.size() > 1) {
                delTemp(this.getSelectedIndex());
            }
        } else if (command == CompositionForm.back) {
            model.display.setCurrent(backForm);
        } else {
            int ind = this.getSelectedIndex();

            NoteLong notelong = (NoteLong) tempNotes.elementAt(ind);
            int tmp = 0;
            for (int i = 3; i <= 5; ++i) {
                tmp = (tmp << 8) | 0xff & notelong.dat[i];
            }

            int mod = (composition.getTicksPer4() << 2) >> (composition.getDenomE());
            int tempBPM = Composition.getTempBeatPerMin(tmp);
            int time = notelong.noteTime / tick;

            editNum = ind;

            viewInsertForm(time, tempBPM);

        }
    }
}

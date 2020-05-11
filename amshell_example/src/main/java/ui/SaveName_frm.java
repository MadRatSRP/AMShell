package ui;

import com.silentknight.amshell.javax.microedition.lcdui.*;
import main.*;

/**
 * Ввод имени файла на Form
 * @author aNNiMON
 */
public class SaveName_frm extends Form implements CommandListener {
    
    /** Предыдущий экран */
    private Displayable previousDisplayable;
    
    /** Комнады */
    private Command ok, back;
    /** Поле ввода имени */
    private TextField nameTF;
    
    /** Имя файла*/
    private String name;
    
    /** Путь к папке */
    private String path;
    
    
    public SaveName_frm(Displayable b, String path) {
        super(L.str[L.midiName]);
        name = P.openSaveString;
        this.path = path;
        nameTF = new TextField(L.str[L.midiName], name, 48, TextField.ANY);
        back = new Command(L.str[L.back], Command.BACK, 3);
        ok = new Command(L.str[L.ok], Command.OK, 1);
        this.previousDisplayable=b;
        append(nameTF);
        addCommand(back);
        addCommand(ok);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if(c==back && d==this) Main.dsp.setCurrent(previousDisplayable);
        else if(c==ok && d==this) {
            name=nameTF.getString();
            if (name.length() > 0) {
                save();
            }
        }
    }

    private void save() {
        try {
            P.openSaveString = name;
            Main.midlet.compositionForm.saveComposition(path + name + ".mid");
            Main.midlet.compositionForm.setTitle(name);
            Main.midlet.compositionForm.setNew(false);
        } catch (Exception e) {
            Alert a = new Alert(L.str[L.error], e.getMessage(), null, null);
            Main.dsp.setCurrent(a);
        }
    }
}

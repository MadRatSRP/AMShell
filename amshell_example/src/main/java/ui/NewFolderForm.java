package ui;

import java.io.IOException;
import com.silentknight.amshell.javax.microedition.io.Connector;
import com.silentknight.amshell.javax.microedition.io.file.FileConnection;
import com.silentknight.amshell.javax.microedition.lcdui.*;
import main.L;
import main.P;

/**
 * Создание новой папки
 * @author aNNiMON
 */
public class NewFolderForm extends Form implements CommandListener {

    private Command ok, back;
    private FileManager fm;
    private String basePath;
    private TextField newFolderTF;

    public NewFolderForm(String title, FileManager prev, String p) {
        super(title);
        basePath = p;
        fm = prev;
        newFolderTF = new TextField("", "NewFolder", 60, TextField.ANY);
        ok = new Command(L.str[L.ok], Command.OK, 1);
        back = new Command(L.str[L.back], Command.BACK, 3);
        append(newFolderTF);
        addCommand(back);
        addCommand(ok);
        setCommandListener(NewFolderForm.this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == back) {
            fm.setCurrent(false);
        } else if ((c == ok || c == List.SELECT_COMMAND)) {
            makeFile(basePath, newFolderTF.getString());
            basePath = null;
            newFolderTF = null;
            fm.pathFile = P.path;
            fm.setCurrent(true);
        }
    }
    
    /** 
     * Создать файл или папку
     * @param path путь
     * @param name имя создаваемого файла или папки
     */
    private void makeFile(String path, String name) {
        try {
            FileConnection fc = (FileConnection) Connector.open("file://" + path + name);
            fc.mkdir();
            P.path += name + "/";
            fc.close();
        } catch (IOException io) {
        }
    }
}

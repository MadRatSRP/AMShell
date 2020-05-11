package main;

import java.io.*;
import java.util.Vector;
import com.silentknight.amshell.javax.microedition.util.ContextHolder;
import util.BufDataInputStream;

/**
 * Класс текстовых меток
 * @author aNNiMON
 */
public class L {

    /** Номера строк текстовых меток */
    public static final byte
            create = 0,
            resume = 1,
            about = 2,
            open = 3,
            save = 4,
            saveAs = 5,
            file = 6,
            options = 7,
            exit = 8,
            RMS = 9,
            cancel = 10,
            error = 11,
            openError = 12,
            listInstruments = 13,
            impossible = 14,
            saved = 15,
            savingError = 16,
            saving = 17,
            opening = 18,
            chooserError = 19,
            updateError = 20,
            apiError = 21,
            instruments = 22,
            menu = 23,
            back = 24,
            insertNoteWithCurrentAttributes = 25,
            noteAttributeHelp = 26,
            deleteNote = 27,
            undo = 28,
            noteVolume = 29,
            playFromCurrent = 30,
            playNoteOnCursor = 31,
            selectNoteAttribute = 32,
            changeNoteAttribute = 33,
            stop = 34,
            navigationOnComposition = 35,
            quicknav = 36,
            markBegin = 37,
            markEnd = 38,
            copy = 39,
            pasteInsert = 40,
            pasteReplace = 41,
            pasteOverwrite = 42,
            shiftDelete = 43,
            clean = 44,
            playChannelOnScreen = 45,
            playChannelAll = 46,
            redo = 47,
            addInstrument = 48,
            edit = 49,
            setInstrument = 50,
            delInstrument = 51,
            tempoBox = 52,
            volume = 53,
            meter = 54,
            play = 55,
            playOrigin = 56,
            ok = 57,
            language = 58,
            delete = 59,
            instrlang = 60,
            noteWidth = 61,
            pleaseWait = 62,
            noteHeight = 63,
            saveInThisFolder = 64,
            insertTempo = 65,
            deleteTempo = 66,
            tempo = 67,
            time = 68,
            seek = 69,
            numerator = 70,
            denominator = 71,
            meterInfo = 72,
            delta = 73,
            playStop = 74,
            playAll = 75,
            playScreen = 76,
            trackAll = 77,
            trackScreen = 78,
            mark = 79,
            unmark = 80,
            modifyBlock = 81,
            modifyMode = 82,
            paste = 83,
            insert = 84,
            replace = 85,
            blend = 86,
            removeSelection = 87,
            help = 88,
            keymap = 89,
            quickCommands = 90,
            navigation = 91,
            numkeysOptionString = 92,
            midiName = 93,
            tempDir = 94,
            newFolder = 95,
            memory = 96,
            midedit = 97,
            editMode = 98,
            markMode = 99,
            solo = 100,
            channel = 101;
    
    
    /** Массив текстовых меток */
    public static String[] str, instr;

    /**
     * Прочитать языковой ресурс
     * @param lang имя локализации (en, ru)
     * @param appLang читать языковой ресурс приложения (true), инструментов (false)
     */
    public static void readLang(String lang, boolean appLang)  {
        try {
            final String path = (appLang ? "strings" : "instr") + "_";
            BufDataInputStream bdis = new BufDataInputStream(ContextHolder.getResourceAsStream("/res/lang/" + path + lang + ".loc"));
            if (!bdis.checkBOM()) {
                bdis.close();
            }
            char c = ' ';
            Vector v = new Vector();
            StringBuffer s;
            while (bdis.available() > 0) {
                s = new StringBuffer();
                do {
                    c = bdis.readCharUTF();
                    if (c == '\n') {
                        break;
                    }
                    s.append(c);
                } while (bdis.available() > 0);
                v.addElement(s.toString());
            }
            if(appLang) {
                str = new String[v.size()];
                v.copyInto(str);
            } else {
                instr = new String[v.size()];
                v.copyInto(instr);
            }
            bdis.close();
            v = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
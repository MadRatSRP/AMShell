package ui;

import java.io.IOException;
import javax.microedition.lcdui.*;
import main.*;

/**
 * Класс настроек
 * @author aNNiMON
 */
public class SettingsForm extends Form implements CommandListener {

    private static final String[] langList = {"English", "Русский", "Українська"};
    private static final String[] langChars = {"en", "ru", "ua"};
    
    private Displayable previousScreen;
    private Command back, ok;

    private ChoiceGroup langChoice, langInstr, controlChoice;
    private Gauge noteHeightGauge;
    private Gauge noteWidthGauge;
    private TextField tempDirField;
    
    public SettingsForm(Displayable prev) {
        super(L.str[L.options]);
        previousScreen = prev;
        initComponents();
        setComponentsParameters();
        // Добавляем компоненты на форму
        append(langChoice);
        append(langInstr);
        append(noteWidthGauge);
        append(noteHeightGauge);
        append(controlChoice);
        append(tempDirField);
        addCommand(ok);
        addCommand(back);
        setCommandListener(SettingsForm.this);
    }

    private void initComponents() {
        // Команды
        ok = new Command(L.str[L.ok], Command.OK, 1);
        back = new Command(L.str[L.back], Command.BACK, 3);
        
        // Язык
        Image[] icons = createImages(langChars);
        langChoice = new ChoiceGroup(L.str[L.language], ChoiceGroup.EXCLUSIVE, langList, icons);
        langInstr = new ChoiceGroup(L.str[L.instrlang], ChoiceGroup.EXCLUSIVE, langList, icons);
        
        // Настройки размера нот
        noteWidthGauge = new Gauge(L.str[L.noteWidth], true, 10, Rms.noteWidth);
        noteHeightGauge = new Gauge(L.str[L.noteHeight], true, 10, Rms.noteHeight);

        // Управление
        controlChoice = new ChoiceGroup(L.str[L.navigation], ChoiceGroup.MULTIPLE);
        
        // Временная папка
        tempDirField = new TextField(L.str[L.tempDir], Rms.tempDir, 64, 4);
    }
    
    private void setComponentsParameters() {
        // Устанавливаем флажок языка
        for(int i = 0; i < langChars.length; i++) {
            if(Rms.languageApp.equals(langChars[i])) {
                langChoice.setSelectedIndex(i, true);
            }
            if(Rms.languageInstr.equals(langChars[i])) {
                langInstr.setSelectedIndex(i, true);
            }
        }
        // Настройки управления
        controlChoice.append(L.str[L.numkeysOptionString], createImage("keypad"));
        controlChoice.setSelectedIndex(0, Rms.numKeysEdit);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == back) {
            Main.dsp.setCurrent(previousScreen);
        } else if ((c == ok || c == List.SELECT_COMMAND)) {
            // Языки
            Rms.languageApp = langChars[langChoice.getSelectedIndex()];
            Rms.languageInstr = langChars[langInstr.getSelectedIndex()];

            Rms.tempDir = tempDirField.getString();
            
            Rms.noteWidth = noteWidthGauge.getValue() + 1;
            Rms.noteHeight = noteHeightGauge.getValue() + 1;
            
            Rms.numKeysEdit = controlChoice.isSelected(0);
            Main.dsp.setCurrent(previousScreen);
        }
    }

    /**
     * Получить иконки флагов стран
     * @param lang язык
     * @return массив иконок
     */
    private Image[] createImages(String[] lang) {
        Image[] icons = new Image[lang.length];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = createImage(lang[i]);
        }
        return icons;
    }
    
    /**
     * Получить иконку по указанному пути
     * @param path путь
     * @return картинка
     */
    private Image createImage(String path) {
        try {
            return Image.createImage("/img/"+path+".png");
        } catch (IOException ex) {
            return null;
        }
    }
}

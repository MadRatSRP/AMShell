package main;

/*
 * aNNiMON 2011
 * For more info visit http://annimon.com/
 */

import com.silentknight.amshell.javax.microedition.lcdui.*;
import com.silentknight.amshell.javax.microedition.midlet.*;
import midedit.CompositionForm;
import midedit.Constants;
import midedit.MixerModel;
import ui.Menu;

/**
 * @author aNNiMON
 */
public class Main extends MIDlet {
    
    public static Display dsp;
    public static Main midlet;
    
    public Menu menu;
    public MixerModel model;
    public CompositionForm compositionForm;
        
    public Main() {
        midlet = Main.this;
        dsp = Display.getDisplay(Main.this);
    }
    
    public void startApp() {
        Rms.restoreOptions();
        L.readLang(Rms.languageApp, true);
        L.readLang(Rms.languageInstr, false);
        Constants.setTimeConst(0);
        P.comCancel = new Command(L.str[L.cancel], Command.BACK, 10);
        menu = new Menu();
        model = new MixerModel();
        dsp.setCurrent(menu);
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean ex) {
        if(Rms.firstStart) Rms.firstStart = false;
        Rms.saveOptions();
        notifyDestroyed();
    }
    
    public MixerModel getModel() {
        return model;
    }

    /**
     *
     */
    public void comBack() {
        P.extMenu = true;
        P.isRMSMode = false;
        menu.addNewItems();
        setCurrentlistMenu(null);
    }
    /**
     *
     * @param a
     */
    public void setCurrentlistMenu(Alert a) {
        if (a != null) {
            dsp.setCurrent(a, menu);
        } else {
            dsp.setCurrent(menu);
        }
    }

    /**
     *
     * @return
     */
    public Displayable getCurrentlistMenu() {
        return menu;
    }

    @Override
    public void initApp() {
        
    }
}

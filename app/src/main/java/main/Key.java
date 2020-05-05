package main;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sinaz
 */
public class Key {

    public static String Platform = "";//можно убрать если не нужно
    public static int leftSoftKey = -6;//ALL Devices
    public static int rightSoftKey = -7;//ALL Devices
    public static int Clear = -1000;//SonyEricsson & Nokia(S60)
    public static int Call = - 1000;// Samsung Z500 & Siemens Devices
    public static int Hangup = - 1000;//Siemens Devices (S-Gold)
    public static int Back = -1000;//SonyEricsson
    public static int InputMode = -1000;//Only Nokia Devices (S60)

    //Only SonyEricsson JP6
    public static int VolPlus = -1000;
    public static int VolMinus = -1000;
    public static int Camera = -1000;
    public static int Focus = -1000;
    public static int Snapshot = -1000;
    //*************************
    

    static {
        String microeditionPlatform = System.getProperty("microedition.platform");
        if (microeditionPlatform != null) {
            String melc = microeditionPlatform.toLowerCase();
            if (melc.indexOf("ericsson") != -1)//SonyEricsson
            {
                Clear = -8;
                Back = -11;
                Call = -10;
                //Only SonyEricsson JP6
                VolPlus = -36;
                VolMinus = -37;
                Camera = -24;
                Focus = -25;
                Snapshot = -26;
                Platform = "Sony Ericsson";
            } else if (melc.indexOf("nokia") != -1)//Nokia Devices (S40) (S60)
            {
                //Only Nokia Devices (S60)
                Clear = -8;
                InputMode = -50;
                Platform = "Nokia";
            } else {//остальные не выдают фирму,а только марку мобилы например Z500,потому их лучше ловить по классам
                try {
                    Class.forName("com.samsung.util.Vibration");//Samsung
                    Call = -5;// Samsung Z500
                    Platform = "Samsung";
                } catch (Throwable t0) {

                    try {
                        Class.forName("com.siemens.mp.io.file.FileConnection");//Siemens Devices (S-Gold)
                        leftSoftKey = -1;
                        rightSoftKey = -4;
                        Call = -11;
                        Hangup = -12;
                        Camera = -20;
                        Platform = "Siemens";
                    } catch (Throwable t1) {

                        try {
                            Class.forName("com.motorola.io.file.FileConnection");
                            leftSoftKey = -21;
                            rightSoftKey = -22;
                            Call = -10;
                            //Motorola V300, V500, V525 должны быть софты leftSoftKey = 21;rightSoftKey = 22;
                            Platform = "Motorola";
                        } catch (Throwable t2) {}
                    }
                }
            }
        }
    }
}

package main;

import javax.microedition.lcdui.*;

/**
 * Класс параметров и констант
 * @author aNNiMON
 */
public class P {
    
    /** Маленький шрифт */
    public static final Font smPlain = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    /** Жирный маленький шрифт */
    public static final Font smBold = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
    /** Средний шрифт */
    public static final Font medPlain = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    /** Жирный средний шрифт */
    public static final Font medBold = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    
    /** Значок на фоне */
    public static Image bgKey;
    static {
        try {
            bgKey = Image.createImage("/img/key.png");
        } catch (java.io.IOException ex) {
            bgKey = Image.createImage(1, 1);
        }
    }
    
    /** Команда отмены */
    public static Command comCancel;
    
    /** Путь в файл-менеджере */
    public static String path = "/";
    /** Имя файла */
    public static String openSaveString = "newmix";
    /** Режим работы с RMS */
    public static boolean isRMSMode = false;
    /** Расширеное меню (продолжить, сохранить и т.д) */
    public static boolean extMenu = false;
    
    //скины
    //title and soft
    public static int colup = 0x593C77;
    public static int coldn = 0x926DB6;
    //задний фон
    public static int backgrnd = 0xFFFFFF;
    // фон курсора
    public static int obv = 0xA0A0E0;
    // фм
    public static int fmback1 = 0xEEEEEE;
    public static int fmtextcur = 0x31316C;
    public static int fmtextnc = 0x2B174A;
    public static int fmbord = 0x9C80FD;
    
}

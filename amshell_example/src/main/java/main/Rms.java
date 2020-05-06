package main;




import java.io.*;
import javax.microedition.rms.*;

/**
 * Класс сохранения настроек
 * @author aNNiMON
 */
public class Rms {

    private static final String rmsName = "MidEdit_2_1";
    private static RecordStore rmsStore;
    
    // Параметры 
    public static boolean firstStart = true; // первый старт
    public static String languageApp = "en"; // язык программы
    public static String languageInstr = "en"; // язык инструментов
    public static String tempDir = "/e:/other/"; // временная папка
    public static int noteWidth = 6; // ширина ноты
    public static int noteHeight = 5; // высота ноты
    public static boolean numKeysEdit = false; // редактирование нот клавиатурой (true) или джойстиком (false)

    /**
     * Сохранение настроек
     */
    public static void saveOptions() {
        if (rmsStore != null) {
            byte[] options = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeBoolean(firstStart);
                dos.writeUTF(languageApp);
                dos.writeUTF(languageInstr);
                dos.writeUTF(tempDir);
                dos.writeInt(noteWidth);
                dos.writeInt(noteHeight);
                dos.writeBoolean(numKeysEdit);
                dos.writeUTF(P.path); // путь в фм
                dos.flush();
                options = baos.toByteArray();
                dos.close();
                rmsStore.setRecord(1, options, 0, options.length);
            } catch (InvalidRecordIDException ridex) {
                try {
                    rmsStore.addRecord(options, 0, options.length);
                } catch (RecordStoreException ex) {
                }
            } catch (Exception ex) {
            }
        }
        if (rmsStore != null) {
            try {
                rmsStore.closeRecordStore();
                rmsStore = null;
            } catch (RecordStoreException ex) {
            }
        }
    }

    /**
     * Восстановить настройки
     * Пустые dis.readBoolean() оставлены для совместимости с прошлыми версиями
     */
    public static void restoreOptions() {
        try {
            rmsStore = RecordStore.openRecordStore(rmsName, true);
        } catch (RecordStoreException ex) {
            rmsStore = null;
        }
        if (rmsStore != null) {
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rmsStore.getRecord(1)));
                firstStart = dis.readBoolean(); // первый раз запущен
                languageApp = dis.readUTF();
                languageInstr = dis.readUTF();
                tempDir = dis.readUTF();
                noteWidth = dis.readInt();
                noteHeight = dis.readInt();
                numKeysEdit = dis.readBoolean();
                P.path = dis.readUTF();
                dis.close();
            } catch (Exception ex) {
            }
        }
    }
}


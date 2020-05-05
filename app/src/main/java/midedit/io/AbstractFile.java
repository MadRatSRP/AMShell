package midedit.io;

import java.io.*;
import java.util.Vector;

/**
 *
 * @author user
 */
public abstract class AbstractFile {

    public static final String[] types = {"mid", "midi"};
    /**
     *
     */
    protected String lastPath = "";
    protected InputStream is = null;

    /**
     *
     * @param pathName
     * @return
     */
    public abstract String[] list(String pathName);

    /**
     *
     * @param fileDescriptor
     * @param buf
     * @param offset
     * @param numBytes
     * @return
     * @throws IOException
     */
    public abstract int write(int fileDescriptor,
            byte[] buf,
            int offset,
            int numBytes) throws IOException;

    /**
     *
     * @param fileDescriptor
     * @param buf
     * @param offset
     * @param numBytes
     * @return
     * @throws IOException
     */
    public abstract int read(int fileDescriptor,
            byte[] buf,
            int offset,
            int numBytes) throws IOException;

    /**
     *
     * @param fileDescriptor
     * @return
     * @throws IOException
     */
    public abstract int close(int fileDescriptor) throws IOException;

    /**
     *
     * @param fileDescriptor
     * @return
     * @throws IOException
     */
    public abstract int length(int fileDescriptor) throws IOException;

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public abstract int exists(String fileName) throws IOException;

    /**
     *
     * @param fileName
     * @param isSave
     * @return
     * @throws IOException
     */
    public abstract int open(String fileName, boolean isSave) throws IOException;

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public abstract InputStream getInputStreambyURL(String url) throws IOException;
// /**
//  *
//  * @param fileName
//  * @return
//  * @throws IOException
//  */
// protected abstract  int checkFileName(String fileName) throws IOException;

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public abstract int delete(String fileName) throws IOException;

    /**
     *
     * @return
     */
    public abstract String getPrefix();

    /**
     *
     * @return
     */
    public abstract String getURL();

    /**
     *
     * @return
     */
    public abstract String getAns();
    
    /**
     * Пузырьковая сортировка.
     * Сортирует по имени, а потом подымает папки в начало
     * @param files вектор со списком имён файлов и папок
     * @param reverse true - сортировка по убыванию
     * @return 
     */
    protected static String[] bubbleSort(Vector files) {
        String tmp;
        for (int i = files.size() - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if ((tmp = (String) files.elementAt(j)).toLowerCase().compareTo(((String) files.elementAt(j + 1)).toLowerCase()) > 0) {
                    files.setElementAt((String) files.elementAt(j + 1), j);
                    files.setElementAt(tmp, j + 1);
                }
            }
        }
        String[] arr = sortFiles(files);
        return arr;
    }
    
    /**
     * Сортировка файлов.
     * Папки поднимаются в начало списка, остальные файлы не трогаются.
     * @param files вектор со списком имён файлов и папок
     * @return массив с отсортированным списком
     */
    private static String[] sortFiles(Vector files) {
        int length = files.size();
        String[] out = new String[length];
        int i = 0;
        for (int j = 0; j < length; j++) {
            if (((String)files.elementAt(j)).indexOf("/") != -1) {
                out[i] = ((String)files.elementAt(j));
                i++;
            }
        }
        for (int k = 0; k < length; k++) {
            if (((String)files.elementAt(k)).indexOf("/") == -1) {
                out[i] = ((String)files.elementAt(k));
                i++;
            }
        }
        return out;
    }
}

package midedit.io;

import java.io.*;
import java.util.*;
import com.silentknight.amshell.javax.microedition.io.*;
import com.silentknight.amshell.javax.microedition.io.file.*;
import main.L;
import main.Rms;

/**
 *
 * @author user
 */
public class JSR75File extends AbstractFile {

    private FileConnection fc = null;
    private DataOutputStream dos = null;
    private boolean isSave = false;
    private String lastFileURL = null;

    /**
     * аЌаОаЛбібµаИбІб  б±аПаИб±аОаК бґаАаЙаЛаОаВ аВ аПаАаПаКаЕ path
     * @param pathName аПбібІб  аК аПаАаПаКаЕ 
     * @return аМаАб±б±аИаВ б± аОбІб±аОбЂбІаИбЂаОаВаАаНаНб‚аМ б±аПаИб±аКаОаМ бґаАаЙаЛаОаВ
     */
    public String[] list(String pathName) {
        lastPath = pathName;
        Vector vector = new Vector();
        Enumeration en;
        try {
            FileConnection currDir = null;
            // аѕаЛбЅ аПаОаЛбібµаЕаНаИбЅ б±аПаИб±аКаА аДаИб±аКаОаВ аИб±аПаОаЛб аЗбіаЕаМ FileSystemRegistry
            if ("/".equals(pathName))  en = FileSystemRegistry.listRoots();
            else {
                // аѕаЛбЅ аОб±бІаАаЛб аНб‚б… аПаАаПаОаК - аОаБб‚бµаНб‚аЙ FileConnection
                currDir = (FileConnection) Connector.open("file://" + pathName, Connector.READ);
                en = currDir.list();
            }
            // аЌаЕбЂаЕаМаЕбЃаАаЕаМ б±аПаИб±аОаК аМаИаДаИб¶аЕаК аВ аВаЕаКбІаОбЂ
            while(en.hasMoreElements()) {
                String s1 = (String) en.nextElement();
                for(int i=0; i<types.length; i++) {
                    if ((s1.toLowerCase().endsWith("."+types[i])) || s1.endsWith("/")) {
                        vector.addElement(s1);
                        break;
                    }
                }
                // vector.addElement(s1);
            }
            if (currDir != null) currDir.close();
        } catch (IOException ion) {
            ion.printStackTrace();
        }
        // аЁаОбЂбІаИбЂбіаЕаМ аИ аВаОаЗаВбЂаАбЃаАаЕаМ б±аПаИб±аОаК
        return bubbleSort(vector);
    }

    /**
     *
     * @param fileDescriptor
     * @param buf
     * @param offset
     * @param numBytes
     * @return
     * @throws IOException
     */
    public int write(int fileDescriptor,
            byte[] buf,
            int offset,
            int numBytes) throws IOException {
        dos.write(buf, offset, numBytes);
        return 0;
    }

    /**
     *
     * @param fileDescriptor
     * @param buf
     * @param offset
     * @param numBytes
     * @return
     * @throws IOException
     */
    public int read(int fileDescriptor,
            byte[] buf,
            int offset,
            int numBytes) throws IOException {
        return is.read(buf, offset, numBytes);
    }

    /**
     *
     * @param fileDescriptor
     * @return
     * @throws IOException
     */
    public int close(int fileDescriptor) throws IOException {
        if (is != null) {
            is.close();
        }
        is = null;
        if (dos != null) {
            dos.close();
        }
        dos = null;
        if (fc != null) {
            fc.close();
        }
        fc = null;
        return 0;
    }

    /**
     *
     * @param fileDescriptor
     * @return
     * @throws IOException
     */
    public int length(int fileDescriptor) throws IOException {
        int len = 0;
        if (fc != null) {
            len = (int) fc.fileSize();
        }
        return len;
    }

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public int exists(String fileName) throws IOException {
        FileConnection fcTmp = (FileConnection) Connector.open(getPrefix() + fileName);
        if (!fcTmp.exists()) {
            fcTmp.close();
            return -1;
        }
        fcTmp.close();
        return 0;

    }

    /**
     *
     * @param fileName
     * @param isSaveMode
     * @return
     * @throws IOException
     */
    public int open(String fileName, boolean isSaveMode) throws IOException {
        String fullName;
        if (fileName.charAt(0) != '/') {
            try {
                Enumeration e = FileSystemRegistry.listRoots();
                if (e.hasMoreElements()) {
                    FileConnection fcTmp = (FileConnection) Connector.open(getPrefix() + Rms.tempDir);
                    if (!fcTmp.exists()) {
                        fcTmp.mkdir();
                    }
                }
            } catch (Exception e) {
                throw new IOException("Can't create tmp directory");
            }
            fullName = getPrefix() + "/" + Rms.tempDir + fileName;
        } else {
            fullName = getPrefix() + fileName;
        }
        lastFileURL = fullName;
        isSave = isSaveMode;
        fc = (FileConnection) Connector.open(fullName);

        if (isSaveMode) {
            if (!fc.exists()) {
                fc.create();
            } else {
                fc.truncate(0);
            }
        }


        if (!isSave) {
            is = fc.openInputStream();
            dos = null;
        } else {
            dos = fc.openDataOutputStream();
            is = null;
        }

        return 0;

    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public InputStream getInputStreambyURL(String url) throws IOException {
        FileConnection fcTmp = (FileConnection) Connector.open(url);

        InputStream input = fcTmp.openInputStream();
        return input;
    }

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
// public int checkFileName(String fileName) throws IOException
// {
//  return 0;
// }
    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public int delete(String fileName) throws IOException {
        FileConnection fcTmp = (FileConnection) Connector.open(getPrefix() + fileName);
        fcTmp.delete();
        fcTmp.close();
        return 0;
    }

    /**
     *
     * @return
     */
    public String getPrefix() {
        return "file://";
    }

    /**
     *
     * @return
     */
    public String getURL() {
        return lastFileURL;
    }

    /**
     *
     * @return
     */
    public String getAns() {
        return L.str[L.saved];
    }
    
}

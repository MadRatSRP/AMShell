package midedit.io;

import java.io.*;
import java.util.Vector;
import com.silentknight.amshell.javax.microedition.rms.*;
import main.L;

/**
 * <p>Title: </p> <p>Description: </p> <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RMSFile extends AbstractFile {

    private RecordStore curRecordStore = null;
    private int recordID = 0;

    public String[] list(String pathName) {
        Vector vector = new Vector();
        String[] fileList = RecordStore.listRecordStores();
        for (int idx = 0; idx < fileList.length; idx++) {
            String s1 = fileList[idx];
            for (int i = 0; i < types.length; i++) {
                if (s1.toLowerCase().endsWith("." + types[i])) {
                    vector.addElement(s1);
                    break;
                }
            }
        }
        // аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ аАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎаАаЌаБтЂЁаАтЂЎ
        return bubbleSort(vector);
    }

    public int write(int fileDescriptor,
            byte[] buf,
            int offset,
            int numBytes) throws IOException {
        if (curRecordStore != null) {
            try {
                curRecordStore.addRecord(buf, offset, numBytes);
            } catch (RecordStoreException ex) {
                throw new IOException(ex.getMessage());
            }
        } else {
            throw new IOException("not opened");
        }
        return 0;
    }

    public int read(int fileDescriptor,
            byte[] buf,
            int offset,
            int numBytes) throws IOException {

        if (curRecordStore != null) {
            //readBuf
            is.read(buf, offset, numBytes);
            //System.arraycopy(readBuf,readBufInd,buf,offset,numBytes);
            //readBufInd += numBytes;
        } else {
            throw new IOException("not opened");
        }

        return numBytes;
    }

    public int close(int fileDescriptor) throws IOException {
        try {
            if (curRecordStore != null) {
                curRecordStore.closeRecordStore();
                curRecordStore = null;
                //readBuf = null;
                is = null;
                //System.out.println ("Local file closed");
            }
        } catch (RecordStoreException ex) {
            throw new IOException(ex.getMessage());
        }
        return 0;
    }

    public int length(int fileDescriptor) throws IOException {
        if (curRecordStore != null) {
            int len = curRecordStore.getRecordSize(recordID);
            return len;
        }
        return 0;
    }

    public int exists(String fileName) throws IOException {
        String name = getLastName(fileName);
        RecordStore test = null;
        try {
            test = RecordStore.openRecordStore(name, false);
        } catch (RecordStoreException ex) {
        }
        if (test != null) {
            try {
                test.closeRecordStore();
            } catch (RecordStoreException ex1) {
            }
            return 0;
        } else {
            return -1;
        }
    }

    public int open(String fileName, boolean isSave) throws IOException {
        String name = getLastName(fileName);
        lastPath = name;
        try {
            curRecordStore = RecordStore.openRecordStore(name, isSave);
            if (curRecordStore == null) {
                throw new IOException("not found in RMS");
            }
            if (isSave == false) /// read
            {
                RecordEnumeration enumer = curRecordStore.enumerateRecords(null, null, false);
                if (enumer.hasNextElement()) {
                    recordID = enumer.nextRecordId();

                    //recordID = curRecordStore.getNextRecordID();
                    ByteArrayInputStream bais = new ByteArrayInputStream(curRecordStore.getRecord(recordID));
                    is = bais;
                }
            }
            //curRecordStore.closeRecordStore();
        } catch (RecordStoreException ex) {
            //close(0);
            curRecordStore = null;
            throw new IOException(ex.toString() + " open");
        }

        return 0;
    }

    public int delete(String fileName) throws IOException {
        String name = getLastName(fileName);
        try {
            RecordStore.deleteRecordStore(name);
        } catch (RecordStoreException ex) {
        }
        return 0;
    }

    public String getURL() {
        return lastPath;
    }

    public String getAns() {
        return "RMS: " + L.str[L.saved];
    }

    public static String getLastName(String fullName) {
        int ind = fullName.lastIndexOf('/');
        String name;
        name = (ind > 0) ? fullName.substring(ind + 1) : fullName;
        return name;
    }

    public InputStream getInputStreambyURL(String url) throws IOException {
        String fileName = url;
        InputStream isLocal = null;
////// copy from open();

        String name = getLastName(fileName);
        lastPath = name;
        try {
            boolean isSave = false;
            curRecordStore = RecordStore.openRecordStore(name, isSave);
            if (curRecordStore == null) {
                throw new IOException("not found in RMS");
            }
            if (isSave == false) /// read
            {
                RecordEnumeration enumer = curRecordStore.enumerateRecords(null, null, false);
                if (enumer.hasNextElement()) {
                    recordID = enumer.nextRecordId();

                    //recordID = curRecordStore.getNextRecordID();
                    ByteArrayInputStream bais = new ByteArrayInputStream(curRecordStore.getRecord(recordID));
                    isLocal = bais;
                }
            }
        } catch (RecordStoreException ex) {
            curRecordStore = null;
            throw new IOException(ex.toString() + " open");
        }

        return isLocal;
    }

    public String getPrefix() {
        return "";
    }
}

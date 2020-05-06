package util;

import java.io.*;

/**
 * аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а… аПб‡а…аПб‡а…аПб‡а…аПб‡а…-аПб‡а…аПб‡а…аПб‡а…аПб‡а…аПб‡а…
 * @author vmx
 */
public class BufDataInputStream extends InputStream implements DataInput {
    public byte[] buffer;
    public int capacity,  is_available,  bpos,  blen;
    protected InputStream is;

    public BufDataInputStream(InputStream iis) throws IOException {
        bpos = blen = 0;
        is = iis;
        capacity = is_available = is.available ();
        buffer = new byte[capacity];
        if (is != null) is.read(buffer);
    }

    public void close() throws IOException {
        if (is != null)  is.close();
    }

    public int available() {
        return blen - bpos + is_available;
    }

    public int read() throws IOException {
        if (bpos > buffer.length) {
            return -1;
        }
        return ((int) buffer[bpos++]) & 0xFF;
    }

    public boolean readBoolean() throws IOException {
        int r = read();
        if (r == -1) {
            throw new IOException("EOF");
        }
        return r != 0;
    }

    public byte readByte() throws IOException {
        int r = read();
        if (r == -1) {
            throw new IOException("EOF");
        }
        return (byte) r;
    }

    public char readChar() throws IOException {
        return (char) ((readUnsignedByte() << 8) | readUnsignedByte());
    }

    public void readFully(byte[] b) throws IOException {
        if (read(b) < b.length) {
            throw new IOException("EOF");
        }
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        if (read(b, off, len) < len) {
            throw new IOException("EOF");
        }
    }

    public int readInt() throws IOException {
        return (readUnsignedByte() << 24) |
                (readUnsignedByte() << 16) |
                (readUnsignedByte() << 8) |
                (readUnsignedByte());
    }

    public long readLong() throws IOException {
        byte bb[] = new byte[8];
        readFully(bb);
        return (bb[0] << 24) |
                (bb[1] << 16) |
                (bb[2] << 8) |
                (bb[3]);
    }

    public short readShort() throws IOException {
        return (short) ((readUnsignedByte() << 8) | readUnsignedByte());
    }

    public int readUnsignedByte() throws IOException {
        return ((int) readByte()) & 0xFF;
    }

    public int readUnsignedShort() throws IOException {
        return ((int) readShort()) & 0xFFFF;
    }

    public int skipBytes(int len) throws IOException {
        return (int) skip(len);
    }

    public String readUTF() throws IOException, UTFDataFormatException {
        int n = readUnsignedShort();
        byte b[] = new byte[n];
        readFully(b);
        return new String(b, 0, b.length, "UTF-8");
    }

    public char readCharUTF() throws IOException, UTFDataFormatException {
        int b, c, d;
        b = read();
        if (b == -1) {
            return (char) -1;
        }
        if ((b & 0x80) == 0) {
            return (char) b;
        } else if ((b & 0xE0) == 0xC0) {
            c = read();
            if ((c & 0xC0) != 0x80) {
                throw new UTFDataFormatException();
            }
            return (char) (((b & 0x1F) << 6) | (c & 0x3F));
        } else if ((b & 0xF0) == 0xE0) {
            c = read();
            d = read();
            if ((c & 0xC0) != 0x80 || (d & 0xC0) != 0x80) {
                throw new UTFDataFormatException();
            }
            return (char) (((b & 0x0F) << 12) | ((c & 0x3F) << 6) | (d & 0x3F));
        }
        throw new UTFDataFormatException();
    }

    public boolean checkBOM() {
        try {
            if (available() < 3 ||
                    read() != 0xEF ||
                    read() != 0xBB ||
                    read() != 0xBF) {
                return false;
            }
        } catch (IOException iox) {
            return false;
        }
        return true;
    }

    public double readDouble() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float readFloat() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String readLine() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

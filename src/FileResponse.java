import java.io.Serializable;

public class FileResponse implements Serializable {
    private int size;
    private byte[] mybytearray;

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getRequestData() {
        return mybytearray;
    }
    public void setRequestData(byte[] mybytearray) {
        this.mybytearray = mybytearray;
    }

}


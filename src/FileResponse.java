import java.io.Serializable;

//Class to make it easy write file objects through the socket
public class FileResponse implements Serializable {
    //File size
    private int size;
    //File content
    private byte[] mybytearray;

    //Getters and setters
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


import java.io.Serializable;

//File to make it easy to make requests through the socket
public class IndexerRequest implements Serializable {
    //Request type
    private IndexerRequestType type;
    //Data
    private Object data;

    //Getters and setters
    public IndexerRequestType getRequestType() {
        return type;
    }
    public void setRequestType(IndexerRequestType requestType) {
        this.type = requestType;
    }
    public Object getRequestData() {
        return data;
    }
    public void setRequestData(Object data) {
        this.data = data;
    }

}


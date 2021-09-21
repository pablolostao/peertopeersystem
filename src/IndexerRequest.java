import java.io.Serializable;

public class IndexerRequest implements Serializable {
    private IndexerRequestType type;
    private Object data;

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


package Model;

/**
 * Created by win on 2016/11/29.
 */
public class Cache extends Storage {
    public static final int MODIFIED = 2;
    public static final int SHARED = 1;
    public static final int INVALID = 0;
    private int status;
    private int memoryId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(int memoryId) {
        this.memoryId = memoryId;
    }

    Cache(int id) {
        super(id);
        this.status = INVALID;
        this.memoryId = -1;
    }
}

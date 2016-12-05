package Model;

/**
 * Created by AMXPC on 2016/12/5.
 */
public class Movement {
    public static final int READIN = 1;
    public static final int WRITEBACK = 2;
    public int CPUModelId;
    public int CacheId;
    public int MemoryId;
    public int Direction;

    public Movement(int CPUModelId, int cacheId, int memoryId, int direction) {
        this.CPUModelId = CPUModelId;
        this.CacheId = cacheId;
        this.MemoryId = memoryId;
        this.Direction = direction;
    }
}

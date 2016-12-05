package Model;

/**
 * Created by win on 2016/11/29.
 */
public class MemoryUnit extends Storage {
    private int mapValue;

    MemoryUnit(int id) {
        super(id);
        this.mapValue = id % 4;
    }

    public int getMapValue() {
        return mapValue;
    }
}

package Model;

/**
 * Created by win on 2016/11/29.
 */
public class Memory {
    public MemoryUnit[] memoryUnits;

    public Memory() {
        memoryUnits = new MemoryUnit[32];
        for (int i = 0; i < 32; i++) {
            memoryUnits[i] = new MemoryUnit(i);
        }
    }
}

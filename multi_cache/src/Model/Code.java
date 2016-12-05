package Model;

/**
 * Created by win on 2016/12/1.
 */
public class Code {
    public static final int READ = 0;
    public static final int WRITE = 1;
    public int visitCPUModel;
    public int visitMemoryUnit;
    public int codeType;

    public Code(int visitCPUModel, int visitMemoryUnit, int codeType) {
        this.visitCPUModel = visitCPUModel;
        this.visitMemoryUnit = visitMemoryUnit;
        this.codeType = codeType;
    }
}

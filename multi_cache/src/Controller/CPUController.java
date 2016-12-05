package Controller;

import Model.*;
import javafx.util.Pair;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Created by win on 2016/11/29.
 */
public class CPUController extends Observable implements Observer {
    private static final int RDMISS = 1;
    private static final int WTMISS = 2;
    private static final int INVALIDATE = 3;

    private int command;        // bus control command
    private Memory memory;
    private CPUModel[] cpuModels;   // all caches
    private Cache cache;        // temporary variable for change cache
    private Code code;          // code for change by user
    private int mapCacheId;     // cacheid mapping the memoryid
    private int cacheStatus;    // status of the right cache
    private Vector<Movement> movements;     // movement for view
    private Vector<Pair<Integer, Integer>> invalidates;

    private static CPUController cpuController;


    private CPUController() {
        super();
        this.command = -1;      //init
        this.memory = new Memory();
        this.cpuModels = new CPUModel[4];
        for (int i = 0; i < 4; i++) {
            this.cpuModels[i] = new CPUModel(i);
        }
        this.code = new Code(0, 0, 0);
        this.mapCacheId = -1;
        this.cacheStatus = -1;
        this.movements = new Vector<Movement>(0);
        this.invalidates = new Vector<Pair<Integer, Integer>>(0);
        addObserver(this);
    }

    public static void initCPUController() {
        cpuController = new CPUController();
    }

    public static CPUController getCpuController() {
        return cpuController;
    }

    public void setCode(int visitCPUModel, int visitMemoryUnit, int codeType) {
        this.code = new Code(visitCPUModel, visitMemoryUnit, codeType);
    }

    public Code getCode() {
        return code;
    }

    private void resetCode() {
        this.code = new Code(0, 0, 0);
    }

    @Override
    protected synchronized void clearChanged() {
        super.clearChanged();
        this.resetCode();
    }

    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.code = (Code) arg;
        this.mapCacheId = memory.memoryUnits[code.visitMemoryUnit].getMapValue();
        this.cache = cpuModels[code.visitCPUModel].getCache(mapCacheId);
        this.cacheStatus = cache.getStatus();

        if (Code.READ == code.codeType) {
            switch (cacheStatus) {
                case Cache.INVALID:
                    readInvalid();
                    break;
                case Cache.SHARED:
                    readShared();
                    break;
                case Cache.MODIFIED:
                    readModified();
                    break;
                default:
                    break;
            }

        }
        if (Code.WRITE == code.codeType) {
            switch (cacheStatus) {
                case Cache.INVALID:
                    writeInvalid();
                    break;
                case Cache.SHARED:
                    writeShared();
                    break;
                case Cache.MODIFIED:
                    writeModified();
                    break;
                default:
                    break;
            }
        }
        if (movements.isEmpty() && invalidates.isEmpty()) {
            movements.add(new Movement(0, 0, 0, 0));
        }
    }

    private void readInvalid() {
        this.command = RDMISS;
        this.otherCache();
        movements.add(new Movement(code.visitCPUModel, mapCacheId, code.visitMemoryUnit, Movement.READIN));
        cache.setMemoryId(code.visitMemoryUnit);     //memory to cache
        cache.setStatus(Cache.SHARED);          //invalid to share
    }

    private void readShared() {
        if (code.visitMemoryUnit == cache.getMemoryId()) {
        }      // no change
        else {
            this.command = RDMISS;
            this.otherCache();
            movements.add(new Movement(code.visitCPUModel, mapCacheId, code.visitMemoryUnit, Movement.READIN));
            cache.setMemoryId(code.visitMemoryUnit);
        }
    }

    private void readModified() {
        if (code.visitMemoryUnit == cache.getMemoryId()) {
        }      // no change
        else {
            this.command = RDMISS;
            this.otherCache();
            movements.add(new Movement(code.visitCPUModel, mapCacheId, cache.getMemoryId(), Movement.WRITEBACK));
            movements.add(new Movement(code.visitCPUModel, mapCacheId, code.visitMemoryUnit, Movement.READIN));
            cache.setMemoryId(code.visitMemoryUnit);
            cache.setStatus(Cache.SHARED);
        }
    }

    private void writeInvalid() {
        this.command = WTMISS;
        this.otherCache();
        movements.add(new Movement(code.visitCPUModel, mapCacheId, code.visitMemoryUnit, Movement.READIN));
        cache.setMemoryId(code.visitMemoryUnit);
        cache.setStatus(Cache.MODIFIED);
    }

    private void writeShared() {
        if (code.visitMemoryUnit == cache.getMemoryId()) {
            this.command = INVALIDATE;
            this.otherCache();
            cache.setStatus(Cache.MODIFIED);
        } else {
            this.command = WTMISS;
            this.otherCache();
            movements.add(new Movement(code.visitCPUModel, mapCacheId, code.visitMemoryUnit, Movement.READIN));
            cache.setMemoryId(code.visitMemoryUnit);
            cache.setStatus(Cache.MODIFIED);
        }

    }

    private void writeModified() {
        if (code.visitMemoryUnit == cache.getMemoryId()) {
        }      //no change
        else {
            this.command = WTMISS;
            this.otherCache();
            movements.add(new Movement(code.visitCPUModel, mapCacheId, cache.getMemoryId(), Movement.WRITEBACK));
            movements.add(new Movement(code.visitCPUModel, mapCacheId, code.visitMemoryUnit, Movement.READIN));
            cache.setMemoryId(code.visitMemoryUnit);
        }
    }

    private void otherCache() {
        Cache cache;
        for (int cpuModelId = 0; cpuModelId < 4; cpuModelId++) {
            cache = cpuModels[cpuModelId].getCache(mapCacheId);
            if (cpuModelId == code.visitCPUModel) {
                continue;
            }
            if (cache.getMemoryId() == code.visitMemoryUnit) {
                switch (command) {
                    case RDMISS:
                        if (cache.getStatus() == Cache.MODIFIED) {
                            cache.setStatus(Cache.SHARED);
                            movements.add(new Movement(cpuModelId, cache.getId(), cache.getMemoryId(), Movement.WRITEBACK));
                        }
                        break;
                    case WTMISS:
                        if (cache.getStatus() == Cache.MODIFIED) {
                            cache.setStatus(Cache.INVALID);
                            movements.add(new Movement(cpuModelId, cache.getId(), cache.getMemoryId(), Movement.WRITEBACK));
                        }
                        if (cache.getStatus() == Cache.SHARED) {
                            cache.setStatus(Cache.INVALID);
                            invalidates.add(new Pair<Integer, Integer>(cpuModelId, cache.getId()));
                        }
                        break;
                    case INVALIDATE:
                        if (cache.getStatus() == Cache.SHARED) {
                            cache.setStatus(Cache.INVALID);
                            invalidates.add(new Pair<Integer, Integer>(cpuModelId, cache.getId()));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public Color setCacheColor(int cpuModelId, int cacheId) {
        switch (cpuModels[cpuModelId].getCache(cacheId).getStatus()) {
            case Cache.INVALID:
                return Color.LIGHT_GRAY;
            case Cache.SHARED:
                return Color.CYAN;
            case Cache.MODIFIED:
                return Color.MAGENTA;
            default:
                return Color.LIGHT_GRAY;
        }
    }


    public Vector<Movement> getMovements() {
        return movements;
    }

    public Vector<Pair<Integer, Integer>> getInvalidates() {
        return invalidates;
    }

    public void clearMovementsAndInvalidates() {
        this.movements.clear();
        this.invalidates.clear();
    }
}
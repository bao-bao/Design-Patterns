package Controller;

import Model.*;

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
    private CPUModel[] cpuModels;   // all caches
    private Cache cache;        // temporary variable for change cache
    private Vector<Movement> movements;     // movement for view
    private Code code;          // code for change by user
    private int mapCacheId;     // cacheid mapping the memoryid
    private int cacheStatus;    // status of the right cache

    public CPUController() {
        super();
        this.command = -1;      //init
        this.cpuModels = new CPUModel[4];
        for (int i = 0; i < 4; i++) {
            this.cpuModels[i] = new CPUModel(i);
        }
        this.movements = new Vector<Movement>(0);
        this.code = new Code(0, 0, 0);             //init
        this.mapCacheId = -1;   // init
        this.cacheStatus = -1;  // init
        addObserver(this);
    }

    public void setCode(int visitCPUModel, int visitMemoryUnit, int codeType) {
        this.code = new Code(visitCPUModel, visitMemoryUnit, codeType);
    }

    private void resetCode() {
        this.code = new Code(0, 0, 0);
    }

    private void resetMovement() {
        this.movements.clear();
    }

    @Override
    protected synchronized void clearChanged() {
        super.clearChanged();
        this.resetCode();
        this.resetMovement();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.code = (Code) arg;
        this.mapCacheId = Memory.memoryUnits[code.visitMemoryUnit].getMapValue();
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

        } else {
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
                        }
                        break;
                    case INVALIDATE:
                        if (cache.getStatus() == Cache.SHARED) {
                            cache.setStatus(Cache.INVALID);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

package Controller;

import Model.*;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Created by win on 2016/11/29.
 */
public class CPUController extends Observable implements Observer {
    private CPUModel[] cpuModels;   // all caches
    private Code code;          // code for change by user

    public CPUController() {
        super();
        this.cpuModels = new CPUModel[4];
        for (int i = 0; i < 4; i++) {
            this.cpuModels[i] = new CPUModel(i);
        }
        this.code = new Code(0, 0, 0);             //init
        addObserver(this);
    }

    public void setCode(int visitCPUModel, int visitMemoryUnit, int codeType) {
        this.code = new Code(visitCPUModel, visitMemoryUnit, codeType);
    }

    private void resetCode() {
        this.code = new Code(0, 0, 0);
    }


    @Override
    protected synchronized void clearChanged() {
        super.clearChanged();
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}

package Model;

/**
 * Created by win on 2016/11/29.
 */
public class CPUModel {
    private int id;
    private Cache[] caches;

    public CPUModel(int id) {
        this.id = id;
        for(int i = 0; i < 4; i++) {
            caches[i] = new Cache(i);
        }
    }

    public Cache getCache(int id) {
        return caches[id];
    }
}

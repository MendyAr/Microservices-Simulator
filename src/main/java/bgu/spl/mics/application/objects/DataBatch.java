package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;

    public DataBatch(Data d,int start_index){
        data=d;
        this.start_index=start_index;
    }

    public Data getData() {
        return data;
    }

    public int getStart_index() {
        return start_index;
    }
}

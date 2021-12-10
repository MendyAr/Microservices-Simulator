package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;


/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    /** @INV: tickCounter > 0
     * data.size() >= 0
     */

    private final int cores;
    private final int imageProcessTime;
    private final int textProcessTime;
    private final int tabularProcessTime;

    private List<DataBatch> data; //creating List for each type?
    private Cluster cluster;
    private int tickCounter;


    public CPU(int cores){
        this.cores = cores;
        imageProcessTime = (32/cores) * 4;
        textProcessTime =  (32/cores) * 2;
        tabularProcessTime = (32/cores);

        data = new LinkedList<>();
        cluster = Cluster.getInstance();
        tickCounter = 0;
    }

    public int getCores() {
        return cores;
    }

    public int getImageProcessTime() {
        return imageProcessTime;
    }

    public int getTextProcessTime() {
        return textProcessTime;
    }

    public int getTabularProcessTime() {
        return tabularProcessTime;
    }

    public List<DataBatch> getData() {
        return data;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    // @post tickCounter = tickCounter + 1;
    public void advanceClock(){}

    // @pre data.size() == 0
    // @post data.size() == @pre data.size() + 1
    public void receiveDataBatch(){} //asking for unprocessed dataBatch from the cluster

    // @pre data.size() > 0
    // @post data.size() == ( @pre data.size() - 1 )
    public void sendDataBatch(){} //sending processed data batch to the cluster

    // @pre data.size() > 0
    // @post tickCounter > (@pre tickCounter ) + processTime
    public void process(){} //process the data - wait for the defined ticks to happen

}
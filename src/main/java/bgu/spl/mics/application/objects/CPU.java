package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


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
    private Cluster cluster;
    private int tickCounter;
    private Queue<DataBatch>dataBatches;
    private DataBatch currentDBProcessed;


    public CPU(int cores){
        this.cores = cores;
        imageProcessTime = (32/cores) * 4;
        textProcessTime =  (32/cores) * 2;
        tabularProcessTime = (32/cores);
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

    public int getTickCounter() {
        return tickCounter;
    }

    // @post tickCounter = tickCounter + 1;
    public void advanceClock(){
        if(currentDBProcessed!=null) {//cpu is processed data
            tickCounter++;
            cluster.setCpuUTUed();
            if (DataIsProcessed()) {
                tickCounter = 0;
                cluster.receiveProcessed(currentDBProcessed);
                currentDBProcessed = null;
            }
        }
        else {
            if (dataBatches.size()>0) {//there is un processed data to process
                currentDBProcessed=dataBatches.poll();
                tickCounter++;
                cluster.setCpuUTUed();
            }
        }
    }

    public void process(DataBatch db){ //get un process data from the cluster
        dataBatches.add(db);
    }
    private boolean DataIsProcessed(){
        Data.Type dbType=currentDBProcessed.getData().getType();
        switch (dbType){
            case Images:
                return tickCounter==imageProcessTime;
            case Text:
                return tickCounter==textProcessTime;
            case Tabular:
                return tickCounter==tabularProcessTime;
        }
        return false;
    }
}
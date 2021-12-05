package bgu.spl.mics.application.objects;


import java.awt.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    // @Inv tickCounter > 0
    // data.size() >= 0

    private int tickCounter;
    private int cores;
    private Container data;
    private Cluster cluster;
    private int processTime; //add final

    public int getTickCounter(){return tickCounter;}
    public Container getData(){return data;}
    public int getProcessTime(){return processTime;}
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
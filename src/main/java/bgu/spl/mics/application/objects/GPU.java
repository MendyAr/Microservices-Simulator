package bgu.spl.mics.application.objects;

import java.awt.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {

    // @Inv tickCounter > 0
    // vRam.size() >= 0 & vRam.size() <= vRamCapacity
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model = null;
    private Cluster cluster;

    private int tickCounter;
    private int processTime; //add final
    private Container vRam;
    private int vRamCapacity;
    private int samplesIdx;

    public int getTickCounter(){return tickCounter;}



    // @post tickCounter = tickCounter + 1;
    public void advanceClock(){}

    public Container getvRam() {
        return vRam;
    }

    public int getvRamCapacity() {
        return vRamCapacity;
    }

    public int getSamplesIdx() {
        return samplesIdx;
    }

    public Model getModel() {
        return model;
    }

    public int getProcessTime() {
        return processTime;
    }

    // @pre vRam.size() < vRamCapacity
    // @post vRam.size() ==  ( @pre vRam.size() ) + 1
    public void receiveDataBatch(){} //asking for processed dataBatch from the cluster

    // @pre samplesIdx < model.data.getSize() &  vRam.size() < vRamCapacity
    // @post samplesIdx == ( @pre samplesIdx + 1000 )
    public void sendDataBatch(){} //sending unprocessed data batch to the cluster

    // @pre vRam.size() > 0
    // @post tickCounter > (@pre tickCounter ) + processTime
    public void process(){} //process the data - wait for the defined ticks to happen

}
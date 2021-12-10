package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

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

    private final Type type;
    private final int vRamCapacity;
    private final  int processTimeCost;

    private Cluster cluster;
    private int tickCounter;
    private List<DataBatch> vRam;
    private Model model;
    private int samplesIdx;

    public GPU(String type) {
        switch (type) {
            case "RTX3090":
                this.type = Type.RTX3090;
                processTimeCost = 1;
                vRamCapacity = 32;
                break;
            case "RTX2080":
                this.type = Type.RTX2080;
                processTimeCost = 2;
                vRamCapacity = 16;
                break;
            case "GTX1080":
                this.type = Type.GTX1080;
                processTimeCost = 4;
                vRamCapacity = 8;
                break;
            default:
                throw new IllegalArgumentException("GPU type is not recognized!");
        }

        cluster = Cluster.getInstance();
        tickCounter = 0;
        vRam = new LinkedList<>();
        model = null;
        samplesIdx = 0;
    }

    public int getTickCounter(){return tickCounter;}

    // @post tickCounter = tickCounter + 1;
    public void advanceClock(){}

    public List<DataBatch> getvRam() {
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

    public int getProcessTimeCost() {
        return processTimeCost;
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
package bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentLinkedQueue;

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

    private final Cluster cluster;
    private int tickCounter;
    private final ConcurrentLinkedQueue<DataBatch> vRam;
    private int samplesIdx;
    private DataBatch currentDataB;
    private Model model;

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
        vRam = new ConcurrentLinkedQueue<>();
        model = null;
        samplesIdx = 0;
    }

    public int getTickCounter(){return tickCounter;}

    public ConcurrentLinkedQueue<DataBatch> getvRam() {
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

    public void setModel(Model model) {
        this.model = model;
    }

    public int getProcessTimeCost() {
        return processTimeCost;
    }


    // @pre vRam.size() < vRamCapacity
    // @post vRam.size() ==  ( @pre vRam.size() ) + 1
    public void receiveProcessedDB(DataBatch ProcessedDB) {     //receive processed dataBatch from the cluster
        vRam.add(ProcessedDB);
    }

    // @post tickCounter = tickCounter + 1;
    public boolean advanceClock() {
        //send db to the cpu
        if (samplesIdx < model.getData().getSize() & vRam.size() < vRamCapacity) {
            DataBatch dataBatch = new DataBatch(model.getData(), samplesIdx);
            cluster.sendUnProcessed(dataBatch, this);
            samplesIdx = samplesIdx + 1000;
        }
        //train db
        if (currentDataB == null & !vRam.isEmpty()){
            currentDataB = vRam.remove();
        }
        if (currentDataB != null) { //means the gpu is currently processed a DB
            tickCounter++;
            cluster.setGpuUTUed();
            if (processTimeCost == tickCounter) {
                currentDataB = null;
                tickCounter = 0;
                model.getData().incProcessed();
            }
        }
        if(model.getData().getProcessed() == model.getData().getSize()) {
            model.setStatus(Model.Status.Trained);
            samplesIdx = 0;
            return true;
        }
        return false;
    }

    public void testModel(Model trainedModel){
        trainedModel.setResult();
        trainedModel.setStatus(Model.Status.Tested);
    }

}
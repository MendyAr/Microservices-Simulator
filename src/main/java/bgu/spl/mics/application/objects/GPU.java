package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
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

    private Cluster cluster;
    private int tickCounter;
    private ConcurrentLinkedQueue<DataBatch> vRam;
    private Model model;
    private int samplesIdx;
    private DataBatch currentDataB;

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
        vRam = new ConcurrentLinkedQueue();
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

    public int getProcessTimeCost() {
        return processTimeCost;
    }
    public void setModel(Model m){
        model=m;
        model.setStatus(Model.Status.Training);
        for (int i=0;i<vRamCapacity && model.getData().getSize()>samplesIdx;i++){
            DataBatch db=new DataBatch(model.getData(),samplesIdx);
            cluster.sendUnProcessed(db,this);
            samplesIdx=samplesIdx+1000;
        }
    }

    // @pre vRam.size() < vRamCapacity
    // @post vRam.size() ==  ( @pre vRam.size() ) + 1
    public void receiveProcessedDB(DataBatch ProcessedDB) {//receive processed dataBatch from the cluster
        vRam.add(ProcessedDB);
    }
    // @post tickCounter = tickCounter + 1;
    public void advanceClock() {
        if (model != null) {//means the gpu is currently processed a model
            if (currentDataB != null) {
                tickCounter++;
                cluster.setGpuUTUed();
                if (processTimeCost == tickCounter) {
                    currentDataB = null;
                    tickCounter = 0;
                    model.getData().incProcessed();
                    if (samplesIdx<model.getData().getSize()) {
                        DataBatch dataBatch = new DataBatch(model.getData(), samplesIdx);
                        cluster.sendUnProcessed(dataBatch,this);
                        samplesIdx=samplesIdx+1000;
                    }
                }
            }
            else if (!vRam.isEmpty())
                currentDataB = vRam.remove();
            if(model.getData().getProcessed()==model.getData().getSize()) {
                model.setStatus(Model.Status.Trained);
                cluster.addTrainedModel(model);
                model=null;
            }
        }
    }

    // @pre vRam.size() > 0
    // @post tickCounter > (@pre tickCounter ) + processTime
    public void processModel(Model m) { //process the data - wait for the defined ticks to happen
        if (m.getStatus()== Model.Status.PreTrained)
            setModel(m);
        else if (m.getStatus()== Model.Status.Trained) {
            model.setStatus(Model.Status.Tested);
            model.setResult();
        }
    }

}
package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.objects.GPU;

import java.util.LinkedList;
import java.util.List;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private static int availableIdx = 0;

    private final GPU gpu;
    private final List<TrainModelEvent> trainEvents;

    public GPUService(GPU gpu) {
        super(getAvailableName());
        if (gpu == null)
            throw new IllegalArgumentException("GPU service received null gpu!");

        this.gpu = gpu;
        trainEvents = new LinkedList<>();
    }

    private static String getAvailableName(){
        String output = "GpuService" + availableIdx;
        availableIdx++;
        return output;
    }

    @Override
     protected void initialize() {
        subscribeEvent(TrainModelEvent.class, this::trainModel);
        subscribeEvent(TestModelEvent.class, this::testModel);
        subscribeBroadcast(terminateBroadcast.class, c->endSession());
        subscribeBroadcast(TickBroadcast.class, c->advanceClock());
    }

    private void trainModel(TrainModelEvent trainModelEvent){
        trainEvents.add(trainModelEvent);
        System.out.println(getName() + " recived a model");
    }

    private void advanceClock(){
        if (gpu.getModel() == null & !trainEvents.isEmpty()) {
            gpu.setModel(trainEvents.get(0).getModel());
        }
        if (gpu.getModel() != null) {
            boolean isDone = gpu.advanceClock();
            if (isDone) {
                complete(trainEvents.remove(0), gpu.getModel());
                System.out.println(getName() + " finished processing a model. isNull:" + (gpu.getModel()==null));
                gpu.setModel(null);
            }
        }
    }

    private void testModel(TestModelEvent testModelEvent){
        System.out.println(getName() + " testing model");
        gpu.testModel(testModelEvent.getModel());
        complete(testModelEvent, testModelEvent.getModel());
    }

    protected void endSession(){
        for (TrainModelEvent trainModelEvent : trainEvents){
            complete(trainModelEvent, null);
        }
        terminate();
    }

}

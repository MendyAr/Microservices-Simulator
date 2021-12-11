package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPU;

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
    private int gpuUseTime;

    public GPUService(GPU gpu) {
        super(getAvailableName());
        if (gpu == null)
            throw new IllegalArgumentException("GPU service received null gpu!");

        this.gpu = gpu;
        gpuUseTime = 0;
        initialize();
    }

    private static String getAvailableName(){
        String output = "GpuService" + availableIdx;
        availableIdx++;
        return output;
    }

    public int getGpuUseTime() {
        return gpuUseTime;
    }

    @Override
     protected void initialize() {
        // TODO Implement this

    }

}

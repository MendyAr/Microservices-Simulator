package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.CPU;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private static int availableIdx = 0;

    private final CPU cpu;
    private int cpuUseTime;
    private int batchesProcessed;

    public CPUService(CPU cpu) {
        super(getAvailableName());
        if (cpu == null)
            throw new IllegalArgumentException("CPU service received null cpu!");

        this.cpu = cpu;
        cpuUseTime = 0;
        batchesProcessed = 0;
        initialize();
    }

    private static String getAvailableName(){
        String output = "CpuService" + availableIdx;
        availableIdx++;
        return output;
    }

    public int getCpuUseTime() {
        return cpuUseTime;
    }

    public int getBatchesProcessed() {
        return batchesProcessed;
    }

    @Override
    protected void initialize() {
        // TODO Implement this

    }
}

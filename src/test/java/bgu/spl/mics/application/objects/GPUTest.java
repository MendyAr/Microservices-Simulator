package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {

    GPU gpu;

    @BeforeEach
    void setUp() {
        gpu=new GPU("RTX3090");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void advanceClock() {
        int curTickCounter=gpu.getTickCounter();
        gpu.advanceClock();
        assertEquals(gpu.getTickCounter(),curTickCounter+1);
    }

    @Test
    void receiveDataBatch() {
        assertTrue(gpu.getvRam().size()< gpu.getvRamCapacity());
        int currVramSize=gpu.getvRam().size();
        assertEquals(gpu.getvRam().size(),currVramSize+1);

    }

    @Test
    void sendDataBatch() {
        assertTrue(gpu.getSamplesIdx()<gpu.getModel().getData().getSize());
        assertTrue(gpu.getvRam().size()< gpu.getvRamCapacity());
        int currSampleIdx= gpu.getSamplesIdx();
        gpu.sendDataBatch();
        assertEquals(gpu.getSamplesIdx(),currSampleIdx+1000);
    }

    @Test
    void process() {
        assertTrue(gpu.getvRam().size()>0);
        int currTickCounter= gpu.getTickCounter();
        gpu.process();
        assertEquals(gpu.getTickCounter(),currTickCounter+gpu.getProcessTimeCost());

    }
}
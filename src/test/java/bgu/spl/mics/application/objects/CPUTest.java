package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {
    private CPU cpu;

    @BeforeEach
    void setUp() {
        cpu=new CPU(32);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void advanceClock() {
        int curTickCounter=cpu.getTickCounter();
        cpu.advanceClock();
        assertEquals(curTickCounter,cpu.getTickCounter());
    }

    @Test
    void receiveDataBatch() {
        assertEquals(cpu.getData().size(),0);
        cpu.receiveDataBatch();
        assertEquals(cpu.getData().size(),1);
    }

    @Test
    void sendDataBatch() {
        assertTrue(cpu.getData().size()>0);
        int curDataSize=cpu.getData().size();
        cpu.receiveDataBatch();
        assertEquals(cpu.getData().size(),curDataSize+1);
    }

    @Test
    void process() {
        assertTrue(cpu.getData().size()>0);
        int curTickCounter= cpu.getTickCounter();
        cpu.process();
        assertTrue(cpu.getTickCounter()>curTickCounter+ cpu.getImageProcessTime());
    }
}
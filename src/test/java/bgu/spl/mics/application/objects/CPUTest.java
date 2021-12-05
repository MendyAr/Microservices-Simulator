package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CPUTest {
    private CPU cpu;

    @BeforeEach
    void setUp() {
        cpu=new CPU();
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
    void receiveDataBatch() {//how to check the wait method, is it ok to assert to one
        assertEquals(cpu.getData().getSize(),0);
        cpu.receiveDataBatch();
        assertEquals(cpu.getData().getSize(),1);
    }

    @Test
    void sendDataBatch() {//if i need to had try and catch
        assertTrue(cpu.getData().getWidth()>0);//container width
        int curDataSize=cpu.getData().getWidth();
        cpu.receiveDataBatch();
        assertEquals(cpu.getData().getWidth(),curDataSize+1);
    }

    @Test
    void process() {
        assertTrue(cpu.getData().getWidth()>0);
        int curTickCounter= cpu.getTickCounter();
        cpu.process();
        assertTrue(cpu.getTickCounter()>curTickCounter+ cpu.getProcessTime());
    }
}
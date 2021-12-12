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
    void process() {

    }
}
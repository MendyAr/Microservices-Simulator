package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {
    Future<Model>future;
    Model model;
    Model result;
    @BeforeEach
    void setUp() {
        future=new Future<>();
        model=new Model("name1", "Image", 1000);
        result=new Model("name2", "Image", 1000);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void get() throws InterruptedException {
        Model.Status currStatus=model.getStatus();
        result=future.get();
        assertNull(result);
        future.resolve(model);
        assertNotNull(result);
        assertTrue(future.isDone());
        assertNotEquals(model.getStatus(),currStatus);
    }

    @Test
    void resolve() {
        Model.Status currStatus=model.getStatus();
        assertNull(result);
        assertFalse(future.isDone());
        future.resolve(model);
        result=future.get();
        assertNotNull(result);
        assertTrue(future.isDone());
        assertNotEquals(model.getStatus(),currStatus);
    }

    @Test
    void isDone() {

        assertFalse(future.isDone());
        future.resolve(model);
        assertNotNull(result);

    }

    @Test
    void testGet() throws InterruptedException {

        long timeOut=1000;
        TimeUnit unit=TimeUnit.MILLISECONDS;

        Thread t1=new Thread(()->{
            result=future.get(timeOut,unit);
        });
        Thread t2=new Thread(()->{
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            future.resolve(model);
        });
        t1.start();
        t2.start();
        t1.join();
        assertNull(result);
        t2.join();
        t1.start();
        t1.join();
        assertNotNull(result);


    }
}
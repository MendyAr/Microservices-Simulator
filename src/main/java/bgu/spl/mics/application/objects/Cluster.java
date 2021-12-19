package bgu.spl.mics.application.objects;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	//private Collection<GPU> GPU;
	private final Queue<CPU> CPU;
	private final AtomicInteger totalDBProcessed;
	private final AtomicInteger cpuTUUed;	//unit time used
	private final AtomicInteger gpuTUUsed;
	private final ConcurrentHashMap<DataBatch,GPU> gpuDataBatchMap;
	private static class SingletonHolder{
		private static Cluster instance=new Cluster();
	}

	private Cluster(){
		CPU=new LinkedList<>();
		totalDBProcessed=new AtomicInteger(0);
		cpuTUUed=new AtomicInteger(0);
		gpuTUUsed=new AtomicInteger(0);
		gpuDataBatchMap=new ConcurrentHashMap<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return SingletonHolder.instance;
	}
	public int getTotalDBProcessed(){return totalDBProcessed.get();}
	public int getCpuTUUed(){return cpuTUUed.get();}
	public int getGpuTUUed(){return gpuTUUsed.get();}

	public void sendProcessed(DataBatch DBProcessed) {//receive processed data from the cpu
		gpuDataBatchMap.get(DBProcessed).receiveProcessedDB(DBProcessed);

		int oldValue;
		int newValue;
		boolean succeeded = false;
		while (!succeeded) {
			oldValue = totalDBProcessed.get();
			newValue = oldValue + 1;
			succeeded = totalDBProcessed.compareAndSet(oldValue, newValue);
		}
	}

	public void sendUnProcessed(DataBatch DB, GPU senderGpu){
		gpuDataBatchMap.put(DB, senderGpu);
		CPU c;
		synchronized (this) {
			c = CPU.remove();
			CPU.add(c);
		}
		c.process(DB);
	}

	public void setCpuUTUed(){
		int oldCpuUsedTime;
		int newCpuUsedTime;
		boolean succeeded = false;
		while (!succeeded) {
			oldCpuUsedTime = cpuTUUed.get();
			newCpuUsedTime = oldCpuUsedTime + 1;
			succeeded = cpuTUUed.compareAndSet(oldCpuUsedTime, newCpuUsedTime);
		}
	}

	public void setGpuUTUed(){
		int oldGpuUsedTime;
		int newGpuUsedTime;
		boolean succeeded = false;
		while (!succeeded) {
			oldGpuUsedTime = gpuTUUsed.get();
			newGpuUsedTime = oldGpuUsedTime + 1;
			succeeded = gpuTUUsed.compareAndSet(oldGpuUsedTime, newGpuUsedTime);
		}
	}

	public synchronized void registerCPU(CPU cpu){
		CPU.add(cpu);
		notifyAll();
	}

}

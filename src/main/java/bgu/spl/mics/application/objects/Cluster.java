package bgu.spl.mics.application.objects;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private Collection<GPU> GPU;
	private Queue<CPU> CPU;
	private Vector<String> trainedModels;
	AtomicInteger TotalDBProcessed;
	AtomicInteger cpuTUUed;//unit time used
	AtomicInteger gpuTUUsed;
	Map<DataBatch,GPU> gpuDataBatchMap;
	private static class SingletonHolder{
		private static Cluster instance=new Cluster();
	}

	private Cluster(){
		TotalDBProcessed=new AtomicInteger(0);
		cpuTUUed=new AtomicInteger(0);
		gpuTUUsed=new AtomicInteger(0);
		trainedModels=new Vector<>();
		GPU=new LinkedList<>();
		CPU=new LinkedList<>();
		gpuDataBatchMap=new HashMap<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return SingletonHolder.instance;
	}
	public void receiveProcessed(DataBatch DBProcessed) {//receive processed data from the cpu
		int currTotalDBProcessed;
		int newTotalDBProcessed;
		do {
			currTotalDBProcessed = TotalDBProcessed.get();
			newTotalDBProcessed = currTotalDBProcessed++;
		} while (!TotalDBProcessed.compareAndSet(currTotalDBProcessed, newTotalDBProcessed));
		gpuDataBatchMap.get(DBProcessed).receiveProcessedDB(DBProcessed);
	}
	public void sendUnProcessed(DataBatch DB,GPU senderGpu){
		gpuDataBatchMap.put(DB,senderGpu);
		CPU c=CPU.remove();
		CPU.add(c);
		c.process(DB);
	}
	public void setCpuUTUed(){
		int oldCpuUsedTime;
		int newCpuUsedTime;
		do {
			oldCpuUsedTime = cpuTUUed.get();
			newCpuUsedTime = oldCpuUsedTime++;
		}while (!cpuTUUed.compareAndSet(oldCpuUsedTime,newCpuUsedTime));
	}
	public void setGpuUTUed(){
		int oldGpuUsedTime;
		int newGpuUsedTime;
		do {
			oldGpuUsedTime = gpuTUUsed.get();
			newGpuUsedTime = oldGpuUsedTime++;
		}while (!gpuTUUsed.compareAndSet(oldGpuUsedTime,newGpuUsedTime));
	}
	public void addTrainedModel(Model m){
		trainedModels.add(m.getName());
	}

}

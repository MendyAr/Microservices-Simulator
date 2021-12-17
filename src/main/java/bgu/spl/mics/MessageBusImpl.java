package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//TODO adding priority for clock tick broadcast?
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private final ConcurrentHashMap<Class<? extends Event>, List<Integer>> eventsSubscriptions;
	private final ConcurrentHashMap<Class<? extends Broadcast>, List<Integer>> broadcastSubscriptions;
	private final ConcurrentHashMap<Integer,Queue<Message>> microServiceQueues;
	private final ConcurrentHashMap<Event, Future> eventFutureMap;

	private static class InstanceHolder {
		public static final MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl() {
		eventsSubscriptions = new ConcurrentHashMap<>();
		broadcastSubscriptions = new ConcurrentHashMap<>();
		microServiceQueues = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return InstanceHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!microServiceQueues.containsKey(m.hashCode()))
			throw new RuntimeException ("Micro service " + m.getName() + " must register before subscribe actions");

		List<Integer> list = new LinkedList<>();
		list.add(m.hashCode());
		if(eventsSubscriptions.putIfAbsent(type, list)!=null) {
			eventsSubscriptions.get(type).add(m.hashCode());
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!microServiceQueues.containsKey(m.hashCode()))
			throw new RuntimeException ("Micro service " + m.getName() + " must register before subscribe actions");

		List<Integer> list = new LinkedList<>();
		list.add(m.hashCode());
		if(broadcastSubscriptions.putIfAbsent(type, list)!=null) {
			broadcastSubscriptions.get(type).add(m.hashCode());
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for(Integer m : broadcastSubscriptions.get(b.getClass())){
			microServiceQueues.get(m).add(b);
		}
		notifyAll();
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (eventsSubscriptions.containsKey(e.getClass())) { //assuming if the key exist than the value holds a non-empty record
			synchronized (this) {
				RoundRobin(eventsSubscriptions.get(e.getClass())).add(e);

			}
			Future<T> future = new Future<>();
			eventFutureMap.put(e, future);
			notifyAll();
			return future;
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		microServiceQueues.putIfAbsent(m.hashCode(), new PriorityQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		for(List<Integer> eventSubscribers : eventsSubscriptions.values()){
			eventSubscribers.remove((Integer) m.hashCode());
		}
		for(List<Integer> broadcastSubscribers : broadcastSubscriptions.values()){
			broadcastSubscribers.remove((Integer) m.hashCode());
		}
		microServiceQueues.remove(m.hashCode());
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(microServiceQueues.get(m.hashCode())!=null) {
			while (microServiceQueues.get(m.hashCode()).isEmpty()) {
				wait();
			}
			return microServiceQueues.get(m.hashCode()).remove();
		}
		return null;
	}


	private Queue<Message> RoundRobin(List<Integer> microServicesHash){
		Integer output = microServicesHash.remove(0);
		microServicesHash.add(output);
		return microServiceQueues.get(output);
	}

	

}

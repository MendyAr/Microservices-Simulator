package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private final ConcurrentHashMap<Class<? extends Event>, List<String>> eventsSubscriptions;
	private final ConcurrentHashMap<Class<? extends Broadcast>, List<String>> broadcastSubscriptions;
	private final ConcurrentHashMap<String,Queue<Message>> microServiceQueues;
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
		if (!microServiceQueues.containsKey(m.getName()))
			throw new RuntimeException ("Micro service " + m.getName() + " must register before subscribe actions");

		List<String> list = new LinkedList<>();
		list.add(m.getName());
		if(eventsSubscriptions.putIfAbsent(type, list)!=null) {
			eventsSubscriptions.get(type).add(m.getName());
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!microServiceQueues.containsKey(m.getName()))
			throw new RuntimeException ("Micro service " + m.getName() + " must register before subscribe actions");

		List<String> list = new LinkedList<>();
		list.add(m.getName());
		if(broadcastSubscriptions.putIfAbsent(type, list) != null) {
			broadcastSubscriptions.get(type).add(m.getName());
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureMap.get(e).resolve(result);
		eventFutureMap.remove(e);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		try {
			for(String m : broadcastSubscriptions.get(b.getClass())){
				if(microServiceQueues.get(m)!=null) {
					microServiceQueues.get(m).add(b);
				}
			}
		} catch (Exception e){
			int i =5;
		}

		notifyAll();
	}

	
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		if (eventsSubscriptions.containsKey(e.getClass()) && !eventsSubscriptions.get(e.getClass()).isEmpty()) {
			Queue<Message> microServiceQueue = RoundRobin(eventsSubscriptions.get(e.getClass()));
			microServiceQueue.add(e);
			Future<T> future = new Future<>();
			eventFutureMap.put(e, future);
			notifyAll();
			return future;
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		microServiceQueues.putIfAbsent(m.getName(), new LinkedList<>());
	}

	@Override
	public synchronized void unregister(MicroService m) {
		for(List<String> eventSubscribers : eventsSubscriptions.values()){
			eventSubscribers.remove(m.getName());
		}
		for(List<String> broadcastSubscribers : broadcastSubscriptions.values()){
			broadcastSubscribers.remove(m.getName());
		}
		Queue<Message> unResolvedMessages = microServiceQueues.remove(m.getName());
		for (Message message : unResolvedMessages){
			if (message instanceof Event){
				complete((Event) message, null);
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(microServiceQueues.get(m.getName()) != null) {
			while (microServiceQueues.get(m.getName()).isEmpty()) {
				synchronized (this){
					wait();
				}
			}
			return microServiceQueues.get(m.getName()).remove(); //assuming each Micro Service runs in a different this is a thread safe
		}
		return null;
	}


	private Queue<Message> RoundRobin(List<String> microServicesList){
		String output = microServicesList.remove(0);
		microServicesList.add(output);
		return microServiceQueues.get(output);
	}

	/*@Override
	public Boolean isMicroServiceRegistered(Class<? extends Event> type) {
		return eventsSubscriptions.containsKey(type);
	}*/
}

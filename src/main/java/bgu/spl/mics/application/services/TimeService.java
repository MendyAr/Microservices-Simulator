package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private static TimeService timeService = null;

	private final int tickTime;
	private final int duration;

	private TimeService(int tickTime, int duration) {
		super("TimeService");
		if (tickTime <= 0 | duration <= 0)
			throw new IllegalArgumentException("tickTime or duration isn't a positive value!");

		this.tickTime = tickTime;
		this.duration = duration;
		initialize();
	}

	public static TimeService getFirstInstance(int tickTime, int duration){
		if (timeService != null)
			throw new RuntimeException("an instance of time service already exist!");

		timeService = new TimeService(tickTime, duration);
		return timeService;
	}

	public static TimeService getInstance(){
		if (timeService == null)
			throw new RuntimeException("instance of time service not exist!");

		return timeService;
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		
	}

}

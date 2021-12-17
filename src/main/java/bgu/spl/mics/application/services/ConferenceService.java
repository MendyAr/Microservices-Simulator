package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.LinkedList;
import java.util.List;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private static int availableIdx = 0;

    private final ConfrenceInformation conference;

    public ConferenceService(ConfrenceInformation conference){
        super(getAvailableName());
        if (conference == null)
            throw new IllegalArgumentException("Conference service constructor received null conference!");

        this.conference = conference;
        initialize();
    }

    private static String getAvailableName(){
        String output = "ConferenceService" + availableIdx;
        availableIdx++;
        return output;
    }
    public void publish(){
        conference.advanceClock();
        if (conference.getTickCounter()==conference.getDate()) {
            PublishConferenceBroadcast broadcast = new PublishConferenceBroadcast(conference.getPublishedModels());
            sendBroadcast(broadcast);
            terminate();
        }
    }


    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class,(PublishResultsEvent event)-> conference.aggregateIfSucc(event.getModel()));
        subscribeBroadcast(terminateBroadcast.class,c->terminate());
        subscribeBroadcast(TickBroadcast.class,c->publish());
    }

    @Override
    public String toString() {
        return conference.toString();
    }
}

package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private static int availableIdx = 0;

    private final Student student;

    public StudentService(Student student) {
        super(getAvailableName());
        if (student == null)
            throw new IllegalArgumentException("Student service received null student!");

        this.student = student;
        initialize();
    }

    private static String getAvailableName(){
        String output = "StudentService" + availableIdx;
        availableIdx++;
        return output;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConferenceBroadcast.class,(PublishConferenceBroadcast broadcast)->student.addpapersRead(broadcast.getPublishedModels()));
        subscribeBroadcast(terminateBroadcast.class, c->terminate());
        for (int i=0;i<student.getModels().size();i++) {
            TrainModelEvent trainModelEvent = new TrainModelEvent(student.getModels().get(i));
            Future<Model> trainedResult=sendEvent(trainModelEvent);
            if (trainedResult.get()!=null) {
                TestModelEvent testModelEvent = new TestModelEvent(trainedResult.get());
                Future<Model> testResult = sendEvent(testModelEvent);
                if (testResult.get()!=null && testResult.get().getResult()== Model.Result.Good){
                    PublishResultsEvent publishResultsEvent=new PublishResultsEvent(testResult.get());
                    sendEvent(publishResultsEvent);
                }

            }
        }

    }

    public String toString(){
        return student.toString();
    }
}

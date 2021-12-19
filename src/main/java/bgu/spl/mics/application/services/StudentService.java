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
    }

    private static String getAvailableName(){
        String output = "StudentService" + availableIdx;
        availableIdx++;
        return output;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConferenceBroadcast.class,(PublishConferenceBroadcast broadcast)->student.addPapersRead(broadcast.getPublishedModels()));
        subscribeBroadcast(terminateBroadcast.class, c->terminate());
        boolean terminated = false;
        for (int i = 0; i < student.getModels().size() && !terminated ;i++) {
            TrainModelEvent trainModelEvent = new TrainModelEvent(student.getModels().get(i));
            Future<Model> trainedResult = sendEvent(trainModelEvent);
            System.out.println(getName() + " student sent train event");
            Model trainedModel = trainedResult.get();
            System.out.println(getName() + " student received train event.     isNull:" + (trainedModel==null));
            //System.out.println(trainedModel.toString());
            if (trainedModel != null) {
                TestModelEvent testModelEvent = new TestModelEvent(trainedModel);
                Future<Model> testResult = sendEvent(testModelEvent);
                System.out.println(getName() + " student sent test event");
                Model testedModel = testResult.get();
                System.out.println(getName() + " student received test event.     isNull:" + (testedModel==null));
                if (testedModel !=null && testedModel.getResult() == Model.Result.Good){
                    System.out.println(getName() + " student sent publication event");
                    PublishResultsEvent publishResultsEvent = new PublishResultsEvent(testedModel);
                    sendEvent(publishResultsEvent);
                }
                else if (testedModel == null){
                    terminated = true;
                }
            }
            else{
                terminated = true;
            }
        }
        System.out.println(getName() + " finish sending models");
    }

    public String toString(){
        return student.toString();
    }
}

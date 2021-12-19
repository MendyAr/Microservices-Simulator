package bgu.spl.mics.application.objects;

import java.util.Random;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public enum Status {PreTrained,Training,Trained,Tested}
    public enum Result {None,Good,Bad}

    private final String name;
    private final Data data;
    private Status status;
    private Result result;
    private Student student;
    private boolean published;

    public Model(String name, String dataType, int dataSize) {
        this.name = name;
        this.data = new Data(dataType, dataSize);
        status = Status.PreTrained;
        result = Result.None;
        student = null;
        published = false;
    }

    void initializeCreatingStudent(Student creatingStudent){
        if(student != null)
            throw new IllegalArgumentException("Another student already created this model!");

        this.student = creatingStudent;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public Student getStudent() {
        return student;
    }

    public void setStatus(Model.Status s){
        status=s;
    }

    public boolean isPublished(){
        return published;
    }

    public void setPublished(){
        published = true;
    }

    public void setResult(){
        Random rand = new Random();
        int random = rand.nextInt(10);  // Generate random integers in range 0 to 9
        if (student.getStatus()== Student.Degree.MSc) {
            if (random <= 5)
                result = Result.Good;
            else
                result = Result.Bad;
        }
        else if (random <= 7)
            result=Result.Good;
        else
            result=Result.Bad;
    }

    public String toString(){
        String output =  "name: " + getName() + "\t" + "status: " + getStatus().toString() + "\t\t" + "result: " + getResult().toString();
        if (isPublished()){
            output += ",\t published.";
        }
        return output;
    }
}

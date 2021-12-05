package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;
    private Student student;
    public enum status {PreTrained,Training,Trained,Tested}
    enum result {None,Good,Bad}
    status status;

    public Data getData() {
        return data;
    }
    public status getStatus(){return status;}
    public void setStatus(status s){status=s;}

}

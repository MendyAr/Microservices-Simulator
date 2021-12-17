package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Locale;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {

    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private final String name;
    private final String department;
    private final Degree status;
    private int numOfPublications;
    private int numOfPapersRead;
    private List<Model> models;

    public Student(String name, String department, String status, List<Model> models) {
        this.name = name;
        this.department = department;
        status = status.toLowerCase(Locale.ROOT);
        switch (status){
            case "msc":
                this.status = Degree.MSc;
                break;
            case "phd":
                this.status = Degree.PhD;
                break;
            default:
                throw new IllegalArgumentException("Student type is not recognized!");
        }
        numOfPublications = 0;
        numOfPapersRead = 0;
        this.models = models;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getNumOfPublications() {
        return numOfPublications;
    }

    public int getNumOfPapersRead() {
        return numOfPapersRead;
    }

    public List<Model> getModels() {
        return models;
    }

    /* ?? public List<Model> getPublishedModels() {
        return null; //TODO implementation
    } */


    public String toString(){
        String output = "";
        output += "Student name: " + getName() + "\n" + "Models: \n";
        for (Model model : models){
            if (model.getStatus() == Model.Status.Trained | model.getStatus() == Model.Status.Tested){
                output += model.toString() + "\n";
            }
        }
        output += "Number of papers he/she read: " + getNumOfPapersRead();
        return output;
    }

    public void addpapersRead(List<Model> publishedModels) {
        for (Model m:publishedModels){
            if (m.getStudent()!=this)
                numOfPapersRead++;
        }
    }
}

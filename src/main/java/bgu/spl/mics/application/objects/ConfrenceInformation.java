package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private final String name;
    private final int date;

    private List<Model> publishedModels;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        publishedModels = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }

    public String toString(){
        String output = "Conference name: " + getName() + "\n" + "published model: " + "\n";
        if (!publishedModels.isEmpty()) {
            for (Model model : publishedModels) {
                output += model.getName() + ", ";
            }
            output = output.substring(0, output.length() - 3);
        }
        else
        {
            output += "-nothing was published-" + "\n";
        }
        return output;
    }
}

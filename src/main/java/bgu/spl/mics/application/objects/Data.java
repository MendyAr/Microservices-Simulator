package bgu.spl.mics.application.objects;

import java.util.Locale;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int size;
    private int processed;

    public Data(String type, int size) {
        type = type.toLowerCase(Locale.ROOT);
        switch (type){
            case "images":
                this.type = Type.Images;
                break;
            case "text":
                this.type = Type.Text;
                break;
            case "tabular":
                this.type = Type.Tabular;
                break;
            default:
                throw new IllegalArgumentException("Data type is not recognized!");
        }
        this.size = size;
        processed = 0;
    }

    public int getSize() {
        return size;
    }

    public Type getType() {
        return type;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }
}

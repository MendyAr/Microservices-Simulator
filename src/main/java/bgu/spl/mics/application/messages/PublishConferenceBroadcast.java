package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.List;

public class PublishConferenceBroadcast implements Broadcast {
    private List<Model> publishedModels;
    public PublishConferenceBroadcast(List<Model> publishedModels){
        this.publishedModels=publishedModels;
    }

    public List<Model> getPublishedModels() {
        return publishedModels;
    }
}

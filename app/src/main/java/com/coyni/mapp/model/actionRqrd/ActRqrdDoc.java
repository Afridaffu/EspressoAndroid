package com.coyni.mapp.model.actionRqrd;

import java.io.File;

public class ActRqrdDoc {
    private int docID;
    private File mediaFile;

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public File getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
    }
}

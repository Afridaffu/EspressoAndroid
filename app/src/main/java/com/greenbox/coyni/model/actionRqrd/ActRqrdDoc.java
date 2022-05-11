package com.greenbox.coyni.model.actionRqrd;

import com.greenbox.coyni.model.underwriting.AdditionalDocumentData;
import com.greenbox.coyni.model.underwriting.InformationChangeData;
import com.greenbox.coyni.model.underwriting.ReseveRuleData;
import com.greenbox.coyni.model.underwriting.WebsiteChangeData;

import java.io.File;
import java.util.List;

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

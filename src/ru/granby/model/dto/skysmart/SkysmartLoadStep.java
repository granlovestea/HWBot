package ru.granby.model.dto.skysmart;

import com.google.gson.annotations.SerializedName;

public class SkysmartLoadStep {
    @SerializedName("content")
    private String content;

    public SkysmartLoadStep(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

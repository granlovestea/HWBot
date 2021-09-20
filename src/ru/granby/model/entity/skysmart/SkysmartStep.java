package ru.granby.model.entity.skysmart;

import com.google.gson.annotations.SerializedName;

public class SkysmartStep {
    @SerializedName("stepUuid")
    private String stepUuid;

    @SerializedName("content")
    private String content;

    public SkysmartStep(String stepUuid, String content) {
        this.stepUuid = stepUuid;
        this.content = content;
    }

    public SkysmartStep(String stepUuid) {
        this.stepUuid = stepUuid;
    }

    public String getStepUuid() {
        return stepUuid;
    }

    public void setStepUuid(String stepUuid) {
        this.stepUuid = stepUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

package ru.granby.model.dto.skysmart;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SkysmartTaskMeta {
    @SerializedName("stepUuids")
    private List<String> stepUuids;

    public SkysmartTaskMeta(List<String> stepUuids) {
        this.stepUuids = stepUuids;
    }

    public List<String> getStepUuids() {
        return stepUuids;
    }

    public void setStepUuids(List<String> stepUuids) {
        this.stepUuids = stepUuids;
    }
}

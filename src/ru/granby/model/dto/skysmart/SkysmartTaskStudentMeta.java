package ru.granby.model.dto.skysmart;

import com.google.gson.annotations.SerializedName;
import ru.granby.model.entity.skysmart.SkysmartStep;

import java.util.List;

public class SkysmartTaskStudentMeta {
    @SerializedName("steps")
    private List<SkysmartStep> steps;

    public SkysmartTaskStudentMeta(List<SkysmartStep> steps) {
        this.steps = steps;
    }

    public List<SkysmartStep> getSteps() {
        return steps;
    }

    public void setSteps(List<SkysmartStep> steps) {
        this.steps = steps;
    }
}

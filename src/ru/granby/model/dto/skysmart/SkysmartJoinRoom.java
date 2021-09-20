package ru.granby.model.dto.skysmart;

import com.google.gson.annotations.SerializedName;

public class SkysmartJoinRoom {
    @SerializedName("taskMeta")
    private SkysmartTaskMeta taskMeta;

    @SerializedName("taskStudentMeta")
    private SkysmartTaskStudentMeta taskStudentMeta;

    public SkysmartJoinRoom(SkysmartTaskMeta taskMeta, SkysmartTaskStudentMeta taskStudentMeta) {
        this.taskMeta = taskMeta;
        this.taskStudentMeta = taskStudentMeta;
    }

    public SkysmartTaskMeta getTaskMeta() {
        return taskMeta;
    }

    public void setTaskMeta(SkysmartTaskMeta taskMeta) {
        this.taskMeta = taskMeta;
    }

    public SkysmartTaskStudentMeta getTaskStudentMeta() {
        return taskStudentMeta;
    }

    public void setTaskStudentMeta(SkysmartTaskStudentMeta taskStudentMeta) {
        this.taskStudentMeta = taskStudentMeta;
    }
}

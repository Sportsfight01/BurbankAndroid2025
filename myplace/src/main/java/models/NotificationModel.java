package models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class NotificationModel implements Serializable {

    boolean stage, photo, read;
    int taskId, progressPosition;
    String date, daysTag, heading, currentStage, oldStage;
    int days,minutes;


    public NotificationModel() {
    }

    public NotificationModel(String date, String read, String daysTag) {
        this.photo = true;
        this.date = date;
        if (read.equalsIgnoreCase("true")) {
            this.read = true;
        } else {
            this.read = false;
        }
        this.daysTag = daysTag;
    }

    public NotificationModel(String date, String read, String daysTag,int days, int minutes) {
        this.photo = true;
        this.date = date;
        if (read.equalsIgnoreCase("true")) {
            this.read = true;
        } else {
            this.read = false;
        }
        this.daysTag = daysTag;
        this.days = days;
        this.minutes = minutes;
    }

    public NotificationModel(String heading, int progressPosition, int taskId, String date, String read, String daysTag) {
        this.heading = heading;
        this.progressPosition = progressPosition;
        this.taskId = taskId;
        this.date = date;
        if (read.equalsIgnoreCase("true")) {
            this.read = true;
        } else {
            this.read = false;
        }
        this.daysTag = daysTag;


        this.photo = false;
        this.stage = false;
    }

    public NotificationModel(String heading, int progressPosition, int taskId, String date, String read, String daysTag,int days, int minutes) {
        this.heading = heading;
        this.progressPosition = progressPosition;
        this.taskId = taskId;
        this.date = date;
        if (read.equalsIgnoreCase("true")) {
            this.read = true;
        } else {
            this.read = false;
        }
        this.daysTag = daysTag;


        this.photo = false;
        this.stage = false;
        this.days = days;
        this.minutes = minutes;
    }

    public NotificationModel(String heading, int progressPosition, int taskId, String date, String read, String currentStage, String oldStage, String daysTag) {
        this.heading = heading;
        this.progressPosition = progressPosition;
        this.taskId = taskId;
        this.date = date;
        if (read.equalsIgnoreCase("true")) {
            this.read = true;
        } else {
            this.read = false;
        }
        this.currentStage = currentStage;
        this.oldStage = oldStage;
        this.daysTag = daysTag;

        this.photo = false;
        this.stage = true;
    }

    public NotificationModel(String heading, int progressPosition, int taskId, String date, String read, String currentStage, String oldStage, String daysTag,int days, int minutes) {
        this.heading = heading;
        this.progressPosition = progressPosition;
        this.taskId = taskId;
        this.date = date;
        if (read.equalsIgnoreCase("true")) {
            this.read = true;
        } else {
            this.read = false;
        }
        this.currentStage = currentStage;
        this.oldStage = oldStage;
        this.daysTag = daysTag;

        this.photo = false;
        this.stage = true;
        this.days = days;
        this.minutes = minutes;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDaysTag() {
        return daysTag;
    }

    public void setDaysTag(String daysTag) {
        this.daysTag = daysTag;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getOldStage() {
        return oldStage;
    }

    public void setOldStage(String oldStage) {
        this.oldStage = oldStage;
    }

    public boolean isStage() {
        return stage;
    }

    public void setStage(boolean stage) {
        this.stage = stage;
    }

    public int getProgressPosition() {
        return progressPosition;
    }

    public void setProgressPosition(int progressPosition) {
        this.progressPosition = progressPosition;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPhoto() {
        return photo;
    }

    public void setPhoto(boolean photo) {
        this.photo = photo;
    }
}

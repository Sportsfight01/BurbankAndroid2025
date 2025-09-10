package models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jaya.Krishna on 20-11-2017.
 */

public class AdministrationDataForQldOrSA {
    int taskId, stageId, sequence;
    String resourceName, phaseCode, name, status, dateDescription, dateActual, comment, stageName, displayDate;
    boolean forClient;

    public AdministrationDataForQldOrSA(String result) {
        try {
            JSONObject job = new JSONObject(result);
            taskId = job.isNull("taskid") ? 0 : job.getInt("taskid");
            stageId = job.isNull("stageId") ? 0 : job.getInt("stageId");
            sequence = job.isNull("sequence") ? 0 : job.getInt("sequence");

            resourceName = job.isNull("resourcename") ? "" : job.getString("resourcename");
            phaseCode = job.isNull("phasecode") ? "" : job.getString("phasecode");
            name = job.isNull("name") ? "" : job.getString("name");
            status = job.isNull("status") ? "" : job.getString("status");
            dateDescription = job.isNull("datedescription") ? "" : job.getString("datedescription");
            dateActual = job.isNull("dateactual") ? "" : job.getString("dateactual");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfString = new SimpleDateFormat("dd - MMMM - yyyy");
            try {
                Date complionDate = sdf.parse(dateActual);
                displayDate = sdfString.format(complionDate);
            } catch (ParseException e) {
                e.printStackTrace();
                displayDate = "";
            }
            comment = job.isNull("comment") ? "" : job.getString("comment");
            stageName = job.isNull("stageName") ? "" : job.getString("stageName");
            forClient = job.isNull("forclient") ? false : job.getBoolean("forclient");
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getPhaseCode() {
        return phaseCode;
    }

    public void setPhaseCode(String phaseCode) {
        this.phaseCode = phaseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateDescription() {
        return dateDescription;
    }

    public void setDateDescription(String dateDescription) {
        this.dateDescription = dateDescription;
    }

    public String getDateActual() {
        return dateActual;
    }

    public void setDateActual(String dateActual) {
        this.dateActual = dateActual;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public boolean isForClient() {
        return forClient;
    }

    public void setForClient(boolean forClient) {
        this.forClient = forClient;
    }
}

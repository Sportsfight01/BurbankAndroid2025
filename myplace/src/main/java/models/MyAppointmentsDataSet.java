package models;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jaya.Krishna on 10-01-2018.
 */

public class MyAppointmentsDataSet {
    int taskid, sequence, stageId, ContractId;
    String resourcename, phasecode, name,
            status, datedescription, dateactual, comment, stageName, displayDate = "--";
    boolean forclient;


    public MyAppointmentsDataSet(String result, boolean vic) {
        try {
            JSONObject job = new JSONObject(result);
            if (vic) {
                ContractId = job.isNull("ContractId") ? 0 : job.getInt("ContractId");
                name = job.isNull("ItemName") ? "--" : job.getString("ItemName");
                dateactual = job.isNull("DateCompleted") ? "--" : job.getString("DateCompleted");
                stageName = job.isNull("StageName") ? "--" : job.getString("StageName");
                stageId = job.isNull("Id") ? 0 : job.getInt("Id");
            } else {

                taskid = job.isNull("taskid") ? 0 : job.getInt("taskid");
                sequence = job.isNull("sequence") ? 0 : job.getInt("sequence");
                stageId = job.isNull("stageId") ? 0 : job.getInt("stageId");
                resourcename = job.isNull("resourcename") ? "--" : job.getString("resourcename");
                phasecode = job.isNull("phasecode") ? "--" : job.getString("phasecode");
                name = job.isNull("name") ? "--" : job.getString("name");
                status = job.isNull("status") ? "--" : job.getString("status");
                datedescription = job.isNull("datedescription") ? "--" : job.getString("datedescription");
                dateactual = job.isNull("dateactual") ? "--" : job.getString("dateactual");
                comment = job.isNull("comment") ? "--" : job.getString("comment");
                stageName = job.isNull("stageName") ? "--" : job.getString("stageName");
                forclient = job.isNull("forclient") ? false : job.getBoolean("forclient");
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat formatter5 = new SimpleDateFormat("dd MMMM yyyy");
            try {
                Date VisitedDate = formatter.parse(dateactual);
                displayDate = formatter5.format(VisitedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                displayDate = "--";
            }

        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public int getContractId() {
        return ContractId;
    }

    public void setContractId(int contractId) {
        ContractId = contractId;
    }

    public String getResourcename() {
        return resourcename;
    }

    public void setResourcename(String resourcename) {
        this.resourcename = resourcename;
    }

    public String getPhasecode() {
        return phasecode;
    }

    public void setPhasecode(String phasecode) {
        this.phasecode = phasecode;
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

    public String getDatedescription() {
        return datedescription;
    }

    public void setDatedescription(String datedescription) {
        this.datedescription = datedescription;
    }

    public String getDateactual() {
        return dateactual;
    }

    public void setDateactual(String dateactual) {
        this.dateactual = dateactual;
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

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public boolean isForclient() {
        return forclient;
    }

    public void setForclient(boolean forclient) {
        this.forclient = forclient;
    }
}

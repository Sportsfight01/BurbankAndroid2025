package models;

import org.json.JSONObject;

public class SupportAndHelpModel {
    String jobNumber,stage,contactPerson,mobile,email,designation;
    public SupportAndHelpModel(String result) {
        try {

            JSONObject job = new JSONObject(result);
            jobNumber = job.isNull("JobNumber") ? "" : job.getString("JobNumber").trim();
            stage = job.isNull("Stage") ? "" : job.getString("Stage").trim();
            contactPerson = job.isNull("ContactPerson") ? "" : job.getString("ContactPerson").trim();
            mobile = job.isNull("Mobile") ? "" : job.getString("Mobile").trim();
            email = job.isNull("Email") ? "" : job.getString("Email").trim();
            designation = job.isNull("Designation") ? "" : job.getString("Designation").trim();

        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}

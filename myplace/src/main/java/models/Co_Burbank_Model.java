package models;

import org.json.JSONObject;

/**
 * Created by Ashish.Kumar on 14-06-2017.
 */

public class Co_Burbank_Model {

    String UserId = "";
    String Name = "";
    String Email = "";
    String Mobile = "";
    boolean PrimaryApplicant = false;
    String JobNumber = "";
    String PrimaryAcceptance = "";
    String InvitationAcceptance = "";
    boolean CanDelete = false;
    boolean CanReject = false;
    boolean CanInvite = false;
    boolean CanReInvite = false;
    boolean Rejected = false;
    boolean CoBurbank = false;
    boolean Reffered = false;
    boolean Invited = false;
    String StatusMessage = "";

    public Co_Burbank_Model(JSONObject jsonObject) {
        try {
            this.UserId = jsonObject.getString("UserId");
            this.Name = jsonObject.getString("Name");
            this.Email = jsonObject.getString("Email");
            this.Mobile = jsonObject.getString("Mobile");
            this.PrimaryAcceptance = jsonObject.getString("PrimaryAcceptance");
            this.PrimaryApplicant = jsonObject.getBoolean("PrimaryApplicant");
            this.JobNumber = jsonObject.getString("JobNumber");
            this.InvitationAcceptance = jsonObject.getString("InvitationAcceptance");
            this.StatusMessage = jsonObject.getString("StatusMessage");
            this.CanDelete = jsonObject.getBoolean("CanDelete");
            this.CanReject = jsonObject.getBoolean("CanReject");
            this.CanInvite = jsonObject.getBoolean("CanInvite");
            this.CanReInvite = jsonObject.getBoolean("CanReInvite");
            this.Rejected = jsonObject.getBoolean("Rejected");
            this.Reffered = jsonObject.getBoolean("Referred");
            this.Invited= jsonObject.getBoolean("Invited");
            this.CoBurbank = jsonObject.getBoolean("CoBurbank");

        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public boolean isReffered() {
        return Reffered;
    }

    public boolean isInvited() {
        return Invited;
    }

    public String getJobNumber() {
        return JobNumber;
    }

    public String getEmail() {
        return Email;
    }

    public String getInvitationAcceptance() {
        return InvitationAcceptance;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getName() {
        return Name;
    }

    public String getPrimaryAcceptance() {
        return PrimaryAcceptance;
    }

    public boolean isPrimaryApplicant() {
        return PrimaryApplicant;
    }

    public String getStatusMessage() {
        return StatusMessage;
    }

    public String getUserId() {
        return UserId;
    }

    public boolean isCanDelete() {
        return CanDelete;
    }

    public boolean isCanInvite() {
        return CanInvite;
    }

    public boolean isCanReInvite() {
        return CanReInvite;
    }

    public boolean isCanReject() {
        return CanReject;
    }

    public boolean isCoBurbank() {
        return CoBurbank;
    }

    public boolean isRejected() {
        return Rejected;
    }
}

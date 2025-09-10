package models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import common.Common;

/**
 * Created by Ashish.Kumar on 29-05-2017.
 */

public class UserProfile {
    String passCode, jobNumer, email, myPlacePassword="";
    boolean isNewUser = false, passCodeAlreadySent = false, passCodeExpired = false, isMultipleEmails = false, isMultipleJobs = false, isCentralLoginUser = false, isEmailNotMapped = false;
    String message;
    ArrayList<UserDetails> userDetailses = new ArrayList<UserDetails>();



    /*
            "MyPlacePassword": null,
            "UserDetails":*/

    public UserProfile(String result) {
        try {
            JSONObject job = new JSONObject(result);
            passCode = job.isNull("PassCode") ? "" : job.getString("PassCode");
            isNewUser = job.isNull("IsNewUser") ? false : job.getBoolean("IsNewUser");
            passCodeAlreadySent = job.isNull("PassCodeAlreadySent") ? false : job.getBoolean("PassCodeAlreadySent");
            passCodeExpired = job.isNull("PassCodeExpired") ? false : job.getBoolean("PassCodeExpired");
            isMultipleEmails = job.isNull("IsMultipleEmails") ? false : job.getBoolean("IsMultipleEmails");
            isMultipleJobs = job.isNull("IsMultipleJobs") ? false : job.getBoolean("IsMultipleJobs");
            isCentralLoginUser = job.isNull("IsCentralLoginUser") ? false : job.getBoolean("IsCentralLoginUser");
            isEmailNotMapped = job.isNull("IsEmailNotMapped") ? false : job.getBoolean("IsEmailNotMapped");
            jobNumer = job.isNull("JobNumber") ? "" : job.getString("JobNumber");
            email = job.isNull("Email") ? "" : job.getString("Email");
            myPlacePassword = job.isNull("MyPlacePassword") ? "" : job.getString("MyPlacePassword");
            message = job.isNull(Common.Message) ? "" : job.getString(Common.Message);

            if (!job.isNull("UserDetails")) {
                JSONArray userDetailsArray = job.getJSONArray("UserDetails");
                for (int i = 0; i < userDetailsArray.length(); i++) {
                    UserDetails userDetails = new UserDetails(userDetailsArray.get(i).toString());
                    userDetailses.add(userDetails);
                }
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public boolean isEmailNotMapped() {
        return isEmailNotMapped;
    }

    public void setEmailNotMapped(boolean emailNotMapped) {
        isEmailNotMapped = emailNotMapped;
    }

    public ArrayList<UserDetails> getUserDetails() {
        return userDetailses;
    }

    public void setUserDetails(ArrayList<UserDetails> userDetailses) {
        this.userDetailses = userDetailses;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public String getJobNumer() {
        return jobNumer;
    }

    public void setJobNumer(String jobNumer) {
        this.jobNumer = jobNumer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMyPlacePassword() {
        return myPlacePassword;
    }

    public void setMyPlacePassword(String myPlacePassword) {
        this.myPlacePassword = myPlacePassword;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }

    public boolean isPassCodeAlreadySent() {
        return passCodeAlreadySent;
    }

    public void setPassCodeAlreadySent(boolean passCodeAlreadySent) {
        this.passCodeAlreadySent = passCodeAlreadySent;
    }

    public boolean isPassCodeExpired() {
        return passCodeExpired;
    }

    public void setPassCodeExpired(boolean passCodeExpired) {
        this.passCodeExpired = passCodeExpired;
    }

    public boolean isMultipleEmails() {
        return isMultipleEmails;
    }

    public void setMultipleEmails(boolean multipleEmails) {
        isMultipleEmails = multipleEmails;
    }

    public boolean isMultipleJobs() {
        return isMultipleJobs;
    }

    public void setMultipleJobs(boolean multipleJobs) {
        isMultipleJobs = multipleJobs;
    }

    public boolean isCentralLoginUser() {
        return isCentralLoginUser;
    }

    public void setCentralLoginUser(boolean centralLoginUser) {
        isCentralLoginUser = centralLoginUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<UserDetails> getUserDetailses() {
        return userDetailses;
    }

    public void setUserDetailses(ArrayList<UserDetails> userDetailses) {
        this.userDetailses = userDetailses;
    }
}

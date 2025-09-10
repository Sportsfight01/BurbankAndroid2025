package models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ashish.Kumar on 31-05-2017.
 */

public class MyPlaceJobDetails {
    int id, userId;
    String jobNo, userName, password, jobType, region, isPrimary, createdOn, updatedOn;
    boolean selected;

    ArrayList<MyPlaceEmail> myPlaceEmails = new ArrayList<MyPlaceEmail>();

    public MyPlaceJobDetails(String result) {
        try {
            JSONObject job = new JSONObject(result);

            id = job.isNull("Id") ? 0 : job.getInt("Id");
            userId = job.isNull("UserId") ? 0 : job.getInt("UserId");

            jobNo = job.isNull("JobNo") ? "" : job.getString("JobNo");
            userName = job.isNull("UserName") ? "" : job.getString("UserName");
            password = job.isNull("Password") ? "" : job.getString("Password");
            jobType = job.isNull("JobType") ? "" : job.getString("JobType");
            region = job.isNull("Region") ? "" : job.getString("Region");
            isPrimary = job.isNull("IsPrimary") ? "" : job.getString("IsPrimary");
            createdOn = job.isNull("JobNo") ? "" : job.getString("CreatedOn");
            updatedOn = job.isNull("JobNo") ? "" : job.getString("UpdatedOn");
            if (!job.isNull("MyPlaceEmail")) {
                JSONArray myPlaceMails = job.getJSONArray("MyPlaceEmail");
                for (int i = 0; i < myPlaceMails.length(); i++) {
                    MyPlaceEmail myPlaceEmail = new MyPlaceEmail(myPlaceMails.get(i).toString());
                    myPlaceEmails.add(myPlaceEmail);
                }
            }
            selected = false;
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }

    }

    /*MyPlaceEmail": [
    {
        "ContactId": "143130",
            "FullName": "Mr Paul Josef Zoudlik",
            "FirstName": "Paul",
            "LastName": "Zoudlik",
            "Email": "pauljaz@gmail.com",
            "UserName": "natalie   ",
            "Password": "zoudlik   "
    },
    {
        "ContactId": "143131",
            "FullName": "Mrs Natalie Taryn Zoudlik",
            "FirstName": "Natalie",
            "LastName": "Zoudlik",
            "Email": "natalie.zoudlik@gmail.com",
            "UserName": "natalie   ",
            "Password": "zoudlik   "
    }
            ]*/

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public ArrayList<MyPlaceEmail> getMyPlaceEmails() {
        return myPlaceEmails;
    }

    public void setMyPlaceEmails(ArrayList<MyPlaceEmail> myPlaceEmails) {
        this.myPlaceEmails = myPlaceEmails;
    }
}

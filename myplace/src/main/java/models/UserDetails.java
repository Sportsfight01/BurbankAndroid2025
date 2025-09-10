package models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ashish.Kumar on 31-05-2017.
 */

public class UserDetails {
    int id;
    String firstName, middleName, lastName, fullName, image, email, mobile, password, region, userGuid, createdOn, updatedOn;
    boolean isMyPlaceAccessible, isActive;
    ArrayList<MyPlaceJobDetails> myPlaceJobDetailses = new ArrayList<MyPlaceJobDetails>();

    public UserDetails(String result) {
        try {
            JSONObject job = new JSONObject(result);

            id = job.isNull("Id") ? 0 : job.getInt("Id");

            firstName = job.isNull("FirstName") ? "" : job.getString("FirstName");
            middleName = job.isNull("MiddleName") ? "" : job.getString("MiddleName");
            lastName = job.isNull("LastName") ? "" : job.getString("LastName");
            fullName = job.isNull("FullName") ? "" : job.getString("FullName");
            image = job.isNull("Image") ? "" : job.getString("Image");
            email = job.isNull("Email") ? "" : job.getString("Email");
            mobile = job.isNull("Mobile") ? "" : job.getString("Mobile");
            password = job.isNull("Password") ? "" : job.getString("Password");

            region = job.isNull("Region") ? "" : job.getString("Region");
            userGuid = job.isNull("UserGuid") ? "" : job.getString("UserGuid");
            createdOn = job.isNull("CreatedOn") ? "" : job.getString("CreatedOn");
            updatedOn = job.isNull("UpdatedOn") ? "" : job.getString("UpdatedOn");


            isMyPlaceAccessible = job.isNull("isMyPlaceAccessible") ? false : job.getBoolean("isMyPlaceAccessible");
            isActive = job.isNull("IsActive") ? false : job.getBoolean("IsActive");

            if (!job.isNull("MyPlaceDetails")) {
                JSONArray myPlaceJobDetailsArray = job.getJSONArray("MyPlaceDetails");
                for (int i = 0; i < myPlaceJobDetailsArray.length(); i++) {
                    MyPlaceJobDetails myPlaceJobDetails = new MyPlaceJobDetails(myPlaceJobDetailsArray.get(i).toString());
                    myPlaceJobDetailses.add(myPlaceJobDetails);
                }
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public void updateProfile(JSONObject job) {
        try {
            firstName = job.isNull("FirstName") ? "" : job.getString("FirstName");
            middleName = job.isNull("MiddleName") ? "" : job.getString("MiddleName");
            lastName = job.isNull("LastName") ? "" : job.getString("LastName");
            fullName = job.isNull("FullName") ? "" : job.getString("FullName");
            email = job.isNull("Email") ? "" : job.getString("Email");
            mobile = job.isNull("Mobile") ? "" : job.getString("Mobile");
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
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

    public boolean isMyPlaceAccessible() {
        return isMyPlaceAccessible;
    }

    public void setMyPlaceAccessible(boolean myPlaceAccessible) {
        isMyPlaceAccessible = myPlaceAccessible;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ArrayList<MyPlaceJobDetails> getMyPlaceJobDetailses() {
        return myPlaceJobDetailses;
    }

    public void setMyPlaceJobDetailses(ArrayList<MyPlaceJobDetails> myPlaceJobDetailses) {
        this.myPlaceJobDetailses = myPlaceJobDetailses;
    }
}

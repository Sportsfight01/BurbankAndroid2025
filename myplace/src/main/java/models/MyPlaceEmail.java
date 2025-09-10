package models;

import org.json.JSONObject;

import common.Common;

/**
 * Created by Ashish.Kumar on 31-05-2017.
 */

public class MyPlaceEmail {

    String contactId, fullName, firstName, lastName, email, userName, password;
    boolean isPrimaryUser;

    public MyPlaceEmail(String result) {
        try {
            JSONObject job = new JSONObject(result);
            contactId = job.isNull("ContactId") ? "" : job.getString("ContactId");
            fullName = job.isNull("FullName") ? "" : job.getString("FullName");
            firstName = job.isNull("FirstName") ? "" : job.getString("FirstName");
            lastName = job.isNull("LastName") ? "" : job.getString("LastName");
            email = job.isNull("Email") ? "" : job.getString("Email");
            userName = job.isNull("UserName") ? "" : job.getString("UserName");
            password = job.isNull("Password") ? "" : job.getString("Password");
            isPrimaryUser = job.isNull("IsPrimaryUser") ? false : job.getBoolean("IsPrimaryUser");
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isPrimaryUser() {
        return isPrimaryUser;
    }

    public void setPrimaryUser(boolean primaryUser) {
        isPrimaryUser = primaryUser;
    }
}

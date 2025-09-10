package models;

import android.util.Base64;

/**
 * Created by Ashish.Kumar on 07-06-2017.
 */

public class MyPlaceCredentials {
    String region,jobNumber,username,password;
    String encoded;

    public MyPlaceCredentials(String region,String jobNumber,String username,String password)
    {
        this.region=region;
        this.jobNumber=jobNumber;
        this.username=username;
        this.password=password;
        String sample = username.trim() + ":" + password.trim();
        byte[] message = sample.getBytes();
        encoded = "Basic "+Base64.encodeToString(message, Base64.NO_WRAP);
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getRegion() {
        return region;
    }

    public String getUsername() {
        return username;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }
}


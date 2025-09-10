package models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by jaya.krishna on 07-06-2017.
 */

public class MyPlacePhotoOuterDataSet {
    int month, year;
    String monthName;
    ArrayList<MyPlacePhotosDetailsDataSet> photoList = new ArrayList<MyPlacePhotosDetailsDataSet>();

    public MyPlacePhotoOuterDataSet(String result,Context context) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            month = jsonObject.isNull("Month") ? 0 : jsonObject.getInt("Month");
            year = jsonObject.isNull("Year") ? 0 : jsonObject.getInt("Year");

            SimpleDateFormat formatter = new SimpleDateFormat("MM");
            SimpleDateFormat formatter4 = new SimpleDateFormat("MMMM");
            Date date = formatter.parse(Integer.toString(month));
            monthName = formatter4.format(date);

            if (!jsonObject.isNull("Photos")) {
                JSONArray photoJsonArray = jsonObject.getJSONArray("Photos");
                for (int i = 0; i < photoJsonArray.length(); i++) {
                    //MyPlacePhotosDetailsDataSet myPlacePhotosDetailsDataSet = new MyPlacePhotosDetailsDataSet(0, photoJsonArray.get(i).toString(), false,context);
                    //photoList.add(myPlacePhotosDetailsDataSet);
                }
            }
            Collections.sort(photoList, new Comparator<MyPlacePhotosDetailsDataSet>() {
                public int compare(MyPlacePhotosDetailsDataSet obj1, MyPlacePhotosDetailsDataSet obj2) {
                    int fromObj1Size = obj1.getDayInteger();
                    int fromObj2Size = obj2.getDayInteger();
                    return fromObj1Size - fromObj2Size;
                }
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public ArrayList<MyPlacePhotosDetailsDataSet> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<MyPlacePhotosDetailsDataSet> photoList) {
        this.photoList = photoList;
    }
}

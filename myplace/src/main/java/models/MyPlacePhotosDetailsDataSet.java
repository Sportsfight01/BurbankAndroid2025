package models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.AppController;
import common.MyPlaceDataBase;

/**
 * Created by jaya.krishna on 26-04-2017.
 */
public class MyPlacePhotosDetailsDataSet {

    int index, id, dayInteger;
    String name, text, dateUploaded, extension, imagePath, month, day, year, weekDay, monthAsString, displayTime;
    boolean selected, fav = false;
    String notes = "";
    Context context;
    AppController controller;

    public MyPlacePhotosDetailsDataSet() {
    }

    public MyPlacePhotosDetailsDataSet(int index, String imageDetails, boolean selected, Context context) {
        this.index = index;
        this.selected = selected;
        this.context = context;
        this.controller = (AppController) context.getApplicationContext();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(imageDetails);
            name = jsonObject.isNull("Name") ? "" : jsonObject.getString("Name");
            text = jsonObject.isNull("Text") ? "" : jsonObject.getString("Text");
            String tempDate = jsonObject.isNull("DateUploaded") ? "" : jsonObject.getString("DateUploaded");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            SimpleDateFormat formatter3 = new SimpleDateFormat("EEEE");
            SimpleDateFormat formatter4 = new SimpleDateFormat("MMMM");
            SimpleDateFormat formatter5 = new SimpleDateFormat("hh:mm:ss a");
            try {
                Date VisitedDate = formatter.parse(tempDate);
                dateUploaded = "site photos - date taken: " + formatter2.format(VisitedDate);
                weekDay = formatter3.format(VisitedDate);
                monthAsString = formatter4.format(VisitedDate);
                displayTime = "Photo added on " + formatter5.format(VisitedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                dateUploaded = "site photos - date taken: ";
                displayTime = "";
            }
            extension = jsonObject.isNull("Extension") ? "" : jsonObject.getString("Extension");
            imagePath = jsonObject.isNull("ImagePath") ? "" : jsonObject.getString("ImagePath");
            month = jsonObject.isNull("Month") ? "" : jsonObject.getString("Month");
            day = jsonObject.isNull("Day") ? "" : jsonObject.getString("Day");
            if (day.length() > 0) {
                dayInteger = Integer.parseInt(day);
            } else {
                dayInteger = 0;
            }

            year = jsonObject.isNull("Year") ? "" : jsonObject.getString("Year");
            id = jsonObject.isNull("Id") ? 0 : jsonObject.getInt("Id");
            MyPlaceDataBase myPlaceDataBase = new MyPlaceDataBase(context);
            //notes = myPlaceDataBase.getNotesVic(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(),id);
            //fav = myPlaceDataBase.isFavVic(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(),id);
            /*notes = controller.getPhotoNotes(Integer.toString(id) + "_notes");
            fav = controller.getPhotoFav(Integer.toString(id) + "_fav");*/


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
        //controller.setFavForPhoto(Integer.toString(id) + "_fav", fav);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public int getDayInteger() {
        return dayInteger;
    }

    public void setDayInteger(int dayInteger) {
        this.dayInteger = dayInteger;
    }

    public String getMonthAsString() {
        return monthAsString;
    }

    public void setMonthAsString(String monthAsString) {
        this.monthAsString = monthAsString;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        //controller.setNotesForPhoto(Integer.toString(id) + "_notes", notes);
    }
}

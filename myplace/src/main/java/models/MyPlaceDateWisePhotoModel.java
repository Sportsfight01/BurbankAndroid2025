package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jaya.krishna on 05-05-2017.
 */
public class MyPlaceDateWisePhotoModel {
    String month, day, year, weekDay, monthAsString;
    int numberOfRows;
    ArrayList<MyPlacePhotosDetailsDataSet> adapterPopulatedPhotoList = new ArrayList<MyPlacePhotosDetailsDataSet>();

    public MyPlaceDateWisePhotoModel(String month, String year, ArrayList<MyPlacePhotosDetailsDataSet> adapterPopulatedPhotoList) {
        monthAsString = adapterPopulatedPhotoList.get(0).getMonthAsString();
        this.month = month/*adapterPopulatedPhotoList.get(0).getMonth()*/;
        day = adapterPopulatedPhotoList.get(0).getDay();
        this.year = year/*adapterPopulatedPhotoList.get(0).getYear()*/;
        weekDay = adapterPopulatedPhotoList.get(0).getWeekDay();
        this.adapterPopulatedPhotoList.addAll(adapterPopulatedPhotoList);


    }

    public ArrayList<MyPlacePhotosDetailsDataSet> getAdapterPopulatedPhotoList() {
        return adapterPopulatedPhotoList;
    }

    public void setAdapterPopulatedPhotoList(ArrayList<MyPlacePhotosDetailsDataSet> adapterPopulatedPhotoList) {
        this.adapterPopulatedPhotoList = adapterPopulatedPhotoList;
    }

    public String getMonthAsString() {
        return monthAsString;
    }

    public void setMonthAsString(String monthAsString) {
        this.monthAsString = monthAsString;
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

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }
}

package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jaya.Krishna on 23-11-2017.
 */

public class MyPhotosNewDateWiseDataSet {
    String docDateString;
    String date, monthYear, monthString, day, year;
    String notificationComparingDateString;
    String timeStamp;
    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();

    public MyPhotosNewDateWiseDataSet(String docDateString, ArrayList<MyDocOrPhotosDataSetQldOrSa> qldOrSaPhotosList) {
        timeStamp = docDateString;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat compare = new SimpleDateFormat("dd - MMMM - yyyy");
        SimpleDateFormat dateString = new SimpleDateFormat("dd");
        SimpleDateFormat monthYearString = new SimpleDateFormat("MMMM yyyy");
        SimpleDateFormat monthStringSdf = new SimpleDateFormat("MMMM");
        SimpleDateFormat dayString = new SimpleDateFormat("EEEE");
        SimpleDateFormat yearString = new SimpleDateFormat("yyyy");
        try {
            Date complionDate = sdf.parse(timeStamp);
            date = dateString.format(complionDate);
            monthYear = monthYearString.format(complionDate);
            day = dayString.format(complionDate);
            monthString = monthStringSdf.format(complionDate);
            year = yearString.format(complionDate);
            notificationComparingDateString = compare.format(complionDate);
        } catch (ParseException e) {
            e.printStackTrace();
            date = "";
            monthYear = "";
            day = "";
            monthString = "";
            year = "";
            notificationComparingDateString = "";
        }
        this.docDateString = docDateString;
        QldOrSaPhotosList = qldOrSaPhotosList;
    }

    public MyPhotosNewDateWiseDataSet(MyDocOrPhotosDataSetQldOrSa qldOrSaPhotosList) {
        timeStamp = qldOrSaPhotosList.getDocDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateString = new SimpleDateFormat("dd");
        SimpleDateFormat compare = new SimpleDateFormat("dd - MMMM - yyyy");
        SimpleDateFormat monthYearString = new SimpleDateFormat("MMMM yyyy");
        SimpleDateFormat monthStringSdf = new SimpleDateFormat("MMMM");
        SimpleDateFormat dayString = new SimpleDateFormat("EEEE");
        SimpleDateFormat yearString = new SimpleDateFormat("yyyy");
        try {
            Date complionDate = sdf.parse(timeStamp);
            date = dateString.format(complionDate);
            monthYear = monthYearString.format(complionDate);
            day = dayString.format(complionDate);
            monthString = monthStringSdf.format(complionDate);
            year = yearString.format(complionDate);
            notificationComparingDateString = compare.format(complionDate);
        } catch (ParseException e) {
            e.printStackTrace();
            date = "";
            monthYear = "";
            day = "";
            monthString = "";
            year = "";
            notificationComparingDateString = "";
        }
        this.docDateString = docDateString;
        QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
        QldOrSaPhotosList.add(qldOrSaPhotosList);
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNotificationComparingDateString() {
        return notificationComparingDateString;
    }

    public void setNotificationComparingDateString(String notificationComparingDateString) {
        this.notificationComparingDateString = notificationComparingDateString;
    }

    public MyPhotosNewDateWiseDataSet() {
        QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
    }

    public String getMonthString() {
        return monthString;
    }

    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    public String getDocDateString() {
        return docDateString;
    }

    public void setDocDateString(String docDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateString = new SimpleDateFormat("dd");
        SimpleDateFormat monthYearString = new SimpleDateFormat("MMMM yyyy");
        SimpleDateFormat dayString = new SimpleDateFormat("EEEE");
        try {
            Date complionDate = sdf.parse(docDateString);
            date = dateString.format(complionDate);
            monthYear = monthYearString.format(complionDate);
            day = dayString.format(complionDate);
            SimpleDateFormat yearString = new SimpleDateFormat("yyyy");
            SimpleDateFormat compare = new SimpleDateFormat("dd - MMMM - yyyy");
            notificationComparingDateString = compare.format(complionDate);
            year = yearString.format(complionDate);
        } catch (ParseException e) {
            e.printStackTrace();
            date = "";
            monthYear = "";
            day = "";
            year = "";
            notificationComparingDateString = "";
        }
        this.docDateString = docDateString;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<MyDocOrPhotosDataSetQldOrSa> getQldOrSaPhotosList() {
        return QldOrSaPhotosList;
    }

    public void setQldOrSaPhotosList(ArrayList<MyDocOrPhotosDataSetQldOrSa> qldOrSaPhotosList) {
        QldOrSaPhotosList = qldOrSaPhotosList;
    }

    public void setQldOrSaPhotosList(MyDocOrPhotosDataSetQldOrSa qldOrSaPhotosList) {
        QldOrSaPhotosList.add(qldOrSaPhotosList);
    }
}

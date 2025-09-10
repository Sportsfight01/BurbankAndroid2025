package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jaya.Krishna on 22-11-2017.
 */

public class MyPhotosMonthWiseDataSet {
    String docDateString;
    String displayDateString;
    String monthName, year;
    ArrayList<MyPhotosNewDateWiseDataSet> QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>();

    public MyPhotosMonthWiseDataSet(String docDateString, ArrayList<MyPhotosNewDateWiseDataSet> qldOrSaPhotosList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat monthString = new SimpleDateFormat("MMMM");
        SimpleDateFormat yearString = new SimpleDateFormat("yyyy");
        try {
            Date complionDate = sdf.parse(docDateString);
            displayDateString = monthString.format(complionDate) + "\n" + yearString.format(complionDate);
            monthName = monthString.format(complionDate);
            year = yearString.format(complionDate);
        } catch (ParseException e) {
            e.printStackTrace();
            displayDateString = "";
            monthName = "";
            year = "";
        }
        this.docDateString = docDateString;
        QldOrSaPhotosList = qldOrSaPhotosList;
    }

    public MyPhotosMonthWiseDataSet() {
        displayDateString = "";
        docDateString = "";
        QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>();
    }

    public String getDocDateString() {
        return docDateString;
    }

    public void setDocDateString(String docDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat monthString = new SimpleDateFormat("MMMM");
        SimpleDateFormat yearString = new SimpleDateFormat("yyyy");
        try {
            Date complionDate = sdf.parse(docDateString);
            displayDateString = monthString.format(complionDate) + "\n" + yearString.format(complionDate);
            monthName = monthString.format(complionDate);
            year = yearString.format(complionDate);
        } catch (ParseException e) {
            e.printStackTrace();
            displayDateString = "";
            monthName = "";
            year = "";
        }
        this.docDateString = docDateString;
    }

    public String getDisplayDateString() {
        return displayDateString;
    }

    public void setDisplayDateString(String displayDateString) {
        this.displayDateString = displayDateString;
    }

    public void setQldOrSaPhotosList(ArrayList<MyPhotosNewDateWiseDataSet> qldOrSaPhotosList) {
        QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>();
        QldOrSaPhotosList = qldOrSaPhotosList;
    }

    public ArrayList<MyPhotosNewDateWiseDataSet> getQldOrSaPhotosList() {
        return QldOrSaPhotosList;
    }

    public void setQldOrSaPhotosList(MyPhotosNewDateWiseDataSet qldOrSaPhotosList) {
        QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>();
        QldOrSaPhotosList.add(qldOrSaPhotosList);
    }

    public void addToQldOrSaPhotosList(MyPhotosNewDateWiseDataSet myDocOrPhotosDataSetQldOrSa) {
        QldOrSaPhotosList.add(myDocOrPhotosDataSetQldOrSa);
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

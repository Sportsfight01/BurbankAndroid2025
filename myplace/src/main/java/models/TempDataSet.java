package models;

import java.util.ArrayList;

/**
 * Created by Jaya.Krishna on 23-11-2017.
 */

public class TempDataSet {
    int docMonthInt;
    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();

    public TempDataSet() {
        docMonthInt = 0;
        QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
    }

    public int getDocMonthInt() {
        return docMonthInt;
    }

    public void setDocMonthInt(int docMonthInt) {
        this.docMonthInt = docMonthInt;
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

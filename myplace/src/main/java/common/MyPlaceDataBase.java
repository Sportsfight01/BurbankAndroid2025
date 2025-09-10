package common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import models.MyDocOrPhotosDataSetQldOrSa;
import models.NotificationModel;

/**
 * Created by Jaya.Krishna on 18-12-2017.
 */

public class MyPlaceDataBase extends SQLiteOpenHelper {


    // photos table name
    private static final String TABLE_PHOTOS_QLD_SA = "qldOrSaPhotos";

    private static final String USERNAME = "userName";
    private static final String JOBNUMBER = "jobNumber";
    private static final String QLD_SA_PHOTO_TITLE = "title";
    private static final String QLD_SA_PHOTO_AUTHORNAME = "authorName";
    private static final String QLD_SA_PHOTO_DATE = "docDate";
    private static final String QLD_SA_PHOTO_TYPR = "type";
    private static final String QLD_SA_PHOTO_DISPLAYTIME = "displayTime";
    private static final String QLD_SA_PHOTO_BYCLIENT = "byClient";
    private static final String QLD_SA_PHOTO_CURRENT = "current";
    private static final String QLD_SA_PHOTO_URLINT = "urlInt";
    private static final String QLD_SA_PHOTO_WHOLEDATEINT = "docWholeDateInt";
    private static final String QLD_SA_PHOTO_MONTHINT = "docMonthInt";
    private static final String QLD_SA_PHOTO_DAYINT = "docDayInt";
    private static final String QLD_SA_PHOTO_URL = "url";
    private static final String QLD_SA_PHOTO_NOTES = "notes";
    private static final String QLD_SA_PHOTO_FAV = "fav";


    /*private static final String TABLE_PHOTOS_VIC = "vicPhotos";

    private static final String VIC_PHOTO_INDEX = "photo_index";
    private static final String VIC_PHOTO_ID = "id";
    private static final String VIC_PHOTO_DAYINT = "dayInteger";
    private static final String VIC_PHOTO_NAME = "name";
    private static final String VIC_PHOTO_TEXT = "text";
    private static final String VIC_PHOTO_DATEUPLOADED = "dateUploaded";
    private static final String VIC_PHOTO_EXTENSION = "extension";
    private static final String VIC_PHOTO_IMAGEPATH = "imagePath";
    private static final String VIC_PHOTO_MONTH = "month";
    private static final String VIC_PHOTO_DAY = "day";
    private static final String VIC_PHOTO_YEAR = "year";
    private static final String VIC_PHOTO_WEEKDAY = "weekDay";
    private static final String VIC_PHOTO_MONTHASSTRING = "monthAsString";
    private static final String VIC_PHOTO_DISPLAYTIME = "displayTime";
    private static final String VIC_PHOTO_NOTES = "notes";
    private static final String VIC_PHOTO_FAV = "fav";*/

    private static final String TABLE_PROGRESS = "PROGRESS_STAGE";
    private static final String TASK_ID = "taskid";
    private static final String PROGRESS_POSITION = "progress_position";
    private static final String NAME = "name";
    private static final String STATUS = "status";
    private static final String DATE_ACTUAL = "dateactual";
    private static final String OLD_STATUS = "oldStatus";
    private static final String CURRENT_STATUS = "newStatus";
    private static final String NOTIFICATION_READ = "read_status";
    private static final String PROGRESSTIMESTAMP = "progressTimeStamp";

    private static final String TABLE_PHOTO_NOTIFICATION = "photo_notifications";
    private static final String DATESTRING = "photoDate";
    private static final String PHOTOTIMESTAMP = "photoTimeStamp";
    private static final String PHOTOJOBDATE = "photoJobDate";

    // SQL statement to create notification photo table table
    private static final String Create_Photo_Notification_Table = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PHOTO_NOTIFICATION
            + "(" + USERNAME
            + " VARCHAR ,"
            + JOBNUMBER
            + " VARCHAR ,"
            + DATESTRING
            + " VARCHAR,"
            + PHOTOTIMESTAMP
            + " VARCHAR ,"
            + NOTIFICATION_READ
            + " VARCHAR,"
            + PHOTOJOBDATE
            + " VARCHAR PRIMARY KEY)";

    // SQL statement to create notification progress stage change table table
    private static final String Create_Progress_Stage_Table = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PROGRESS
            + "(" + USERNAME
            + " VARCHAR ,"
            + JOBNUMBER
            + " VARCHAR ,"
            + TASK_ID
            + " INTEGER PRIMARY KEY,"
            + PROGRESS_POSITION
            + " INTEGER ,"
            + NAME
            + " VARCHAR ,"
            + STATUS
            + " VARCHAR ,"
            + DATE_ACTUAL
            + " VARCHAR ,"
            + OLD_STATUS
            + " VARCHAR ,"
            + CURRENT_STATUS
            + " VARCHAR ,"
            + NOTIFICATION_READ
            + " VARCHAR ,"
            + PROGRESSTIMESTAMP
            + " VARCHAR )";

    // SQL statement to create qld photos table table
    private static final String Create_QLD_SA_Table = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PHOTOS_QLD_SA
            + "(" + USERNAME
            + " VARCHAR ,"
            + JOBNUMBER
            + " VARCHAR ,"
            + QLD_SA_PHOTO_TITLE
            + " VARCHAR ,"
            + QLD_SA_PHOTO_AUTHORNAME
            + " VARCHAR ,"
            + QLD_SA_PHOTO_DATE
            + " VARCHAR ,"
            + QLD_SA_PHOTO_TYPR
            + " VARCHAR ,"
            + QLD_SA_PHOTO_DISPLAYTIME
            + " VARCHAR ,"
            + QLD_SA_PHOTO_URLINT
            + " INTEGER PRIMARY KEY ,"
            + QLD_SA_PHOTO_BYCLIENT
            + " VARCHAR ,"
            + QLD_SA_PHOTO_CURRENT
            + " VARCHAR ,"
            + QLD_SA_PHOTO_MONTHINT
            + " INTEGER ,"
            + QLD_SA_PHOTO_DAYINT
            + " INTEGER ,"
            + QLD_SA_PHOTO_NOTES
            + " VARCHAR ,"
            + QLD_SA_PHOTO_URL
            + " VARCHAR ,"
            + QLD_SA_PHOTO_FAV
            + " VARCHAR ,"
            + QLD_SA_PHOTO_WHOLEDATEINT
            + " INTEGER )";


    // SQL statement to create vic photos table table
    /*private static final String Create_VIC_Table = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PHOTOS_VIC
            + "(" + USERNAME
            + " VARCHAR ,"
            + JOBNUMBER
            + " VARCHAR ,"
            + VIC_PHOTO_INDEX
            + " INTEGER ,"
            + VIC_PHOTO_ID
            + " INTEGER PRIMARY KEY,"
            + VIC_PHOTO_DAYINT
            + " INTEGER ,"
            + VIC_PHOTO_NAME
            + " VARCHAR ,"
            + VIC_PHOTO_TEXT
            + " VARCHAR ,"
            + VIC_PHOTO_DATEUPLOADED
            + " VARCHAR ,"
            + VIC_PHOTO_EXTENSION
            + " VARCHAR ,"
            + VIC_PHOTO_IMAGEPATH
            + " VARCHAR ,"
            + VIC_PHOTO_MONTH
            + " VARCHAR ,"
            + VIC_PHOTO_DAY
            + " VARCHAR ,"
            + VIC_PHOTO_YEAR
            + " VARCHAR ,"
            + VIC_PHOTO_WEEKDAY
            + " VARCHAR ,"
            + VIC_PHOTO_MONTHASSTRING
            + " VARCHAR ,"
            + VIC_PHOTO_DISPLAYTIME
            + " VARCHAR ,"
            + VIC_PHOTO_NOTES
            + " VARCHAR ,"
            + VIC_PHOTO_FAV
            + " VARCHAR )";*/

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MyPlaceDB";

    //DataBase reference
    //SQLiteDatabase myPlaceSqliteDB;
    SQLiteDatabase myPlaceSqliteDB;

    public MyPlaceDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create tables
        db.execSQL(Create_QLD_SA_Table);
        /*db.execSQL(Create_VIC_Table);*/
        db.execSQL(Create_Progress_Stage_Table);
        db.execSQL(Create_Photo_Notification_Table);
        myPlaceSqliteDB = db;
        // getting reference to writable DB
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /*public void insertVicPhotos(ArrayList<MyPlacePhotosDetailsDataSet> photosDetailsDataSets, String jobNumber, String username) {
        //String valuesString = "";
        for (int vPCount = 0; vPCount < photosDetailsDataSets.size(); vPCount++) {
            MyPlacePhotosDetailsDataSet detailsDataSet = photosDetailsDataSets.get(vPCount);

            String fav = "false";
            if (detailsDataSet.isFav()) {
                fav = "true";
            }
            String valuesString = "('" + username
                    + "','" + jobNumber
                    + "','" + detailsDataSet.getIndex()
                    + "','" + detailsDataSet.getId()
                    + "','" + detailsDataSet.getDayInteger()
                    + "','" + detailsDataSet.getName()
                    + "','" + detailsDataSet.getText()
                    + "','" + detailsDataSet.getDateUploaded()
                    + "','" + detailsDataSet.getExtension()
                    + "','" + detailsDataSet.getImagePath()
                    + "','" + detailsDataSet.getMonth()
                    + "','" + detailsDataSet.getDay()
                    + "','" + detailsDataSet.getYear()
                    + "','" + detailsDataSet.getWeekDay()
                    + "','" + detailsDataSet.getMonthAsString()
                    + "','" + detailsDataSet.getDisplayTime()
                    + "','" + detailsDataSet.getNotes()
                    + "','" + fav + "')";
            if (valuesString.length() > 0) {
                String Qry = "REPLACE INTO " + TABLE_PHOTOS_VIC + " (" + USERNAME + ", "
                        + JOBNUMBER + " , " + VIC_PHOTO_INDEX + " , " + VIC_PHOTO_ID + " , "
                        + VIC_PHOTO_DAYINT + " , " + VIC_PHOTO_NAME + " , " + VIC_PHOTO_TEXT + " , "
                        + VIC_PHOTO_DATEUPLOADED + " , " + VIC_PHOTO_EXTENSION + " , " +
                        VIC_PHOTO_IMAGEPATH + ", "
                        + VIC_PHOTO_MONTH + " , " + VIC_PHOTO_DAY + " , " + VIC_PHOTO_YEAR + " , "
                        + VIC_PHOTO_WEEKDAY + " , " + VIC_PHOTO_MONTHASSTRING + " , " + VIC_PHOTO_DISPLAYTIME + " , "
                        + VIC_PHOTO_NOTES + " , " + VIC_PHOTO_FAV + " ) VALUES "
                        + valuesString;
                SQLiteDatabase liteDatabase = this.getWritableDatabase();
                liteDatabase.execSQL(Qry);
            }
        }
    }*/

    public void insertQldOrSaPhotos(ArrayList<MyDocOrPhotosDataSetQldOrSa> photosDetailsDataSets, String jobNumber, String username) {
        for (int qSPCount = 0; qSPCount < photosDetailsDataSets.size(); qSPCount++) {
            MyDocOrPhotosDataSetQldOrSa dataSetQldOrSa = photosDetailsDataSets.get(qSPCount);

            String fav = "false", client = "false", current = "false";
            if (dataSetQldOrSa.isFav()) {
                fav = "true";
            }
            if (dataSetQldOrSa.isByClient()) {
                client = "true";
            }
            if (dataSetQldOrSa.isCurrent()) {
                current = "true";
            }

            String titleName = "";
            try {
                 titleName = dataSetQldOrSa.getTitle();
                  titleName = titleName.replace("'", " ");
            } catch (Exception e) {
                titleName = dataSetQldOrSa.getTitle();
            }


            String valuesString = "('" + username
                    + "','" + jobNumber
                    + "','" + titleName
                    + "','" + dataSetQldOrSa.getAuthorName()
                    + "','" + dataSetQldOrSa.getDocDate()
                    + "','" + dataSetQldOrSa.getType()
                    + "','" + dataSetQldOrSa.getDisplayTime()
                    + "','" + dataSetQldOrSa.getUrlInt()
                    + "','" + client
                    + "','" + current
                    + "','" + dataSetQldOrSa.getDocMonthInt()
                    + "','" + dataSetQldOrSa.getDocDayInt()
                    + "','" + dataSetQldOrSa.getNotes()
                    + "','" + dataSetQldOrSa.getUrl()
                    + "','" + fav
                    + "','" + dataSetQldOrSa.getDocWholeDateInt() + "')";
            if (valuesString.length() > 0) {
                String Qry = "REPLACE INTO " + TABLE_PHOTOS_QLD_SA + " (" + USERNAME + ","
                        + JOBNUMBER + "," + QLD_SA_PHOTO_TITLE + "," + QLD_SA_PHOTO_AUTHORNAME + ","
                        + QLD_SA_PHOTO_DATE + "," + QLD_SA_PHOTO_TYPR + "," + QLD_SA_PHOTO_DISPLAYTIME + ","
                        + QLD_SA_PHOTO_URLINT + "," + QLD_SA_PHOTO_BYCLIENT + "," +
                        QLD_SA_PHOTO_CURRENT + ","
                        + QLD_SA_PHOTO_MONTHINT + "," + QLD_SA_PHOTO_DAYINT + "," + QLD_SA_PHOTO_NOTES + ","
                        + QLD_SA_PHOTO_URL + "," + QLD_SA_PHOTO_FAV + "," + QLD_SA_PHOTO_WHOLEDATEINT + ") VALUES "
                        + valuesString;
//                System.out.println("insertQldOrSaPhotos:: "+Qry);

                SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
                myPlaceSqliteDB.execSQL(Qry);
            }
        }
    }

    /*public boolean isFavVic(String jobNumber, String username, int photoId) {
        boolean returnFav = false;
        String query = "SELECT " + VIC_PHOTO_FAV + " FROM " + TABLE_PHOTOS_VIC + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                VIC_PHOTO_ID + " = " + photoId;

        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String fav = cursor.getString(0);
                if (fav != null && fav.length() > 0) {
                    if (fav.equalsIgnoreCase("true")) {
                        returnFav = true;
                    }
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnFav;
    }

    public void setFavVic(String jobNumber, String username, int photoId, boolean fav) {
        String favString = "false";

        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        if (fav) {
            favString = "true";
        }
        String updateQry = " UPDATE " + TABLE_PHOTOS_VIC + " SET " + VIC_PHOTO_FAV + " ='" + favString + "' WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                VIC_PHOTO_ID + " = " + photoId;
        try {
            myPlaceSqliteDB.execSQL(updateQry);
        } catch (Exception e) {
            myPlaceSqliteDB.close();
        }
    }

    public String getNotesVic(String jobNumber, String username, int photoId) {

        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String returnNotes = "";
        String query = "SELECT " + VIC_PHOTO_NOTES + " FROM " + TABLE_PHOTOS_VIC + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                VIC_PHOTO_ID + " = " + photoId;
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnNotes = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnNotes;
    }

    public void setNotesVic(String jobNumber, String username, int photoId, String notes) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String updateQry = " UPDATE " + TABLE_PHOTOS_VIC + " SET " + VIC_PHOTO_NOTES + " ='" + notes + "' WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                VIC_PHOTO_ID + " = " + photoId;
        try {
            myPlaceSqliteDB.execSQL(updateQry);
        } catch (Exception e) {
            myPlaceSqliteDB.close();
        }
    }*/

    public boolean isFavQldSa(String jobNumber, String username, int photoId) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        boolean returnFav = false;
        String query = "SELECT " + QLD_SA_PHOTO_FAV + " FROM " + TABLE_PHOTOS_QLD_SA + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                QLD_SA_PHOTO_URLINT + " = " + photoId;
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String fav = cursor.getString(0);
                if (fav != null && fav.length() > 0) {
                    if (fav.equalsIgnoreCase("true")) {
                        returnFav = true;
                    }
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnFav;
    }

    public void setFavQldSa(String jobNumber, String username, int photoId, boolean fav) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String favString = "false";
        if (fav) {
            favString = "true";
        }
        String updateQry = " UPDATE " + TABLE_PHOTOS_QLD_SA + " SET " + QLD_SA_PHOTO_FAV + " ='" + favString + "' WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                QLD_SA_PHOTO_URLINT + " = " + photoId;
        try {
            myPlaceSqliteDB.execSQL(updateQry);
        } catch (Exception e) {
            myPlaceSqliteDB.close();
        }//
    }

    public String getNotesQldSa(String jobNumber, String username, int photoId) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String returnNotes = "";
        String query = "SELECT " + QLD_SA_PHOTO_NOTES + " FROM " + TABLE_PHOTOS_QLD_SA + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                QLD_SA_PHOTO_URLINT + " = " + photoId;
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                returnNotes = cursor.getString(0);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return returnNotes;
    }

    public void setNotesQldSa(String jobNumber, String username, int photoId, String notes) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String updateQry = " UPDATE " + TABLE_PHOTOS_QLD_SA + " SET " + QLD_SA_PHOTO_NOTES + " ='" + notes + "' WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                QLD_SA_PHOTO_URLINT + " = " + photoId;
        try {
            myPlaceSqliteDB.execSQL(updateQry);
        } catch (Exception e) {
            myPlaceSqliteDB.close();
        }
    }

    /*public ArrayList<MyPlacePhotosDetailsDataSet> getVicFavPhotos(String jobNumber, String username) {
        ArrayList<MyPlacePhotosDetailsDataSet> photosDetailsDataSets = new ArrayList<MyPlacePhotosDetailsDataSet>();
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PHOTOS_VIC + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                VIC_PHOTO_FAV + " ='true'";
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        MyPlacePhotosDetailsDataSet modelBean;
        if (cursor.moveToFirst()) {
            do {
                modelBean = new MyPlacePhotosDetailsDataSet();
                modelBean.setIndex(cursor.getInt(2));
                modelBean.setId(cursor.getInt(3));
                modelBean.setDayInteger(cursor.getInt(4));
                modelBean.setName(cursor.getString(5));
                modelBean.setText(cursor.getString(6));
                modelBean.setDateUploaded(cursor.getString(7));
                modelBean.setExtension(cursor.getString(8));
                modelBean.setImagePath(cursor.getString(9));
                modelBean.setMonth(cursor.getString(10));
                modelBean.setDay(cursor.getString(11));
                modelBean.setYear(cursor.getString(12));
                modelBean.setWeekDay(cursor.getString(13));
                modelBean.setMonthAsString(cursor.getString(14));
                modelBean.setDisplayTime(cursor.getString(15));
                modelBean.setNotes(cursor.getString(16));
                modelBean.setFav(true);

                photosDetailsDataSets.add(modelBean);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return photosDetailsDataSets;
    }*/

    public ArrayList<MyDocOrPhotosDataSetQldOrSa> getQldOrSaFavPhotos(String jobNumber, String username) {
        ArrayList<MyDocOrPhotosDataSetQldOrSa> photosDetailsDataSets = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PHOTOS_QLD_SA + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                QLD_SA_PHOTO_FAV + " ='true'";
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        MyDocOrPhotosDataSetQldOrSa modelBean;
        if (cursor.moveToFirst()) {
            do {
                modelBean = new MyDocOrPhotosDataSetQldOrSa();

                boolean client = false, current = false;
                if (cursor.getString(8).equalsIgnoreCase("true")) {
                    client = true;
                }
                if (cursor.getString(9).equalsIgnoreCase("true")) {
                    current = true;
                }

                modelBean.setTitle(cursor.getString(2));
                modelBean.setAuthorName(cursor.getString(3));
                modelBean.setDocDate(cursor.getString(4));
                modelBean.setType(cursor.getString(5));
                modelBean.setDisplayTime(cursor.getString(6));
                modelBean.setUrlInt(cursor.getInt(7));
                modelBean.setByClient(client);
                modelBean.setCurrent(current);
                modelBean.setDocMonthInt(cursor.getInt(10));
                modelBean.setDocDayInt(cursor.getInt(11));
                modelBean.setNotes(cursor.getString(12));
                modelBean.setUrl(cursor.getString(13));
                modelBean.setFav(true);
                modelBean.setDocWholeDateInt(cursor.getInt(15));
                photosDetailsDataSets.add(modelBean);
            } while (cursor.moveToNext());
        }
        return photosDetailsDataSets;
    }

    public void insertProgress(String jobNumber, String username, int taskId, int progressPosition, String name, String status, String dateActual, String timeStamp) {
        int count = 0;
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String queryCount = "SELECT COUNT(" + TASK_ID + ") FROM " + TABLE_PROGRESS + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                TASK_ID + " ='" + taskId + "'";
        Cursor cursorCount = myPlaceSqliteDB.rawQuery(queryCount, null);
        if (cursorCount.moveToFirst()) {
            do {
                count = cursorCount.getInt(0);
            } while (cursorCount.moveToNext());
        }
        if (count == 0) {
            String valuesString = "('" + username
                    + "','" + jobNumber
                    + "','" + taskId
                    + "','" + progressPosition
                    + "','" + name
                    + "','" + status
                    + "','" + dateActual
                    + "','" + status
                    + "','" + status
                    + "','false','" + timeStamp + "')";
            if (valuesString.length() > 0) {
                String Qry = "REPLACE INTO " + TABLE_PROGRESS + " (" + USERNAME + ", "
                        + JOBNUMBER + " , " + TASK_ID + " , " + PROGRESS_POSITION + " , "
                        + NAME + " , " + STATUS + " , " + DATE_ACTUAL + " , "
                        + OLD_STATUS + " , " + CURRENT_STATUS + " , " + NOTIFICATION_READ + " , " + PROGRESSTIMESTAMP + " ) VALUES "
                        + valuesString;
                SQLiteDatabase liteDatabase = this.getWritableDatabase();
                liteDatabase.execSQL(Qry);
            }
        } else {
            String state = "";
            String query = "SELECT * FROM " + TABLE_PROGRESS + " WHERE " +
                    USERNAME + " ='" + username + "' AND " +
                    JOBNUMBER + " ='" + jobNumber + "' AND " +
                    TASK_ID + " ='" + taskId + "'";
            Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    state = cursor.getString(5);
                } while (cursor.moveToNext());
            }
            if (state.length() > 0 && !state.equalsIgnoreCase(status)) {
                String updateQry = " UPDATE " + TABLE_PROGRESS + " SET " + STATUS + " ='" + status + "'," +
                        OLD_STATUS + " ='" + state +
                        CURRENT_STATUS + " ='" + status +
                        DATE_ACTUAL + " ='" + dateActual +
                        PROGRESSTIMESTAMP + " ='" + timeStamp +
                        NOTIFICATION_READ + " ='false'," + "' WHERE " +
                        USERNAME + " ='" + username + "' AND " +
                        JOBNUMBER + " ='" + jobNumber + "' AND " +
                        TASK_ID + " = " + taskId;
                try {
                    myPlaceSqliteDB.execSQL(updateQry);
                } catch (Exception e) {
                    myPlaceSqliteDB.close();
                }
            }

        }
    }

    public ArrayList<NotificationModel> getProgress(String jobNumber, String username, boolean status) {
        ArrayList<NotificationModel> notificationModels = new ArrayList<NotificationModel>();
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PROGRESS + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "'";
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        NotificationModel modelBean;
        if (cursor.moveToFirst()) {
            do {
                if (status) {
                    if (!cursor.getString(7).equalsIgnoreCase(cursor.getString(8))) {
                        SortModel sortModel = getTimeDifference(cursor.getString(10));
                        modelBean = new NotificationModel(cursor.getString(4), cursor.getInt(3), cursor.getInt(2), cursor.getString(6), cursor.getString(9), cursor.getString(8), cursor.getString(7), sortModel.getDisplayText(), sortModel.getDays(), sortModel.getMinutes());
                        if (!modelBean.isRead()) {
                            notificationModels.add(modelBean);
                        }
                    }
                } else {
                    if (cursor.getString(5).equalsIgnoreCase("Completed")) {
                        SortModel sortModel = getTimeDifference(cursor.getString(10));
                        modelBean = new NotificationModel(cursor.getString(4), cursor.getInt(3), cursor.getInt(2), cursor.getString(6), cursor.getString(9), sortModel.getDisplayText(), sortModel.getDays(), sortModel.getMinutes());
                        if (!modelBean.isRead()) {
                            notificationModels.add(modelBean);
                        }
                    }
                }


            } while (cursor.moveToNext());
        }
        return notificationModels;
    }


    public void updateNotificationProgressReadStatus(String jobNumber, String username, int taskId) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String updateQry = " UPDATE " + TABLE_PROGRESS + " SET " +
                NOTIFICATION_READ + " ='true'" + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                TASK_ID + " = " + taskId;
        try {
//            myPlaceSqliteDB.execSQL(updateQry);
        } catch (Exception e) {
            myPlaceSqliteDB.close();
        }
    }

    public void insertPhotoNotifications(String jobNumber, String username, String dateString, String timeStamp) {
       /* private static final String Create_Photo_Notification_Table = "CREATE TABLE IF NOT EXISTS "
                + TABLE_PHOTO_NOTIFICATION
                + "(" + USERNAME
                + " VARCHAR ,"
                + JOBNUMBER
                + " VARCHAR ,"
                + DATESTRING
                + " VARCHAR,"
                + PHOTOTIMESTAMP
                + " VARCHAR ,"
                + NOTIFICATION_READ
                + " VARCHAR,"
                + PHOTOJOBDATE
                + " VARCHAR PRIMARY KEY)";*/
        int count = 0;
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String queryCount = "SELECT COUNT(" + DATESTRING + ") FROM " + TABLE_PHOTO_NOTIFICATION + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                DATESTRING + " ='" + dateString + "'";
        Cursor cursorCount = myPlaceSqliteDB.rawQuery(queryCount, null);
        if (cursorCount.moveToFirst()) {
            do {
                count = cursorCount.getInt(0);
            } while (cursorCount.moveToNext());
        }
        if (count == 0) {
            String jobDate = jobNumber + " " + dateString;
            String valuesString = "('" + username
                    + "','" + jobNumber
                    + "','" + dateString
                    + "','" + timeStamp
                    + "','false','" + jobDate + "')";
            if (valuesString.length() > 0) {
                String Qry = "REPLACE INTO " + TABLE_PHOTO_NOTIFICATION + " (" + USERNAME + ", "
                        + JOBNUMBER + " , " + DATESTRING + " , "
                        + PHOTOTIMESTAMP + " , " + NOTIFICATION_READ + " , " + PHOTOJOBDATE + " ) VALUES "
                        + valuesString;
                myPlaceSqliteDB.execSQL(Qry);
            }
        } else {
            String state = "";
            String query = "SELECT * FROM " + TABLE_PHOTO_NOTIFICATION + " WHERE " +
                    USERNAME + " ='" + username + "' AND " +
                    JOBNUMBER + " ='" + jobNumber + "' AND " +
                    DATESTRING + " ='" + dateString + "'";
            Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    state = cursor.getString(4);
                } while (cursor.moveToNext());
            }
            if (state.length() > 0 && state.equalsIgnoreCase("true")) {
                String updateQry = " UPDATE " + TABLE_PHOTO_NOTIFICATION + " SET " + NOTIFICATION_READ + " ='true'" + " WHERE " +
                        USERNAME + " ='" + username + "' AND " +
                        JOBNUMBER + " ='" + jobNumber + "' AND " +
                        DATESTRING + " ='" + dateString + "'";
                try {
                    myPlaceSqliteDB.execSQL(updateQry);
                } catch (Exception e) {
                    myPlaceSqliteDB.close();
                }
            }
        }

    }

    public ArrayList<NotificationModel> getPhotoNotifications(String jobNumber, String username) {
        ArrayList<NotificationModel> notificationModels = new ArrayList<NotificationModel>();
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PHOTO_NOTIFICATION + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "'";
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        NotificationModel modelBean;
        if (cursor.moveToFirst()) {
            do {
                SortModel sortModel = getTimeDifference(cursor.getString(3));
                modelBean = new NotificationModel(cursor.getString(2), cursor.getString(4), sortModel.getDisplayText(), sortModel.getDays(), sortModel.getMinutes());
                if (!modelBean.isRead()) {
                    notificationModels.add(modelBean);
                }
            } while (cursor.moveToNext());
        }
        return notificationModels;
    }

    public void updatePhotoNotifications(String jobNumber, String username, String dateString) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String updateQry = " UPDATE " + TABLE_PHOTO_NOTIFICATION + " SET " +
                NOTIFICATION_READ + " ='true'" + " WHERE " +
                USERNAME + " ='" + username + "' AND " +
                JOBNUMBER + " ='" + jobNumber + "' AND " +
                PHOTOJOBDATE + " ='" + jobNumber + " " + dateString + "' AND " +
                DATESTRING + " = '" + dateString + "'";

        try {
            myPlaceSqliteDB.execSQL(updateQry);
        } catch (Exception e) {
            myPlaceSqliteDB.close();
        }
    }

    public int getNotificationCount(String jobNumber, String username) {
        SQLiteDatabase myPlaceSqliteDB = this.getWritableDatabase();
        String query =
                "SELECT COUNT (*) FROM " + TABLE_PROGRESS + " WHERE " + USERNAME + "='"+username+"' AND "+
                        JOBNUMBER+ "='"+jobNumber+"' AND "+NOTIFICATION_READ+"='false'";

        int count = 0;
        Cursor cursor = myPlaceSqliteDB.rawQuery(query, null);
        if(null != cursor) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        String photoQuery =
                "SELECT COUNT (*) FROM " + TABLE_PHOTO_NOTIFICATION + " WHERE " + USERNAME + "='"+username+"' AND "+
                        JOBNUMBER+ "='"+jobNumber+"' AND "+NOTIFICATION_READ+"='false'";
        int photoCount = 0;
        Cursor photoCursor = myPlaceSqliteDB.rawQuery(photoQuery, null);
        if(null != photoCursor) {
            if (photoCursor.getCount() > 0) {
                photoCursor.moveToFirst();
                photoCount = photoCursor.getInt(0);
            }
            photoCursor.close();
        }
        return count+photoCount;
    }

    public SortModel getTimeDifference(String timeStamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd");

        String returnString = "";
        SortModel returnModel = new SortModel();

        Date currentDate = new Date();
        Calendar currentCalendar = new GregorianCalendar();

        Date oldDate = new Date();
        Calendar oldCalendar = new GregorianCalendar();

        try {
            String complionDate = sdf.format(currentDate);
            currentDate = sdf.parse(complionDate);
            String onlyDateCurrent = sdfDate.format(currentDate);

            oldDate = sdf.parse(timeStamp);
            String onlyDateOld = sdfDate.format(oldDate);

            if (currentDate.getTime() > oldDate.getTime()) {
                // in milliseconds
                long diff = currentDate.getTime() - oldDate.getTime();

                long diffMinutes = diff / (60 * 1000);
                long diffHours = diff / (60 * 60 * 1000);
                long diffDays = diff / (24 * 60 * 60 * 1000);

                oldCalendar.setTime(oldDate);

                currentCalendar.setTime(currentDate);
                if (diffHours >= 24) {
                    returnModel.setDays((int) diffDays);
                    if (diffDays < 30) {
                        if (diffDays < 7) {
                            if (diffDays == 1) {
                                returnString = Integer.toString(((int) diffDays)) + " day ago.";
                            } else {
                                returnString = Integer.toString(((int) diffDays)) + " days ago.";
                            }
                        } else {
                            int weekCount = ((int) diffDays) / 7;
                            if (weekCount == 1) {
                                returnString = Integer.toString(((int) weekCount)) + " week ago.";
                            } else {
                                returnString = Integer.toString(((int) weekCount)) + " weeks ago.";
                            }
                        }
                    } else {
                        int currentYear, oldYear, currentMonth, oldMonth, diffYear, diffMonth;

                        currentYear = currentCalendar.get(Calendar.YEAR);
                        oldYear = oldCalendar.get(Calendar.YEAR);
                        currentMonth = currentCalendar.get(Calendar.MONTH);
                        oldMonth = oldCalendar.get(Calendar.MONTH);

                        if (currentMonth == oldMonth) {
                            diffYear = (currentYear - oldYear);

                            if (diffYear == 1) {
                                returnString = Integer.toString(diffYear) + " year ago.";
                            } else {
                                returnString = Integer.toString(diffYear) + " years ago.";
                            }
                        } else {
                            if (currentMonth > oldMonth) {
                                diffMonth = currentMonth - oldMonth;
                                diffYear = (currentYear - oldYear);
                            } else {
                                diffMonth = 12 - (oldMonth - currentMonth);
                                diffYear = (currentYear - oldYear) - 1;
                            }
                            if (diffYear == 0) {
                                if (diffMonth == 1) {
                                    returnString = Integer.toString(diffMonth) + " month ago. ";
                                } else {
                                    returnString = Integer.toString(diffMonth) + " months ago. ";
                                }
                            } else if (diffYear == 1) {
                                if (diffMonth == 1) {
                                    returnString = Integer.toString(diffYear) + " year and "
                                            + Integer.toString(diffMonth) + " month ago. ";
                                } else {
                                    returnString = Integer.toString(diffYear) + " year and "
                                            + Integer.toString(diffMonth) + " months ago. ";
                                }
                            } else {

                                if (diffMonth == 1) {
                                    returnString = Integer.toString(diffYear) + " years and "
                                            + Integer.toString(diffMonth) + " month ago. ";
                                } else {
                                    returnString = Integer.toString(diffYear) + " years and "
                                            + Integer.toString(diffMonth) + " months ago. ";
                                }
                            }
                        }
                    }
                } else {

                    diffHours = diffHours % 24;
                    int hours = (int) diffHours;
                    int minutes = (int) (diffMinutes % 60);

                    returnModel.setDays(0);
                    returnModel.setMinutes(minutes);
                    if (diffMinutes > 59) {
                        if (diffHours == 1) {
                            returnString = Integer.toString(hours) + " hour ago. ";
                        } else {
                            returnString = Integer.toString(hours) + " hours ago. ";
                        }

                    } else if (diffMinutes == 1) {
                        returnString = Integer.toString(minutes) + " minute ago. ";
                    } else if (diffMinutes == 0) {
                        returnString = "Just Now";
                    } else {
                        returnString = Integer.toString(minutes) + " minutes ago. ";
                    }
                }
            } else {
                long diff = oldDate.getTime() - currentDate.getTime();

                long diffSeconds = diff / 1000;
                long diffMinutes = diff / (60 * 1000);
                long diffHours = diff / (60 * 60 * 1000);
                long diffDays = diff / (24 * 60 * 60 * 1000);
                oldCalendar.setTime(oldDate);
                currentCalendar.setTime(currentDate);

                if (diffHours >= 24) {
                    if (diffDays < 30) {
                        if (diffDays < 7) {
                            if (diffDays == 1) {
                                returnString = "After " + Integer.toString(((int) diffDays)) + " day.";
                            } else {
                                returnString = "After " + Integer.toString(((int) diffDays)) + " days.";
                            }
                        } else {
                            int weekCount = ((int) diffDays) / 7;
                            if (weekCount == 1) {
                                returnString = "After " + Integer.toString(((int) weekCount)) + " week.";
                            } else {
                                returnString = "After " + Integer.toString(((int) weekCount)) + " weeks.";
                            }
                        }
                    } else {

                        int currentYear, oldYear, currentMonth, oldMonth, diffYear, diffMonth;

                        currentYear = currentCalendar.get(Calendar.YEAR);
                        oldYear = oldCalendar.get(Calendar.YEAR);
                        currentMonth = currentCalendar.get(Calendar.MONTH);
                        oldMonth = oldCalendar.get(Calendar.MONTH);

                        if (currentMonth == oldMonth) {
                            diffYear = (oldYear - currentYear);

                            if (diffYear == 1) {
                                returnString = "After " + Integer.toString(diffYear) + " year.";
                            } else {
                                returnString = "After " + Integer.toString(diffYear) + " years.";
                            }
                        } else {
                            if (currentMonth > oldMonth) {
                                diffMonth = oldMonth - currentMonth;
                                diffYear = (oldYear - currentYear);
                            } else {
                                diffMonth = 12 - (oldMonth - currentMonth);
                                diffYear = (oldYear - currentYear) - 1;
                            }
                            if (diffYear == 0) {
                                if (diffMonth == 1) {
                                    returnString = "After " + Integer.toString(diffMonth) + " month. ";
                                } else {
                                    returnString = "After " + Integer.toString(diffMonth) + " months. ";
                                }
                            } else if (diffYear == 1) {
                                if (diffMonth == 1) {
                                    returnString = "After " + Integer.toString(diffYear) + " year and "
                                            + Integer.toString(diffMonth) + " month. ";
                                } else {
                                    returnString = "After " + Integer.toString(diffYear) + " year and "
                                            + Integer.toString(diffMonth) + " months. ";
                                }
                            } else {

                                if (diffMonth == 1) {
                                    returnString = "After " + Integer.toString(diffYear) + " years and "
                                            + Integer.toString(diffMonth) + " month. ";
                                } else {
                                    returnString = "After " + Integer.toString(diffYear) + " years and "
                                            + Integer.toString(diffMonth) + " months. ";
                                }
                            }
                        }
                    }
                } else {
                    diffHours = diffHours % 24;
                    int hours = (int) diffHours;
                    int minutes = (int) (diffMinutes % 60);
                    if (diffMinutes > 59) {
                        if (diffHours == 1) {
                            returnString = "After " + Integer.toString(hours) + " hour. ";
                        } else {
                            returnString = "After " + Integer.toString(hours) + " hours. ";
                        }
                    } else if (diffMinutes == 1) {
                        returnString = "After " + Integer.toString(minutes) + " minute. ";
                    } else if (diffMinutes == 0) {
                        returnString = "Just Now";
                    } else {
                        returnString = "After " + Integer.toString(minutes) + " minutes. ";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        returnModel.setDisplayText(returnString);
        return returnModel;
    }

    public class SortModel {
        int minutes, days;
        String displayText;

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public String getDisplayText() {
            return displayText;
        }

        public void setDisplayText(String displayText) {
            this.displayText = displayText;
        }
    }


}

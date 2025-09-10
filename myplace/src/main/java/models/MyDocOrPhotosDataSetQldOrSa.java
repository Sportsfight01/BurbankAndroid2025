package models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.AppController;
import common.MyPlaceDataBase;

/**
 * Created by Jaya.Krishna on 21-11-2017.
 */

public class MyDocOrPhotosDataSetQldOrSa implements Serializable {

    String title, authorName, docDate, type, displayTime;
    boolean byClient, current, fav = false;
    int urlInt, docWholeDateInt, docMonthInt, docDayInt;
    String url, notes = "";
    AppController controller;
    Context mcontext;
    boolean imageDownloaded = false;

    public MyDocOrPhotosDataSetQldOrSa(String result, Context context) {
        try {
            this.mcontext = context;
            this.controller = (AppController) context.getApplicationContext();
            JSONObject job = new JSONObject(result);
            title = job.isNull("title") ? "" : job.getString("title");
            authorName = job.isNull("authorname") ? "" : job.getString("authorname");
            byClient = job.isNull("byclient") ? false : job.getBoolean("byclient");

//            docDate = job.isNull("docdate") ? "" : job.getString("docdate");
            docDate = job.isNull("metaData") ? "" : job.getJSONObject("metaData").getString("createdOn");


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss a");
            SimpleDateFormat sdfString = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat monthString = new SimpleDateFormat("yyyyMM");
            SimpleDateFormat dateString = new SimpleDateFormat("dd");
            String tempDate = "0", tempDate2 = "0", tempDate3 = "0";
            try {
                Date complionDate = sdf.parse(docDate);
                tempDate = sdfString.format(complionDate);
                tempDate2 = monthString.format(complionDate);
                tempDate3 = dateString.format(complionDate);
                displayTime = "Photo added on " + sdfTime.format(complionDate);
            } catch (ParseException e) {
                e.printStackTrace();
                tempDate = "0";
                tempDate2 = "0";
                tempDate3 = "0";
                displayTime = "";
            }
            docWholeDateInt = Integer.parseInt(tempDate);
            docMonthInt = Integer.parseInt(tempDate2);
            docDayInt = Integer.parseInt(tempDate3);

            current = job.isNull("current") ? false : job.getBoolean("current");
            type = job.isNull("type") ? "" : job.getString("type");

            String tempUrl = job.isNull("url") ? "" : job.getString("url");

            if (tempUrl.length() > 1) {
                String urlPart[] = tempUrl.split("\\?+");
                if (urlPart.length > 0) {
                    String urlPart2[] = urlPart[0].split("/");
                    try {
                        urlInt = Integer.parseInt(urlPart2[urlPart2.length - 1]);
                    } catch (Exception e) {
                        urlInt = 0;
                    }
                } else {
                    urlInt = 0;
                }
            } else {
                urlInt = 0;
            }

            url = "https://nationalclickhome.burbankgroup.com.au/clickhome3webservice/" + tempUrl;

            MyPlaceDataBase myPlaceDataBase = new MyPlaceDataBase(context);
            notes = myPlaceDataBase.getNotesQldSa(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), urlInt);
            fav = myPlaceDataBase.isFavQldSa(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), urlInt);

        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }

    public boolean isImageDownloaded() {
        return imageDownloaded;
    }

    public void setImageDownloaded(boolean imageDownloaded) {
        this.imageDownloaded = imageDownloaded;
    }

    public MyDocOrPhotosDataSetQldOrSa() {
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDocDate() {
        return docDate;
    }

    public int getDocWholeDateInt() {
        return docWholeDateInt;
    }

    public void setDocWholeDateInt(int docWholeDateInt) {
        this.docWholeDateInt = docWholeDateInt;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isByClient() {
        return byClient;
    }

    public void setByClient(boolean byClient) {
        this.byClient = byClient;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public int getUrlInt() {
        return urlInt;
    }

    public void setUrlInt(int urlInt) {
        this.urlInt = urlInt;
    }

    public int getDocMonthInt() {
        return docMonthInt;
    }

    public void setDocMonthInt(int docMonthInt) {
        this.docMonthInt = docMonthInt;
    }

    public int getDocDayInt() {
        return docDayInt;
    }

    public void setDocDayInt(int docDayInt) {
        this.docDayInt = docDayInt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
        //controller.setFavForPhoto(Integer.toString(urlInt) + "_fav", fav);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        //controller.setNotesForPhoto(Integer.toString(urlInt) + "_notes", notes);
    }

}

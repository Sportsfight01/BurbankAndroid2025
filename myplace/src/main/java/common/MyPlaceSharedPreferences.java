package common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyPlacePhotosDetailsDataSet;

/**
 * Created by Jaya.Krishna on 14-12-2017.
 */

public class MyPlaceSharedPreferences {

    public static void saveFavDetailsQldOrSA(AppController controller,Context context,String key, MyDocOrPhotosDataSetQldOrSa favDetailsList) {
        Gson gson = new Gson();
        context.getSharedPreferences("MyPlaceSharedPreferences", context.MODE_PRIVATE)
                .edit()
                .putString(controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber() + "_" + key, gson.toJson(favDetailsList))
                .commit();
    }

    public static void saveFavDetailsVic(AppController controller,Context context,String key, String favDetailsList) {
        context.getSharedPreferences("MyPlaceSharedPreferences", context.MODE_PRIVATE)
                .edit()
                .putString(controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber() + "_" + key, favDetailsList)
                .commit();
    }

    public static void saveFaq(Context context,String favDetailsList) {
        Gson gson = new Gson();
        context.getSharedPreferences("MyPlaceFAQSharedPreferences", context.MODE_PRIVATE)
                .edit()
                .putString("FAQdata", favDetailsList)
                .commit();
    }

    public static String getFaq(Context context) {
        Gson gson = new Gson();
        SharedPreferences pref = context.getSharedPreferences("MyPlaceFAQSharedPreferences", context.MODE_PRIVATE);
        String sampleTest = pref.getString("FAQdata","");

        return  sampleTest;
    }


    public static String getFavDetails(AppController controller,Context context) {
        ArrayList<MyPlacePhotosDetailsDataSet> VicPhotosList = new ArrayList<MyPlacePhotosDetailsDataSet>();
        ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
        SharedPreferences pref = context.getSharedPreferences("MyPlaceSharedPreferences", context.MODE_PRIVATE);
        Gson gson = new Gson();
        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String favKey = entry.getKey();
            if (favKey.startsWith(controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber())) {
                String favDetails = entry.getValue().toString();
                if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
                    Type collectionType = new TypeToken<MyPlacePhotosDetailsDataSet>() {
                    }.getType();
                    MyPlacePhotosDetailsDataSet vic = gson.fromJson(favDetails, collectionType);
                    VicPhotosList.add(vic);
                } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {
                    Type collectionType = new TypeToken<MyDocOrPhotosDataSetQldOrSa>() {
                    }.getType();
                    MyDocOrPhotosDataSetQldOrSa qldOrSa = gson.fromJson(favDetails, collectionType);
                    QldOrSaPhotosList.add(qldOrSa);
                }
            }
        }

        if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
            if (VicPhotosList.size() > 0) {
                return gson.toJson(VicPhotosList);
            } else {
                return "";
            }

        } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {
            if (QldOrSaPhotosList.size() > 0) {
                return gson.toJson(QldOrSaPhotosList);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static void removeFavDetails(AppController controller,Context context,String key) {
        SharedPreferences pref = context.getSharedPreferences("MyPlaceSharedPreferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(controller.getMy_Place_Details().getUsername() + "_" + controller.getMy_Place_Details().getJobNumber() + "_" + key);
        editor.apply();
        editor.commit();
    }
}

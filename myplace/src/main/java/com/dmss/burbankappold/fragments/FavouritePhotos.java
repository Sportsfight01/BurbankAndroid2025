package com.dmss.burbankappold.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.io.IOException;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import adapters.MyPlacePhotoDateViseListAdapter;
import common.AppController;
import common.MyPlaceDataBase;
import common.TransparentProgressDialog;
import common.Utils;
import com.dmss.burbankappold.R;

import interfaces.FragmentCallBack;
import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyPlacePhotosDetailsDataSet;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jaya.Krishna on 13-12-2017.
 */

public class FavouritePhotos extends Fragment{

    View rootView;
    ListView photoListView;
    TextView emptyTextView, favJobNumberTextView;
    LinearLayout headerLayout, jobNumberLayout;

    ArrayList<MyPlacePhotosDetailsDataSet> adapterPopulatedPhotoList = new ArrayList<MyPlacePhotosDetailsDataSet>();

    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaAdapterPopulatedPhotoList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();


    MyPlacePhotoDateViseListAdapter myPlacePhotoDateViseListAdapter;
    int currentPosition/*, currentSelectedPhotoPosition*/;
    TransparentProgressDialog dialog;
    LinearLayout emptyElement;
    AppController controller;
    FragmentCallBack fragmentCallBack;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_place_photos, null);
        controller = (AppController) getActivity().getApplicationContext();
        controller.getAnalytics().setScreen(getActivity(),"Favorites_Screen");
        initializeUIElements();
        return rootView;
    }

    public void initializeUIElements() {
        TextView firstText = rootView.findViewById(R.id.firstText);
        firstText.setText("Favourite");
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setText("Photos");
        secondText.setTextColor(ContextCompat.getColor(getActivity(),R.color.black));
        //Favourite Photos
        fragmentCallBack = (FragmentCallBack) getActivity();
        MyPlaceDataBase myPlaceDataBase = new MyPlaceDataBase(getActivity());
        currentPosition = controller.getSelectedDateOfPhotos();
        photoListView = (ListView) rootView.findViewById(R.id.photoListView);
        /*emptyTextView = (TextView) rootView.findViewById(R.id.emptyTextView);
        emptyTextView.setText("No fav photos to display");*/
        favJobNumberTextView = (TextView) rootView.findViewById(R.id.favJobNumberTextView);
        String job = controller.getMy_Place_Details().getJobNumber();
        favJobNumberTextView.setText(job);
        headerLayout = (LinearLayout) rootView.findViewById(R.id.headerLayout);
        headerLayout.setVisibility(View.GONE);
        emptyElement = (LinearLayout) rootView.findViewById(R.id.emptyElement);
        jobNumberLayout = (LinearLayout) rootView.findViewById(R.id.jobNumberLayout);
        jobNumberLayout.setVisibility(View.VISIBLE);
        dialog = Utils.getProgress(getActivity());
        QldOrSaAdapterPopulatedPhotoList.addAll(myPlaceDataBase.getQldOrSaFavPhotos(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername()));
        if (QldOrSaAdapterPopulatedPhotoList.size() > 0) {
            newApiPopulateList();
            fragmentCallBack.actionbarTextChange("My Favourites ( " + Integer.toString(QldOrSaAdapterPopulatedPhotoList.size()) + " )");
        } else {
            emptyElement.setVisibility(View.VISIBLE);
            photoListView.setVisibility(View.GONE);
            dialog.cancel();
        }
    }

    public void newApiPopulateList() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
        byte[] message = sample.getBytes();
        final String encoded = Base64.encodeToString(message, Base64.NO_WRAP);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Basic " + encoded)
                                .addHeader("ContractNumber", controller.getMy_Place_Details().getJobNumber())
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(getActivity())
                .downloader(new OkHttp3Downloader(client))
                .build();

        myPlacePhotoDateViseListAdapter = new MyPlacePhotoDateViseListAdapter(getActivity(),getActivity(), QldOrSaAdapterPopulatedPhotoList, ((metrics.heightPixels) / 3), controller, true, true, emptyElement,picasso);
        photoListView.setAdapter(myPlacePhotoDateViseListAdapter);
        dialog.cancel();
    }

}
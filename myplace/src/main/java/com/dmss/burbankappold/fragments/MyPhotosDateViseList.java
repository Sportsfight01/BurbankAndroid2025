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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.github.chrisbanes.photoview.PhotoView;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import adapters.MyPlacePhotoDateViseListAdapter;
import common.AppController;
import common.MyPlaceDataBase;
import common.TransparentProgressDialog;
import common.Utils;
import com.dmss.burbankappold.R;

import interfaces.AdapterCallBack;
import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyPhotosNewDateWiseDataSet;
import models.MyPlacePhotosDetailsDataSet;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jaya.krishna on 06-06-2017.
 */
public class MyPhotosDateViseList extends Fragment implements View.OnClickListener, AdapterCallBack {

    View rootView;
    ImageView photoPreviousMonthImageView, photoNextMonthImageView;
    ListView photoListView;
    TextView photoMonthNameTextView, photoYearTextView;
    LinearLayout headerLayout;
    ArrayList<MyPlacePhotosDetailsDataSet> adapterPopulatedPhotoList = new ArrayList<MyPlacePhotosDetailsDataSet>();

    MyPhotosNewDateWiseDataSet newDateWiseDataSet;
    ArrayList<MyPhotosNewDateWiseDataSet> QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>();
    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaAdapterPopulatedPhotoList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();

    MyPlacePhotoDateViseListAdapter myPlacePhotoDateViseListAdapter;
    int currentPosition/*, currentSelectedPhotoPosition*/;
    TransparentProgressDialog dialog;

    AppController controller;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_place_photos_datewise, null);
        initializeUIElements();
        return rootView;
    }

    public void initializeUIElements() {
        ImageView backImage = rootView.findViewById(R.id.back_image);
        backImage.setOnClickListener(view -> getActivity().onBackPressed());
        TextView firstText = rootView.findViewById(R.id.firstText);
        firstText.setText("Photos");
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);

        controller = (AppController) getActivity().getApplicationContext();
        currentPosition = controller.getSelectedDateOfPhotos();
        headerLayout = rootView.findViewById(R.id.headerLayout);
        headerLayout.setVisibility(View.VISIBLE);
        photoPreviousMonthImageView = rootView.findViewById(R.id.photoPreviousMonthImageView);
        photoPreviousMonthImageView.setOnClickListener(this);
        photoNextMonthImageView = rootView.findViewById(R.id.photoNextMonthImageView);
        photoNextMonthImageView.setOnClickListener(this);
        photoListView = rootView.findViewById(R.id.photoListView);
        photoMonthNameTextView = rootView.findViewById(R.id.photoMonthNameTextView);
        photoYearTextView = rootView.findViewById(R.id.photoYearTextView);
        int size = 0;
            /*if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
            photosList.addAll(controller.getPhotosTotalDetails());
            size = photosList.size();
            populateList();
        } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {*/
            if (controller.isFromNotifications()) {
                photoPreviousMonthImageView.setVisibility(View.GONE);
                photoNextMonthImageView.setVisibility(View.GONE);
                newApiPopulateFromNotification(controller.getNewDateWiseDataSet());
            } else {
                QldOrSaPhotosList.addAll(controller.getQldOrSaPhotosList());
                size = QldOrSaPhotosList.size();
                newApiPopulateList();
            }
        /*}*/

        if (size != 0) {
            if (size == 1) {
                photoPreviousMonthImageView.setVisibility(View.INVISIBLE);
                photoNextMonthImageView.setVisibility(View.INVISIBLE);
            } else if (size - 1 == currentPosition) {
                photoPreviousMonthImageView.setVisibility(View.INVISIBLE);
                photoNextMonthImageView.setVisibility(View.VISIBLE);
            } else if (currentPosition == 0) {
                photoPreviousMonthImageView.setVisibility(View.VISIBLE);
                photoNextMonthImageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.photoPreviousMonthImageView) {
            currentPosition = currentPosition + 1;
            if (QldOrSaPhotosList.size() != 1) {
                photoNextMonthImageView.setVisibility(View.VISIBLE);
            }
            if (currentPosition == QldOrSaPhotosList.size() - 1) {
                photoPreviousMonthImageView.setVisibility(View.INVISIBLE);
            } else {
                photoPreviousMonthImageView.setVisibility(View.VISIBLE);
            }
            newApiPopulateList();
        } else if (id == R.id.photoNextMonthImageView) {
            currentPosition = currentPosition - 1;
            if (currentPosition == 0) {
                photoNextMonthImageView.setVisibility(View.INVISIBLE);
            } else {
                photoNextMonthImageView.setVisibility(View.VISIBLE);
            }
            if (QldOrSaPhotosList.size() != 1) {
                photoPreviousMonthImageView.setVisibility(View.VISIBLE);
            }
            newApiPopulateList();
        }
    }


    public void newApiPopulateList() {
        dialog = Utils.getProgress(getActivity());
        newDateWiseDataSet = null;
        QldOrSaAdapterPopulatedPhotoList.clear();
        newDateWiseDataSet = QldOrSaPhotosList.get(currentPosition);
        photoMonthNameTextView.setText(newDateWiseDataSet.getDate() + " " + newDateWiseDataSet.getMonthString());
        photoYearTextView.setText(newDateWiseDataSet.getYear());
        QldOrSaAdapterPopulatedPhotoList.addAll(newDateWiseDataSet.getQldOrSaPhotosList());
        if (myPlacePhotoDateViseListAdapter == null) {
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

            myPlacePhotoDateViseListAdapter = new MyPlacePhotoDateViseListAdapter(getActivity(),getActivity(), QldOrSaAdapterPopulatedPhotoList, ((metrics.heightPixels) / 3), controller, true, false, null,picasso);
            photoListView.setAdapter(myPlacePhotoDateViseListAdapter);
            photoListView.smoothScrollToPosition(controller.getPhotoScrollToThePosition());
        } else {
            myPlacePhotoDateViseListAdapter.notifyDataSetChanged();
        }
        dialog.cancel();
    }

    public void newApiPopulateFromNotification(MyPhotosNewDateWiseDataSet newDateWiseDataSet) {
        dialog = Utils.getProgress(getActivity());
        MyPlaceDataBase myPlaceDataBase = new MyPlaceDataBase(getActivity());
        QldOrSaAdapterPopulatedPhotoList.clear();
        photoMonthNameTextView.setText(newDateWiseDataSet.getDate() + " " + newDateWiseDataSet.getMonthString());
        photoYearTextView.setText(newDateWiseDataSet.getYear());
        for (int i = 0; i < newDateWiseDataSet.getQldOrSaPhotosList().size(); i++) {
            MyDocOrPhotosDataSetQldOrSa dataSetQldOrSa = newDateWiseDataSet.getQldOrSaPhotosList().get(i);
            dataSetQldOrSa.setNotes(myPlaceDataBase.getNotesQldSa(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), dataSetQldOrSa.getUrlInt()));
            dataSetQldOrSa.setFav(myPlaceDataBase.isFavQldSa(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), dataSetQldOrSa.getUrlInt()));
            QldOrSaAdapterPopulatedPhotoList.add(dataSetQldOrSa);
        }
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


        myPlacePhotoDateViseListAdapter = new MyPlacePhotoDateViseListAdapter(getActivity(),getActivity(), QldOrSaAdapterPopulatedPhotoList, ((metrics.heightPixels) / 3), controller, true, false, null,picasso);
        photoListView.setAdapter(myPlacePhotoDateViseListAdapter);
        photoListView.smoothScrollToPosition(controller.getPhotoScrollToThePosition());
        dialog.cancel();
    }

    @Override
    public void positionOnClick(String url) {
        String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
        byte[] message = sample.getBytes();
        final String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.zoomable_image_dailog, null);
        PhotoView photoView = mView.findViewById(R.id.imageView);
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
        picasso.load(url)
                .resize(200, 200).into(photoView);
        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
}
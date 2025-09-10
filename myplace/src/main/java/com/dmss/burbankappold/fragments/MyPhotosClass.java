package com.dmss.burbankappold.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.dmss.burbankappold.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import adapters.MyPlacePhotosListAdapter;
import common.AppController;
import common.Common;
import common.MyPlaceDataBase;
import common.TransparentProgressDialog;
import common.Utils;
import interfaces.WebApiJobDetailsResponseCallBack;
import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyPhotosMonthWiseDataSet;
import models.MyPhotosNewDateWiseDataSet;
import models.TempDataSet;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class MyPhotosClass extends Fragment implements View.OnClickListener, WebApiJobDetailsResponseCallBack {

    View rootView;
    ImageView photoPreviousMonthImageView, photoNextMonthImageView;
    ListView photoListView;
    TextView photoMonthNameTextView, photoYearTextView, retryTextView;
    ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();

    MyPlacePhotosListAdapter myPlacePhotosListAdapter;
    LinearLayout emptyElement, headerLayout;
    String photoResult;
    TransparentProgressDialog dialog;
    AppController controller;
    int outerArraySize = 0, outerSelectedPosition = 0;
    ArrayList<TempDataSet> tempDataSets = new ArrayList<TempDataSet>();
    TempDataSet tempDataSet = new TempDataSet();
    int tempDate = 0;
    ArrayList<MyPhotosMonthWiseDataSet> myPhotosMonthWiseDataSets = new ArrayList<MyPhotosMonthWiseDataSet>();
    MyPhotosMonthWiseDataSet monthWiseDataSet = new MyPhotosMonthWiseDataSet();

    ArrayList<MyPhotosNewDateWiseDataSet> newDateWiseDataSets = new ArrayList<MyPhotosNewDateWiseDataSet>();
    MyPhotosNewDateWiseDataSet dateWiseDataSet = new MyPhotosNewDateWiseDataSet();
    DisplayMetrics metrics;

    MyPlaceDataBase myPlaceDataBase;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_place_photos, null);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        controller = (AppController) getActivity().getApplicationContext();
        controller.getAnalytics().setScreen(getActivity(), "Photos_Screen");
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        initializeUIElements();
        controller.setQldOrSaPhotosList(new ArrayList<MyPhotosNewDateWiseDataSet>());
        String result = controller.getLoadedPhotoResult();
        if (result != null && result.length() > 0) {
            onJobDetailsSuccessResult(result);
            /*if (AppController.outerArraySize > 0 && AppController.outerSelectedPosition > 0) {
                loadPreviewsView();
            }*/
        } else {
            callWebNewApiToGetAllPhotosList();
        }


        return rootView;
    }

    public void initializeUIElements() {
        ImageView backImage = rootView.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView firstText = rootView.findViewById(R.id.firstText);
        firstText.setText("Photos");
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);
        myPlaceDataBase = new MyPlaceDataBase(getActivity());
        photoPreviousMonthImageView = (ImageView) rootView.findViewById(R.id.photoPreviousMonthImageView);
        photoPreviousMonthImageView.setOnClickListener(this);
        photoNextMonthImageView = (ImageView) rootView.findViewById(R.id.photoNextMonthImageView);
        photoNextMonthImageView.setOnClickListener(this);
        photoListView = (ListView) rootView.findViewById(R.id.photoListView);
        emptyElement = (LinearLayout) rootView.findViewById(R.id.emptyElement);
        headerLayout = (LinearLayout) rootView.findViewById(R.id.headerLayout);
        retryTextView = (TextView) rootView.findViewById(R.id.retryTextView);
        photoMonthNameTextView = (TextView) rootView.findViewById(R.id.photoMonthNameTextView);
        photoYearTextView = (TextView) rootView.findViewById(R.id.photoYearTextView);
        photoMonthNameTextView.setText(new SimpleDateFormat("MMMMM").format(new Date()));
        photoYearTextView.setText(new SimpleDateFormat("yyyy").format(new Date()));

        retryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callWebNewApiToGetAllPhotosList();
            }
        });

        // loadPreviewsView();

    }

    public void setHeadingData() {
        int size = 0;
        String test = myPhotosMonthWiseDataSets.get(outerSelectedPosition).getMonthName();
        test = myPhotosMonthWiseDataSets.get(outerSelectedPosition).getYear();
        size = myPhotosMonthWiseDataSets.size();
        photoMonthNameTextView.setText(myPhotosMonthWiseDataSets.get(outerSelectedPosition).getMonthName());
        photoYearTextView.setText(myPhotosMonthWiseDataSets.get(outerSelectedPosition).getYear());


        if (size != 0) {
            if (size == 1) {
                photoPreviousMonthImageView.setVisibility(View.INVISIBLE);
                photoNextMonthImageView.setVisibility(View.INVISIBLE);
            } else if (size - 1 == outerSelectedPosition) {
                photoPreviousMonthImageView.setVisibility(View.VISIBLE);
                photoNextMonthImageView.setVisibility(View.INVISIBLE);
            } else if (outerSelectedPosition == 0) {
                photoPreviousMonthImageView.setVisibility(View.INVISIBLE);
                photoNextMonthImageView.setVisibility(View.VISIBLE);
            }
        }
    }


    public void loadPreviewsView() {
        photoPreviousMonthImageView.setVisibility(View.VISIBLE);
        photoNextMonthImageView.setVisibility(View.VISIBLE);

        if (outerSelectedPosition == 0) {
            photoPreviousMonthImageView.setVisibility(View.INVISIBLE);
        } else {
            photoPreviousMonthImageView.setVisibility(View.VISIBLE);
        }

        if (outerSelectedPosition == outerArraySize - 1) {
            photoNextMonthImageView.setVisibility(View.INVISIBLE);
        } else {
            photoNextMonthImageView.setVisibility(View.VISIBLE);
        }

        newApiResultNavigation();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.photoPreviousMonthImageView) {
            if (outerArraySize > 1 && outerSelectedPosition != 0) {
                outerSelectedPosition = outerSelectedPosition - 1;
                AppController.outerArraySize = outerArraySize;
                AppController.outerSelectedPosition = outerSelectedPosition;

                if (outerSelectedPosition == 0) {
                    photoPreviousMonthImageView.setVisibility(View.INVISIBLE);
                } else {
                    photoPreviousMonthImageView.setVisibility(View.VISIBLE);
                }
                photoNextMonthImageView.setVisibility(View.VISIBLE);
                newApiResultNavigation();
            }
        } else if (id == R.id.photoNextMonthImageView) {
            if (outerArraySize > 1 && outerSelectedPosition != outerArraySize - 1) {
                outerSelectedPosition = outerSelectedPosition + 1;


                AppController.outerArraySize = outerArraySize;
                AppController.outerSelectedPosition = outerSelectedPosition;

                if (outerSelectedPosition == outerArraySize - 1) {
                    photoNextMonthImageView.setVisibility(View.INVISIBLE);
                } else {
                    photoNextMonthImageView.setVisibility(View.VISIBLE);
                }
                photoPreviousMonthImageView.setVisibility(View.VISIBLE);
                newApiResultNavigation();
            }
        }
    }


    /**
     * Method used to call the web API to get the data from server
     **/
    /*private void callWebApiToGetAllPhotosList() {
     *//**
     * Checking whether the network is available or not
     *//*
        if (Utils.isNetworkAvailable(getActivity())) {
            */

    /**
     * Checking whether to call the new homes API or Favourite API
     *//*
            dialog = Utils.getProgress(getActivity());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    photoResult = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetAllPhotos + controller.getConstructionId());
                    onSuccessResult(photoResult);


                }
            }).start();
            ;
        }
    }*/
    public void callWebNewApiToGetAllPhotosList() {
        /**
         * Checking whether the network is available or not
         */
        if (Utils.isNetworkAvailable(getActivity())) {
            /**
             * Checking whether to call the new homes API or Favourite API
             */
            dialog = Utils.getProgress(getActivity());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                    byte[] message = sample.getBytes();
                    String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                    String val = controller.webApiCall().getDataWithHeaders(Common.newMyPlacePhotosOrDocuments, encoded, controller.getMy_Place_Details().getJobNumber());*/

                    String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                    byte[] message = sample.getBytes();
                    String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                    //String val = controller.webApiCall().getDataWithHeaders(Common.newMyPlacePhotosOrDocuments, encoded, controller.getMy_Place_Details().getJobNumber());
                    //String result = controller.webApiCall().getDataNewMyPlace(Common.newMyPlacePhotosOrDocuments, controller.getMy_Place_Details());
                    controller.webApiCall().getDataNewMyPlace(Common.newMyPlacePhotosOrDocuments, controller.getMy_Place_Details(), MyPhotosClass.this);
                    //onSuccessResult(result);
                }
            }).start();
        }
    }

    @Override
    public void onJobDetailsSuccessResult(String result) {
        if (result != null) {
            controller.setLoadedPhotoResult(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    MyDocOrPhotosDataSetQldOrSa myDocumentsDataSetQldOrSa = new MyDocOrPhotosDataSetQldOrSa(jsonArray.getJSONObject(i).toString(), getActivity());
                    if (myDocumentsDataSetQldOrSa.getType().trim().equalsIgnoreCase("JPG")) {
                        QldOrSaPhotosList.add(myDocumentsDataSetQldOrSa);
                    }
                }
                myPlaceDataBase.insertQldOrSaPhotos(QldOrSaPhotosList, controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername());
                sortDataByMonthNewApi();
                //Loading Previews View


            } catch (JSONException e) {
                //sortData(false);
                e.printStackTrace();
            }
        }

        if (dialog != null) {
            dialog.cancel();
        }
    }

    @Override
    public void onJobDetailsErrorResult(String error) {

    }

    public void sortDataByMonthNewApi() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Collections.sort(QldOrSaPhotosList, new Comparator<MyDocOrPhotosDataSetQldOrSa>() {
                    public int compare(MyDocOrPhotosDataSetQldOrSa obj1, MyDocOrPhotosDataSetQldOrSa obj2) {
                        int fromObj1Size = obj1.getDocWholeDateInt();
                        int fromObj2Size = obj2.getDocWholeDateInt();
                        return fromObj1Size - fromObj2Size;
                    }
                });

                for (int i = 0; i < QldOrSaPhotosList.size(); i++) {
                    MyDocOrPhotosDataSetQldOrSa myDocOrPhotosDataSetQldOrSa = QldOrSaPhotosList.get(i);
                    if (tempDate == myDocOrPhotosDataSetQldOrSa.getDocMonthInt()) {
                        tempDataSet.setDocMonthInt(tempDate);
                        tempDataSet.setQldOrSaPhotosList(myDocOrPhotosDataSetQldOrSa);
                        if (i == QldOrSaPhotosList.size() - 1) {
                            tempDataSets.add(tempDataSet);
                        }
                    } else {
                        if (i != 0) {
                            tempDataSets.add(tempDataSet);
                        }
                        tempDate = myDocOrPhotosDataSetQldOrSa.getDocMonthInt();
                        tempDataSet = new TempDataSet();
                        tempDataSet.setDocMonthInt(tempDate);
                        tempDataSet.setQldOrSaPhotosList(myDocOrPhotosDataSetQldOrSa);
                        if (i == QldOrSaPhotosList.size() - 1) {
                            tempDataSets.add(tempDataSet);
                        }
                    }
                }
                tempDate = 0;
                for (int i = 0; i < tempDataSets.size(); i++) {
                    TempDataSet dataSet = tempDataSets.get(i);
                    monthWiseDataSet = new MyPhotosMonthWiseDataSet();
                    ArrayList<MyDocOrPhotosDataSetQldOrSa> photosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
                    photosList.addAll(tempDataSets.get(i).getQldOrSaPhotosList());
                    monthWiseDataSet.setDocDateString(photosList.get(0).getDocDate());
                    for (int j = 0; j < photosList.size(); j++) {
                        MyDocOrPhotosDataSetQldOrSa qldOrSa = photosList.get(j);
                        if (tempDate == qldOrSa.getDocDayInt()) {
                            dateWiseDataSet.setQldOrSaPhotosList(qldOrSa);
                            if (j == photosList.size() - 1) {
                                newDateWiseDataSets.add(dateWiseDataSet);
                            }
                        } else {
                            tempDate = qldOrSa.getDocDayInt();
                            if (j != 0) {
                                newDateWiseDataSets.add(dateWiseDataSet);
                            }
                            dateWiseDataSet = new MyPhotosNewDateWiseDataSet(qldOrSa);
                            if (j == photosList.size() - 1) {
                                newDateWiseDataSets.add(dateWiseDataSet);
                            }
                        }
                    }
                    monthWiseDataSet.setQldOrSaPhotosList(newDateWiseDataSets);
                    newDateWiseDataSets = new ArrayList<MyPhotosNewDateWiseDataSet>();
                    myPhotosMonthWiseDataSets.add(monthWiseDataSet);
                }

                if (myPhotosMonthWiseDataSets.size() > 0) {
                    outerArraySize = myPhotosMonthWiseDataSets.size();
                    outerSelectedPosition = myPhotosMonthWiseDataSets.size() - 1;
                    controller.setQldOrSaPhotosList(myPhotosMonthWiseDataSets.get(outerSelectedPosition).getQldOrSaPhotosList());
                    myPlacePhotosListAdapter = new MyPlacePhotosListAdapter(getActivity(), myPhotosMonthWiseDataSets.get(outerSelectedPosition).getQldOrSaPhotosList(), ((metrics.widthPixels) / 4), true);
                    photoListView.setAdapter(myPlacePhotosListAdapter);
                    emptyElement.setVisibility(View.GONE);
                    photoListView.setVisibility(View.VISIBLE);
                    headerLayout.setVisibility(View.VISIBLE);
                    setHeadingData();
                } else {
                    emptyElement.setVisibility(View.VISIBLE);
                    photoListView.setVisibility(View.GONE);
                    headerLayout.setVisibility(View.GONE);
                }

                if (dialog != null) {
                    dialog.cancel();
                }

            }
        });
    }

    public void newApiResultNavigation() {
        myPlacePhotosListAdapter = new MyPlacePhotosListAdapter(getActivity(), myPhotosMonthWiseDataSets.get(outerSelectedPosition).getQldOrSaPhotosList(), ((metrics.widthPixels) / 4), true);
        controller.setQldOrSaPhotosList(myPhotosMonthWiseDataSets.get(outerSelectedPosition).getQldOrSaPhotosList());
        photoListView.setAdapter(myPlacePhotosListAdapter);
        setHeadingData();
    }
}

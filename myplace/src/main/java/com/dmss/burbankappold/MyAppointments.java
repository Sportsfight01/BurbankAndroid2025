package com.dmss.burbankappold;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

import common.AppController;
import common.Common;
import common.TransparentProgressDialog;
import common.Utils;
import models.MyAppointmentsDataSet;

public class MyAppointments extends BaseActivity {

    TextView colourSelectionDateTextView, electricalSelectionDateTextView,
            buildingSelectionDateTextView, tenderSelectionDateTextView, pcInspectionTimeTextView;

    String colourSelectionDate = "- -", electricalSelectionDate = "- -",
            buildingSelectionDate = "- -", tenderSelectionDate = "- -", pcInspectionTime = "- -";

    TransparentProgressDialog dialog;
    AppController controller;

    boolean apiCallStarted = false;
    boolean vic;
    String currentJob = "", previousJob = "";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);
        changeStatusBarColor();
        controller = (AppController) getApplicationContext();
        controller.getAnalytics().dashboardMyAppointmentsIconTouchEvent();
        actionBarSettings();
        initializeUIElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!apiCallStarted){
            checkToCallApi();
        }

    }

    public void initializeUIElements() {
        colourSelectionDateTextView = (TextView) findViewById(R.id.colourSelectionDateTextView);
        electricalSelectionDateTextView = (TextView) findViewById(R.id.electricalSelectionDateTextView);
        buildingSelectionDateTextView = (TextView) findViewById(R.id.buildingSelectionDateTextView);
        tenderSelectionDateTextView = (TextView) findViewById(R.id.tenderSelectionDateTextView);
        pcInspectionTimeTextView = (TextView) findViewById(R.id.pcInspectionTimeTextView);
        apiCallStarted = true;
        checkToCallApi();
    }

    public void checkToCallApi(){
        currentJob = controller.getMy_Place_Details().getJobNumber();
        previousJob = controller.getAppointmentJobNumber();
        ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>();
        if (controller.getMyAppointmentsDataSets() != null && controller.getMyAppointmentsDataSets().size() != 0) {
            myAppointmentsDataSets.addAll(controller.getMyAppointmentsDataSets());
        }
        if (!currentJob.equalsIgnoreCase(previousJob)) {
            apiCallStarted = true;
            setDataToUI(true);
            getMyAppointmentDetails();
            controller.setAppointmentJobNumber(controller.getMy_Place_Details().getJobNumber());
        } else {
            if (myAppointmentsDataSets.size() == 0) {
                setDataToUI(true);
                getMyAppointmentDetails();
            } else {
                setDataToUI(false);
            }
        }
    }

    public void getMyAppointmentDetails(){
        if (controller.getMy_Place_Details() != null) {
            if (Utils.isNetworkAvailable(MyAppointments.this)) {
                dialog = Utils.getProgress(MyAppointments.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                        byte[] message = sample.getBytes();
                        String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                        String contactUsResult = controller.webApiCall().getDataWithHeaders(Common.newMyPlaceAppointment, encoded, controller.getMy_Place_Details().getJobNumber());
                        if (contactUsResult.length() > 2) {
                            try {

                                ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>();
                                JSONArray jobJsonArray = new JSONArray(contactUsResult);
                                for (int i = 0; i < jobJsonArray.length(); i++) {
                                    MyAppointmentsDataSet appointmentsDataSet = new MyAppointmentsDataSet(jobJsonArray.get(i).toString(), false);
                                    myAppointmentsDataSets.add(appointmentsDataSet);
                                }
                                controller.setMyAppointmentsDataSets(myAppointmentsDataSets);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setDataToUI(false);

                                    }
                                });
                                apiCallStarted = false;
                            } catch (Exception ex) {
                                apiCallStarted = false;
                                ex.fillInStackTrace();
                                controller.setAppointmentsDataSet(null);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null) {
                                            dialog.cancel();
                                        }
                                    }
                                });
                            }

                        } else {
                            apiCallStarted = false;
                            controller.setAppointmentsDataSet(null);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null) {
                                        dialog.cancel();
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    public void setDataToUI(boolean empty){
        apiCallStarted = false;
        if(empty){
            colourSelectionDateTextView.setText("- -");
            electricalSelectionDateTextView.setText("- -");
            buildingSelectionDateTextView.setText("- -");
            tenderSelectionDateTextView.setText("- -");
            pcInspectionTimeTextView.setText("- -");

        }else{
            if (controller.getMyAppointmentsDataSets() != null && controller.getMyAppointmentsDataSets().size() != 0) {
                ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>(controller.getMyAppointmentsDataSets());
                if (myAppointmentsDataSets.size() > 0) {
                    for (int i = 0; i < myAppointmentsDataSets.size(); i++) {
                        MyAppointmentsDataSet appointmentsDataSet = myAppointmentsDataSets.get(i);
                        String appointmentName = appointmentsDataSet.getName();
                        if(appointmentName!= null){
                            if (appointmentsDataSet.getName().equalsIgnoreCase("Edge Appointment") ||
                                    appointmentsDataSet.getName().equalsIgnoreCase("Colour Selection")  ) {

                                if (appointmentsDataSet.getStatus().equalsIgnoreCase("unplanned") ) {
                                    colourSelectionDate = "- -";
                                }else {
                                    colourSelectionDate = appointmentsDataSet.getDisplayDate();
                                }

                                    colourSelectionDateTextView.setText(colourSelectionDate);


                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Electrical Selection")) {
                                electricalSelectionDate = appointmentsDataSet.getDisplayDate();
                                electricalSelectionDateTextView.setText(electricalSelectionDate);
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Sign Building Contract")) {
                                buildingSelectionDate = appointmentsDataSet.getDisplayDate();
                                if(appointmentsDataSet.getStatus().equalsIgnoreCase("Completed")) {
                                    buildingSelectionDateTextView.setText(buildingSelectionDate);
                                }
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Tender Presentation")) {
                                tenderSelectionDate = appointmentsDataSet.getDisplayDate();
                                tenderSelectionDateTextView.setText(tenderSelectionDate);
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("PC INSPECTION")) {
                                //pcInspectionTime = appointmentsDataSet.getDisplayDate(); - - uncomment to Enable PC Inspection date
                                if(controller.isShowPcInspection()){
                                    pcInspectionTime = appointmentsDataSet.getDisplayDate();
                                }else{
                                    pcInspectionTime = "- -";
                                }
                                pcInspectionTimeTextView.setText(pcInspectionTime);
                            }
                        }

                    }
                }
            }
        }

        if (dialog != null) {
            dialog.cancel();
        }
    }

    public void actionBarSettings() {
        ImageView backImage = findViewById(R.id.back_image);
        backImage.setVisibility(View.GONE);
        backImage.setOnClickListener(view -> onBackPressed());
        TextView firstText = findViewById(R.id.firstText);
        firstText.setText("MyAppointments");
        TextView secondText = findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*final Dialog exitDialog = new Dialog(MyAppointments.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) exitDialog.findViewById(R.id.dialogOkButton);
        final Button dialogCancelButton = (Button) exitDialog.findViewById(R.id.dialogCancelButton);
        final TextView dialogMessageTextView = (TextView) exitDialog.findViewById(R.id.dialogMessageTextView);
        dialogMessageTextView.setText("Do you want exit from the application");
        LinearLayout cancelButtonLayout = (LinearLayout) exitDialog.findViewById(R.id.cancelButtonLayout);
        cancelButtonLayout.setVisibility(View.VISIBLE);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
            }
        });

        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();*/
    }





























/*    public String getMyPlaceLoginJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Region", controller.getMy_Place_Details().getRegion());
            jsonObject.put("JobNumber", controller.getMy_Place_Details().getJobNumber());
            jsonObject.put("UserName", controller.getMy_Place_Details().getUsername());
            jsonObject.put("Password", controller.getMy_Place_Details().getPassword());
        } catch (JSONException ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    public void onSuccessResult(final String result) {
        vic = false;
        if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
            vic = true;
        }

        if (result != null || result == "" || result.length() < 3) {
            apiCallStarted = false;
        }
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONArray jobJsonArray = new JSONArray(result);
                        for (int i = 0; i < jobJsonArray.length(); i++) {
                            MyAppointmentsDataSet appointmentsDataSet = new MyAppointmentsDataSet(jobJsonArray.get(i).toString(), vic);

                            *//*if (appointmentsDataSet.getName().equalsIgnoreCase("Colour Selection")) {
                                colourSelectionDate = appointmentsDataSet.getDisplayDate();
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Electrical Selection")) {
                                electricalSelectionDate = appointmentsDataSet.getDisplayDate();
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Sign Building Contract")) {
                                buildingSelectionDate = appointmentsDataSet.getDisplayDate();
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Tender Presentation")) {
                                tenderSelectionDate = appointmentsDataSet.getDisplayDate();
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("PC INSPECTION")) {
                                pcInspectionTime = appointmentsDataSet.getDisplayDate();
                            }*//*

                            if (appointmentsDataSet.getName().equalsIgnoreCase("Colour Selection")) {
                                colourSelectionDate = appointmentsDataSet.getDisplayDate();
                                colourSelectionDateTextView.setText(colourSelectionDate);
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Electrical Selection")) {
                                electricalSelectionDate = appointmentsDataSet.getDisplayDate();
                                electricalSelectionDateTextView.setText(electricalSelectionDate);
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Sign Building Contract")) {
                                buildingSelectionDate = appointmentsDataSet.getDisplayDate();
                                buildingSelectionDateTextView.setText(buildingSelectionDate);
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("Tender Presentation")) {
                                tenderSelectionDate = appointmentsDataSet.getDisplayDate();
                                tenderSelectionDateTextView.setText(tenderSelectionDate);
                            } else if (appointmentsDataSet.getName().equalsIgnoreCase("PC INSPECTION")) {
                                pcInspectionTime = appointmentsDataSet.getDisplayDate();
                                pcInspectionTimeTextView.setText(pcInspectionTime);
                            }
                        }
                        dialog.cancel();
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                            }
                        });
                    }
                }
            });

        } catch (Exception ex) {
            ex.fillInStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            });

        }
        *//*new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                colourSelectionDateTextView.setText(colourSelectionDate);
                electricalSelectionDateTextView.setText(electricalSelectionDate);
                buildingSelectionDateTextView.setText(buildingSelectionDate);
                tenderSelectionDateTextView.setText(tenderSelectionDate);
                pcInspectionTimeTextView.setText(pcInspectionTime);

                dialog.cancel();
            }
        });*//*
    }


    *//**
     * Method used to call the web API to get the data from server
     **//*
    private void callMyAppointmentsWebApi() {
        *//**
         * Checking whether the network is available or not
         *//*
        dialog = Utils.getProgress(MyAppointments.this);
        if (Utils.isNetworkAvailable(MyAppointments.this)) {
            if (controller.getMy_Place_Details() != null) {
                if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String urll = Common.myPlaceBaseUrlVic;

                            String result = controller.webApiCall().postData_to_MyPlace(urll + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                            if (result.equalsIgnoreCase("true")) {
                                String loginResult = controller.webApiCall().getData_From_MyPlace(urll + "" + Common.MyPlaceGetUserDetailsUrl);
                                try {
                                    JSONObject jsonObject = new JSONObject(loginResult);
                                    String constructionID = jsonObject.isNull("ConstructionID") ? "" : jsonObject.getString("ConstructionID");
                                    String officeTicket = jsonObject.isNull("OfficeID") ? "" : jsonObject.getString("OfficeID");
                                    if (constructionID.length() > 0) {
                                        String contactUsResult = controller.webApiCall().getData_From_MyPlace("https://www.burbank.com.au/victoria/myplace/api/progress/GetAdminProgress?constructionTicketID=" + constructionID
                                                + "&officeTicketID=" + officeTicket);
                                        if (contactUsResult.length() > 2) {
                                            try {

                                                ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>();
                                                JSONArray jobJsonArray = new JSONArray(contactUsResult);
                                                for (int i = 0; i < jobJsonArray.length(); i++) {
                                                    MyAppointmentsDataSet appointmentsDataSet = new MyAppointmentsDataSet(jobJsonArray.get(i).toString(), false);
                                                    myAppointmentsDataSets.add(appointmentsDataSet);
                                                }
                                                controller.setMyAppointmentsDataSets(myAppointmentsDataSets);
                                                //initializeUIElements();
                                            } catch (Exception ex) {
                                                ex.fillInStackTrace();
                                                controller.setAppointmentsDataSet(null);
                                            }
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialog != null) {
                                                        dialog.cancel();
                                                    }
                                                }
                                            });
                                        } else {
                                            controller.setAppointmentsDataSet(null);
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialog != null) {
                                                        dialog.cancel();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        controller.setAppointmentsDataSet(null);
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialog != null) {
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    controller.setAppointmentsDataSet(null);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog != null) {
                                                dialog.cancel();
                                            }
                                        }
                                    });

                                }
                            } else if (result.equalsIgnoreCase("false")) {
                                controller.setAppointmentsDataSet(null);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        Utils.showToast(MyAppointments.this, "My place details not valid for this job number", Common.errorCase);
                                    }
                                });
                            } else {
                                controller.setAppointmentsDataSet(null);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                    }
                                });
                            }
                        }
                    });
                    t.start();
                } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                            byte[] message = sample.getBytes();
                            String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                            String contactUsResult = controller.webApiCall().getDataWithHeaders(Common.newMyPlaceAppointment, encoded, controller.getMy_Place_Details().getJobNumber());
                            if (contactUsResult.length() > 2) {
                                try {

                                    ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>();
                                    JSONArray jobJsonArray = new JSONArray(contactUsResult);
                                    for (int i = 0; i < jobJsonArray.length(); i++) {
                                        MyAppointmentsDataSet appointmentsDataSet = new MyAppointmentsDataSet(jobJsonArray.get(i).toString(), false);
                                        myAppointmentsDataSets.add(appointmentsDataSet);
                                    }
                                    controller.setMyAppointmentsDataSets(myAppointmentsDataSets);
                                    //initializeUIElements();
                                } catch (Exception ex) {
                                    ex.fillInStackTrace();
                                    controller.setAppointmentsDataSet(null);
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null) {
                                            dialog.cancel();
                                        }
                                    }
                                });
                            } else {
                                controller.setAppointmentsDataSet(null);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null) {
                                            dialog.cancel();
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                }else{
                    controller.setAppointmentsDataSet(null);
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            } else {
                controller.setAppointmentsDataSet(null);
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        } else {
            controller.setAppointmentsDataSet(null);
            if (dialog != null) {
                dialog.cancel();
            }
        }
    }

    public void getContactsDetails() {
        if (controller.getMy_Place_Details() != null) {
            if (Utils.isNetworkAvailable(MyAppointments.this)) {
                dialog = Utils.getProgress(MyAppointments.this);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                        byte[] message = sample.getBytes();
                        String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                        String contactUsResult = controller.webApiCall().getDataWithHeaders(Common.newMyPlaceAppointment, encoded, controller.getMy_Place_Details().getJobNumber());
                        if (contactUsResult.length() > 2) {
                            try {

                                ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>();
                                JSONArray jobJsonArray = new JSONArray(contactUsResult);
                                for (int i = 0; i < jobJsonArray.length(); i++) {
                                    MyAppointmentsDataSet appointmentsDataSet = new MyAppointmentsDataSet(jobJsonArray.get(i).toString(), false);
                                    myAppointmentsDataSets.add(appointmentsDataSet);
                                }
                                controller.setMyAppointmentsDataSets(myAppointmentsDataSets);
                            } catch (Exception ex) {
                                ex.fillInStackTrace();
                                controller.setAppointmentsDataSet(null);
                            }
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null) {
                                        dialog.cancel();
                                    }
                                }
                            });
                        } else {
                            controller.setAppointmentsDataSet(null);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null) {
                                        dialog.cancel();
                                    }
                                }
                            });
                        }
                    }
                }).start();





                *//*Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String urll = Common.myPlaceBaseUrlVic;
                        String result = controller.webApiCall().postData_to_MyPlace(urll + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                        if (result.equalsIgnoreCase("true")) {
                            String loginResult = controller.webApiCall().getData_From_MyPlace(urll + "" + Common.MyPlaceGetUserDetailsUrl);
                            try {
                                JSONObject jsonObject = new JSONObject(loginResult);
                                String constructionID = jsonObject.isNull("ConstructionID") ? "" : jsonObject.getString("ConstructionID");
                                String officeTicket = jsonObject.isNull("OfficeID") ? "" : jsonObject.getString("OfficeID");
                                if (constructionID.length() > 0) {
                                    String contactUsResult = controller.webApiCall().getData_From_MyPlace("https://www.burbank.com.au/victoria/myplace/api/progress/GetAdminProgress?constructionTicketID=" + constructionID
                                            + "&officeTicketID=" + officeTicket);
                                    if (contactUsResult.length() > 2) {
                                        try {

                                            ArrayList<MyAppointmentsDataSet> myAppointmentsDataSets = new ArrayList<MyAppointmentsDataSet>();
                                            JSONArray jobJsonArray = new JSONArray(contactUsResult);
                                            for (int i = 0; i < jobJsonArray.length(); i++) {
                                                MyAppointmentsDataSet appointmentsDataSet = new MyAppointmentsDataSet(jobJsonArray.get(i).toString(), false);
                                                myAppointmentsDataSets.add(appointmentsDataSet);
                                            }
                                            controller.setMyAppointmentsDataSets(myAppointmentsDataSets);
                                            //initializeUIElements();
                                        } catch (Exception ex) {
                                            ex.fillInStackTrace();
                                            controller.setAppointmentsDataSet(null);
                                        }
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialog != null) {
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    } else {
                                        controller.setAppointmentsDataSet(null);
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialog != null) {
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    controller.setAppointmentsDataSet(null);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog != null) {
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                controller.setAppointmentsDataSet(null);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null) {
                                            dialog.cancel();
                                        }
                                    }
                                });

                            }
                        } else {
                            controller.setContactsModelDetails(null);
                            apiCallStarted = false;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                }
                            });
                        }
                    }
                });
                t.start();*//*
            } else {
                apiCallStarted = false;
                controller.setContactsModelDetails(null);
            }
        } else {
            apiCallStarted = false;
            controller.setContactsModelDetails(null);
        }

    }*/
}

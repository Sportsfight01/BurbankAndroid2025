package com.dmss.burbankappold.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.AppController;
import common.Common;
import common.CustomHeadingTextView;
import common.TransparentProgressDialog;
import common.Utils;

import com.dmss.burbankappold.R;

import interfaces.WebApiJobDetailsResponseCallBack;
import models.MyPlaceCredentials;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class Contract extends Fragment implements WebApiJobDetailsResponseCallBack {
    String constructionId = "", officeTicketId = "";
    CustomHeadingTextView jobNumberTextView, jobAddressTextView, homeStyleTextView, facadeStyleTextView,
            contractStatusTextView, contractValueTextView, supervisorTextView, liasonTextView,
            siteStartDateTextView, clientNameTextView, homePhoneNumberTextView, mobilePhoneNumberTextView,
            workPhoneNumberTextView, emailAddressTextView;
    AppController controller;
    TransparentProgressDialog dialog;
    String contractResult = null;
    View rootView;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);
        actionBarSettings();
        initializeUIElements();
        callWebApiToGetAllHomesList();
    }*/

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_contract, null);
        initializeUIElements();
        MyPlaceCredentials myPlaceCredentials = controller.getMy_Place_Details();
        callForNewApi();

        return rootView;
    }

    public void initializeUIElements() {
        TextView firsttext = rootView.findViewById(R.id.firstText);
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);
        firsttext.setText(" Details");
        ImageView backImage = rootView.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        controller = (AppController) getActivity().getApplicationContext();
        jobNumberTextView = (CustomHeadingTextView) rootView.findViewById(R.id.jobNumberTextView);
        jobNumberTextView.setHeadText("Job Number");
        jobNumberTextView.setText("");

        jobAddressTextView = (CustomHeadingTextView) rootView.findViewById(R.id.jobAddressTextView);
        jobAddressTextView.setHeadText("Home Address");
        jobAddressTextView.setText("");

        homeStyleTextView = (CustomHeadingTextView) rootView.findViewById(R.id.homeStyleTextView);
        homeStyleTextView.setHeadText("Home Style");
        homeStyleTextView.setText("");

        facadeStyleTextView = (CustomHeadingTextView) rootView.findViewById(R.id.facadeStyleTextView);
        facadeStyleTextView.setHeadText("Facade Style");
        facadeStyleTextView.setText("");

        contractStatusTextView = (CustomHeadingTextView) rootView.findViewById(R.id.contractStatusTextView);
        contractStatusTextView.setHeadText("Home Status");
        contractStatusTextView.setText("");

        contractValueTextView = (CustomHeadingTextView) rootView.findViewById(R.id.contractValueTextView);
        contractValueTextView.setHeadText("Home Value");
        contractValueTextView.setText("");

        supervisorTextView = (CustomHeadingTextView) rootView.findViewById(R.id.supervisorTextView);
        supervisorTextView.setHeadText("Site Supervisor");
        supervisorTextView.setText("");

        liasonTextView = (CustomHeadingTextView) rootView.findViewById(R.id.liasonTextView);
        liasonTextView.setHeadText("New Home Coordinator");
        liasonTextView.setText("");

        siteStartDateTextView = (CustomHeadingTextView) rootView.findViewById(R.id.siteStartDateTextView);
        siteStartDateTextView.setHeadText("Site Start Date");
        siteStartDateTextView.setText("");


        clientNameTextView = (CustomHeadingTextView) rootView.findViewById(R.id.clientNameTextView);
        clientNameTextView.setHeadText("Client Name");
        clientNameTextView.setText("");

        homePhoneNumberTextView = (CustomHeadingTextView) rootView.findViewById(R.id.homePhoneNumberTextView);
        homePhoneNumberTextView.setHeadText("Home Phone Number");
        homePhoneNumberTextView.setText("");

        mobilePhoneNumberTextView = (CustomHeadingTextView) rootView.findViewById(R.id.mobilePhoneNumberTextView);
        mobilePhoneNumberTextView.setHeadText("Mobile Phone Number");
        mobilePhoneNumberTextView.setText("");

        workPhoneNumberTextView = (CustomHeadingTextView) rootView.findViewById(R.id.workPhoneNumberTextView);
        workPhoneNumberTextView.setHeadText("Work Phone Number");
        workPhoneNumberTextView.setText("");

        emailAddressTextView = (CustomHeadingTextView) rootView.findViewById(R.id.emailAddressTextView);
        emailAddressTextView.setHeadText("Email Address");
        emailAddressTextView.setText("");

    }

    /*public void actionBarSettings() {
        android.support.v7.app.ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        view.setBackgroundResource(R.color.black);
        final ImageView backImageView = (ImageView) view.findViewById(R.id.backImageView);
        backImageView.setVisibility(View.VISIBLE);
        final ImageView burbankLogoImageView = (ImageView) view.findViewById(R.id.burbankLogoImageView);
        burbankLogoImageView.setVisibility(View.GONE);
        TextView actionBarHeadingTextView = (TextView) view.findViewById(R.id.actionBarHeadingTextView);
        actionBarHeadingTextView.setVisibility(View.VISIBLE);
        actionBarHeadingTextView.setText("Contract");
        actionBar.setCustomView(view, new android.support.v7.app.ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM | android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME);

       */

    @Override
    public void onJobDetailsSuccessResult(final String result) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        jobNumberTextView.setText(jsonObject.isNull("job") ? "" : jsonObject.getString("job"));
                        jobAddressTextView.setText(jsonObject.isNull("lotaddress") ? "" : jsonObject.getString("lotaddress"));
                        homeStyleTextView.setText(jsonObject.isNull("housetype") ? "" : jsonObject.getString("housetype"));
                        facadeStyleTextView.setText(jsonObject.isNull("facade") ? "" : jsonObject.getString("facade"));
                        contractStatusTextView.setText(jsonObject.isNull("jobstatus") ? "" : jsonObject.getString("jobstatus"));
                        String value = jsonObject.isNull("contractvalue") ? "" : jsonObject.getString("contractvalue");
                        if (value.length() > 0) {
                            contractValueTextView.setText("$ " + Utils.getTwoDecimalValue(Double.parseDouble(value)));
                        } else {
                            contractValueTextView.setText("$ " + value);
                        }
                        String supervisor = jsonObject.isNull("supervisor") ? "--" : jsonObject.getString("supervisor");
                        supervisorTextView.setText(supervisor);
                        liasonTextView.setText(jsonObject.isNull("clientliaison") ? "" : jsonObject.getString("clientliaison"));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        SimpleDateFormat sdfString = new SimpleDateFormat("EEEE, MMMM d, yyyy");
                        String dateString = jsonObject.isNull("sitestartdate") ? "" : jsonObject.getString("sitestartdate");
                        if (supervisor != null && supervisor.length() > 2 && !supervisor.equalsIgnoreCase("--")) {
                            try {
                                Date complionDate = sdf.parse(dateString);
                                dateString = sdfString.format(complionDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                dateString = "";
                            }
                            siteStartDateTextView.setText(dateString);
                        } else {
                            siteStartDateTextView.setText("--");
                        }
                        clientNameTextView.setText(jsonObject.isNull("clienttitle") ? (jsonObject.isNull("contactname") ? "" : jsonObject.getString("contactname")) : jsonObject.getString("clienttitle"));
                        homePhoneNumberTextView.setText(jsonObject.isNull("homephone") ? "" : jsonObject.getString("homephone"));
                        mobilePhoneNumberTextView.setText(jsonObject.isNull("homephone1") ? "" : jsonObject.getString("homephone1"));
                        workPhoneNumberTextView.setText(jsonObject.isNull("workphone") ? "" : jsonObject.getString("workphone"));
                        emailAddressTextView.setText(jsonObject.isNull("contactemail") ? "" : jsonObject.getString("contactemail"));// scale_rotate

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            }
        });


    }

    @Override
    public void onJobDetailsErrorResult(String error) {

    }
/*
    @Override
    public void onErrorResult(String error) {
        String test = error;
        Utils.showToast(ContractActivity.this, error, Common.errorCase);
        if (dialog != null) {
            dialog.cancel();
        }
    }*/

    /**
     * Method used to call the web API to get the data from server
     **/
    /*private void callWebApiToGetAllHomesList() {
     *//**
     * Checking whether the network is available or not
     *//*
        if (controller.getConstructionId().length() > 0) {
            constructionId = controller.getConstructionId();
        }
        if (controller.getOfficeId().length() > 0) {
            officeTicketId = controller.getOfficeId();
        }
        if (Utils.isNetworkAvailable(getActivity())) {
            */

    /**
     * Checking whether to call the new homes API or Favourite API
     *//*
            dialog = Utils.getProgress(getActivity());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    contractResult = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceContractUrl + "constructionTicketID=" + constructionId + "&officeTicketID=" + officeTicketId + "&region=" + controller.getMy_Place_Details().getRegion() + "");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onSuccessResult(contractResult);
                        }
                    });

                }
            }).start();
        }
    }*/
    public void callForNewApi() {
        dialog = Utils.getProgress(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                byte[] message = sample.getBytes();
                String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                //contractResult = controller.webApiCall().getDataWithHeaders(Common.newMyPlaceProgressDetails, encoded, controller.getMy_Place_Details().getJobNumber());
                //contractResult = controller.webApiCall().getDataNewMyPlace(Common.newMyPlaceProgressDetails, controller.getMy_Place_Details());
                controller.webApiCall().getDataNewMyPlace(Common.newMyPlaceProgressDetails, controller.getMy_Place_Details(), Contract.this);
                /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccessResult(contractResult);
                    }
                });*/

            }
        }).start();
    }


}

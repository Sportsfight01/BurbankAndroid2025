package com.dmss.burbankappold.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.FinaceListAdapter;
import common.AppController;
import common.Common;
import common.TransparentProgressDialog;
import common.Utils;

import com.dmss.burbankappold.R;

import models.FinanceDataSet;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class Finance extends Fragment {

    ListView list;
    LinearLayout mLLyFinanceDetails;
    public static ArrayList<FinanceDataSet> listarray = new ArrayList<>();
    TextView jobNumber, contractValue;
    public static String response = null;
    TransparentProgressDialog pd = null;
    AppController controller;
    View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_my_documents, null);
        initializeUI();
        return rootView;
    }


    public void initializeUI() {
        TextView firstText = rootView.findViewById(R.id.firstText);
        firstText.setText("Finance");
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);

        ImageView backImage = rootView.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        controller = (AppController) getActivity().getApplicationContext();
        list = (ListView) rootView.findViewById(R.id.lVMyDocumentsId);
        mLLyFinanceDetails = (LinearLayout) rootView.findViewById(R.id.mLLyFinanceDetails);
        mLLyFinanceDetails.setVisibility(View.VISIBLE);
        jobNumber = (TextView) rootView.findViewById(R.id.mTxtJobNumber);
        contractValue = (TextView) rootView.findViewById(R.id.mTxtContractValue);
        jobNumber.setText(controller.getMy_Place_Details().getJobNumber());
        FinanceDataSet fds = controller.getFinanceDataSet();
        if(fds!= null){
            parseJson(fds);
        }
    }

    public void callApi() {

        pd = Utils.getProgress(getActivity());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                if (result.equalsIgnoreCase("true")) {

                    String result2 = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl);
                    try{
                        JSONObject jsonObject = new JSONObject(result);
                        controller.setConstructionId(jsonObject.getString("ConstructionID"));
                        controller.setOfficeId(jsonObject.getString("OfficeID"));
                    } catch (JSONException ex) {
                        ex.fillInStackTrace();
                    }

                    String s = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.myPlaceGetFinance + "" + controller.getJobNumber());
                    onSuccessResult(s);
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (pd != null) {
                                pd.cancel();
                            }
                        }
                    });
                }
                /*String val = controller.getJobNumber();

                if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
                    String s = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.myPlaceGetFinance + "" + val);
                    onSuccessResult(s);
                } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {
                    String s = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.myPlaceGetFinance + "" + val);
                    onSuccessResult(s);
                }else{
                    String s = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.myPlaceGetFinance + "" + val);
                    onSuccessResult(s);
                }*/
            }
        });
        t.start();
    }

    public String getMyPlaceLoginJson() {
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

    public void parseJson(final FinanceDataSet fds) {
        //final FinanceDataSet fds = getFinanceData(json);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                jobNumber.setText(controller.getMy_Place_Details().getJobNumber());
                String contractV = fds.getContractAmount();
                Double value = Double.parseDouble(contractV);
                contractValue.setText("$" + String.format("%,.2f", value));
                //  contractValue.setText("$"+fds.getContractAmount());
                mLLyFinanceDetails.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                list.setAdapter(new FinaceListAdapter(getActivity(), fds.getContract(), fds.getContractAmount()));
                if (pd != null) {
                    pd.cancel();
                }
            }
        });
    }


    public void onSuccessResult(String s) {
        if ((s != null) && (s.length() > 0)) {
            response = s;
            //parseJson(s);
        }
    }


    public void onErrorResult(String error) {

    }


}

package com.dmss.burbankappold.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import common.AppController;
import common.Common;
import common.ProgressWheel;
import common.TransparentProgressDialog;
import common.Utils;

import com.dmss.burbankappold.R;

import interfaces.FragmentCallBack;
import interfaces.WebApiJobDetailsResponseCallBack;
import models.AdministartionDataForVic;
import models.AdministrationDataForQldOrSA;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class ProgressOverView extends Fragment implements WebApiJobDetailsResponseCallBack {
    /*************
     * Declaration of variables
     **************/
    TextView tv, mTxtadmin, mTxtframe, mTxtlock, mTxtFixing, mTxtCompletion, mTxtHandOver, progressTextView1, progressTextView2;
    TextView mTxtSiteStatus, tv1, tv2, noDataTextView;

    ProgressWheel progressBaraDMIN, progressBarfRAME, progressBarLockUp, progressBarFixing, progressBarCompletion, progressBarHandOver;
    int pStatus = 0;
    private Handler handler = new Handler();
    LinearLayout mLLyAdministartion, mLLyFrameStage, mLLYLockUp, mLLYFixingStage, mLLyCompletion, mLLYHandOverStage;
    Button mLLyAdministartionButton, mLLyFrameStageButton, mLLYLockUpButton, mLLYFixingStageButton, mLLyCompletionButton;
    private static final int MENU_ADD = Menu.FIRST;
    private static final int MENU_GAAP = Menu.FIRST + 1;
    private static final int MENU_MAP = Menu.FIRST + 2;
    private static final int MENU_GAAP1 = Menu.FIRST + 3;
    public static ArrayList<AdministartionDataForVic> VICadminData = new ArrayList<AdministartionDataForVic>();
    public static ArrayList<AdministartionDataForVic> VICbaseStageData = new ArrayList<AdministartionDataForVic>();
    public static ArrayList<AdministartionDataForVic> VICframeStageData = new ArrayList<AdministartionDataForVic>();
    public static ArrayList<AdministartionDataForVic> VIClockUpStageData = new ArrayList<AdministartionDataForVic>();
    public static ArrayList<AdministartionDataForVic> VICmiscellaneousData = new ArrayList<AdministartionDataForVic>();
    public static ArrayList<AdministartionDataForVic> VICcompletionData = new ArrayList<AdministartionDataForVic>();
    public static ArrayList<AdministartionDataForVic> VICfixoutData = new ArrayList<AdministartionDataForVic>();
    public static ArrayList<AdministartionDataForVic> VIChandOverData = new ArrayList<AdministartionDataForVic>();


    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAadminData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAframeStageData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAlockUpStageData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAcompletionData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAfixoutData = new ArrayList<AdministrationDataForQldOrSA>();


    public static int adminCompletedTask = 0, baseStageCompletedTask = 0, frameStageCompletedTask = 0, lockUpCompletedTask = 0, miscellaneousCompletedTask = 0, completionTask = 0, fixedOutCompletedTask = 0, handOverCompletedTask = 0;
    public static int lockpercent = 0, adminpercent = 0, framepercent = 0, fixedOutpercent = 0, completionpercent = 0, miscellanouspercent = 0, avgPercent = 0;
    public static boolean isDataFetched = false;
    AppController controller;
    String constructionId, officeId, surveyNumber;
    TransparentProgressDialog pd = null;
    View rootView;
    LinearLayout noDataLayout;
    ScrollView dataLayout;

    FragmentCallBack fragmentCallBack;
    ProgressWheel customProgressWheel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = (AppController) getActivity().getApplicationContext();
        fragmentCallBack = (FragmentCallBack) getActivity();
        controller.getAnalytics().setScreen(getActivity(), "MyProgress_Screen");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_progress_status, null);
        initializeUI();
        pd = Utils.getProgress(getActivity());
        String result = controller.getLoadedProgressResult();
        if (result != null && result.length() > 0) {
            onJobDetailsSuccessResult(result);
        } else {
            callForNewApi();
        }


        return rootView;
    }

    public void callForNewApi() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String sample = controller.getMy_Place_Details().getUsername().trim() + ":" + controller.getMy_Place_Details().getPassword().trim();
                byte[] message = sample.getBytes();
                String encoded = Base64.encodeToString(message, Base64.NO_WRAP);
                //String val = controller.webApiCall().getDataWithHeaders(Common.newMyPlaceProgress, encoded, controller.getMy_Place_Details().getJobNumber());
                controller.webApiCall().getDataNewMyPlace(Common.newMyPlaceProgress, controller.getMy_Place_Details(), ProgressOverView.this);
                //String result = controller.webApiCall().getDataNewMyPlace(Common.newMyPlaceProgress, controller.getMy_Place_Details());
                //onSuccessResult(result);
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


    private void initializeUI() {
        TextView firstText = rootView.findViewById(R.id.firstText);
        TextView secondText = rootView.findViewById(R.id.secondText);
        firstText.setText("MyHome");
        secondText.setText("Progress");

        ImageView back_image = rootView.findViewById(R.id.back_image);
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        mTxtSiteStatus = new TextView(getActivity());
        mTxtSiteStatus.setTypeface(controller.getTypefaceProximoNova());
        tv1 = new TextView(getActivity());
        tv1.setTypeface(controller.getTypefaceProximoNova());
        tv2 = new TextView(getActivity());
        tv2.setTypeface(controller.getTypefaceProximoNova());
        tv = (TextView) rootView.findViewById(R.id.textView1);
        tv.setTypeface(controller.getTypefaceProximoNova());
        mTxtadmin = (TextView) rootView.findViewById(R.id.mTxtadmin);
        mTxtadmin.setTypeface(controller.getTypefaceProximoNova());
        mTxtframe = (TextView) rootView.findViewById(R.id.mTxtframe);
        mTxtframe.setTypeface(controller.getTypefaceProximoNova());
        mTxtlock = (TextView) rootView.findViewById(R.id.mTxtlock);
        mTxtlock.setTypeface(controller.getTypefaceProximoNova());
        mTxtFixing = (TextView) rootView.findViewById(R.id.mTxtFixing);
        mTxtFixing.setTypeface(controller.getTypefaceProximoNova());
        mTxtCompletion = (TextView) rootView.findViewById(R.id.mTxtCompletion);
        mTxtCompletion.setTypeface(controller.getTypefaceProximoNova());
        progressTextView1 = (TextView) rootView.findViewById(R.id.mTxtCompletion);
        progressTextView1.setTypeface(controller.getTypefaceProximoNova());
        progressTextView2 = (TextView) rootView.findViewById(R.id.mTxtCompletion);
        progressTextView2.setTypeface(controller.getTypefaceProximoNova());
        mLLyAdministartionButton = (Button) rootView.findViewById(R.id.administartionBreakdown);
        mLLyFrameStageButton = (Button) rootView.findViewById(R.id.frameBreakdown);
        mLLYLockUpButton = (Button) rootView.findViewById(R.id.lookupBreakdown);
        mLLYFixingStageButton = (Button) rootView.findViewById(R.id.fixingBreakdown);
        mLLyCompletionButton = (Button) rootView.findViewById(R.id.finishingBreakdown);
        mLLyAdministartionButton.setTypeface(controller.getTypeface());
        mLLyFrameStageButton.setTypeface(controller.getTypeface());
        mLLYLockUpButton.setTypeface(controller.getTypeface());
        mLLYFixingStageButton.setTypeface(controller.getTypeface());
        mLLyCompletionButton.setTypeface(controller.getTypeface());
        noDataLayout = (LinearLayout) rootView.findViewById(R.id.noDataLayout);
        dataLayout = (ScrollView) rootView.findViewById(R.id.dataLayout);
        noDataTextView = (TextView) rootView.findViewById(R.id.noDataTextView);
        noDataTextView.setTypeface(controller.getTypefaceProximoNova());

        mLLyAdministartionButton.setText("Break down");
        mLLyFrameStageButton.setText("Break down");
        mLLYLockUpButton.setText("Break down");
        mLLYFixingStageButton.setText("Break down");
        mLLyCompletionButton.setText("Break down");
        mLLyAdministartionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setProgressSelectedPosition(0);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                controller.getAnalytics().myProgressAdministrationStageBreakDownButtonTouchEvent();
                //Intent i = new Intent(getActivity(), ProgressDetails.class);
                //startActivity(i);
            }
        });
        mLLyFrameStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(1);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                controller.getAnalytics().myProgressFrameStageBreakDownButtonTouchEvent();
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });
        mLLYLockUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(2);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                controller.getAnalytics().myProgressLockupStageBreakDownButtonTouchEvent();
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });
        mLLYFixingStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(3);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                controller.getAnalytics().myProgressFixoutStageBreakDownButtonTouchEvent();
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });
        mLLyCompletionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(4);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                controller.getAnalytics().myProgressFinishingStageBreakDownButtonTouchEvent();
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });
        /*Intializing Progress bars */
        customProgressWheel = (ProgressWheel) rootView.findViewById(R.id.wheelprogress);
        progressBaraDMIN = (ProgressWheel) rootView.findViewById(R.id.progressBaraDMIN);
        progressBarfRAME = (ProgressWheel) rootView.findViewById(R.id.progressBarfRAME);
        progressBarLockUp = (ProgressWheel) rootView.findViewById(R.id.progressBarLockUp);
        progressBarFixing = (ProgressWheel) rootView.findViewById(R.id.progressBarFixing);
        progressBarCompletion = (ProgressWheel) rootView.findViewById(R.id.progressBarCompletion);
        mLLyAdministartion = (LinearLayout) rootView.findViewById(R.id.mLLyAdministartion);
        mLLyAdministartion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setProgressSelectedPosition(0);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                //Intent i = new Intent(getActivity(), ProgressDetails.class);
                //startActivity(i);
            }
        });

        mLLyFrameStage = (LinearLayout) rootView.findViewById(R.id.mLLyFrameStage);
        mLLyFrameStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(1);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });
        mLLYLockUp = (LinearLayout) rootView.findViewById(R.id.mLLYLockUp);
        mLLYLockUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(2);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });
        mLLYFixingStage = (LinearLayout) rootView.findViewById(R.id.mLLYFixingStage);
        mLLYFixingStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(3);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });
        mLLyCompletion = (LinearLayout) rootView.findViewById(R.id.mLLyCompletion);
        mLLyCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                controller.setProgressSelectedPosition(4);
                fragmentCallBack.fragmentCallBack(new ProgressDetails());
                /*Intent i = new Intent(getActivity(), ProgressDetails.class);
                startActivity(i);*/
            }
        });

    }


    public static boolean isTaskCompeted(String date) {

        try {
            Date currentdate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date complionDate = sdf.parse(date);
            if (complionDate.before(currentdate)) {
                return true;
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return false;
    }

    public void UpdateProgress() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (adminpercent > 0 || framepercent > 0 || lockpercent > 0 || fixedOutpercent > 0 || completionpercent > 0) {
                    noDataLayout.setVisibility(View.GONE);
                    dataLayout.setVisibility(View.VISIBLE);

                    int adminpercentProgress = (int) (adminpercent * 3.6);
                    progressBaraDMIN.setPercentage(adminpercentProgress);

                    mTxtadmin.setText(String.valueOf(adminpercent) + "%");

                    int framepercentProgress = (int) (framepercent * 3.6);
                    progressBarfRAME.setPercentage(framepercentProgress);
                    mTxtframe.setText(String.valueOf(framepercent) + "%");

                    int lockpercentProgress = (int) (lockpercent * 3.6);
                    progressBarLockUp.setPercentage(lockpercentProgress);
                    mTxtlock.setText(String.valueOf(lockpercent) + "%");

                    int fixedOutpercentProgress = (int) (fixedOutpercent * 3.6);
                    progressBarFixing.setPercentage(fixedOutpercentProgress);

                    mTxtFixing.setText(String.valueOf(fixedOutpercent) + "%");
                    int completionpercentProgress = (int) (completionpercent * 3.6);
                    progressBarCompletion.setPercentage(completionpercentProgress);
                    mTxtCompletion.setText(String.valueOf(completionpercent) + "%");


                    int progress = (int) (avgPercent * 3.6);

                    customProgressWheel.setPercentage(progress);

                    tv.setText(String.valueOf(avgPercent) + "%");
                } else {
                    noDataLayout.setVisibility(View.VISIBLE);
                    dataLayout.setVisibility(View.GONE);
                }

                isDataFetched = true;
                if (pd != null) {
                    pd.cancel();
                }
            }
        });
    }

    @Override
    public void onJobDetailsSuccessResult(String s) {
        Log.e("Progress Data ", s);
        VICadminData = new ArrayList<AdministartionDataForVic>();
        VICbaseStageData = new ArrayList<AdministartionDataForVic>();
        VICframeStageData = new ArrayList<AdministartionDataForVic>();
        VIClockUpStageData = new ArrayList<AdministartionDataForVic>();
        VICmiscellaneousData = new ArrayList<AdministartionDataForVic>();
        VICcompletionData = new ArrayList<AdministartionDataForVic>();
        VICfixoutData = new ArrayList<AdministartionDataForVic>();
        VIChandOverData = new ArrayList<AdministartionDataForVic>();

        QldOrSAadminData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAframeStageData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAlockUpStageData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAcompletionData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAfixoutData = new ArrayList<AdministrationDataForQldOrSA>();
        if ((s != null) && (s.length() > 0)) {
            controller.setLoadedProgressResult(s);
            adminCompletedTask = 0;
            baseStageCompletedTask = 0;
            frameStageCompletedTask = 0;
            lockUpCompletedTask = 0;
            miscellaneousCompletedTask = 0;
            completionTask = 0;
            fixedOutCompletedTask = 0;
            handOverCompletedTask = 0;
            lockpercent = 0;
            adminpercent = 0;
            framepercent = 0;
            fixedOutpercent = 0;
            completionpercent = 0;
            miscellanouspercent = 0;
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {

                    AdministrationDataForQldOrSA data = new AdministrationDataForQldOrSA(jsonArray.getJSONObject(i).toString());
                    if (data.getPhaseCode().equalsIgnoreCase("Presite")) {
                        if (data.getStatus().equalsIgnoreCase("Completed")) {
                            adminCompletedTask = adminCompletedTask + 1;
                        }
                        QldOrSAadminData.add(data);
                    } else {
                        if (data.getStageName().equalsIgnoreCase("Frame Stage")) {
                            if (data.getStatus().equalsIgnoreCase("Completed")) {
                                frameStageCompletedTask = frameStageCompletedTask + 1;
                            }
                            QldOrSAframeStageData.add(data);
                        } else if (data.getStageName().equalsIgnoreCase("Lockup Stage")) {
                            if (data.getStatus().equalsIgnoreCase("Completed")) {
                                lockUpCompletedTask = lockUpCompletedTask + 1;
                            }
                            QldOrSAlockUpStageData.add(data);
                        } else if (data.getStageName().equalsIgnoreCase("Fixout Stage")) {
                            if (data.getStatus().equalsIgnoreCase("Completed")) {
                                fixedOutCompletedTask = fixedOutCompletedTask + 1;
                            }
                            QldOrSAfixoutData.add(data);
                        } else if (data.getStageName().equalsIgnoreCase("Completion") || data.getStageName().equalsIgnoreCase("Handover")) {
                            if (data.getStatus().equalsIgnoreCase("Completed")) {
                                completionTask = completionTask + 1;
                            }
                            QldOrSAcompletionData.add(data);
                        }
                    }

                }

                if (adminCompletedTask != 0) {
                    adminpercent = (100 * adminCompletedTask / QldOrSAadminData.size());
                }
                if (frameStageCompletedTask != 0) {
                    framepercent = (100 * frameStageCompletedTask / QldOrSAframeStageData.size());
                }
                if (lockUpCompletedTask != 0) {
                    lockpercent = (100 * lockUpCompletedTask / QldOrSAlockUpStageData.size());
                }
                if (fixedOutCompletedTask != 0) {
                    fixedOutpercent = ((100 * fixedOutCompletedTask / QldOrSAfixoutData.size()));
                }
                if (completionTask != 0) {
                    completionpercent = (100 * completionTask / QldOrSAcompletionData.size());
                }

                //}
                int finalCountTotal = adminpercent + framepercent + lockpercent + fixedOutpercent + completionpercent;
                avgPercent = (finalCountTotal / 5);
                int avg = finalCountTotal % 5;
                if (avg > 2.5) {
                    avgPercent = avgPercent + 1;
                }
                UpdateProgress();
            } catch (JSONException ex) {
                ex.fillInStackTrace();
                noDataLayout.setVisibility(View.VISIBLE);
                dataLayout.setVisibility(View.GONE);
                if (pd != null) {
                    pd.cancel();
                }
            }
        }
        if (pd != null) {
            pd.cancel();
        }
    }

    @Override
    public void onJobDetailsErrorResult(String error) {

    }

}



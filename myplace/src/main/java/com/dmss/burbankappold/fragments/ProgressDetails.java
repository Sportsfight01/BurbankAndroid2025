package com.dmss.burbankappold.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;

import adapters.ProgressList_Adapter;
import common.AppController;

import com.dmss.burbankappold.DashBoard;
import com.dmss.burbankappold.R;

import common.ProgressWheel;
import interfaces.FragmentCallBack;
import models.AdministrationDataForQldOrSA;
import models.NotificationModel;

/**
 * Created by jaya.krishna on 05-06-2017.
 */

public class ProgressDetails extends Fragment {

    /*************
     * Declaration of variables
     **************/
    ProgressList_Adapter progressListAdapterVIC, progressListAdapterQldOrSa;
    ListView mList_adminstartion;
    ProgressWheel progressBarAdminstration;
    //View mViewbottom;
    int position;
    TextView firstText;
    Button nextStage, previoustage;
    NestedScrollView progressDetailsScrollView;
    TextView textViewProgress, mTxtPrgCompleted, mTxtProgressTitle;
    String[] IndividualHeading = {"Administration ", "Frame stage ", "Lock up stage ", "Fixing stage ", "Finishing stage ", "Miscellaneous"};
    //TextView actionBarHeadingTextView;
    View rootView;
    AppController controller;
    FragmentCallBack fragmentCallBack;
    int lockPercent = 0, adminPercent = 0, framePercent = 0, fixedOutPercent = 0, completionPercent = 0;
    ArrayList<AdministrationDataForQldOrSA> QldOrSAadminData = new ArrayList<AdministrationDataForQldOrSA>();
    ArrayList<AdministrationDataForQldOrSA> QldOrSAframeStageData = new ArrayList<AdministrationDataForQldOrSA>();
    ArrayList<AdministrationDataForQldOrSA> QldOrSAlockUpStageData = new ArrayList<AdministrationDataForQldOrSA>();
    ArrayList<AdministrationDataForQldOrSA> QldOrSAcompletionData = new ArrayList<AdministrationDataForQldOrSA>();
    ArrayList<AdministrationDataForQldOrSA> QldOrSAfixoutData = new ArrayList<AdministrationDataForQldOrSA>();
    ArrayList<AdministrationDataForQldOrSA> QldOrSAhandOverData = new ArrayList<AdministrationDataForQldOrSA>();
    Space spaceLeft, spaceRight;

    Bundle bundle;
    NotificationModel notificationModel;

    /**
     * Override method which called when the activity is created and sets an xml layout as content view.
     *
     * @param savedInstanceState A variable of type Bundle.
     **/
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_progressbar);
        actionBarSettings();
        initializeUI();
    }*/
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_detail_progressbar, null);
        controller = (AppController) getActivity().getApplicationContext();
        fragmentCallBack = (FragmentCallBack) getActivity();
        position = controller.getProgressSelectedPosition();
        fetchDataFromLastClass();
        initializeUI();
        return rootView;
    }


    /***
     * Initializing the UI widgets based on their ids given in xml. and setting font to them if they are text.
     */
    private void initializeUI() {

        ImageView back_image = rootView.findViewById(R.id.back_image);
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        firstText = rootView.findViewById(R.id.firstText);
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);
        notificationModel = new NotificationModel();
        bundle = getArguments();
        if (bundle != null) {
            if (bundle.getSerializable("NotificationModel") != null) {
                notificationModel = (NotificationModel) bundle.getSerializable("NotificationModel");
            }
        }
        firstText.setText(IndividualHeading[position]);

        fragmentCallBack.actionbarTextChange(IndividualHeading[position]);
        //actionBarHeadingTextView.setText(Title[position]);
        progressDetailsScrollView = (NestedScrollView) rootView.findViewById(R.id.progressDetailsScrollView);
        nextStage = (Button) rootView.findViewById(R.id.nextStage);
        previoustage = (Button) rootView.findViewById(R.id.previousStage);
        spaceLeft = (Space) rootView.findViewById(R.id.spaceLeft);
        spaceRight = (Space) rootView.findViewById(R.id.spaceRight);
        progressBarAdminstration = (ProgressWheel) rootView.findViewById(R.id.progressBarAdminstration);
        textViewProgress = (TextView) rootView.findViewById(R.id.textViewProgress);
        textViewProgress.setTypeface(controller.getTypefaceProximoNova());
        mTxtPrgCompleted = (TextView) rootView.findViewById(R.id.mTxtPrgCompleted);
        mTxtPrgCompleted.setTypeface(controller.getTypefaceProximoNova());
        mTxtProgressTitle = (TextView) rootView.findViewById(R.id.mTxtProgressTitle);
        mTxtProgressTitle.setTypeface(controller.getTypefaceProximoNova());
        mTxtProgressTitle.setText(IndividualHeading[position]);
        //mViewbottom = (View) rootView.findViewById(R.id.mViewbottom);
        setBackgroundElements();
        mList_adminstartion = (ListView) rootView.findViewById(R.id.mList_adminstartion);
        /*if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getDataVIC(position).size() + 2) * 105);
            mList_adminstartion.setLayoutParams(layoutParams);
            progressListAdapterVIC = new ProgressList_Adapter(getActivity(), getDataVIC(position));
            mList_adminstartion.setAdapter(progressListAdapterVIC);
        } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {*/
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getDataQldOrSa(position).size() + 5) * 105);
        mList_adminstartion.setLayoutParams(layoutParams);
        progressListAdapterQldOrSa = new ProgressList_Adapter(getActivity(), getDataQldOrSa(position), notificationModel, true);
        mList_adminstartion.setAdapter(progressListAdapterQldOrSa);
        mList_adminstartion.setBackgroundResource(R.drawable.rectangel_white_button);

        /*}*/

        previoustage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0) {
                    position = position - 1;
                    fragmentCallBack.actionbarTextChange(IndividualHeading[position]);
                    //actionBarHeadingTextView.setText(Title[position]);
                    mTxtProgressTitle.setText(IndividualHeading[position]);
                    /*if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getDataVIC(position).size() + 2) * 105);
                        mList_adminstartion.setLayoutParams(layoutParams);
                        progressListAdapterVIC = new ProgressList_Adapter(getActivity(), getDataVIC(position));
                        mList_adminstartion.setAdapter(progressListAdapterVIC);
                    } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {*/
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getDataQldOrSa(position).size() + 5) * 105);
                    mList_adminstartion.setLayoutParams(layoutParams);
                    progressListAdapterQldOrSa = new ProgressList_Adapter(getActivity(), getDataQldOrSa(position), notificationModel, true);
                    mList_adminstartion.setAdapter(progressListAdapterQldOrSa);

                    /*}*/
                    setBackgroundElements();
                } else {
                    Toast.makeText(getActivity(), "You are at First stage", Toast.LENGTH_SHORT).show();
                }
                manageButtons(position);
            }
        });
        manageButtons(position);
        nextStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < 4) {
                    position = position + 1;
                    fragmentCallBack.actionbarTextChange(IndividualHeading[position]);
                    //actionBarHeadingTextView.setText(Title[position]);
                    mTxtProgressTitle.setText(IndividualHeading[position]);
                    /*if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("VIC")) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getDataVIC(position).size() + 2) * 105);
                        mList_adminstartion.setLayoutParams(layoutParams);
                        progressListAdapterVIC = new ProgressList_Adapter(getActivity(), getDataVIC(position));
                        mList_adminstartion.setAdapter(progressListAdapterVIC);
                    } else if (controller.getMy_Place_Details().getRegion().equalsIgnoreCase("QLD") || controller.getMy_Place_Details().getRegion().equalsIgnoreCase("SA")) {*/
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (getDataQldOrSa(position).size() + 5) * 105);
                    mList_adminstartion.setLayoutParams(layoutParams);
                    progressListAdapterQldOrSa = new ProgressList_Adapter(getActivity(), getDataQldOrSa(position), notificationModel, true);
                    mList_adminstartion.setAdapter(progressListAdapterQldOrSa);
                    /*}*/

                    setBackgroundElements();
                } else {
                    Toast.makeText(getActivity(), "You are at Last stage", Toast.LENGTH_SHORT).show();
                }

                manageButtons(position);
            }
        });
        //  progressDetailsScrollView.scrollTo(0, progressDetailsScrollView.getTop());
    }

    /***
     * Setting background elements with respect to the defined classes .
     *
     * @params position
     * passing position of class to set the background elements to that class
     */
    public void setBackgroundElements() {
        firstText.setText(IndividualHeading[position]);
        switch (position) {
            case 0:

                int completionpercentProgress = (int) (adminPercent * 3.6);
                progressBarAdminstration.setPercentage(completionpercentProgress);
                textViewProgress.setText(String.valueOf(adminPercent) + "%");
                break;
            case 1:
                int framePercentProgress = (int) (framePercent * 3.6);
                progressBarAdminstration.setPercentage(framePercentProgress);
                textViewProgress.setText(String.valueOf(framePercent) + "%");
                break;

            case 2:
                int flockPercentProgress = (int) (lockPercent * 3.6);
                progressBarAdminstration.setPercentage(flockPercentProgress);
                textViewProgress.setText(String.valueOf(lockPercent) + "%");
                break;
            case 3:
                int fixedOutPercentProgress = (int) (fixedOutPercent * 3.6);
                progressBarAdminstration.setPercentage(fixedOutPercentProgress);
                textViewProgress.setText(String.valueOf(fixedOutPercent) + "%");
                break;
            case 4:
                int fcompletionPercentProgress = (int) (completionPercent * 3.6);
                progressBarAdminstration.setPercentage(fcompletionPercentProgress);
                textViewProgress.setText(String.valueOf(completionPercent) + "%");
                break;


        }
    }


    /*public ArrayList<AdministartionDataForVic> getDataVIC(int pos) {
        switch (position) {
            case 0:
                return ProgressOverView.VICadminData;
            case 1:
                return ProgressOverView.VICframeStageData;
            case 2:
                return ProgressOverView.VIClockUpStageData;
            case 3:
                return ProgressOverView.VICfixoutData;
            case 4:
                return ProgressOverView.VICcompletionData;
            case 5:
                return ProgressOverView.VICmiscellaneousData;
        }
        return null;
    }*/

    public ArrayList<AdministrationDataForQldOrSA> getDataQldOrSa(int pos) {
        ArrayList<AdministrationDataForQldOrSA> dataArray = new ArrayList<AdministrationDataForQldOrSA>();
        switch (pos) {
            case 0:
                for (int i = 0; i < QldOrSAadminData.size(); i++) {
                    AdministrationDataForQldOrSA administrationDataForQldOrSA = QldOrSAadminData.get(i);
                    //if(ProgressOverView.isTaskCompeted(administrationDataForQldOrSA.getDateActual())){
                    dataArray.add(administrationDataForQldOrSA);
                    //}

                }
                break;
            case 1:
                for (int i = 0; i < QldOrSAframeStageData.size(); i++) {
                    AdministrationDataForQldOrSA administrationDataForQldOrSA = QldOrSAframeStageData.get(i);
                    //if(ProgressOverView.isTaskCompeted(administrationDataForQldOrSA.getDateActual())){
                    dataArray.add(administrationDataForQldOrSA);
                    //}

                }
                break;
            case 2:
                for (int i = 0; i < QldOrSAlockUpStageData.size(); i++) {
                    AdministrationDataForQldOrSA administrationDataForQldOrSA = QldOrSAlockUpStageData.get(i);
                    //if(ProgressOverView.isTaskCompeted(administrationDataForQldOrSA.getDateActual())){
                    dataArray.add(administrationDataForQldOrSA);
                    //}

                }
                break;
            case 3:
                for (int i = 0; i < QldOrSAfixoutData.size(); i++) {
                    AdministrationDataForQldOrSA administrationDataForQldOrSA = QldOrSAfixoutData.get(i);
                    //if(ProgressOverView.isTaskCompeted(administrationDataForQldOrSA.getDateActual())){
                    dataArray.add(administrationDataForQldOrSA);
                    //}

                }
                break;
            case 4:
                for (int i = 0; i < QldOrSAcompletionData.size(); i++) {
                    AdministrationDataForQldOrSA administrationDataForQldOrSA = QldOrSAcompletionData.get(i);
                    //if(ProgressOverView.isTaskCompeted(administrationDataForQldOrSA.getDateActual())){
                    dataArray.add(administrationDataForQldOrSA);
                    //}

                }
                break;
        }
        return dataArray;
    }

    public void fetchDataFromLastClass() {
        if (!controller.isFromNotifications()) {
            lockPercent = ProgressOverView.lockpercent;
            adminPercent = ProgressOverView.adminpercent;
            framePercent = ProgressOverView.framepercent;
            fixedOutPercent = ProgressOverView.fixedOutpercent;
            completionPercent = ProgressOverView.completionpercent;

            QldOrSAadminData.addAll(ProgressOverView.QldOrSAadminData);
            QldOrSAframeStageData.addAll(ProgressOverView.QldOrSAframeStageData);
            QldOrSAlockUpStageData.addAll(ProgressOverView.QldOrSAlockUpStageData);
            QldOrSAcompletionData.addAll(ProgressOverView.QldOrSAcompletionData);
            QldOrSAfixoutData.addAll(ProgressOverView.QldOrSAfixoutData);
        } else {
            lockPercent = DashBoard.lockPercent;
            adminPercent = DashBoard.adminPercent;
            framePercent = DashBoard.framePercent;
            fixedOutPercent = DashBoard.fixedOutPercent;
            completionPercent = DashBoard.completionPercent;

            QldOrSAadminData.addAll(DashBoard.QldOrSAadminData);
            QldOrSAframeStageData.addAll(DashBoard.QldOrSAframeStageData);
            QldOrSAlockUpStageData.addAll(DashBoard.QldOrSAlockUpStageData);
            QldOrSAcompletionData.addAll(DashBoard.QldOrSAcompletionData);
            QldOrSAfixoutData.addAll(DashBoard.QldOrSAfixoutData);
        }

    }


    public void manageButtons(int position) {

        if (position == 0) {
            nextStage.setVisibility(View.VISIBLE);
            previoustage.setVisibility(View.GONE);

            spaceLeft.setVisibility(View.VISIBLE);
            spaceRight.setVisibility(View.VISIBLE);


        } else if (position == 4) {
            nextStage.setVisibility(View.GONE);
            previoustage.setVisibility(View.VISIBLE);

            spaceLeft.setVisibility(View.VISIBLE);
            spaceRight.setVisibility(View.VISIBLE);
        } else {
            nextStage.setVisibility(View.VISIBLE);
            previoustage.setVisibility(View.VISIBLE);

            spaceLeft.setVisibility(View.GONE);
            spaceRight.setVisibility(View.GONE);
        }
    }


}

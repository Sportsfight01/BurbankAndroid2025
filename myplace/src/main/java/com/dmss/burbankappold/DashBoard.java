package com.dmss.burbankappold;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dmss.burbankappold.fragments.Contract;
import com.dmss.burbankappold.fragments.Documents;
import com.dmss.burbankappold.fragments.FavouritePhotos;
import com.dmss.burbankappold.fragments.Finance;
import com.dmss.burbankappold.fragments.MoreFeatures;
import com.dmss.burbankappold.fragments.MyPhotosAddNotes;
import com.dmss.burbankappold.fragments.MyPhotosClass;
import com.dmss.burbankappold.fragments.MyPhotosDateViseList;
import com.dmss.burbankappold.fragments.ProgressDetails;
import com.dmss.burbankappold.fragments.ProgressOverView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import adapters.JobsSpinnerAdapter;
import adapters.NotificationListAdapter;
import adapters.NotificationPopUpAdapter;
import common.AppController;
import common.Common;
import common.CustomCircleBlocks;
import common.CustomEditText;
import common.MyPlaceDataBase;
import common.Receiver;
import common.TransparentProgressDialog;
import common.Utils;
import interfaces.FragmentCallBack;
import interfaces.WebApiJobDetailsResponseCallBack;
import interfaces.WebApiResponseCallback;
import models.AdministrationDataForQldOrSA;
import models.Co_Burbank_Model;
import models.ContactsModel;
import models.ContractFlowDataset;
import models.DetailsData;
import models.FinanceDataSet;
import models.MyDocOrPhotosDataSetQldOrSa;
import models.MyPhotosMonthWiseDataSet;
import models.MyPhotosNewDateWiseDataSet;
import models.MyPlaceCredentials;
import models.MyPlaceJobDetails;
import models.NotificationModel;
import models.SupportAndHelpModel;
import models.TempDataSet;
import models.UserProfile;

;

/**
 * Created by jaya.krishna on 30-05-2017.
 */

public class DashBoard extends BaseActivity implements View.OnClickListener, WebApiResponseCallback, FragmentCallBack, WebApiJobDetailsResponseCallBack {

    LinearLayout leftNotificationList;
    DrawerLayout drawer_layout;
    ListView menuListView;
    NotificationListAdapter notificationListAdapter;
    TextView noNotificationTextView, notificationsJobNumberTextView;
    TextView dateTextView, welcomeDescTextView, title;
    LinearLayout myAppointmentsImageView;
    CustomCircleBlocks lineaLyProgress, lineaLyPhotos, lineaLyContract, lineaLyFinance, lineaLyDocuments, lineaLyMore;
    AppController controller;
    public static int logInCheck = 0, logIn = 1;
    TransparentProgressDialog dialog;
    String userName = "";
    LinearLayout mainLayout;
    FrameLayout fragmentContainer;
    boolean dashBoardVisible = true;
    ImageView backImageView, notificationsImageView, burbankLogoImageView, settingsImageView, logoutImageView;
    TextView actionBarHeadingTextView, saveSettings;
    View actionBarView;
    Spinner jobNumbersSpinner;
    JobsSpinnerAdapter jobsSpinnerAdapter;
    ArrayList<MyPlaceJobDetails> myPlaceJobDetailses = new ArrayList<MyPlaceJobDetails>();
    ImageView addJobNumberImageView;
    boolean loginApiCalled = false;
    LinearLayout jobListView;
    Dialog profileDialog, addJobDialog;
    static int updateUserApiCall = 1, sessionLoginApiCall = 2, acceptInvitation = 4, rejectInvitation = 5, getProfile = 6, addJobApiCall = 7;
    public int apiCall;
    NotificationPopUpAdapter adapter;
    Receiver myBroadcastReceiver;
    Co_Burbank_Model model = null;
    ArrayList<Co_Burbank_Model> notificationList = new ArrayList<>();
    boolean isDialogShown = false;
    Dialog notificationDialog;
    boolean isProfileaccepted = false;
    LinearLayout notification_btn;
    String fullName = "";
    int previousSelectedPosition = -1;
    boolean isNotificationAlreadyShown = false;
    int selectedJobNumberPosition = 0;
    boolean finance = false;
    Dialog enquireDialog;
    boolean activityCreated = false;
    ArrayList<TempDataSet> tempDataSets = new ArrayList<TempDataSet>();
    TempDataSet tempDataSet = new TempDataSet();
    int tempDate = 0;
    ArrayList<MyPhotosMonthWiseDataSet> myPhotosMonthWiseDataSets = new ArrayList<MyPhotosMonthWiseDataSet>();
    MyPhotosMonthWiseDataSet monthWiseDataSet = new MyPhotosMonthWiseDataSet();

    ArrayList<MyPhotosNewDateWiseDataSet> newDateWiseDataSets = new ArrayList<MyPhotosNewDateWiseDataSet>();
    MyPhotosNewDateWiseDataSet dateWiseDataSet = new MyPhotosNewDateWiseDataSet();
    public static int lockPercent = 0, adminPercent = 0, framePercent = 0, fixedOutPercent = 0, completionPercent = 0;
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAadminData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAframeStageData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAlockUpStageData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAcompletionData = new ArrayList<AdministrationDataForQldOrSA>();
    public static ArrayList<AdministrationDataForQldOrSA> QldOrSAfixoutData = new ArrayList<AdministrationDataForQldOrSA>();
    int adminCompletedTask = 0, baseStageCompletedTask = 0, frameStageCompletedTask = 0, lockUpCompletedTask = 0, miscellaneousCompletedTask = 0, completionTask = 0, fixedOutCompletedTask = 0, handOverCompletedTask = 0;

    ArrayList<NotificationModel> menuNotificationModels = new ArrayList<NotificationModel>();
    boolean serviceCallStarted = false;

    MyPlaceDataBase myPlaceDataBase;

    int photosOrProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        changeStatusBarColor();

        activityCreated = false;
        myPlaceDataBase = new MyPlaceDataBase(DashBoard.this);
        controller = (AppController) getApplicationContext();
        controller.getAnalytics().dashboardLoadingEvent();
        selectedJobNumberPosition = controller.getSelectedJobPosition();
        //actionBar = getSupportActionBar();
        drawer_layout = findViewById(R.id.drawer_layout);
        initializeUIElements();
        initializeNotificationList();
        actionBarSettings();
        UserProfile userProfile = controller.getUserProfile();
        boolean profileShown = controller.isUserProfileShown(controller.getUserProfile().getUserDetails().get(0).getEmail().trim());
        if ((!profileShown)) {
            displayProfileDialog();
        }

    }

    public void changeStatusBarColor() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("ONCreate", "onCreate");
    }

    /**
     * this method will be called when we want to display profiledialog
     *
     * @return void.
     */
    public void displayProfileDialog() {
        profileDialog = new Dialog(DashBoard.this);
        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.setCanceledOnTouchOutside(false);
        profileDialog.setContentView(R.layout.set_profile_dialog);
        final CustomEditText firstNameEditText = (CustomEditText) profileDialog.findViewById(R.id.firstNameEditText);
        firstNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        firstNameEditText.setHint("First Name");
        firstNameEditText.setFont(controller.getTypeface());
        firstNameEditText.setImage(R.drawable.profile_account);

        firstNameEditText.setLayoutBackground(R.drawable.grey_border_edittext);
        firstNameEditText.setSelection(firstNameEditText.getText().length());
        final CustomEditText lastNameEditText = (CustomEditText) profileDialog.findViewById(R.id.lastNameEditText);
        lastNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        lastNameEditText.setHint("Last Name");
        lastNameEditText.setFont(controller.getTypeface());
        lastNameEditText.setImage(R.drawable.profile_account);

        lastNameEditText.setLayoutBackground(R.drawable.grey_border_edittext);
        lastNameEditText.setSelection(firstNameEditText.getText().length());
        final CustomEditText phoneNumberEditText = (CustomEditText) profileDialog.findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNumberEditText.setHint("Mobile Number");
        phoneNumberEditText.setFont(controller.getTypeface());
        phoneNumberEditText.setImage(R.drawable.mobile_phone);
        firstNameEditText.setText(controller.getUserProfile().getUserDetails().get(0).getFirstName());
        lastNameEditText.setText(controller.getUserProfile().getUserDetails().get(0).getLastName());
        phoneNumberEditText.setText(controller.getUserProfile().getUserDetails().get(0).getMobile());
        if (firstNameEditText.getText().length() > 0) {
            firstNameEditText.setSelection(firstNameEditText.getText().length());
        }
        if (phoneNumberEditText.getText().length() > 0) {
            phoneNumberEditText.setSelection(phoneNumberEditText.getText().length());
        }
        if (lastNameEditText.getText().length() > 0) {
            lastNameEditText.setSelection(lastNameEditText.getText().length());
        }

        phoneNumberEditText.setLayoutBackground(R.drawable.grey_border_edittext);

        phoneNumberEditText.customEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Method for validating the input data
                    validatingInputProfileDetails(firstNameEditText, lastNameEditText, phoneNumberEditText);
                }
                return false;
            }
        });

        Button skipProfileButton = (Button) profileDialog.findViewById(R.id.skipProfileButton);
        Button saveProfileButton = (Button) profileDialog.findViewById(R.id.saveProfileButton);
        final CheckBox fingerPrintCheckBox = (CheckBox) profileDialog.findViewById(R.id.fingerPrintCheckBox);
        skipProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDialog.dismiss();
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatingInputProfileDetails(firstNameEditText, lastNameEditText, phoneNumberEditText);
            }
        });
        profileDialog.show();
    }

    /**
     * this method will be called when we want to validate all edittext fields
     *
     * @return void.
     */
    public void validatingInputProfileDetails(CustomEditText firstNameEditText, CustomEditText lastNameEditText, CustomEditText phoneNumberEditText) {
        //Checking whether network is available or not.
        if (Utils.isNetworkAvailable(DashBoard.this)) {
            //Validating the first name given by the user.
            if (!controller.getValidation().isNotNull(firstNameEditText)) {
                Utils.showToast(DashBoard.this, "First name should not be empty", Common.errorCase);
            } else if (!controller.getValidation().isNotNull(lastNameEditText)) //Validating the last name given by the user.
            {
                Utils.showToast(DashBoard.this, "Last name should not be empty", Common.errorCase);
            } else if (controller.getValidation().validPhoneLength(DashBoard.this,phoneNumberEditText)) //Validating the mobile number given by the user.
            {
                JSONObject jsonObject = new JSONObject();
                try {
                    fullName = firstNameEditText.getText().toString().trim() + " " + lastNameEditText.getText().toString().trim();
                    jsonObject.put("Email", controller.getUserProfile().getUserDetailses().get(0).getEmail());
                    jsonObject.put("FirstName", firstNameEditText.getText().toString().trim());
                    jsonObject.put("LastName", lastNameEditText.getText().toString().trim());
                    jsonObject.put("FullName", fullName);
                    jsonObject.put("Mobile", phoneNumberEditText.getText().toString().trim());
                    jsonObject.put("Id", controller.getUserProfile().getUserDetails().get(0).getId());
                    jsonObject.put("Region", controller.getUserProfile().getUserDetails().get(0).getRegion());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                apiCall = updateUserApiCall;
                controller.webApiCall().postData(Common.updateUser_Url, jsonObject.toString(), this, Utils.getProgress(DashBoard.this));
            }
        }
    }

    public void initializeUIElements() {
        controller = (AppController) getApplicationContext();
        myBroadcastReceiver = new Receiver();

        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        jobListView = (LinearLayout) findViewById(R.id.JobListView);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        notification_btn = (LinearLayout) findViewById(R.id.notificationBtn);
        welcomeDescTextView = (TextView) findViewById(R.id.welcomeDescTextView);
        controller.setDashBoardWelcomeText(welcomeDescTextView);
        if ((controller.getUserProfile().getUserDetails().size() > 0) && (controller.getUserProfile().getUserDetails().get(0).getFullName() != null)) {
            welcomeDescTextView.setText(Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
        } /*else {
            welcomeDescTextView.setText("Welcome ");
        }*/
        notification_btn.setOnClickListener(this);
        lineaLyProgress = (CustomCircleBlocks) findViewById(R.id.lineaLyProgress);
        lineaLyProgress.setText("Progress");
        lineaLyProgress.setImageResource(R.drawable.programs_new);
        lineaLyProgress.setOnClickListener(this);

        lineaLyPhotos = (CustomCircleBlocks) findViewById(R.id.lineaLyPhotos);
        lineaLyPhotos.setText("Photos");
        lineaLyPhotos.setImageResource(R.drawable.photos_new);
        lineaLyPhotos.setOnClickListener(this);

        lineaLyContract = (CustomCircleBlocks) findViewById(R.id.lineaLyContract);
        lineaLyContract.setText("Details");
        lineaLyContract.setImageResource(R.drawable.details_new);
        lineaLyContract.setOnClickListener(this);

        lineaLyFinance = (CustomCircleBlocks) findViewById(R.id.lineaLyFinance);
        lineaLyFinance.setText("Finance");
        lineaLyFinance.setImageResource(R.drawable.finance_new);
        lineaLyFinance.setOnClickListener(this);

        lineaLyDocuments = (CustomCircleBlocks) findViewById(R.id.lineaLyDocuments);
        lineaLyDocuments.setText("Support/Help");
        lineaLyDocuments.setImageResource(R.drawable.support_new);
        lineaLyDocuments.setOnClickListener(this);

        lineaLyMore = (CustomCircleBlocks) findViewById(R.id.lineaLyMore);
        lineaLyMore.setText("More");
        lineaLyMore.setImageResource(R.drawable.more_new);
        lineaLyMore.setOnClickListener(this);

        jobNumbersSpinner = (Spinner) findViewById(R.id.jobNumbersSpinner);
        // jobNumbersSpinnerDownImageView = (ImageView) findViewById(R.id.jobNumbersSpinnerDownImageView);
        if (controller.getUserProfile().isMultipleJobs() || controller.getUserProfile().getUserDetailses().get(0).getMyPlaceJobDetailses().size() > 0) {
            myPlaceJobDetailses.addAll(controller.getUserProfile().getUserDetailses().get(0).getMyPlaceJobDetailses());
            if (!controller.isUserLoggedInWithEmailId() && controller.getJobNumber() != null /*controller.getUserProfile().getJobNumer() != null*/) {
                for (int i = 0; i < myPlaceJobDetailses.size(); i++) {
                    MyPlaceJobDetails myPlaceJobDetails = myPlaceJobDetailses.get(i);
                    if (controller.getJobNumber().equalsIgnoreCase(myPlaceJobDetails.getJobNo())) {
                        myPlaceJobDetailses.remove(i);
                        myPlaceJobDetailses.add(0, myPlaceJobDetails);
                        break;
                    }
                }
            }

            jobListView.setVisibility(View.VISIBLE);
            notification_btn.setVisibility(View.VISIBLE);
            // Creating adapter for spinner
            jobsSpinnerAdapter = new JobsSpinnerAdapter(this, myPlaceJobDetailses);
            // attaching data adapter to spinner
            jobNumbersSpinner.setAdapter(jobsSpinnerAdapter);
            // checkJobNumbersList();
            jobNumbersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    controller.setLoadedProgressResult("");
                    controller.setLoadedPhotoResult("");
                    controller.setFinanceDataSet(null);
                    controller.setSelectedJobPosition(position);
                    myPlaceJobDetailses.get(selectedJobNumberPosition).setSelected(false);
                    myPlaceJobDetailses.get(position).setSelected(true);
                    jobsSpinnerAdapter.notifyDataSetChanged();
                    selectedJobNumberPosition = position;
                    controller.setJobNumber(myPlaceJobDetailses.get(position).getJobNo());
                    controller.setMy_Place_Details(new MyPlaceCredentials(myPlaceJobDetailses.get(position).getRegion(),
                            myPlaceJobDetailses.get(position).getJobNo(),
                            myPlaceJobDetailses.get(position).getUserName(),
                            myPlaceJobDetailses.get(position).getPassword()));

                    menuListView.setVisibility(View.VISIBLE);
                    menuListView.bringToFront();

                    if (!loginApiCalled) {
                        apiCall = sessionLoginApiCall;
                        finance = false;
                        callWebApi();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            jobNumbersSpinner.setSelection(controller.getSelectedJobPosition());
        } else {
            jobListView.setVisibility(View.GONE);
        }
       /* jobNumbersSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                controller.getAnalytics().dashboardDropdownJobArrowTouchEvent();
            }
        });*/

        /*jobNumbersSpinner.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // jobNumbersSpinner.performClick();
            }
        });*/
        addJobNumberImageView = (ImageView) findViewById(R.id.addJobNumberImageView);
        addJobNumberImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to implement add job!
                controller.getAnalytics().dashboardPlusButtonTouchEvent();
                displayAddJobDialog();
            }
        });

    }


    public void displayAddJobDialog() {
        addJobDialog = new Dialog(DashBoard.this);
        addJobDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addJobDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addJobDialog.setCanceledOnTouchOutside(false);
        addJobDialog.setContentView(R.layout.add_job_dialog);

        final CustomEditText addJobNumberEditText = (CustomEditText) addJobDialog.findViewById(R.id.addJobNumberEditText);
        addJobNumberEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        addJobNumberEditText.setHint("Enter Job Number");
        addJobNumberEditText.setFont(controller.getTypeface());
        addJobNumberEditText.setImage(R.drawable.notification_key_icon);
        addJobNumberEditText.setLayoutBackground(R.drawable.grey_border_edittext);

        final CustomEditText addJobMyPlacePasswordEditText = (CustomEditText) addJobDialog.findViewById(R.id.addJobMyPlacePasswordEditText);
        addJobMyPlacePasswordEditText.customEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        addJobMyPlacePasswordEditText.setHint("Enter MyPlace Password");
        addJobMyPlacePasswordEditText.setFont(controller.getTypeface());
        addJobMyPlacePasswordEditText.setImage(R.drawable.jobnuber_icon);
        addJobMyPlacePasswordEditText.setLayoutBackground(R.drawable.grey_border_edittext);
        addJobMyPlacePasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        addJobMyPlacePasswordEditText.customEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Method for validating the input data
                    validAddJobFields(addJobNumberEditText, addJobMyPlacePasswordEditText);
                }
                return false;
            }
        });

        TextView addJobCancelButton = (TextView) addJobDialog.findViewById(R.id.addJobCancelButton);
        TextView addJobButton = (TextView) addJobDialog.findViewById(R.id.addJobButton);
        addJobCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addJobDialog.dismiss();
            }
        });

        addJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validAddJobFields(addJobNumberEditText, addJobMyPlacePasswordEditText);
            }
        });
        addJobDialog.show();
    }

    public void validAddJobFields(CustomEditText jobNumber, CustomEditText password) {
        if (!controller.getValidation().isNotNull(jobNumber)) {
            Utils.showToast(DashBoard.this, "Job Number should not be empty", Common.errorCase);
        } else if (!controller.getValidation().isNotNull(password)) {
            Utils.showToast(DashBoard.this, "Password should not be empty", Common.errorCase);
        } else {
            boolean callCancel = false;
            for (int i = 0; i < myPlaceJobDetailses.size(); i++) {
                String jobTempNumber = myPlaceJobDetailses.get(i).getJobNo();
                if (jobTempNumber.equalsIgnoreCase(jobNumber.getText().toString().trim())) {
                    callCancel = true;
                    break;
                }
            }
            if (!callCancel) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("LoginUserId", controller.getUserProfile().getUserDetailses().get(0).getId());
                    jsonObject.put("JobNumber", jobNumber.getText().toString().trim());
                    jsonObject.put("MyPlacePassword", password.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                apiCall = addJobApiCall;
                controller.webApiCall().postData(Common.addJob_Url, jsonObject.toString(), DashBoard.this, Utils.getProgress(DashBoard.this));
            } else {
                Utils.showToast(DashBoard.this, "Unable to add the job,as the job is already added.", Common.errorCase);
            }

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.notificationBtn) {
            controller.getAnalytics().dashboardInvitationButtonTouchEvent();
            showNotificationDialog();
        } else if (id == R.id.lineaLyProgress) {
            controller.getAnalytics().dashboardProgressIconButtonTouchEvent();
            if (controller.getMy_Place_Details() != null) {
                updateActionBarUI(false, "My Home Progress");//Documents
                Fragment fragment = new ProgressOverView();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                dashBoardVisible = false;
            } else {
                infoDialog();
            }
        } else if (id == R.id.lineaLyPhotos) {
            controller.getAnalytics().dashboardPhotosIconButtonTouchEvent();
            if (controller.getMy_Place_Details() != null) {
                updateActionBarUI(false, "Photos");
                Fragment fragmentPhotos = new MyPhotosClass();
                FragmentManager fragmentManagerPhotos = getFragmentManager();
                FragmentTransaction transactionPhotos = fragmentManagerPhotos.beginTransaction();
                transactionPhotos.replace(R.id.fragmentContainer, fragmentPhotos).addToBackStack(null).commit();
                dashBoardVisible = false;
            } else {
                infoDialog();
            }
        } else if (id == R.id.lineaLyContract) {
            controller.getAnalytics().dashboardDetailsIconButtonTouchEvent();
            if (controller.getMy_Place_Details() != null) {
                updateActionBarUI(false, "My Home Details");
                Fragment fragmentContract = new Contract();
                FragmentManager fragmentManagerContract = getFragmentManager();
                FragmentTransaction transactionContract = fragmentManagerContract.beginTransaction();
                transactionContract.replace(R.id.fragmentContainer, fragmentContract).addToBackStack(null).commit();
                dashBoardVisible = false;
            } else {
                infoDialog();
            }
        } else if (id == R.id.lineaLyFinance) {//iuhvsrhvkhsfkvf;
            controller.getAnalytics().dashboardFinanceIconButtonTouchEvent();
            FinanceDataSet fds = controller.getFinanceDataSet();
            if (previousSelectedPosition == jobNumbersSpinner.getSelectedItemPosition() && fds != null) {
                if (controller.getMy_Place_Details() != null) {
                    updateActionBarUI(false, "Finance");
                    Fragment fragmentFinance = new Finance();
                    FragmentManager fragmentManagerFinance = getFragmentManager();
                    FragmentTransaction transactionFinance = fragmentManagerFinance.beginTransaction();
                    transactionFinance.replace(R.id.fragmentContainer, fragmentFinance).addToBackStack(null).commit();
                    dashBoardVisible = false;
                } else {
                    infoDialog();
                }
            } else {
                apiCall = sessionLoginApiCall;
                finance = true;
                callWebApi();
            }

                /*if (controller.getMy_Place_Details() != null) {
                    updateActionBarUI(false, "Finance");
                    Fragment fragmentFinance = new Finance();
                    FragmentManager fragmentManagerFinance = getFragmentManager();
                    FragmentTransaction transactionFinance = fragmentManagerFinance.beginTransaction();
                    transactionFinance.replace(R.id.fragmentContainer, fragmentFinance).addToBackStack(null).commit();
                    dashBoardVisible = false;
                } else {
                    infoDialog();
                }*/
        } else if (id == R.id.lineaLyDocuments) {
            controller.getAnalytics().dashboardSupportHelpButtonTouchEvent();
            //getSupportAndHelpDetails();
            String currentJob = controller.getMy_Place_Details().getJobNumber();
            String previousJob = controller.getContactsJobNumber();
            ContactsModel contactsModel = controller.getContactsModelDetails();
            if (!currentJob.equalsIgnoreCase(previousJob)) {
                getNewContactsDetails();
                controller.setContactsJobNumber(controller.getMy_Place_Details().getJobNumber());
            } else {
                if (contactsModel == null) {
                    getNewContactsDetails();
                    Log.v("Support Test", "Step1 F");
                } else {
                    showCallDialog();
                    Log.v("Support Test", "Step 1 s");
                }
            }
        } else if (id == R.id.lineaLyMore) {
            controller.getAnalytics().dashboardMoreButtonTouchEvent();
            updateActionBarUI(false, "More Features");
            Fragment fragmentMore = new MoreFeatures();
            FragmentManager fragmentManagerMore = getFragmentManager();
            FragmentTransaction transactionMore = fragmentManagerMore.beginTransaction();
            transactionMore.replace(R.id.fragmentContainer, fragmentMore).addToBackStack(null).commit();
            dashBoardVisible = false;
        } else {
            throw new IllegalStateException("Unexpected value: " + view.getId());
        }


    }

    public void onResume() {
        super.onResume();
        registerReceiver(myBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        if (activityCreated) {
            updateNotifications();
        }
    }

    public void onPause() {
        super.onPause();
        activityCreated = true;
        String test = controller.getConstructionId();
        unregisterReceiver(myBroadcastReceiver);
        if (enquireDialog != null) {
            enquireDialog.cancel();
        }
        if (drawer_layout.isDrawerOpen(leftNotificationList)) {
            drawer_layout.closeDrawer(leftNotificationList);
        }

    }

    @SuppressLint("InflateParams")
    public void actionBarSettings() {
       /* notificationsImageView = findViewById(R.id.iv_notification);

        notificationsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer_layout.isDrawerOpen(leftNotificationList)) {
                    drawer_layout.closeDrawer(leftNotificationList);
                } else {
                    drawer_layout.openDrawer(leftNotificationList);
                }
                notificationsJobNumberTextView.setText(controller.getMy_Place_Details().getJobNumber());
                controller.getAnalytics().notificationScreenBellIconTouchEvent();
            }
        });*/

        backImageView = (ImageView) findViewById(R.id.backImageView);
        notificationsImageView = (ImageView) findViewById(R.id.notificationsImageView);
        burbankLogoImageView = (ImageView) findViewById(R.id.burbankLogoImageView);
        actionBarHeadingTextView = (TextView) findViewById(R.id.actionBarHeadingTextView);
        settingsImageView = (ImageView) findViewById(R.id.settingsImageView);
        logoutImageView = (ImageView) findViewById(R.id.logoutImageView);
        saveSettings = (TextView) findViewById(R.id.saveSettings);
        updateActionBarUI(true, "");
        notificationsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer_layout.isDrawerOpen(leftNotificationList)) {
                    drawer_layout.closeDrawer(leftNotificationList);
                } else {
                    drawer_layout.openDrawer(leftNotificationList);
                }
                notificationsJobNumberTextView.setText(controller.getMy_Place_Details().getJobNumber());
                controller.getAnalytics().notificationScreenBellIconTouchEvent();
            }
        });
        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, MyPlaceSettingActivity.class);
                startActivity(i);
            }
        });
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment backGoingFragment = DashBoard.this.getFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (!dashBoardVisible) {
                    if (backGoingFragment instanceof MyPhotosAddNotes) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        MyPlaceDataBase myPlaceDataBase = new MyPlaceDataBase(DashBoard.this);
                        if (controller.isCameFromFav()) {
                            myPlaceDataBase.setNotesQldSa(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), controller.getSelectedFavPhotoUrlInt(), controller.getTempPhotoNotes());
                            updateActionBarUI(false, "My Favourites");
                            Fragment fragmentDocuments = new FavouritePhotos();
                            FragmentManager fragmentManagerDocuments = getFragmentManager();
                            FragmentTransaction transactionDocuments = fragmentManagerDocuments.beginTransaction();
                            transactionDocuments.replace(R.id.fragmentContainer, fragmentDocuments).addToBackStack(null).commit();
                        } else {
                            int innerPosition = controller.getSelectedPhotoPosition();
                            int outerPosition = controller.getSelectedDateOfPhotos();

                            // list Update
                            MyPhotosNewDateWiseDataSet dateWisePhotoModel;
                            ArrayList<MyPhotosNewDateWiseDataSet> photosTotalDetails = new ArrayList<MyPhotosNewDateWiseDataSet>();
                            if (!controller.isFromNotifications()) {
                                photosTotalDetails.addAll(controller.getQldOrSaPhotosList());
                                dateWisePhotoModel = photosTotalDetails.get(outerPosition);
                            } else {
                                dateWisePhotoModel = controller.getNewDateWiseDataSet();
                            }

                            ArrayList<MyDocOrPhotosDataSetQldOrSa> adapterPopulatedPhotoList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>(dateWisePhotoModel.getQldOrSaPhotosList());
                            MyDocOrPhotosDataSetQldOrSa detailsDataSet = adapterPopulatedPhotoList.get(innerPosition);
                            detailsDataSet.setNotes(controller.getTempPhotoNotes());
                            adapterPopulatedPhotoList.remove(innerPosition);
                            adapterPopulatedPhotoList.add(innerPosition, detailsDataSet);
                            dateWisePhotoModel.setQldOrSaPhotosList(adapterPopulatedPhotoList);

                            if (!controller.isFromNotifications()) {
                                photosTotalDetails.remove(outerPosition);
                                photosTotalDetails.add(outerPosition, dateWisePhotoModel);
                                controller.setQldOrSaPhotosList(photosTotalDetails);
                            } else {
                                controller.setNewDateWiseDataSet(dateWisePhotoModel);
                            }
                            myPlaceDataBase.setNotesQldSa(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), detailsDataSet.getUrlInt(), controller.getTempPhotoNotes());
                            controller.setTempPhotoNotes("");
                            updateActionBarUI(false, "Photos");
                            Fragment fragment = new MyPhotosDateViseList();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                        }

                    }
                }
            }
        });
    }

    public void updateActionBarUI(boolean homePage, String heading) {
        if (homePage) {
            //  actionBarView.setBackgroundResource(R.color.white);
            backImageView.setVisibility(View.GONE);
            actionBarHeadingTextView.setVisibility(View.GONE);
            logoutImageView.setVisibility(View.GONE);
            saveSettings.setVisibility(View.GONE);
            settingsImageView.setVisibility(View.VISIBLE);
            notificationsImageView.setVisibility(View.VISIBLE);
            burbankLogoImageView.setVisibility(View.VISIBLE);


            mainLayout.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
            fragmentContainer = null;
            dashBoardVisible = true;
            DashBoard.this.getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            // actionBarView.setBackgroundResource(R.color.black);
            backImageView.setVisibility(View.VISIBLE);
            actionBarHeadingTextView.setVisibility(View.VISIBLE);
            actionBarHeadingTextView.setText(heading);
            notificationsImageView.setVisibility(View.GONE);
            burbankLogoImageView.setVisibility(View.GONE);
            settingsImageView.setVisibility(View.GONE);
            logoutImageView.setVisibility(View.GONE);
            if (heading.equalsIgnoreCase("Add Notes")) {
                saveSettings.setVisibility(View.VISIBLE);
            } else {
                saveSettings.setVisibility(View.GONE);
            }
            dashBoardVisible = false;
            mainLayout.setVisibility(View.GONE);
            fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
            fragmentContainer.setVisibility(View.VISIBLE);
        }

        if (dashBoardVisible) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /**
     * Method used to call the web API to get the data from server
     **/
    private void callWebApi() {
        /**
         * Checking whether the network is available or not
         */
        if (Utils.isNetworkAvailable(DashBoard.this)) {
            dialog = Utils.getProgress(DashBoard.this);

            if (finance) {
                //iudchkvsdvksdfhv;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginApiCalled = true;

                        String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson(false));
                        if (result.equalsIgnoreCase("true")) {

                            onSuccessResult(controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl));
                            loginApiCalled = false;
                        } else if (result.equalsIgnoreCase("false")) {
                            finance = false;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    loginApiCalled = false;
                                    Utils.showToast(DashBoard.this, "My place details not valid for this job number", Common.errorCase);
                                    controller.setMy_Place_Details(null);
                                    infoDialog();
                                }
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    loginApiCalled = false;
                                }
                            });
                        }
                    }
                });
                t.start();
            } else {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginApiCalled = true;
                        //onSuccessResult(controller.webApiCall().postData_to_MyPlace(Common.newMyPlaceClientLogin, "application/json", getMyPlaceLoginJson(true))); postDataNewMyPlace
                        WebApiResponseCallback responseCallback = (WebApiResponseCallback) DashBoard.this;
                        controller.webApiCall().postData(Common.IsUserPresent_Url, getJson(), responseCallback);
                        //String result = controller.webApiCall().postDataNewMyPlace(Common.newMyPlaceClientLogin, getMyPlaceLoginJson(true), controller.getMy_Place_Details());
                        //onSuccessResult(result);
                        //dialog.cancel();
                        loginApiCalled = false;
                    }
                });
                t.start();
            }
        }
    }

    public String getMyPlaceLoginJson(boolean newApi) {
        JSONObject jsonObject = new JSONObject();
        try {

            if (controller.getUserProfile().getUserDetails().size() > 0) {
                controller.setMy_Place_Details(new MyPlaceCredentials(myPlaceJobDetailses.get(selectedJobNumberPosition).getRegion(),
                        myPlaceJobDetailses.get(selectedJobNumberPosition).getJobNo(),
                        myPlaceJobDetailses.get(selectedJobNumberPosition).getUserName(),
                        myPlaceJobDetailses.get(selectedJobNumberPosition).getPassword()));
                if (!newApi) {
                    jsonObject.put("Region", controller.getMy_Place_Details().getRegion());
                    jsonObject.put("JobNumber", controller.getMy_Place_Details().getJobNumber());
                    jsonObject.put("UserName", controller.getMy_Place_Details().getUsername());
                    jsonObject.put("Password", controller.getMy_Place_Details().getPassword());
                } else {
                    jsonObject.put("job", controller.getMy_Place_Details().getJobNumber());
                    jsonObject.put("username", controller.getMy_Place_Details().getUsername());
                    jsonObject.put("password", controller.getMy_Place_Details().getPassword());
                }


            }
        } catch (JSONException ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    public void infoDialog() {
        final Dialog infoDialog = new Dialog(DashBoard.this);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) infoDialog.findViewById(R.id.dialogOkButton);
        final TextView dialogMessageTextView = (TextView) infoDialog.findViewById(R.id.dialogMessageTextView);
        TextView dialogHeadingTextView = (TextView) infoDialog.findViewById(R.id.dialogHeadingTextView);
        dialogHeadingTextView.setText("My Place Validation");
        {
            dialogMessageTextView.setText("Error : Invalid details for selected job number.\nPlease select other job.");
        }
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });
        infoDialog.show();
    }

    @Override
    public void onSuccessResult(String result) {
        if (result != null) {
            try {

                if (apiCall == updateUserApiCall) {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean(Common.Status_Key)) {
                        if (profileDialog != null) {
                            profileDialog.cancel();
                        }
                        final JSONObject jobb = jsonObject.getJSONObject(Common.Result_Key);
                        controller.setUserProfileShown(controller.getUserProfile().getUserDetails().get(0).getEmail().trim(), true);
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {

                                controller.getUserProfile().getUserDetails().get(0).updateProfile(jobb);
                                controller.updateUserProfile(jobb);
                                welcomeDescTextView.setText(Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
                            }
                        });
                        Utils.showToast(DashBoard.this, jsonObject.getString(Common.Message), Common.sucessCase);
                    } else {
                        Utils.showToast(DashBoard.this, jsonObject.getString(Common.Message), Common.errorCase);
                    }
                } else if (apiCall == getProfile) {
                    JSONObject job = new JSONObject(result);
                    JSONObject jobb = job.getJSONObject(Common.Result_Key);
                    if (job.getBoolean(Common.Status_Key)) {
                        controller.setProfileInfo(job.getJSONObject(Common.Result_Key).toString());
                        welcomeDescTextView.setText(Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
                        updateSpinnerValues();

                    }
                } else if (apiCall == sessionLoginApiCall) {
                    JSONObject jsonObject = new JSONObject(result);
                    //This is for updating of the
                    if (!finance) {
                        controller.setProfileInfo(jsonObject.getJSONObject(Common.Result_Key).toString());
                    }
                    //controller.setProfileInfo(jsonObject.toString());
                    if (finance) {

                        controller.setConstructionId(jsonObject.getString("ConstructionID"));
                        controller.setOfficeId(jsonObject.getString("OfficeID"));
                        userName = jsonObject.getString("FullName");
                        String financeResult = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.myPlaceGetFinance + "" + controller.getJobNumber());

                        if (financeResult != null) {
                            FinanceDataSet fds = getFinanceData(financeResult);
                            if (fds != null) {
                                controller.setFinanceDataSet(fds);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finance) {
                                            if (controller.getMy_Place_Details() != null) {
                                                updateActionBarUI(false, "Finance");
                                                Fragment fragmentFinance = new Finance();
                                                FragmentManager fragmentManagerFinance = getFragmentManager();
                                                FragmentTransaction transactionFinance = fragmentManagerFinance.beginTransaction();
                                                transactionFinance.replace(R.id.fragmentContainer, fragmentFinance).addToBackStack(null).commit();
                                                dashBoardVisible = false;
                                            } else {
                                                infoDialog();
                                            }
                                            finance = false;
                                        }
                                    }
                                });
                            }
                        }

                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (previousSelectedPosition != jobNumbersSpinner.getSelectedItemPosition()) {
                                previousSelectedPosition = jobNumbersSpinner.getSelectedItemPosition();
                                fullName = Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName());
                                welcomeDescTextView.setText(Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
                                getContactsDetails();
                                callWebNewApiToGetAllPhotosList();

                            } else {
                                String username = controller.getUserProfile().getUserDetails().get(0).getFullName();
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                            }
                        }
                    });
                } else if (apiCall == addJobApiCall) {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean(Common.Status_Key)) {
                        JSONObject job = jsonObject.getJSONObject(Common.Result_Key);
                        if (job.getBoolean(Common.Sucess_Key)) {
                            Utils.showToast(DashBoard.this, job.getString(Common.Message), Common.sucessCase);
                            controller.setProfileInfo(job.toString());
                            myPlaceJobDetailses.clear();
                            myPlaceJobDetailses.addAll(controller.getUserProfile().getUserDetailses().get(0).getMyPlaceJobDetailses());
                            //checkJobNumbersList();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    jobsSpinnerAdapter.notifyDataSetChanged();
                                    jobNumbersSpinner.setSelection(0);
                                    addJobDialog.dismiss();
                                }
                            });
                            /*

                            if (myPlaceJobDetailses.size() < controller.getUserProfile().getUserDetailses().get(0).getMyPlaceJobDetailses().size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        myPlaceJobDetailses.add(controller.getUserProfile().getUserDetailses().get(0).getMyPlaceJobDetailses().get(myPlaceJobDetailses.size()));

                                        ArrayList<MyPlaceJobDetails> presentList = new ArrayList<MyPlaceJobDetails>();
                                        presentList.addAll(myPlaceJobDetailses);
                                        myPlaceJobDetailses.clear();
                                        myPlaceJobDetailses.addAll(presentList);

                                        jobsSpinnerAdapter.notifyDataSetChanged();
                                        jobNumbersSpinner.setSelection(myPlaceJobDetailses.size() - 1);
                                        addJobDialog.dismiss();
                                    }
                                });
                            }*/
                        } else {
                            Utils.showToast(DashBoard.this, jsonObject.getString(Common.Message), Common.errorCase);
                        }
                    } else {
                        Utils.showToast(DashBoard.this, jsonObject.getString(Common.Message), Common.errorCase);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (dialog != null) {
                    dialog.cancel();
                }
            }


        } else {
            if (dialog != null) {
                dialog.cancel();
            }
        }

    }

    @Override
    public void onErrorResult(String error) {
        if (dialog != null) {
            dialog.cancel();
        }
        Utils.showToast(DashBoard.this, error, Common.errorCase);
    }

    @Override
    public void onBackPressed() {
        Fragment backGoingFragment = DashBoard.this.getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (!dashBoardVisible) {
            hideKeyboard(DashBoard.this);
            if (backGoingFragment instanceof ProgressDetails) {
                if (controller.isFromNotifications()) {
                    controller.setFromNotifications(false);
                    updateActionBarUI(true, "");
                } else {
                    updateActionBarUI(false, "My Home Progress");
                    Fragment fragment = new ProgressOverView();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                }
            } else if (backGoingFragment instanceof MyPhotosDateViseList) {
                if (controller.isFromNotifications()) {
                    controller.setFromNotifications(false);
                    updateActionBarUI(true, "");
                } else {
                    updateActionBarUI(false, "Photos");
                    Fragment fragmentPhotos = new MyPhotosClass();
                    FragmentManager fragmentManagerPhotos = getFragmentManager();
                    FragmentTransaction transactionPhotos = fragmentManagerPhotos.beginTransaction();
                    transactionPhotos.replace(R.id.fragmentContainer, fragmentPhotos).addToBackStack(null).commit(); /**///MyPhotosAddNotes
                }
            } else if (backGoingFragment instanceof MyPhotosAddNotes) {
                if (controller.isCameFromFav()) {
                    updateActionBarUI(false, "My Favourites");
                    Fragment fragmentDocuments = new FavouritePhotos();
                    FragmentManager fragmentManagerDocuments = getFragmentManager();
                    FragmentTransaction transactionDocuments = fragmentManagerDocuments.beginTransaction();
                    transactionDocuments.replace(R.id.fragmentContainer, fragmentDocuments).addToBackStack(null).commit();
                } else {
                    hideKeyboard(DashBoard.this);
                    updateActionBarUI(false, "Photos");
                    Fragment fragmentPhotos = new MyPhotosDateViseList();
                    FragmentManager fragmentManagerPhotos = getFragmentManager();
                    FragmentTransaction transactionPhotos = fragmentManagerPhotos.beginTransaction();
                    transactionPhotos.replace(R.id.fragmentContainer, fragmentPhotos).addToBackStack(null).commit();
                }

            } else if (backGoingFragment instanceof Documents) {
                updateActionBarUI(false, "More Features");
                Fragment fragmentMore = new MoreFeatures();
                FragmentManager fragmentManagerMore = getFragmentManager();
                FragmentTransaction transactionMore = fragmentManagerMore.beginTransaction();
                transactionMore.replace(R.id.fragmentContainer, fragmentMore).addToBackStack(null).commit();
            } else if (backGoingFragment instanceof FavouritePhotos) {
                updateActionBarUI(false, "More Features");
                Fragment fragmentMore = new MoreFeatures();
                FragmentManager fragmentManagerMore = getFragmentManager();
                FragmentTransaction transactionMore = fragmentManagerMore.beginTransaction();
                transactionMore.replace(R.id.fragmentContainer, fragmentMore).addToBackStack(null).commit();
            } else {
                updateActionBarUI(true, "");
            }
            System.gc();
        } else {
            exitDialog();
        }


    }

    public void exitDialog() {
        final Dialog exitDialog = new Dialog(DashBoard.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (exitDialog.getWindow() != null) {
            exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
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
        exitDialog.show();
    }

    @Override
    public void fragmentCallBack(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void actionbarTextChange(String name) {
        updateActionBarUI(false, name);
    }

    public void showNotificationDialog() {
        isNotificationAlreadyShown = true;
        isDialogShown = true;
        notificationDialog = new Dialog(DashBoard.this);
        notificationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notificationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notificationDialog.setContentView(R.layout.notificationpopup);
        notificationDialog.setCancelable(false);
        Button close = (Button) notificationDialog.findViewById(R.id.closeButton);
        ListView listView = (ListView) notificationDialog.findViewById(R.id.listView);
        TextView noInvitation = (TextView) notificationDialog.findViewById(R.id.noInvitation);
        if (notificationList.size() > 0) {
            adapter = new NotificationPopUpAdapter(notificationList, DashBoard.this);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);
            noInvitation.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            noInvitation.setVisibility(View.VISIBLE);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogShown = false;
                notificationDialog.dismiss();
                if (isProfileaccepted) {
                    callGetUSerProfile();
                }
            }
        });
        notificationDialog.show();

    }

    public void callGetUSerProfile() {
        if (Utils.isNetworkAvailable(DashBoard.this)) {
            apiCall = getProfile;
            controller.webApiCall().postData(Common.IsUserPresent_Url, getJson(), DashBoard.this, Utils.getProgress(DashBoard.this));
        }
    }

    public String getJson() {
        JSONObject job = new JSONObject();
        try {
            job.put("Email", controller.getUserProfile().getUserDetails().get(0).getEmail());
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return job.toString();
    }

    public void updateSpinnerValues() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myPlaceJobDetailses.clear();
                myPlaceJobDetailses.addAll(controller.getUserProfile().getUserDetailses().get(0).getMyPlaceJobDetailses());
                //checkJobNumbersList();
                jobListView.setVisibility(View.VISIBLE);
                // Creating adapter for spinner
                JobsSpinnerAdapter jobsSpinnerAdapter = new JobsSpinnerAdapter(DashBoard.this, myPlaceJobDetailses);
                // attaching data adapter to spinner
                jobNumbersSpinner.setAdapter(jobsSpinnerAdapter);
                if (previousSelectedPosition != -1) {
                    jobNumbersSpinner.setSelection(previousSelectedPosition);
                }
            }
        });

    }

    public void initializeNotificationList() {
        leftNotificationList = (LinearLayout) findViewById(R.id.drawer_layout_list);
        if (drawer_layout == null) {
            ((DrawerLayout) findViewById(R.id.drawer_layout)).addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    //updateNotifications();
                    controller.getAnalytics().notificationScreenLoadingEvent();
                    controller.getAnalytics().setScreen(DashBoard.this, "Notification_Screen");
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    controller.getAnalytics().setScreen(DashBoard.this, "DashBoard_Screen");
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    if (newState != 0) {
                        updateNotifications();
                    }
                }
            });
        } else {

            drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    //updateNotifications();
                    controller.getAnalytics().notificationScreenLoadingEvent();
                    controller.getAnalytics().setScreen(DashBoard.this, "Notification_Screen");
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    controller.getAnalytics().setScreen(DashBoard.this, "DashBoard_Screen");
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    if (newState != 0) {
                        updateNotifications();
                    }
                }
            });
        }
        menuListView = (ListView) findViewById(R.id.menuListView);
        noNotificationTextView = (TextView) findViewById(R.id.noNotificationTextView);
        notificationsJobNumberTextView = (TextView) findViewById(R.id.notificationsJobNumberTextView);
        noNotificationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationModel notificationModel = menuNotificationModels.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("NotificationModel", notificationModel);

                String compareString = notificationModel.getDate();
                Fragment fragmentNext;
                controller.setFromNotifications(true);
                controller.getAnalytics().notificationsScreenNotificationsTouchEvent();
                if (notificationModel.isPhoto()) {
                    boolean dataStored = false;
                    for (int i = 0; i < myPhotosMonthWiseDataSets.size(); i++) {
                        MyPhotosMonthWiseDataSet photosMonthWiseDataSet = myPhotosMonthWiseDataSets.get(i);
                        ArrayList<MyPhotosNewDateWiseDataSet> QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>(photosMonthWiseDataSet.getQldOrSaPhotosList());
                        for (int j = 0; j < QldOrSaPhotosList.size(); j++) {
                            MyPhotosNewDateWiseDataSet myPhotosNewDateWiseDataSet = QldOrSaPhotosList.get(j);
                            if (compareString.equalsIgnoreCase(myPhotosNewDateWiseDataSet.getNotificationComparingDateString())) {
                                controller.setNewDateWiseDataSet(myPhotosNewDateWiseDataSet);
                                dataStored = true;
                                break;
                            }
                        }
                        if (dataStored) {
                            break;
                        }
                    }
                    if (!notificationModel.isRead()) {
                        myPlaceDataBase.updatePhotoNotifications(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), notificationModel.getDate());
                        notificationModel.setRead(true);
                        menuNotificationModels.remove(position);
                        menuNotificationModels.add(position, notificationModel);
                        notificationListAdapter.notifyDataSetChanged();
                    }
                    drawer_layout.closeDrawer(leftNotificationList);
                    updateActionBarUI(false, "Photos");
                    fragmentNext = new MyPhotosDateViseList();

                } else {
                    if (!notificationModel.isRead()) {
                        myPlaceDataBase.updateNotificationProgressReadStatus(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), notificationModel.getTaskId());
                        notificationModel.setRead(true);
                        menuNotificationModels.remove(position);
                        menuNotificationModels.add(position, notificationModel);
                        notificationListAdapter.notifyDataSetChanged();
                    }
                    controller.setProgressSelectedPosition(notificationModel.getProgressPosition());
                    drawer_layout.closeDrawer(leftNotificationList);
                    String[] IndividualHeading = {"ADMINISTRATION ", "FRAME STAGE ", "LOCK UP STAGE ", "FIXING STAGE ", "FINISHING STAGE ", "MISCELLANEOUS"};
                    updateActionBarUI(false, IndividualHeading[notificationModel.getProgressPosition()]);
                    fragmentNext = new ProgressDetails();
                    fragmentNext.setArguments(bundle);
                }
                FragmentManager fragmentManagerPhotos = getFragmentManager();
                FragmentTransaction transactionPhotos = fragmentManagerPhotos.beginTransaction();
                transactionPhotos.replace(R.id.fragmentContainer, fragmentNext).addToBackStack(null).commit();
            }
        });
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

    public void sortNotifications() {
        ArrayList<NotificationModel> sortedModels = new ArrayList<NotificationModel>();
        ArrayList<NotificationModel> minutesModels = new ArrayList<NotificationModel>();
        ArrayList<NotificationModel> daysModels = new ArrayList<NotificationModel>();

        ArrayList<NotificationModel> basicModels = new ArrayList<NotificationModel>(menuNotificationModels);
        for (int i = 0; i < basicModels.size(); i++) {
            NotificationModel model1 = basicModels.get(i);
            if (model1.getDays() == 0) {
                minutesModels.add(model1);
            } else {
                daysModels.add(model1);
            }
        }
        Collections.sort(daysModels, new SortByDate());
        Collections.sort(minutesModels, new SortByMin());
        menuNotificationModels.clear();
        menuNotificationModels.addAll(minutesModels);
        menuNotificationModels.addAll(daysModels);
    }

    static class SortByDate implements Comparator<NotificationModel> {
        // Used for sorting in ascending order of
        // roll number
        public int compare(NotificationModel a, NotificationModel b) {
            return a.getDays() - b.getDays();
        }
    }

    static class SortByMin implements Comparator<NotificationModel> {
        // Used for sorting in ascending order of
        // roll number
        public int compare(NotificationModel a, NotificationModel b) {
            return a.getMinutes() - b.getMinutes();
        }
    }

    public void getSupportAndHelpDetails() {
        if (controller.getMy_Place_Details() != null) {
            if (controller.getSupportAndHelpModel() == null || !controller.getSupportAndHelpModel().getJobNumber().equalsIgnoreCase(controller.getMy_Place_Details().getJobNumber())) {
                if (Utils.isNetworkAvailable(DashBoard.this)) {
                    dialog = Utils.getProgress(DashBoard.this);
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                            if (result.equalsIgnoreCase("true")) {
                                String loginResult = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetUserDetailsUrl);
                                if (loginResult != null) {
                                    String supportDetails = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetSupportDetails + "" + controller.getMy_Place_Details().getJobNumber());
                                    try {
                                        SupportAndHelpModel supportAndHelpModel = new SupportAndHelpModel(supportDetails);
                                        controller.setSupportAndHelpModel(supportAndHelpModel);
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialog != null) {
                                                    dialog.cancel();
                                                }
                                                showCallDialog();

                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        controller.setSupportAndHelpModel(null);
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
                                    controller.setSupportAndHelpModel(null);
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
                                controller.setSupportAndHelpModel(null);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        Utils.showToast(DashBoard.this, "My place details not valid for this job number", Common.errorCase);
                                    }
                                });
                            } else {
                                controller.setSupportAndHelpModel(null);
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


                } else {
                    controller.setSupportAndHelpModel(null);
                }
            } else {
                showCallDialog();
            }
        }

    }


    public void getNewContactsDetails() {
        if (controller.getMy_Place_Details() != null) {
            if (Utils.isNetworkAvailable(DashBoard.this)) {
                dialog = Utils.getProgress(DashBoard.this);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                        if (result.equalsIgnoreCase("true")) {

                            String result2 = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl);
                            if (result2 != null && !result2.equalsIgnoreCase("null")) {

                                final String contactDetails = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetContactDetails + "" + controller.getJobNumber());
                                if (contactDetails != null) {
                                    if (contactDetails.contains("Error")) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (contactDetails.contains("timeout")) {
                                                    Toast.makeText(DashBoard.this, "Error: Network is slow,\nPlease try again later", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(DashBoard.this, contactDetails, Toast.LENGTH_LONG).show();
                                                }
                                                dialog.cancel();
                                            }
                                        });
                                    } else {

                                        if (contactDetails.length() > 0) {
                                            ContactsModel contactsModelDetails = new ContactsModel(contactDetails);

                                            if (contactsModelDetails.isExceptionRaised()) {
                                                controller.setContactsModelDetails(null);

                                            } else {
                                                controller.setContactsModelDetails(contactsModelDetails);
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialog != null) {
                                                            dialog.cancel();
                                                        }
                                                        showCallDialog();
                                                    }
                                                });
                                            }
                                        }
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialog != null) {
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                        //onSuccessResult(contactDetails);
                                    }
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.cancel();
                                            Utils.showToast(DashBoard.this, "My place details not valid for this job number", Common.errorCase);
                                            infoDialog();
                                        }
                                    });
                                }
                            }

                        } else if (result.equalsIgnoreCase("false")) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    Utils.showToast(DashBoard.this, "My place details not valid for this job number", Common.errorCase);
                                    infoDialog();
                                }
                            });
                        } else {
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
            } else {
                controller.setContactsModelDetails(null);
            }
        } else {
            controller.setContactsModelDetails(null);
        }

    }

    @SuppressLint("SetTextI18n")
    public void showCallDialog() {

        enquireDialog = new Dialog(DashBoard.this);
        enquireDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        enquireDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        enquireDialog.setContentView(R.layout.dialog_call_burbank);
        ImageView closeButton = (ImageView) enquireDialog.findViewById(R.id.closeButton);
        TextView supportHelpHeadingTextView = (TextView) enquireDialog.findViewById(R.id.supportHelpHeadingTextView),
                supportUserNameTextView = (TextView) enquireDialog.findViewById(R.id.supportUserNameTextView),
                supportRoleTextView = (TextView) enquireDialog.findViewById(R.id.supportRoleTextView),
                supporterEmailTextView = (TextView) enquireDialog.findViewById(R.id.supporterEmailTextView),
                supporterMobileTextView = (TextView) enquireDialog.findViewById(R.id.supporterMobileTextView);

        Button emailButton = (Button) enquireDialog.findViewById(R.id.emailButton),
                callButton = (Button) enquireDialog.findViewById(R.id.callButton);
        supportHelpHeadingTextView.setText("SUPPORT/HELP - JOB NO.: " + controller.getMy_Place_Details().getJobNumber());

        /*final SupportAndHelpModel supportAndHelpModel = controller.getSupportAndHelpModel();
        String userName = supportAndHelpModel.getContactPerson().trim();
        String role = supportAndHelpModel.getDesignation().trim();
        final String email = supportAndHelpModel.getEmail().trim();
        final String mobile = supportAndHelpModel.getMobile().trim();*/

        final ContactsModel contactsModel = controller.getContactsModelDetails();
        String userName = contactsModel.getClientRelationsOfficer().trim();
        String role = "New Home Coordinator";
        final String email = contactsModel.getcROEmail().trim();
        final String mobile = contactsModel.getcROPhone().trim();


        if (!userName.equalsIgnoreCase("null") && userName.length() > 0) {
            supportUserNameTextView.setText(userName);
        } else {
            supportUserNameTextView.setText("Name : N/A");
        }
        if (role != null && role.length() > 0) {
            supportRoleTextView.setText(role);
        } else {
            supportRoleTextView.setText("Designation ID : N/A");
        }
        if (!email.equalsIgnoreCase("null") && email.length() > 0) {
            supporterEmailTextView.setText(email);
        } else {
            supporterEmailTextView.setText("Email ID : N/A");
        }
        if (!mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
            supporterMobileTextView.setText(mobile);
        } else {
            supporterMobileTextView.setText("Mobile No : N/A");
        }

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.equalsIgnoreCase("null") && email.length() > 0) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", contactsModel.getcROEmail(), null));
                    String name = "";
                    if ((controller.getUserProfile().getUserDetails().size() > 0) && (controller.getUserProfile().getUserDetails().get(0).getFullName() != null)) {
                        name = " - " + controller.getUserProfile().getUserDetails().get(0).getFullName();
                    } else {
                        name = "";
                    }
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MyPlace – " + controller.getMy_Place_Details().getJobNumber() + name);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n* Kindly do not change the subject to track your queries.");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    enquireDialog.cancel();
                    controller.getAnalytics().dashboardSupportHelpEmailButtonTouchEvent();
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
                    String test2;
                    if (mobile.length() > 10) {
                        String test = mobile.substring(1);
                        test2 = "+61" + test;
                    } else {
                        test2 = "+61" + mobile;
                    }

                    controller.getAnalytics().dashboardSupportHelpCallButtonTouchEvent();
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", test2, null)));
                    enquireDialog.cancel();
                }
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getAnalytics().dashboardSupportHelpCloseIconTouchEvent();
                enquireDialog.cancel();
            }
        });

        enquireDialog.show();

/*        final TextView numberTextView = (TextView) enquireDialog.findViewById(R.id.numberTextView);
        numberTextView.setVisibility(View.VISIBLE);
        TextView headingTextView = (TextView) enquireDialog.findViewById(R.id.headingTextView);
        headingTextView.setText("Call Burbank");
        Button cancelButton = (Button) enquireDialog.findViewById(R.id.cancelButton);
        cancelButton.setTypeface(controller.getTypeface());
        Button sendButton = (Button) enquireDialog.findViewById(R.id.sendButton);
        sendButton.setTypeface(controller.getTypeface());
        sendButton.setText("Call");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                enquireDialog.cancel();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+61 3 9328 0222", null)));

            }
        });
        enquireDialog.show();*/
    }


    /**
     * METHODS TO CALL PHOTOS AND PROGRESS FOR NOTIFICATIONS LIST
     **/
    public void getContactsDetails() {
        if (controller.getMy_Place_Details() != null) {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                        if (result.equalsIgnoreCase("true")) {
                            String result2 = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl);
                            if (result2 != null && !result2.equalsIgnoreCase("null")) {
                                final String contactDetails = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetContactDetails + "" + controller.getJobNumber());
                                if (contactDetails != null) {
                                    if (!contactDetails.contains("Error")) {
                                        if (contactDetails != null && contactDetails.length() > 0) {
                                            ContactsModel contactsModelDetails = new ContactsModel(contactDetails);
                                            controller.setContactsModelDetails(contactsModelDetails);
                                            controller.setContactsJobNumber(controller.getMy_Place_Details().getJobNumber());
                                        } else {
                                            controller.setContactsModelDetails(null);
                                        }
                                    } else {
                                        controller.setContactsModelDetails(null);
                                    }
                                } else {
                                    controller.setContactsModelDetails(null);
                                }
                            }
                        }
                    }
                });
                t.start();
            }
        }

    }


    public void callWebNewApiToGetAllPhotosList() {
        serviceCallStarted = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //jvhbjsbvjfsv;
                photosOrProgress = 1;
                //String result = controller.webApiCall().getDataNewMyPlace(Common.newMyPlacePhotosOrDocuments, controller.getMy_Place_Details());
                controller.webApiCall().getDataNewMyPlace(Common.newMyPlacePhotosOrDocuments, controller.getMy_Place_Details(), DashBoard.this);
                //onSuccessPhotoResult(result);

            }
        }).start();
    }

    public void callForNewApiProgress() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                photosOrProgress = 2;
                //String result = controller.webApiCall().getDataNewMyPlace(Common.newMyPlaceProgress, controller.getMy_Place_Details());
                controller.webApiCall().getDataNewMyPlace(Common.newMyPlaceProgress, controller.getMy_Place_Details(), DashBoard.this);
                //onSuccessResultOfProgress(result);

            }
        });
        t.start();
    }

    @Override
    public void onJobDetailsSuccessResult(String result) {
        if (photosOrProgress == 1) {
            onSuccessPhotoResult(result);
        } else if (photosOrProgress == 2) {
            onSuccessResultOfProgress(result);
        } else if (photosOrProgress == 3) {
            onSuccessNoticeSelection(result);
        }
    }

    @Override
    public void onJobDetailsErrorResult(String error) {
        if (photosOrProgress == 1) {

        } else if (photosOrProgress == 2) {

        }
    }

    public void onSuccessPhotoResult(String result) {
        MyPlaceDataBase myPlaceDataBase;
        myPlaceDataBase = new MyPlaceDataBase(DashBoard.this);
        if (result != null) {
            controller.setLoadedPhotoResult(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    MyDocOrPhotosDataSetQldOrSa myDocumentsDataSetQldOrSa = new MyDocOrPhotosDataSetQldOrSa(jsonArray.getJSONObject(i).toString(), DashBoard.this);
                    if (myDocumentsDataSetQldOrSa.getType() != null && myDocumentsDataSetQldOrSa.getType().length() > 0 && myDocumentsDataSetQldOrSa.getType().trim().equalsIgnoreCase("JPG")) {
                        QldOrSaPhotosList.add(myDocumentsDataSetQldOrSa);
                    }
                }
                myPlaceDataBase.insertQldOrSaPhotos(QldOrSaPhotosList, controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername());
                sortDataByMonthNewApi(QldOrSaPhotosList);
            } catch (JSONException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }
                });
                e.printStackTrace();
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callForNewApiProgress();
                }
            });
        }
    }

    public void sortDataByMonthNewApi(final ArrayList<MyDocOrPhotosDataSetQldOrSa> QldOrSaPhotosList) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Collections.sort(QldOrSaPhotosList, (obj1, obj2) -> {
                    int fromObj1Size = obj1.getDocWholeDateInt();
                    int fromObj2Size = obj2.getDocWholeDateInt();
                    return fromObj1Size - fromObj2Size;
                });
                ArrayList<MyDocOrPhotosDataSetQldOrSa> temp = new ArrayList<MyDocOrPhotosDataSetQldOrSa>();
                for (int i = QldOrSaPhotosList.size() - 1; i >= 0; i--) {
                    temp.add(QldOrSaPhotosList.get(i));
                }
                QldOrSaPhotosList.clear();
                QldOrSaPhotosList.addAll(temp);
                tempDataSets.clear();
                tempDataSets = new ArrayList<TempDataSet>();
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
                myPhotosMonthWiseDataSets = new ArrayList<MyPhotosMonthWiseDataSet>();
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
                    controller.setMyPhotosMonthWiseDataSets(myPhotosMonthWiseDataSets);
                }

                for (int i = 0; i < myPhotosMonthWiseDataSets.size(); i++) {
                    MyPhotosMonthWiseDataSet photosMonthWiseDataSet = myPhotosMonthWiseDataSets.get(i);
                    ArrayList<MyPhotosNewDateWiseDataSet> QldOrSaPhotosList = new ArrayList<MyPhotosNewDateWiseDataSet>();
                    QldOrSaPhotosList.addAll(photosMonthWiseDataSet.getQldOrSaPhotosList());
                    for (int j = 0; j < QldOrSaPhotosList.size(); j++) {
                        myPlaceDataBase.insertPhotoNotifications(controller.getMy_Place_Details().getJobNumber(),
                                controller.getMy_Place_Details().getUsername(), QldOrSaPhotosList.get(j).getNotificationComparingDateString(), QldOrSaPhotosList.get(j).getTimeStamp());
                    }
                }

                callForNewApiProgress();
            }
        });
    }


    public void onSuccessResultOfProgress(final String progressDetails) {
        QldOrSAadminData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAframeStageData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAlockUpStageData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAcompletionData = new ArrayList<AdministrationDataForQldOrSA>();
        QldOrSAfixoutData = new ArrayList<AdministrationDataForQldOrSA>();
        controller.setLoadedProgressResult(progressDetails);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (progressDetails != null) {
                    adminCompletedTask = 0;
                    baseStageCompletedTask = 0;
                    frameStageCompletedTask = 0;
                    lockUpCompletedTask = 0;
                    miscellaneousCompletedTask = 0;
                    completionTask = 0;
                    fixedOutCompletedTask = 0;
                    handOverCompletedTask = 0;
                    lockPercent = 0;
                    adminPercent = 0;
                    framePercent = 0;
                    fixedOutPercent = 0;
                    completionPercent = 0;

                    try {
                        JSONArray jsonArray = new JSONArray(progressDetails);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            AdministrationDataForQldOrSA data = new AdministrationDataForQldOrSA(jsonArray.getJSONObject(i).toString());
                            if (data.getPhaseCode().equalsIgnoreCase("Presite")) {
                                if (data.getStatus().equalsIgnoreCase("Completed")) {
                                    adminCompletedTask = adminCompletedTask + 1;
                                }
                                myPlaceDataBase.insertProgress(controller.getMy_Place_Details().getJobNumber(),
                                        controller.getMy_Place_Details().getUsername(),
                                        data.getTaskId(), 0, data.getName(), data.getStatus(), data.getDisplayDate(), data.getDateActual());

                                QldOrSAadminData.add(data);
                            } else {
                                if (data.getStageName().equalsIgnoreCase("Frame Stage")) {
                                    if (data.getStatus().equalsIgnoreCase("Completed")) {
                                        frameStageCompletedTask = frameStageCompletedTask + 1;
                                    }
                                    myPlaceDataBase.insertProgress(controller.getMy_Place_Details().getJobNumber(),
                                            controller.getMy_Place_Details().getUsername(),
                                            data.getTaskId(), 1, data.getName(), data.getStatus(), data.getDisplayDate(), data.getDateActual());

                                    QldOrSAframeStageData.add(data);
                                } else if (data.getStageName().equalsIgnoreCase("Lockup Stage")) {
                                    if (data.getStatus().equalsIgnoreCase("Completed")) {
                                        lockUpCompletedTask = lockUpCompletedTask + 1;
                                    }
                                    myPlaceDataBase.insertProgress(controller.getMy_Place_Details().getJobNumber(),
                                            controller.getMy_Place_Details().getUsername(),
                                            data.getTaskId(), 2, data.getName(), data.getStatus(), data.getDisplayDate(), data.getDateActual());

                                    QldOrSAlockUpStageData.add(data);
                                } else if (data.getStageName().equalsIgnoreCase("Fixout Stage")) {
                                    if (data.getStatus().equalsIgnoreCase("Completed")) {
                                        fixedOutCompletedTask = fixedOutCompletedTask + 1;
                                    }
                                    myPlaceDataBase.insertProgress(controller.getMy_Place_Details().getJobNumber(),
                                            controller.getMy_Place_Details().getUsername(),
                                            data.getTaskId(), 3, data.getName(), data.getStatus(), data.getDisplayDate(), data.getDateActual());

                                    QldOrSAfixoutData.add(data);
                                } else if (data.getStageName().equalsIgnoreCase("Completion") || data.getStageName().equalsIgnoreCase("Handover")) {
                                    if (data.getStatus().equalsIgnoreCase("Completed")) {
                                        completionTask = completionTask + 1;
                                    }
                                    myPlaceDataBase.insertProgress(controller.getMy_Place_Details().getJobNumber(),
                                            controller.getMy_Place_Details().getUsername(),
                                            data.getTaskId(), 4, data.getName(), data.getStatus(), data.getDisplayDate(), data.getDateActual());

                                    QldOrSAcompletionData.add(data);
                                }
                            }
                        }

                        if (adminCompletedTask != 0 && QldOrSAadminData.size() > 0) {
                            adminPercent = (100 * adminCompletedTask / QldOrSAadminData.size());
                        }
                        if (frameStageCompletedTask != 0 && QldOrSAframeStageData.size() > 0) {
                            framePercent = (100 * frameStageCompletedTask / QldOrSAframeStageData.size());
                        }
                        if (lockUpCompletedTask != 0 && QldOrSAlockUpStageData.size() > 0) {
                            lockPercent = (100 * lockUpCompletedTask / QldOrSAlockUpStageData.size());
                        }
                        if (fixedOutCompletedTask != 0 && QldOrSAfixoutData.size() > 0) {
                            fixedOutPercent = ((100 * fixedOutCompletedTask / QldOrSAfixoutData.size()));
                        }
                        if (completionTask != 0 && QldOrSAcompletionData.size() > 0) {
                            completionPercent = (100 * completionTask / QldOrSAcompletionData.size());
                        }
                        serviceCallStarted = false;
                        getNotificationSelection();
                    } catch (JSONException ex) {
                        ex.fillInStackTrace();
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }
                } else {
                    getNotificationSelection();
                }
            }
        });

    }

    public void getNotificationSelection() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                photosOrProgress = 3;
                //String progressDetails = controller.webApiCall().postData(Common.getMyPlaceNotificationSetting, getNoticeSelectionJson());
                WebApiJobDetailsResponseCallBack webApiJobDetailsResponseCallBack = (WebApiJobDetailsResponseCallBack) DashBoard.this;
                controller.webApiCall().postData(Common.getMyPlaceNotificationSetting, getNoticeSelectionJson(), webApiJobDetailsResponseCallBack);
                //onSuccessNoticeSelection(progressDetails);
            }
        });
        t.start();
    }

    public String getNoticeSelectionJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", controller.getUserProfile().getUserDetailses().get(0).getId());
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    public void onSuccessNoticeSelection(final String noticeDetails) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject job = new JSONObject(noticeDetails);
                    boolean status = job.getBoolean("Status");
                    final String message = job.getString("Message");
                    if (status == true) {
                        JSONObject jsonObject = job.getJSONObject("Result");
                        JSONArray jsonArray = jsonObject.getJSONArray("NotificationTypes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject notification = jsonArray.getJSONObject(i);
                            switch (i) {
                                case 0:
                                    controller.setPhotoNotifications(notification.isNull("IsUserOpted") ? false : notification.getBoolean("IsUserOpted"));
                                    break;
                                case 1:
                                    controller.setStageNotifications(notification.isNull("IsUserOpted") ? false : notification.getBoolean("IsUserOpted"));
                                    break;
                                case 2:
                                    controller.setProgressNotifications(notification.isNull("IsUserOpted") ? false : notification.getBoolean("IsUserOpted"));
                                    break;

                            }
                        }
                        updateNotifications();
                    } else {
                        Utils.showToast(DashBoard.this, message, Common.errorCase);
                    }
                } catch (Exception ex) {
                    ex.fillInStackTrace();

                }
                if (dialog != null) {
                    dialog.cancel();
                }
                //callMyAppointmentsWebApi();
            }
        });
    }

    public void updateNotifications() {
        menuNotificationModels.clear();
        notificationListAdapter = null;
        boolean photo = controller.isPhotoNotifications();
        boolean stage = controller.isProgressNotifications();
        boolean progress = controller.isStageNotifications();

        if (photo && progress && stage) {
            menuNotificationModels.addAll(myPlaceDataBase.getPhotoNotifications(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername()));
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), false));
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), true));
            if (menuNotificationModels.size() > 0) {
                noNotificationTextView.setVisibility(View.GONE);
                menuListView.setVisibility(View.VISIBLE);
                sortNotifications();
                notificationListAdapter = new NotificationListAdapter(DashBoard.this, menuNotificationModels);
                menuListView.setAdapter(notificationListAdapter);
                menuListView.bringToFront();
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.bringToFront();
                noNotificationTextView.setText("No Notifications to display");
                menuListView.setVisibility(View.GONE);
            }
        } else if (photo && progress && !stage) {
            menuNotificationModels.addAll(myPlaceDataBase.getPhotoNotifications(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername()));
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), false));
            if (menuNotificationModels.size() > 0) {
                noNotificationTextView.setVisibility(View.GONE);
                menuListView.setVisibility(View.VISIBLE);
                sortNotifications();
                notificationListAdapter = new NotificationListAdapter(DashBoard.this, menuNotificationModels);
                menuListView.setAdapter(notificationListAdapter);
                menuListView.bringToFront();
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.bringToFront();
                noNotificationTextView.setText("No Notifications to display");
                menuListView.setVisibility(View.GONE);
            }
        } else if (photo && !progress && stage) {
            menuNotificationModels.addAll(myPlaceDataBase.getPhotoNotifications(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername()));
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), true));
            if (menuNotificationModels.size() > 0) {
                noNotificationTextView.setVisibility(View.GONE);
                menuListView.setVisibility(View.VISIBLE);
                sortNotifications();
                notificationListAdapter = new NotificationListAdapter(DashBoard.this, menuNotificationModels);
                menuListView.setAdapter(notificationListAdapter);
                menuListView.bringToFront();
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.bringToFront();
                noNotificationTextView.setText("No Notifications to display");
                menuListView.setVisibility(View.GONE);
            }
        } else if (!photo && progress && stage) {
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), false));
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), true));
            if (menuNotificationModels.size() > 0) {
                noNotificationTextView.setVisibility(View.GONE);
                menuListView.setVisibility(View.VISIBLE);
                sortNotifications();
                notificationListAdapter = new NotificationListAdapter(DashBoard.this, menuNotificationModels);
                menuListView.setAdapter(notificationListAdapter);
                menuListView.bringToFront();
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.bringToFront();
                noNotificationTextView.setText("No Notifications to display");
                menuListView.setVisibility(View.GONE);
            }
        } else if (photo && !progress && !stage) {
            menuNotificationModels.addAll(myPlaceDataBase.getPhotoNotifications(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername()));
            if (menuNotificationModels.size() > 0) {
                noNotificationTextView.setVisibility(View.GONE);
                menuListView.setVisibility(View.VISIBLE);
                sortNotifications();
                notificationListAdapter = new NotificationListAdapter(DashBoard.this, menuNotificationModels);
                menuListView.setAdapter(notificationListAdapter);
                menuListView.bringToFront();
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.bringToFront();
                noNotificationTextView.setText("No photo Notifications to display");
                menuListView.setVisibility(View.GONE);
            }
        } else if (!photo && progress && !stage) {
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), false));
            if (menuNotificationModels.size() > 0) {
                menuListView.setVisibility(View.VISIBLE);
                noNotificationTextView.setVisibility(View.GONE);
                sortNotifications();
                notificationListAdapter = new NotificationListAdapter(DashBoard.this, menuNotificationModels);
                menuListView.setAdapter(notificationListAdapter);
                menuListView.bringToFront();
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.bringToFront();
                noNotificationTextView.setText("No progress Notifications to display");
                menuListView.setVisibility(View.GONE);
            }
        } else if (!photo && !progress && stage) {
            menuNotificationModels.addAll(myPlaceDataBase.getProgress(controller.getMy_Place_Details().getJobNumber(), controller.getMy_Place_Details().getUsername(), true));
            if (menuNotificationModels.size() > 0) {
                menuListView.setVisibility(View.VISIBLE);
                noNotificationTextView.setVisibility(View.GONE);
                sortNotifications();
                notificationListAdapter = new NotificationListAdapter(DashBoard.this, menuNotificationModels);
                menuListView.setAdapter(notificationListAdapter);
                menuListView.bringToFront();
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE);
                noNotificationTextView.bringToFront();
                noNotificationTextView.setText("No Stage change Notifications to display");
                menuListView.setVisibility(View.GONE);
            }
        } else {
            noNotificationTextView.bringToFront();
            noNotificationTextView.setVisibility(View.VISIBLE);
            noNotificationTextView.setText("Notifications settings are not enabled, please enable preferred notifications from settings.");
            menuListView.setVisibility(View.GONE);
        }
        //}

        boolean readExist = false;
        for (int i = 0; i < menuNotificationModels.size(); i++) {
            if (!menuNotificationModels.get(i).isRead()) {
                readExist = true;
                break;
            }
        }

       /* if (readExist) {
            notificationsImageView.setImageResource(R.drawable.alert_select);
        } else {
            notificationsImageView.setImageResource(R.drawable.notification_icon);
        }*/
        notificationsJobNumberTextView.setText(controller.getMy_Place_Details().getJobNumber());
    }


    public FinanceDataSet getFinanceData(String data) {
        FinanceDataSet finaceData = null;
        try {
            JSONObject jsonData = new JSONObject(data);
            String ContractValue = jsonData.getString("ContractPrice");
            JSONArray json_finaceVariation = jsonData.getJSONArray("FinanceVariations");
            JSONArray json_finaceClaims = jsonData.getJSONArray("FinanceClaims");
            JSONArray json_finaceReceipt = jsonData.getJSONArray("FinanceReceipts");
            ArrayList<ContractFlowDataset> c_FDs = new ArrayList<ContractFlowDataset>();
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0:
                        ContractFlowDataset finaceVariationList_details = new ContractFlowDataset();
                        ArrayList<DetailsData> finace_variation_list = new ArrayList<DetailsData>();
                        for (int j = 0; j < json_finaceVariation.length(); j++) {
                            JSONObject contrctFlowData = json_finaceVariation.getJSONObject(j);
                            if (!contrctFlowData.getString("Description").equalsIgnoreCase("null")) {
                                DetailsData details = new DetailsData(contrctFlowData.getString("Description"), contrctFlowData.getString("Amount"));
                                finace_variation_list.add(details);
                            }
                        }
                        finaceVariationList_details.setContractFlow(finace_variation_list);
                        finaceVariationList_details.setHeaderName("Variations to Date :");
                        c_FDs.add(finaceVariationList_details);
                        break;
                    case 1:
                        ContractFlowDataset Claims = new ContractFlowDataset();
                        ArrayList<DetailsData> Claimsdata = new ArrayList<DetailsData>();
                        for (int j = 0; j < json_finaceClaims.length(); j++) {
                            JSONObject claimsFlowData = json_finaceClaims.getJSONObject(j);
                            if (!claimsFlowData.getString("Description").equalsIgnoreCase("null")) {
                                DetailsData details = new DetailsData(claimsFlowData.getString("Description"), claimsFlowData.getString("Amount"));
                                Claimsdata.add(details);
                            }
                        }
                        Claims.setHeaderName("Claims to Date :");
                        Claims.setContractFlow(Claimsdata);
                        c_FDs.add(Claims);
                        break;
                    case 2:
                        ContractFlowDataset receiptsFlow = new ContractFlowDataset();
                        ArrayList<DetailsData> receiptFlowdata = new ArrayList<DetailsData>();
                        for (int j = 0; j < json_finaceReceipt.length(); j++) {
                            JSONObject receiptsFlowData = json_finaceReceipt.getJSONObject(j);
                            if (!receiptsFlowData.getString("Description").equalsIgnoreCase("null")) {
                                DetailsData details = new DetailsData(receiptsFlowData.getString("Description"), receiptsFlowData.getString("Amount"));
                                receiptFlowdata.add(details);
                            }
                        }
                        receiptsFlow.setHeaderName("Receipts to Date :");
                        receiptsFlow.setContractFlow(receiptFlowdata);
                        c_FDs.add(receiptsFlow);
                        break;
                }
            }
            finaceData = new FinanceDataSet(c_FDs, ContractValue);
        } catch (JSONException ex) {
            ex.fillInStackTrace();
        }
        return finaceData;
    }








 /*   public boolean isNotificationNeedTobeShown() {
        boolean status = false;
        for (int i = 0; i < notificationList.size(); i++) {
            if (notificationList.get(i).isCoBurbank() == false) {
                status = true;
            }
        }
        return status;
    }

    public String getAccept_Reject_InvitationJson(int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("JobNumber", notificationList.get(position).getJobNumber());
            jsonObject.put("Email", notificationList.get(position).getEmail());
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }*/


    /**
     * Method used to call the web API to get the data from server
     **/
    /*private void callMyAppointmentsWebApi() {
     *//**
     * Checking whether the network is available or not
     *//*

        if (Utils.isNetworkAvailable(DashBoard.this)) {
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
                                        Utils.showToast(DashBoard.this, "My place details not valid for this job number", Common.errorCase);
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
        } else {
            controller.setAppointmentsDataSet(null);
            if (dialog != null) {
                dialog.cancel();
            }
        }
    }*/

    /**
     * Method used to call the web API to get the data from server
     **/
    /*private void callContactsWebApi() {
     */

    /**
     * Checking whether the network is available or not
     *//*
        if (Utils.isNetworkAvailable(DashBoard.this)) {
            urll = Common.myPlaceBaseUrlQldOrSa;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = controller.webApiCall().postData_to_MyPlace(urll + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                    if (result.equalsIgnoreCase("true")) {
                        String loginResult = controller.webApiCall().getData_From_MyPlace(urll + "" + Common.MyPlaceGetUserDetailsUrl);
                        if (loginResult != null && loginResult.length() > 0) {
                            String contactResult = controller.webApiCall().getData_From_MyPlace(urll + "" + Common.MyPlaceGetContactsDetails + controller.getMy_Place_Details().getJobNumber());
                            if (contactResult != null && contactResult.length() > 0) {
                                controller.setContactsModelDetails(new ContactsModel(contactResult));
                            } else {
                                controller.setContactsModelDetails(null);
                            }
                        }
                    } else if (result.equalsIgnoreCase("false")) {
                        controller.setContactsModelDetails(null);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.showToast(DashBoard.this, "My place details not valid for this job number", Common.errorCase);
                                controller.setMy_Place_Details(null);
                                infoDialog();
                            }
                        });
                    } else {
                        controller.setContactsModelDetails(null);

                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callMyAppointmentsWebApi();
                        }
                    });
                }
            });
            t.start();
        }
    }*/
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
package com.dmss.burbankappold;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dmss.burbankappold.dashboard.DashboardNewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import adapters.NotificationPopUpAdapter;
import common.AppController;
import common.Common;
import common.CustomEditText;
import common.Receiver;
import common.TransparentProgressDialog;
import common.Utils;
import interfaces.Notification_Callback;
import interfaces.WebApiResponseCallback;
import models.Co_Burbank_Model;

/**
 * Created by Ashish.Kumar on 08-06-2017.
 */

public class EmptyDashboard extends AppCompatActivity implements View.OnClickListener, WebApiResponseCallback, Notification_Callback {
    TextView dateTextView, welcomeDescTextView, title;
    AppController controller;
    ActionBar actionBar;
    View actionBarView;
    Button logout;
    ImageView notificationsImageView, settingsImageView;
    ImageView logoutt;
    Dialog profileDialog;
    Dialog notificationDialog;
    ArrayList<Co_Burbank_Model> notificationList = new ArrayList<>();
    boolean isDialogShown = false;
    NotificationPopUpAdapter adapter;
    static int notificationApi = 3, updateProfileApi = 4, getProfile = 5, addJobApiCall = 7;
    int apiCall;
    TransparentProgressDialog dialog = null;
    Receiver myBroadcastReceiver;
    boolean isProfileaccepted = false;
    Button notification_btn;
    String fullName = "";
    ImageView emptyDashboardAddJobImageView;

    Dialog addJobDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_dashboard);
        actionBar = getSupportActionBar();
        actionBarSettings();
        initializeUIElements();
        callNotificationApi();
    }

    public void onResume() {
        super.onResume();
        registerReceiver(myBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }

    public void initializeUIElements() {
        controller = (AppController) getApplicationContext();
        myBroadcastReceiver = new Receiver();
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        notification_btn = (Button) findViewById(R.id.notificationBtn);
        title = (TextView) findViewById(R.id.textViewTitle);
        welcomeDescTextView = (TextView) findViewById(R.id.welcomeDescTextView);
        logout = (Button) findViewById(R.id.logoutButton);
        emptyDashboardAddJobImageView = (ImageView) findViewById(R.id.emptyDashboardAddJobImageView);
        emptyDashboardAddJobImageView.setOnClickListener(this);
        dateTextView.setText(new SimpleDateFormat("EEEE, dd MM yyyy").format(new Date()));
        if ((controller.getUserProfile().getUserDetails().size() > 0) && (controller.getUserProfile().getUserDetails().get(0).getFullName() != null)) {
            welcomeDescTextView.setText("Welcome " + Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
        } else {
            welcomeDescTextView.setText("Welcome ");
        }
        notification_btn.setOnClickListener(this);
        boolean profileShown = controller.isUserProfileShown(controller.getUserProfile().getUserDetails().get(0).getEmail().trim());
        if ((profileShown == false)) {
            displayProfileDialog();
        }
        logout.setTypeface(controller.getTypeface());
        dateTextView.setTypeface(controller.getTypeface());
        welcomeDescTextView.setTypeface(controller.getTypeface());
        title.setTypeface(controller.getTypeface());
        logout.setVisibility(View.GONE);
        notification_btn.setVisibility(View.VISIBLE);
    }

    public void callNotificationApi() {

        if (Utils.isNetworkAvailable(EmptyDashboard.this)) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    apiCall = notificationApi;
                    controller.webApiCall().postData(Common.getJobNotificationUrl, getJonNotificationUrl(), EmptyDashboard.this);
                }

            });
            t.start();
        }
    }

    public String getJonNotificationUrl() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Email", controller.getUserProfile().getUserDetails().get(0).getEmail());
            //jsonObject.put("Email","anill@gmal.com");
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * this method will be called when we want to display profiledialog
     *
     * @return void.
     */
    public void displayProfileDialog() {
        //try {
        profileDialog = new Dialog(EmptyDashboard.this);
        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.setCanceledOnTouchOutside(false);
        profileDialog.setContentView(R.layout.set_profile_dialog);
        final CustomEditText firstNameEditText = (CustomEditText) profileDialog.findViewById(R.id.firstNameEditText);
        firstNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        firstNameEditText.setHint("First Name");
        firstNameEditText.setFont(controller.getTypeface());
        firstNameEditText.setImage(R.drawable.icon_user);

        firstNameEditText.setLayoutBackground(R.drawable.grey_border_edittext);
        firstNameEditText.setSelection(firstNameEditText.getText().length());
        final CustomEditText lastNameEditText = (CustomEditText) profileDialog.findViewById(R.id.lastNameEditText);
        lastNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        lastNameEditText.setHint("Last Name");
        lastNameEditText.setFont(controller.getTypeface());
        lastNameEditText.setImage(R.drawable.icon_user);

        lastNameEditText.setLayoutBackground(R.drawable.grey_border_edittext);
        lastNameEditText.setSelection(firstNameEditText.getText().length());
        final CustomEditText phoneNumberEditText = (CustomEditText) profileDialog.findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNumberEditText.setHint("Mobile Number");
        phoneNumberEditText.setFont(controller.getTypeface());
        phoneNumberEditText.setImage(R.drawable.icon_mobile);
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
        skipProfileButton.setTypeface(controller.getTypeface());
        Button saveProfileButton = (Button) profileDialog.findViewById(R.id.saveProfileButton);
        saveProfileButton.setTypeface(controller.getTypeface());
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
        /*} catch (Exception ex) {
            ex.fillInStackTrace();
        }*/
    }

    /**
     * this method will be called when we want to validate all edittext fields
     *
     * @return void.
     */
    public void validatingInputProfileDetails(CustomEditText firstNameEditText, CustomEditText lastNameEditText, CustomEditText phoneNumberEditText) {
        //Checking whether network is available or not.
        if (Utils.isNetworkAvailable(EmptyDashboard.this)) {
            //Validating the first name given by the user.
            if (!controller.getValidation().isNotNull(firstNameEditText)) {
                Utils.showToast(EmptyDashboard.this, "First name should not be empty", Common.errorCase);
            } else if (!controller.getValidation().isNotNull(lastNameEditText)) //Validating the last name given by the user.
            {
                Utils.showToast(EmptyDashboard.this, "Last name should not be empty", Common.errorCase);
            } else if (controller.getValidation().validPhoneLength(EmptyDashboard.this,phoneNumberEditText)) //Validating the mobile number given by the user.
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
                    jsonObject.put("Region", "VIC");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                apiCall = updateProfileApi;
                controller.webApiCall().postData(Common.updateUser_Url, jsonObject.toString(), this, Utils.getProgress(EmptyDashboard.this));
            }
        }
    }

    public void actionBarSettings() {
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        actionBarView = inflater.inflate(R.layout.custom_actionbar_transparent, null);
        notificationsImageView = (ImageView) actionBarView.findViewById(R.id.notificationsImageView);
        settingsImageView = (ImageView) actionBarView.findViewById(R.id.settingsImageView);
        logoutt = (ImageView) actionBarView.findViewById(R.id.logoutImageView);
        actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        actionBarView.setBackgroundResource(R.color.white);
        settingsImageView.setVisibility(View.GONE);
        notificationsImageView.setVisibility(View.GONE);
        logoutt.setVisibility(View.VISIBLE);
        logoutt.setImageResource(R.drawable.logout);
        logoutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setUserLoggedIn(false);
                controller.setLoggedInFromSocial(false);
                controller.setMy_Place_Details(null);
                Intent in = new Intent(EmptyDashboard.this, FirstClass.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(in);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    @Override
    public void onSuccessResult(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (apiCall == updateProfileApi) {
                    if (jsonObject.getBoolean(Common.Status_Key)) {
                        if (profileDialog != null) {
                            profileDialog.cancel();
                        }
                        final JSONObject jsonObjectt = jsonObject.getJSONObject(Common.Result_Key);
                        controller.setUserProfileShown(controller.getUserProfile().getUserDetails().get(0).getEmail().trim(), true);
                        Utils.showToast(EmptyDashboard.this, jsonObject.getString(Common.Message), Common.sucessCase);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                controller.getUserProfile().getUserDetails().get(0).updateProfile(jsonObjectt);
                                controller.updateUserProfile(jsonObjectt);
                                /*startActivity(new Intent(EmptyDashboard.this, MyPlaceTabActivity.class));
                                overridePendingTransition(0, 0);
                                finish();*/

                                //welcomeDescTextView.setText("Welcome " + Utils.getCamelCase(controller.getUserProfile().getUserDetails().get(0).getFullName()));
                            }
                        });
                    } else {
                        Utils.showToast(EmptyDashboard.this, jsonObject.getString(Common.Message), Common.errorCase);
                    }
                } else if (apiCall == getProfile) {
                    JSONObject job = new JSONObject(result);
                    JSONObject jobb = job.getJSONObject(Common.Result_Key);
                    if (job.getBoolean(Common.Status_Key)) {
                        controller.setProfileInfo(job.getJSONObject(Common.Result_Key).toString());
                        if (controller.getUserProfile().getUserDetails().get(0).isMyPlaceAccessible()) {
                            Intent in = new Intent(EmptyDashboard.this, DashboardNewActivity.class);
                            startActivity(in);
                            finish();
                        }
                    }

                } else if (apiCall == notificationApi) {
                    final String message = jsonObject.getString(Common.Message);
                    notificationList.clear();
                    if (jsonObject.getBoolean(Common.Status_Key)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Common.Result_Key);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            notificationList.add(new Co_Burbank_Model(jsonArray.getJSONObject(i)));
                        }
                        if (notificationList.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (adapter == null) {
                                        if (isNotificationNeedTobeShown()) {
                                            showNotificationDialog();
                                        }
                                    } else {
                                        if (isDialogShown) {
                                            adapter.notifyDataSetChanged();
                                            Utils.showToast(EmptyDashboard.this, message, Common.sucessCase);
                                        } else {
                                            showNotificationDialog();
                                            Utils.showToast(EmptyDashboard.this, message, Common.sucessCase);
                                        }
                                    }

                                }
                            });
                        } else {
                            if (isDialogShown) {
                                isDialogShown = false;
                                notificationDialog.cancel();
                                Utils.showToast(EmptyDashboard.this, message, Common.errorCase);

                            }

                        }

                    } else {
                        Utils.showToast(EmptyDashboard.this, jsonObject.getString(Common.Message), Common.errorCase);
                    }
                    if (dialog != null) {
                        dialog.cancel();
                    }

                } else if (apiCall == addJobApiCall) {
                    if (jsonObject.getBoolean(Common.Status_Key)) {
                        JSONObject job = jsonObject.getJSONObject(Common.Result_Key);
                        if (job.getBoolean(Common.Sucess_Key)) {
                            Utils.showToast(EmptyDashboard.this, job.getString(Common.Message), Common.sucessCase);
                            controller.setProfileInfo(job.toString());
                            startActivity(new Intent(this, DashboardNewActivity.class));
                            overridePendingTransition(0, 0);
                            finish();
                        } else {
                            Utils.showToast(EmptyDashboard.this, jsonObject.getString(Common.Message), Common.errorCase);
                        }
                    } else {
                        Utils.showToast(EmptyDashboard.this, jsonObject.getString(Common.Message), Common.errorCase);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Utils.showToast(EmptyDashboard.this, Common.somethingErrorMessage, Common.errorCase);
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

    public void callGetUSerProfile() {
        if (Utils.isNetworkAvailable(EmptyDashboard.this)) {
            apiCall = getProfile;
            controller.webApiCall().postData(Common.IsUserPresent_Url, getJson(), EmptyDashboard.this, Utils.getProgress(EmptyDashboard.this));
        }
    }

    public boolean isNotificationNeedTobeShown() {
        boolean status = false;
        for (int i = 0; i < notificationList.size(); i++) {
            if (!notificationList.get(i).isCoBurbank()) {
                status = true;
            }
        }
        return status;
    }

    @Override
    public void onErrorResult(String error) {
        Utils.showToast(EmptyDashboard.this, error, Common.errorCase);
    }

    public void showNotificationDialog() {

        isDialogShown = true;
        notificationDialog = new Dialog(EmptyDashboard.this);
        notificationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notificationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notificationDialog.setContentView(R.layout.notificationpopup);
        notificationDialog.setCancelable(false);
        Button close = (Button) notificationDialog.findViewById(R.id.closeButton);
        ListView listView = (ListView) notificationDialog.findViewById(R.id.listView);
        TextView noInvitation = (TextView) notificationDialog.findViewById(R.id.noInvitation);
        noInvitation.setTypeface(controller.getTypeface());
        if (notificationList.size() > 0) {
            adapter = new NotificationPopUpAdapter(notificationList, EmptyDashboard.this);
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
                if (isProfileaccepted == true) {
                    callGetUSerProfile();
                }

            }
        });
        notificationDialog.show();
    }

    @Override
    public void onAccept(final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Utils.isNetworkAvailable(EmptyDashboard.this)) {
                    apiCall = notificationApi;
                    isProfileaccepted = true;
                    controller.webApiCall().postData(Common.getAcceptInvitationUrl, getAccept_Reject_InvitationJson(position), EmptyDashboard.this, Utils.getProgress(EmptyDashboard.this));
                }
            }
        });

    }

    @Override
    public void onReject(final int position) {
        if (Utils.isNetworkAvailable(EmptyDashboard.this)) {
            apiCall = notificationApi;
            controller.webApiCall().postData(Common.getRejectInvitationUrl, getAccept_Reject_InvitationJson(position), EmptyDashboard.this, Utils.getProgress(EmptyDashboard.this));
        }
    }

    public String getAccept_Reject_InvitationJson(int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("JobNumber", notificationList.get(position).getJobNumber());
            jsonObject.put("Email", notificationList.get(position).getEmail());
            // jsonObject.put("Email", "anill@gmal.com");
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == notification_btn.getId()) {
            if (isDialogShown == false) {
                isDialogShown = true;
                showNotificationDialog();
            }
        } else if (v.getId() == emptyDashboardAddJobImageView.getId()) {
            displayAddJobDialog();
        }

    }

    public void displayAddJobDialog() {
        //try {
        addJobDialog = new Dialog(EmptyDashboard.this);
        addJobDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addJobDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addJobDialog.setCanceledOnTouchOutside(false);
        addJobDialog.setContentView(R.layout.add_job_dialog);

        final CustomEditText addJobNumberEditText = (CustomEditText) addJobDialog.findViewById(R.id.addJobNumberEditText);
        addJobNumberEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        addJobNumberEditText.setHint("Enter Job Number");
        addJobNumberEditText.setFont(controller.getTypeface());
        addJobNumberEditText.setImage(R.drawable.jobnumber);
        addJobNumberEditText.setLayoutBackground(R.drawable.grey_border_edittext);

        final CustomEditText addJobMyPlacePasswordEditText = (CustomEditText) addJobDialog.findViewById(R.id.addJobMyPlacePasswordEditText);
        addJobMyPlacePasswordEditText.customEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        addJobMyPlacePasswordEditText.setHint("Enter MyPlace Password");
        addJobMyPlacePasswordEditText.setFont(controller.getTypeface());
        addJobMyPlacePasswordEditText.setImage(R.drawable.passsword_new);
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

        Button addJobCancelButton = (Button) addJobDialog.findViewById(R.id.addJobCancelButton);
        addJobCancelButton.setTypeface(controller.getTypeface());
        Button addJobButton = (Button) addJobDialog.findViewById(R.id.addJobButton);
        addJobButton.setTypeface(controller.getTypeface());
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
            Utils.showToast(EmptyDashboard.this, "Job Number should not be null", Common.errorCase);
        } else if (!controller.getValidation().isNotNull(password)) {
            Utils.showToast(EmptyDashboard.this, "Password should not be null", Common.errorCase);
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("LoginUserId", controller.getUserProfile().getUserDetailses().get(0).getId());
                jsonObject.put("JobNumber", jobNumber.getText().toString().trim());
                jsonObject.put("MyPlacePassword", password.getText().toString().trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            apiCall = addJobApiCall;
            controller.webApiCall().postData(Common.addJob_Url, jsonObject.toString(), this, Utils.getProgress(EmptyDashboard.this));
        }
    }

}


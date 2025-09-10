package com.dmss.burbankappold;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dmss.burbankappold.dashboard.DashboardNewActivity;
import com.dmss.burbankappold.network.ApiRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import common.AppController;
import common.Common;
import common.Receiver;
import common.Utils;
import interfaces.WebApiResponseCallback;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import models.MyPlaceCredentials;
import models.profile.MyPlaceDetail;
import models.profile.UserJobProfile;

/**
 * Created by Ashish.Kumar on 30-05-2017.
 */

public class MyPlaceLogin extends BaseActivity implements View.OnClickListener, WebApiResponseCallback {
    Button loginButton;
    TextView heading, forgetPassword;
    AppController controller;
    common.CustomEditText emailEditText;
    common.CustomEditText password, loginJobNumberET;
    Receiver myBroadcastReceiver;
    TextView fingurePrint;
    Boolean isUserDetailsApiCalled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeStatusBarColor();
        customActionBar();
        initializeAll();
    }


    private void initializeAll() {
        controller = (AppController) getApplicationContext();
        myBroadcastReceiver = new Receiver();
        fingurePrint = (TextView) findViewById(R.id.fingurePrint);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        heading = (TextView) findViewById(R.id.heading);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        //heading.setText("Login to MyPlace");
        heading.setTypeface(controller.getTypeface());
        emailEditText = (common.CustomEditText) findViewById(R.id.loginEmailET);
        emailEditText.setImage(R.drawable.icon_user);
        emailEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        emailEditText.setEnabledd(false);
        password = (common.CustomEditText) findViewById(R.id.passwordEditText);
        loginJobNumberET = (common.CustomEditText) findViewById(R.id.loginJobNumberET);
        loginJobNumberET.setHint("Enter MyPlace JobNumber");
        loginJobNumberET.setInputType(InputType.TYPE_CLASS_TEXT);
        loginJobNumberET.setImage(R.drawable.jobnumber);
        emailEditText.setEnabledd(false);
        password.setImage(R.drawable.passsword_new);
        password.setHint("Enter MyPlace Password");
        forgetPassword.setVisibility(View.GONE);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        /*if (!controller.isUserLoggedInWithEmailId()) {

        }*/
        if (!controller.getUserProfile().isMultipleJobs()) {
            emailEditText.setText(controller.getUserProfile().getEmail());
            loginJobNumberET.setVisibility(View.VISIBLE);
            loginJobNumberET.setText(controller.getUserProfile().getJobNumer());
            loginJobNumberET.setEnabledd(false);

        }
        forgetPassword.setVisibility(View.GONE);
        fingurePrint.setVisibility(View.GONE);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactCroDialog();
            }
        });
        /*if(controller.getLastLoggedEmailId() != null && controller.getLastLoggedEmailId().length() > 0){
            jobNumber.setText(controller.getLastLoggedEmailId());
        }else if(!controller.getUserProfile().getJobNumer().equalsIgnoreCase("null")){
            jobNumber.setText(controller.getUserProfile().getJobNumer());
        }*/
    }

    /**
     * Method for custom action bar
     *
     * @return void.
     */
    public void customActionBar() {
        //getSupportActionBar().hide();
       /* getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar_transparent);
        View view = getSupportActionBar().getCustomView();
        ImageView back = (ImageView) view.findViewById(R.id.backImageView);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

    }

    public void onResume() {
        super.onResume();
        registerReceiver(myBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        if (loginJobNumberET.getText() != null && loginJobNumberET.getText().length() > 0) {
            if (password.getText() != null && password.getText().length() > 0) {
                if (Utils.isNetworkAvailable(MyPlaceLogin.this)) {
                    controller.webApiCall().postData(Common.validateMyPlacePassword_Url, getJson(), MyPlaceLogin.this, Utils.getProgress(MyPlaceLogin.this));
                }
            } else {
                //information regarding empty password
                Utils.showToast(MyPlaceLogin.this, "Password field should not be empty", Common.errorCase);
            }
        } else {
            //information regarding empty job number
            Utils.showToast(MyPlaceLogin.this, "Job number field should not be empty", Common.errorCase);
        }

    }

    public String getJson() {
        JSONObject job = new JSONObject();
        try {
            job.put("Email", controller.getLastLoggedEmailId());
            job.put("JobNumber", loginJobNumberET.getText());
            job.put("MyPlacePassword", password.getText());
            job.put("IsMultipleEmails", controller.getUserProfile().isMultipleEmails());
            job.put("IsMultipleJobs", controller.getUserProfile().isMultipleJobs());
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return job.toString();
    }

    @Override
    public void onSuccessResult(String result) {
        if (result != null) {
            Log.w("pass code output", result);
            try {
                JSONObject jobb = new JSONObject(result);
                if (jobb.has(Common.Result_Key)) {
                    JSONObject job = jobb.getJSONObject(Common.Result_Key);
                    if (job.getBoolean(Common.Sucess_Key)) {
                        // controller.setUserLoggedIn(true);
                        if (!isUserDetailsApiCalled) {
                            controller.setProfileInfo(job.toString());
                            if (controller.getUserProfile().getUserDetails().size() == 0) {
                                controller.getUserProfile().setJobNumer(loginJobNumberET.getText().toString());
                                controller.getUserProfile().setMyPlacePassword(password.getText().toString());
                            }
                            if (controller.getUserProfile().getPassCode().length() > 0) {
                                Intent in = new Intent(MyPlaceLogin.this, SetPassword.class);
                                startActivity(in);
                            } else {
                                controller.setUserLoggedIn(true);
                                isUserDetailsApiCalled = true;
                                controller.webApiCall().postData(Common.IsUserPresent_Url, getJsonBody());
                            }
                            Utils.showToast(MyPlaceLogin.this, job.getString(Common.Message), Common.sucessCase);
                        }else {
                            parseAPIResponse(job);
                        }

                    } else {
                        Utils.showToast(MyPlaceLogin.this, job.getString(Common.Message), Common.errorCase);
                    }
                }else {
                    Utils.showToast(MyPlaceLogin.this, jobb.getString(Common.Message), Common.errorCase);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
                Utils.showToast(MyPlaceLogin.this, Common.somethingErrorMessage, Common.errorCase);
            }

        } else{
            Utils.showToast(MyPlaceLogin.this, Common.somethingErrorMessage, Common.errorCase);
        }
    }

    private void parseAPIResponse(JSONObject jsonObject){
        try {
            if (jsonObject.getBoolean(Common.Status_Key)) {
                JSONObject job = jsonObject.getJSONObject(Common.Result_Key);
                if (job.getBoolean(Common.Sucess_Key)) {
                    controller.setProfileInfo(job.toString());
                    Gson gson = new GsonBuilder().create();
                    UserJobProfile profile = gson.fromJson(job.toString(), UserJobProfile.class);
                    controller.setUserJobProfile(profile);
                    controller.setUserJobDetail(profile.getUserDetails());
                    controller.setMyPlaceDetail(profile.getUserDetails().get(0).getMyPlaceDetails());
                    MyPlaceDetail myPlaceDetails = profile.getUserDetails().get(0).getMyPlaceDetails().get(0);
                    MyPlaceCredentials myPlaceCredentials = new MyPlaceCredentials(
                            myPlaceDetails.getRegion(),
                            myPlaceDetails.getJobNo(),
                            myPlaceDetails.getUserName(),
                            myPlaceDetails.getPassword()
                    );
                    controller.setMy_Place_Details(myPlaceCredentials);

                    Intent in = new Intent(MyPlaceLogin.this, DashboardNewActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(in);
                    finish();

                } else {
                    Utils.showToast(
                            this,
                    jsonObject.getString(Common.Message),
                            Common.errorCase
                    );
                }
            } else {
                Utils.showToast(
                        this,
                jsonObject.getString(Common.Message),
                        Common.errorCase
                );
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    @Override
    public void onErrorResult(String error) {
        Utils.showToast(MyPlaceLogin.this, error, Common.errorCase);
    }

    private String getJsonBody(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Email", controller.getUserProfile().getUserDetails().get(0).getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /*public void showErrorMessage(final String message,boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showToast(MyPlaceLogin.this, message, Common.errorCase);
            }
        });
    }*/

    public void contactCroDialog() {
        final Dialog exitDialog = new Dialog(MyPlaceLogin.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) exitDialog.findViewById(R.id.dialogOkButton);
        final TextView dialogMessageTextView = (TextView) exitDialog.findViewById(R.id.dialogMessageTextView);
        dialogMessageTextView.setText("Please contact CRO...");
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

        exitDialog.show();
    }
}
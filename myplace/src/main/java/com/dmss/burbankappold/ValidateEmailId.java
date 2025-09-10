package com.dmss.burbankappold;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmss.burbankappold.dashboard.DashboardNewActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import common.AppController;
import common.Common;
import common.CustomEditText;
import common.Receiver;
import common.Utils;
import interfaces.WebApiResponseCallback;
import models.MyPlaceEmail;
import models.MyPlaceJobDetails;
import models.UserProfile;

/**
 * Created by Ashish.Kumar on 06-06-2017.
 */

public class ValidateEmailId extends AppCompatActivity implements View.OnClickListener, WebApiResponseCallback {
    Button submitButton;
    AppController controller;
    CustomEditText jobNumber, emailId, password;
    boolean isPasswordEnabled = false;
    Receiver myBroadcastReceiver;
    TextView hintTextView, validateEmailHeadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__validate_email);
        customActionBar();
        initializeAll();
    }

    private void initializeAll() {
        myBroadcastReceiver = new Receiver();
        controller = (AppController) getApplicationContext();
        submitButton = (Button) findViewById(R.id.submitButton);
        emailId = (CustomEditText) findViewById(R.id.emailId);
        password = (CustomEditText) findViewById(R.id.passwordEditText);
        jobNumber = (CustomEditText) findViewById(R.id.jobNumber);
        jobNumber.setImage(R.drawable.jobnumber);
        jobNumber.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailId.setHint("Enter Job Number");
        password.setImage(R.drawable.passsword_new);
        password.setHint("Enter MyPlace Password");
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        emailId = (CustomEditText) findViewById(R.id.emailId);
        emailId.setImage(R.drawable.mail_icon);
        emailId.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailId.setHint("Enter Email Id");
        submitButton.setOnClickListener(this);
        hintTextView = (TextView) findViewById(R.id.hintTextView);
        hintTextView.setTypeface(controller.getTypeface());
        validateEmailHeadingTextView = (TextView) findViewById(R.id.validateEmailHeadingTextView);
        validateEmailHeadingTextView.setTypeface(controller.getTypeface());

        if (controller.isUserLoggedInWithEmailId()) {

            emailId.customEditText.setEnabled(true);
            jobNumber.setEnabledd(false);
            jobNumber.setText(controller.getLastLoggedEmailId());

            hintTextView.setText("Multiple job numbers are associated with this mail, Please enter a valid job number which is associated with your mail ID.");
            emailId.setText("");
            emailId.setHint("Enter Job Number");
            jobNumber.setImage(R.drawable.mail_icon);
            emailId.setImage(R.drawable.jobnumber);

        } else {
            jobNumber.setText(controller.getUserProfile().getJobNumer());
            hintTextView.setText("Multiple Emails are associated with this job number, Please enter a valid email which is associated with your Job Number.");
            emailId.customEditText.setEnabled(true);
            jobNumber.setEnabledd(false);
            emailId.setImage(R.drawable.mail_icon);
           jobNumber.setImage(R.drawable.jobnumber);
        }

        if (controller.getUserProfile().getMyPlacePassword().length() == 0) {
            password.setVisibility(View.VISIBLE);
            isPasswordEnabled = true;
        }
    }

    /**
     * Method for custom action bar
     *
     * @return void.
     */
    public void customActionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        });


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
        if (controller.isUserLoggedInWithEmailId()) {
            if (emailId.getText().length() > 0) {
                if (Utils.isNetworkAvailable(ValidateEmailId.this)) {
                    if (isPasswordEnabled) {
                        if (password.getText().toString().length() == 0) {
                            Utils.showToast(ValidateEmailId.this, "Please enter password", Common.errorCase);
                        } else {
                            controller.webApiCall().postData(Common.validateMyPlacePassword_Url, getJson(), ValidateEmailId.this, Utils.getProgress(ValidateEmailId.this));
                        }

                    } else {
                        controller.webApiCall().postData(Common.validateMyPlacePassword_Url, getJson(), ValidateEmailId.this, Utils.getProgress(ValidateEmailId.this));
                    }
                }
            }
        } else {
            if (emailId.getText().length() > 0) {
                if (controller.getValidation().validateEmail(emailId, ValidateEmailId.this)) {
                    if (Utils.isNetworkAvailable(ValidateEmailId.this)) {
                        if (isPasswordEnabled) {
                            if (password.getText().toString().length() == 0) {
                                Utils.showToast(ValidateEmailId.this, "Please enter password", Common.errorCase);
                            } else {
                                if (controller.isEmailNotMapped()) {
                                    controller.webApiCall().postData(Common.validateEmailPassword_Url, getJson(), ValidateEmailId.this, Utils.getProgress(ValidateEmailId.this));
                                } else {
                                    controller.webApiCall().postData(Common.validateMyPlacePassword_Url, getJson(), ValidateEmailId.this, Utils.getProgress(ValidateEmailId.this));
                                }
                            }
                        } else {
                            if (controller.isEmailNotMapped()) {
                                controller.webApiCall().postData(Common.validateEmailPassword_Url, getJson(), ValidateEmailId.this, Utils.getProgress(ValidateEmailId.this));
                            } else {
                                controller.webApiCall().postData(Common.validateMyPlacePassword_Url, getJson(), ValidateEmailId.this, Utils.getProgress(ValidateEmailId.this));
                            }
                        }
                    }
                } else {
                    Utils.showToast(ValidateEmailId.this, "Please enter Valid Email Id", Common.errorCase);
                }
            } else {
                Utils.showToast(ValidateEmailId.this, "Please enter Email Id", Common.errorCase);
            }
        }

    }

    public String getJson() {
        JSONObject job = new JSONObject();
        try {
            if (controller.isUserLoggedInWithEmailId()) {
                job.put("Email", jobNumber.getText().toString());
                job.put("JobNumber", emailId.getText().toString());
            } else {
                job.put("Email", emailId.getText().toString());
                job.put("JobNumber", jobNumber.getText().toString());
            }
            if (isPasswordEnabled == true) {
                job.put("MyPlacePassword", password.getText().toString());
            } else {
                job.put("MyPlacePassword", controller.getUserProfile().getMyPlacePassword());
            }
            job.put("IsMultipleEmails", controller.getUserProfile().isMultipleEmails());
            job.put("ISMultipleJobs", controller.getUserProfile().isMultipleJobs());
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
                    if (job.getBoolean(Common.Sucess_Key) == true) {

                        Utils.showToast(ValidateEmailId.this, job.getString(Common.Message), Common.sucessCase);
                        controller.setProfileInfo(job.toString());
                        if (controller.getUserProfile().isCentralLoginUser()) {
                            controller.setUserLoggedIn(true);
                            Intent in = new Intent(ValidateEmailId.this, DashboardNewActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(in);
                            finish();
                        } else {
                            if (needToShowSecondaryDialog(controller.getUserProfile())) {
                                showAlertForSecondUser();
                            } else {
                                navigateToNextScreen();
                            }
                        }


                    } else {
                        Utils.showToast(ValidateEmailId.this, job.getString(Common.Message), Common.errorCase);
                    }
                }else {
                    Utils.showToast(ValidateEmailId.this, jobb.getString(Common.Message), Common.errorCase);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
            }

        } else {
            Utils.showToast(ValidateEmailId.this, Common.somethingErrorMessage, Common.errorCase);
        }
    }

    @Override
    public void onErrorResult(String error) {
        Utils.showToast(ValidateEmailId.this, error, Common.errorCase);
    }

    public void navigateToNextScreen() {
        if (controller.getUserProfile().getPassCode().length() > 0) {
            if (controller.getUserProfile().getMyPlacePassword() == null || controller.getUserProfile().getMyPlacePassword().length() == 0) {
                controller.getUserProfile().setMyPlacePassword(password.getText().toString());
            }
            if (controller.getUserProfile().getJobNumer() == null || controller.getUserProfile().getJobNumer().length() == 0) {
                controller.getUserProfile().setJobNumer(jobNumber.getText().toString());
            }
            if (controller.getUserProfile().getEmail() == null || controller.getUserProfile().getEmail().length() == 0) {
                controller.getUserProfile().setEmail(emailId.getText().toString());
            }
            Intent in = new Intent(ValidateEmailId.this, SetPassword.class);
            startActivity(in);
        }
    }

    public boolean needToShowSecondaryDialog(UserProfile profile) {
        if (profile.isMultipleEmails() == true) {
            ArrayList<MyPlaceJobDetails> myPlaceJobDetailses = new ArrayList<MyPlaceJobDetails>();
            if (profile.getUserDetailses() != null && profile.getUserDetailses().size() > 0) {
                myPlaceJobDetailses.addAll(profile.getUserDetailses().get(0).getMyPlaceJobDetailses());
                for (int j = 0; j < myPlaceJobDetailses.size(); j++) {
                    MyPlaceJobDetails myPlaceJobDetails = myPlaceJobDetailses.get(j);
                    if (myPlaceJobDetails.getJobNo().equalsIgnoreCase(profile.getJobNumer())) {
                        ArrayList<MyPlaceEmail> myPlaceEmails = new ArrayList<MyPlaceEmail>();
                        myPlaceEmails.addAll(myPlaceJobDetails.getMyPlaceEmails());
                        for (int i = 0; i < myPlaceEmails.size(); i++) {
                            MyPlaceEmail myPlaceEmail = myPlaceEmails.get(i);
                            if (myPlaceEmail.isPrimaryUser()) {
                                if (!myPlaceEmail.getEmail().equals(controller.getLastLoggedEmailId())) {
                                    return true; 
                                }
                            }
                        }
                    }
                }
            }

        }
        return false;
    }

    public void showAlertForSecondUser() {
        final Dialog exitDialog = new Dialog(ValidateEmailId.this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setCanceledOnTouchOutside(false);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) exitDialog.findViewById(R.id.dialogOkButton);
        final Button dialogCancelButton = (Button) exitDialog.findViewById(R.id.dialogCancelButton);
        final TextView dialogMessageTextView = (TextView) exitDialog.findViewById(R.id.dialogMessageTextView);
        dialogMessageTextView.setText("An email is already associated with your job number as primary, \n Do you want to continue as a co-applicant?");
        LinearLayout cancelButtonLayout = (LinearLayout) exitDialog.findViewById(R.id.cancelButtonLayout);
        cancelButtonLayout.setVisibility(View.VISIBLE);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextScreen();
                exitDialog.dismiss();
            }
        });

        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }
}
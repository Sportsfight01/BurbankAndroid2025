package com.dmss.burbankappold;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dmss.burbankappold.dashboard.DashboardNewActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import common.AppController;
import common.Common;
import common.CustomEditText;
import common.Receiver;
import common.TransparentProgressDialog;
import common.Utils;
import interfaces.WebApiResponseCallback;
import models.UserProfile;

/**
 * Created by Ashish.Kumar on 30-05-2017.
 */

public class SetPassword extends AppCompatActivity implements View.OnClickListener, WebApiResponseCallback {
    Button resendPasscodeButton, submitButton;
    AppController controller;
    CustomEditText setPasswordEmailET, otpEditText, forgotPwdNewPwdEditText, forgotPwdConfirmPwdEditText;
    String passCode, password;
    int apiCall;
    static int resendApiCall = 1, setPasswordApiCall = 2;
    Receiver myBroadcastReceiver;
    //TextView heading;
    Button magicButton;
    TransparentProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        customActionBar();
        initializeAll();
        infoDialog(false);
    }

    private void initializeAll() {
        controller = (AppController) getApplication();
        myBroadcastReceiver = new Receiver();
        magicButton = (Button) findViewById(R.id.magicButton);
        //heading = (TextView) findViewById(R.id.heading);
        //heading.setTypeface(controller.getTypeface());
        resendPasscodeButton = (Button) findViewById(R.id.resendPasscodeButton);
        resendPasscodeButton.setOnClickListener(this);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        setPasswordEmailET = (CustomEditText) findViewById(R.id.setPasswordEmailET);
        setPasswordEmailET.customEditText.setEnabled(false);
        setPasswordEmailET.setImage(R.drawable.icon_user);
        setPasswordEmailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if (controller.getUserProfile().getEmail() != null && controller.getUserProfile().getEmail().length() > 0) {
            setPasswordEmailET.setText(controller.getUserProfile().getEmail());
            setPasswordEmailET.setImage(R.drawable.mail_icon);
        } else if (!controller.getUserProfile().getJobNumer().equalsIgnoreCase("null")) {
            setPasswordEmailET.setText(controller.getUserProfile().getJobNumer());
            setPasswordEmailET.setImage(R.drawable.jobnumber);
        }

        otpEditText = (CustomEditText) findViewById(R.id.otpEditText);
        otpEditText.setImage(R.drawable.passcode_icon);
        otpEditText.customEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        otpEditText.setHint("Enter Passcode");
        forgotPwdNewPwdEditText = (CustomEditText) findViewById(R.id.forgotPwdNewPwdEditText);
        forgotPwdNewPwdEditText.setImage(R.drawable.passsword_new);
        forgotPwdNewPwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        forgotPwdNewPwdEditText.setHint("Set App Password");
        forgotPwdNewPwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        forgotPwdConfirmPwdEditText = (CustomEditText) findViewById(R.id.forgotPwdConfirmPwdEditText);
        forgotPwdConfirmPwdEditText.setImage(R.drawable.passsword_new);
        forgotPwdConfirmPwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        forgotPwdConfirmPwdEditText.setHint("Confirm App Password");
        forgotPwdConfirmPwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        magicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpEditText.setText(controller.getUserProfile().getPassCode());
                otpEditText.setSelection(controller.getUserProfile().getPassCode().length());
            }
        });
    }

    /**
     * Method for custom action bar
     *
     * @return void.
     */
    public void customActionBar() {
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        ImageView back = (ImageView) findViewById(R.id.back_image);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        int id = v.getId();//finishing activity up on click of back arrow button
        if (id == R.id.resendPasscodeButton) {
            if (Utils.isNetworkAvailable(SetPassword.this)) {
                apiCall = resendApiCall;
                controller.webApiCall().postData(Common.setResendOtp_Url, getJson(), SetPassword.this, Utils.getProgress(SetPassword.this));
            }
        } else if (id == R.id.submitButton) {
            passCode = otpEditText.getText();
            password = forgotPwdNewPwdEditText.getText();
            String confirmPassword = forgotPwdConfirmPwdEditText.getText();
            if (passCode != null && passCode.length() > 0) {
                if (password != null && password.length() > 0) {
                    if (confirmPassword != null && confirmPassword.length() > 0) {
                        if (confirmPassword.equals(password)) {
                            if (Utils.isNetworkAvailable(SetPassword.this)) {
                                apiCall = setPasswordApiCall;
                                if ((controller.isUserLoggedInWithEmailId()) && (controller.getUserProfile().isNewUser())) {
                                    controller.webApiCall().postData(Common.verifyEmailAddUser_Url, getJson(), SetPassword.this, Utils.getProgress(SetPassword.this));
                                } else {
                                    controller.webApiCall().postData(Common.verifyEmailSetPassword_Url, getJson(), SetPassword.this, Utils.getProgress(SetPassword.this));
                                }
                            }
                        } else {
                            //information regarding mismatch of password and confirm password
                            Utils.showToast(SetPassword.this, "Password and confirm password are not matching", Common.errorCase);
                        }
                    } else {
                        //information regarding empty confirm password
                        Utils.showToast(SetPassword.this, "Confirm password field should not be empty", Common.errorCase);
                    }
                } else {
                    //information regarding empty password
                    Utils.showToast(SetPassword.this, "Password field should not be empty", Common.errorCase);
                }
            } else {
                //information regarding empty passcode
                Utils.showToast(SetPassword.this, "Passcode field should not be empty", Common.errorCase);
            }
        }
    }

    public String getJson() {
        JSONObject job = new JSONObject();
        try {
            job.put("Email", setPasswordEmailET.getText());

            if (apiCall == setPasswordApiCall) {

                if (!controller.getUserProfile().isNewUser()) {
                    //job.put("IsEmailNotMapped", true);
                    job.put("JobNumber", controller.getUserProfile().getJobNumer());
                    job.put("IsMultipleEmails", controller.getUserProfile().isMultipleEmails());
                    job.put("ISMultipleJobs", controller.getUserProfile().isMultipleJobs());
                    job.put("MyPlacePassword", controller.getUserProfile().getMyPlacePassword());
                }
                job.put("Passcode", passCode);
                job.put("centralLoginPassword", password);
                job.put("IsEmailNotMapped", controller.getUserProfile().isEmailNotMapped());
            }
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
                        if (apiCall == resendApiCall) {
                            controller.setProfileInfo(jobb.getJSONObject(Common.Result_Key).toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    infoDialog(true);
                                    otpEditText.setText("");
                                }
                            });

                            Utils.showToast(SetPassword.this, job.getString(Common.Message), Common.sucessCase);
                        } else if (apiCall == setPasswordApiCall) {

                            if (job.getBoolean("PassCodeExpired")) {
                                Utils.showToast(SetPassword.this, job.getString(Common.Message), Common.errorCase);
                            } else {
                                //Calling DashBoard
                                controller.setUserLoggedIn(true);
                                controller.setProfileInfo(job.toString());
                                UserProfile userProfile = controller.getUserProfile();
                                Utils.showToast(SetPassword.this, job.getString(Common.Message), Common.sucessCase);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setNotificationSelection();
                                    }
                                });
                            }
                        }
                    } else {
                        Utils.showToast(SetPassword.this, job.getString(Common.Message), Common.errorCase);
                    }
                }else {
                    Utils.showToast(SetPassword.this, jobb.getString(Common.Message), Common.errorCase);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
            }
        } else {
            Utils.showToast(SetPassword.this, "Please try again", Common.errorCase);
        }
    }

    @Override
    public void onErrorResult(String error) {
        Utils.showToast(SetPassword.this, error, Common.errorCase);
    }

    /*public void showErrorMessage(final String message,final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(success){
                    Utils.showToast(SetPassword.this, message, Common.sucessCase);
                }else{
                    Utils.showToast(SetPassword.this, message, Common.errorCase);
                }

            }
        });
    }*/

    @SuppressLint("SetTextI18n")
    public void infoDialog(boolean resend) {
        final Dialog infoDialog = new Dialog(SetPassword.this);
        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (infoDialog.getWindow() != null)
            infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        infoDialog.setContentView(R.layout.custom_dialog);
        final Button dialogOkButton = (Button) infoDialog.findViewById(R.id.dialogOkButton);
        final TextView dialogMessageTextView = (TextView) infoDialog.findViewById(R.id.dialogMessageTextView);
        TextView dialogHeadingTextView = (TextView) infoDialog.findViewById(R.id.dialogHeadingTextView);
        dialogHeadingTextView.setText("Email Verification");
        if (resend) {
            dialogMessageTextView.setText("Passcode has been sent again to your mail " + splitEmail(controller.getUserProfile().getEmail()));
        } else {
            if (controller.getUserProfile().isPassCodeAlreadySent()) {
                dialogMessageTextView.setText("A Passcode has been already sent to your mail " + splitEmail(controller.getUserProfile().getEmail()));
            } else {
                dialogMessageTextView.setText("A Passcode has been sent to your mail " + splitEmail(controller.getUserProfile().getEmail()));
            }

        }
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });
        infoDialog.show();
    }

    public String splitEmail(String mail) {
        if (mail != null && mail.contains("@")) {
            String[] sample = mail.split("@");
            if (sample[0].length() > 4) {
                return "xxx" + sample[0].substring(sample[0].length() - 4) + "@" + sample[1];
            } else if (sample[0].length() > 0) {
                return "xxx" + mail;
            } else {
                return "";
            }
        } else {
            return mail;
        }
    }


    public void setNotificationSelection() {
        dialog = Utils.getProgress(SetPassword.this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String progressDetails = controller.webApiCall().postData(Common.updateMyPlaceNotificationSetting, getUpdateSettingsJson());
                onSuccessNoticeSelection(progressDetails);
            }
        });
        t.start();
    }

    public void onSuccessNoticeSelection(final String noticeDetails) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject job = new JSONObject(noticeDetails);
                    boolean status = job.getBoolean("Status");
                    final String message = job.getString("Message");
                    if (status) {
                        JSONObject jsonObject = job.getJSONObject("Result");
                        JSONArray jsonArray = jsonObject.getJSONArray("NotificationTypes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject notification = jsonArray.getJSONObject(i);
                            switch (i) {
                                case 0:
                                    controller.setPhotoNotifications(!notification.isNull("IsUserOpted") && notification.getBoolean("IsUserOpted"));
                                    break;
                                case 1:
                                    controller.setProgressNotifications(!notification.isNull("IsUserOpted") && notification.getBoolean("IsUserOpted"));
                                    break;
                                case 2:
                                    controller.setStageNotifications(!notification.isNull("IsUserOpted") && notification.getBoolean("IsUserOpted"));
                                    break;
                            }
                        }
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    } else {
                        Utils.showToast(SetPassword.this, message, Common.errorCase);
                    }
                } catch (Exception ex) {
                    ex.fillInStackTrace();
                }

                Intent in = null;

                if (controller.getUserProfile().getUserDetails().get(0).isMyPlaceAccessible()) {
                    in = new Intent(SetPassword.this, DashboardNewActivity.class);
                } else {
                    in = new Intent(SetPassword.this, EmptyDashboard.class);
                }
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(in);
                finish();
            }
        });
    }

    public String getUpdateSettingsJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UserName", controller.getUserProfile().getUserDetailses().get(0).getFullName());
            jsonObject.put("Email", controller.getUserProfile().getUserDetailses().get(0).getEmail());
            jsonObject.put("UserId", controller.getUserProfile().getUserDetailses().get(0).getId());
            JSONArray jsonArray = new JSONArray();
            for (int i = 1; i < 4; i++) {
                JSONObject notication = new JSONObject();
                notication.put("NotificationTypeId", i);
                notication.put("IsActive", true);
                switch (i) {
                    case 1:
                        notication.put("IsUserOpted", true);
                        jsonArray.put(0, notication);
                        break;
                    case 2:
                        notication.put("IsUserOpted", true);
                        jsonArray.put(1, notication);
                        break;
                    case 3:
                        notication.put("IsUserOpted", true);
                        jsonArray.put(2, notication);
                        break;
                }


            }
            jsonObject.put("NotificationTypes", jsonArray);


        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }
}

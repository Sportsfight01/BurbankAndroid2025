package com.dmss.burbankappold;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dmss.burbankappold.databinding.ActivityUpdatePasswordBinding;

import org.json.JSONObject;

import common.AppController;
import common.Common;
import common.CustomEditText;
import common.Utils;
import interfaces.WebApiResponseCallback;

public class UpdatePassword extends BaseActivity implements WebApiResponseCallback {
    AppController controller;
    CustomEditText resetPasswordEmailET, resetOldPasswordEditText, resetNewPwdEditText, resetConfirmPwdEditText;
    Button resetSubmitButton;
    TextView resetPasswordHeading;
    String password;
    public static int resetPasswordApiCall = 0, updatePasswordApiCall = 1;
    int apiCall;
    private ActivityUpdatePasswordBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityUpdatePasswordBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        changeDarkStatusBarColor();
        apiCall = resetPasswordApiCall;
        initializeAll();
        intiToolBar();
        actionBarSettings();
        callApi();
    }

    private void intiToolBar(){
        mBinding.toolbar.ivNavMenu.setVisibility(View.GONE);
        mBinding.toolbar.ivToolBarBack.setVisibility(View.VISIBLE);
        mBinding.toolbar.ivToolBarBack.setOnClickListener(view -> onBackPressed());
        mBinding.toolbar.ivChat.setVisibility(View.GONE);
    }


    private void initializeAll() {
        mBinding.toolbar.line.setVisibility(View.VISIBLE);

        controller = (AppController) getApplication();
        resetSubmitButton = (Button) findViewById(R.id.resetSubmitButton);
        resetSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        resetPasswordEmailET = (CustomEditText) findViewById(R.id.resetPasswordEmailET);
        resetPasswordEmailET.setEnabledd(false);
        resetPasswordEmailET.setImage(R.drawable.email_icn);
        resetPasswordEmailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        resetPasswordEmailET.setText(controller.getUserProfile().getUserDetailses().get(0).getEmail());

        resetOldPasswordEditText = (CustomEditText) findViewById(R.id.resetOldPasswordEditText);
//        resetOldPasswordEditText.setImage(0);
        resetOldPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        resetOldPasswordEditText.customEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        resetOldPasswordEditText.setHint("Enter Current App Password");
        resetOldPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        resetNewPwdEditText = (CustomEditText) findViewById(R.id.resetNewPwdEditText);
//        resetNewPwdEditText.setImage(0);
        resetNewPwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        resetNewPwdEditText.setHint("Set New Password");
        resetNewPwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        resetConfirmPwdEditText = (CustomEditText) findViewById(R.id.resetConfirmPwdEditText);
//        resetConfirmPwdEditText.setImage(0);
        resetConfirmPwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        resetConfirmPwdEditText.setHint("Confirm password");
        resetConfirmPwdEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void actionBarSettings() {
          /*  ActionBar actionBar;
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
        actionBarHeadingTextView.setText("Reset Password");
        TextView saveSettings = (TextView) view.findViewById(R.id.saveSettings);
        saveSettings.setVisibility(View.GONE);
        actionBar.setCustomView(view, new     ActionBar.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        actionBar.setDisplayOptions(    ActionBar.DISPLAY_SHOW_CUSTOM |     ActionBar.DISPLAY_SHOW_HOME);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

    }

    public void callApi() {
        if (Utils.isNetworkAvailable(UpdatePassword.this)) {
            if (apiCall == updatePasswordApiCall) {
                controller.webApiCall().postData(Common.updatePasswordUrl, getJson(), UpdatePassword.this, Utils.getProgress(UpdatePassword.this));
            } else {
                controller.webApiCall().postData(Common.resetUserPassword_Url, getJson(), UpdatePassword.this, Utils.getProgress(UpdatePassword.this));
            }
        }
    }

    public String getJson() {
        JSONObject job = new JSONObject();
        try {
            if (apiCall == updatePasswordApiCall) {
                job.put("Email",controller.getUserProfile().getUserDetails().get(0).getEmail());
                job.put("CentralLoginPassword",resetNewPwdEditText.getText().toString().trim() );
            } else {
                job.put("Email", controller.getUserProfile().getUserDetails().get(0).getEmail());
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return job.toString();
    }
    public void showMessageAlert(Activity mContext, String message,String resultcode) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(message);
                builder.setTitle(mContext.getString(R.string.app_name));
                builder.setCancelable(false);
                builder.setPositiveButton(Html.fromHtml("<font color=" + mContext.getResources().getColor(R.color.appColor) + ">OK</font>"), (DialogInterface.OnClickListener) (dialog, which) -> {
                    controller.setProfileInfo(resultcode);
                    finish();
                    dialog.dismiss();
                });
       /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/

                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });
    }
    @Override
    public void onSuccessResult(String result) {
        Log.d("TAG", "onSuccessResult: " + result);
        if (result != null) {
            try {
                JSONObject job = new JSONObject(result);
                JSONObject jobb = job.getJSONObject(Common.Result_Key);
                if (job.getBoolean(Common.Status_Key) == true) {
                    if (apiCall == updatePasswordApiCall) {
                        if (jobb.getBoolean(Common.Sucess_Key)) {
                            showMessageAlert(UpdatePassword.this,job.getString(Common.Message),jobb.toString());

                        }
//                        Common.showMessageAlert(UpdatePassword.this, job.getString(Common.Message));
//                        Utils.showToast(UpdatePassword.this, job.getString(Common.Message), Common.sucessCase);
                    } else {
                        password = jobb.getString("Password");
                    }
                } else {
                    Common.showMessageAlert(UpdatePassword.this, job.getString(Common.Message));

//                    Utils.showToast(UpdatePassword.this, job.getString(Common.Message), Common.errorCase);
                }
            } catch (Exception ex) {
                ex.fillInStackTrace();
            }
        } else {
            Utils.showToast(UpdatePassword.this, Common.somethingErrorMessage, Common.errorCase);
            if (apiCall == resetPasswordApiCall) {
                finish();
            }
        }
    }

    @Override
    public void onErrorResult(String error) {

    }

    public void updatePassword() {
        String enteredNewPassword = resetNewPwdEditText.getText().toString();
        String enteredConfirmPassword = resetConfirmPwdEditText.getText().toString();
        String enteredOldPassword = resetOldPasswordEditText.getText().toString();
        if(enteredOldPassword==null || enteredOldPassword==""){
            Common.showMessageAlert(UpdatePassword.this, "Please enter your current password");
        }else if(enteredNewPassword==null || enteredNewPassword==""){
            Common.showMessageAlert(UpdatePassword.this, "Please enter new password");
        }
        if(enteredNewPassword.length()<6){
            Common.showMessageAlert(UpdatePassword.this,"Please enter minimum 6 characters for new password");
        }
        else if(enteredConfirmPassword==null || enteredConfirmPassword==""){
            Common.showMessageAlert(UpdatePassword.this, "Please enter confirm password");
        }else if(!enteredConfirmPassword.equals(enteredNewPassword)){
            Common.showMessageAlert(UpdatePassword.this, "New and confirm password does not match");
        }else if(!enteredOldPassword.equals(password)){
            Common.showMessageAlert(UpdatePassword.this, "Current password is incorrect");
        }
        else{
            apiCall = updatePasswordApiCall;
            callApi();
        }
    }
}

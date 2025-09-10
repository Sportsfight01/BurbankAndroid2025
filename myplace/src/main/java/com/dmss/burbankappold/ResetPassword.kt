package com.dmss.burbankappold

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import interfaces.WebApiResponseCallback
import com.dmss.burbankappold.ResetPassword
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.EmptyDashboard
import common.*
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by Ashish.Kumar on 09-06-2017.
 */
class ResetPassword : BaseActivity(), View.OnClickListener, WebApiResponseCallback {
    var resendPasscodeButton: Button? = null
    var submitButton: Button? = null
    var controller: AppController? = null
    var setPasswordEmailET: CustomEditText? = null
    var otpEditText: CustomEditText? = null
    var forgotPwdNewPwdEditText: CustomEditText? = null
    var forgotPwdConfirmPwdEditText: CustomEditText? = null
    var passCode: String? = null
    var password: String? = null
    var apiCall = 0
    var myBroadcastReceiver: Receiver? = null
    var heading: TextView? = null
    var magicButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_password)
        customActionBar()
        changeStatusBarColor()
        initializeAll()
    }

    fun backPressed() {
        val back_image = findViewById<View>(R.id.back_image) as ImageView
        back_image.setOnClickListener { onBackPressed() }
    }

    private fun initializeAll() {
        myBroadcastReceiver = Receiver()
        controller = application as AppController
        // heading = (TextView) findViewById(R.id.heading);
        val b = intent.extras
        val SucessMssage = b!!.getString(Common.SucessMssage)
        if (SucessMssage != null) {
            Common.showMessageAlert(this@ResetPassword, SucessMssage)
        }
        backPressed()
        magicButton = findViewById<View>(R.id.magicButton) as Button
        /*heading.setText("Forgot Password");
        heading.setTypeface(controller.getTypeface());*/resendPasscodeButton =
            findViewById<View>(R.id.resendPasscodeButton) as Button
        resendPasscodeButton!!.setOnClickListener(this)
        submitButton = findViewById<View>(R.id.submitButton) as Button
        submitButton!!.setOnClickListener(this)
        setPasswordEmailET = findViewById<View>(R.id.setPasswordEmailET) as CustomEditText
        setPasswordEmailET!!.customEditText.isEnabled = false
        setPasswordEmailET!!.setImage(0)
        setPasswordEmailET!!.setEnabledd(false)
        setPasswordEmailET!!.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        setPasswordEmailET!!.text = controller!!.userProfile.email
        otpEditText = findViewById<View>(R.id.otpEditText) as CustomEditText
        otpEditText!!.setImage(0)
        otpEditText!!.setInputType(InputType.TYPE_CLASS_NUMBER)
        otpEditText!!.customEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        otpEditText!!.setHint("Enter Passcode")
        magicButton!!.setOnClickListener {
            otpEditText!!.text = controller!!.userProfile.passCode
            otpEditText!!.setSelection(controller!!.userProfile.passCode.length)
        }
        forgotPwdNewPwdEditText = findViewById<View>(R.id.forgotPwdNewPwdEditText) as CustomEditText
        forgotPwdNewPwdEditText!!.setImage(0)
        forgotPwdNewPwdEditText!!.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        forgotPwdNewPwdEditText!!.setHint("New Password")
        forgotPwdNewPwdEditText!!.setTransformationMethod(PasswordTransformationMethod.getInstance())
        forgotPwdConfirmPwdEditText =
            findViewById<View>(R.id.forgotPwdConfirmPwdEditText) as CustomEditText
        forgotPwdConfirmPwdEditText!!.setImage(0)
        forgotPwdConfirmPwdEditText!!.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        forgotPwdConfirmPwdEditText!!.setHint("Confirm Password")
        forgotPwdConfirmPwdEditText!!.setTransformationMethod(PasswordTransformationMethod.getInstance())
    }

    /**
     * Method for custom action bar
     *
     * @return void.
     */
    fun customActionBar() {
        //getSupportActionBar().hide();
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
    }

    val json: String
        get() {
            val job = JSONObject()
            try {
                job.put("Email", controller!!.userProfile.email)
                job.put("name", controller!!.userProfile.userDetails[0].firstName)
                if (apiCall == setPasswordApiCall) {
                    job.put("Passcode", otpEditText!!.text.toString())
                    job.put("JobNumber", controller!!.userProfile.jobNumer)
                    job.put("CentralLoginPassword", forgotPwdNewPwdEditText!!.text.toString())
                }
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }

    public override fun onResume() {
        super.onResume()
        registerReceiver(myBroadcastReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(myBroadcastReceiver)
    }

    override fun onClick(v: View) {
        if (v.id == submitButton!!.id) {
            passCode = otpEditText!!.text
            password = forgotPwdNewPwdEditText!!.text
            val confirmPassword = forgotPwdConfirmPwdEditText!!.text
            if (passCode != null && passCode!!.length > 0) {
                if (password != null && password!!.length > 0) {
                    if (password!!.length < 6) {
                        Common.showMessageAlert(
                            this@ResetPassword,
                            "Please enter minimum 6 characters for new password"
                        )
                    } else if (confirmPassword != null && confirmPassword.length > 0) {
                        if (confirmPassword == password) {
                            apiCall = setPasswordApiCall
                            controller!!.webApiCall().postData(
                                Common.updatePasswordUrl,
                                json,
                                this@ResetPassword,
                                Utils.getProgress(this@ResetPassword)
                            )
                        } else {
                            //information regarding mismatch of password and confirm password
//                            Utils.showToast(ResetPassword.this, "Password and confirm password are not matching", Common.errorCase);
                            Common.showMessageAlert(
                                this@ResetPassword,
                                "Password and confirm password are not same"
                            )
                        }
                    } else {
                        //information regarding empty confirm password
//                        Utils.showToast(ResetPassword.this, "Confirm password field should not be empty", Common.errorCase);
                        Common.showMessageAlert(this@ResetPassword, "Please enter confirm password")
                    }
                } else {
                    //information regarding empty password
//                    Utils.showToast(ResetPassword.this, "Password field should not be empty", Common.errorCase);
                    Common.showMessageAlert(this@ResetPassword, "Please enter password")
                }
            } else {
                //information regarding empty passcode
//                Utils.showToast(ResetPassword.this, "Passcode field should not be empty", Common.errorCase);
                Common.showMessageAlert(this@ResetPassword, "Please enter passcode")
            }
        } else {
            if (Utils.isNetworkAvailableWithError(this)) {
                apiCall = resendApiCall
                controller!!.webApiCall().postData(
                    Common.setResendOtp_Url,
                    json,
                    this@ResetPassword,
                    Utils.getProgress(this@ResetPassword)
                )
            }
        }
    }

    fun splitEmail(mail: String?): String? {
        return if (mail != null && mail.contains("@")) {
            val sample = mail.split("@".toRegex()).toTypedArray()
            if (sample[0].length > 4) {
                "xxx" + sample[0].substring(sample[0].length - 4) + "@" + sample[1]
            } else if (sample[0].length > 0) {
                "xxx$mail"
            } else {
                ""
            }
        } else {
            mail
        }
    }

    fun infoDialog(resend: Boolean) {
        runOnUiThread {
            val infoDialog = Dialog(this@ResetPassword)
            infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            infoDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            infoDialog.setContentView(R.layout.custom_dialog)
            val dialogOkButton = infoDialog.findViewById<View>(R.id.dialogOkButton) as Button
            val dialogMessageTextView =
                infoDialog.findViewById<View>(R.id.dialogMessageTextView) as TextView
            val dialogHeadingTextView =
                infoDialog.findViewById<View>(R.id.dialogHeadingTextView) as TextView
            dialogHeadingTextView.text = getString(R.string.app_name)
            dialogMessageTextView.text = "Passcode sent to Email successfully"
            dialogOkButton.setOnClickListener { infoDialog.dismiss() }
            infoDialog.show()
        }
    }

    override fun onSuccessResult(result: String) {
        if(Utils.isNetworkAvailableWithError(this)) {
            if (result != null) {
                Log.w("pass code output", result)
                try {
                    val jobb = JSONObject(result)
                    if (!jobb.getBoolean("Status")) {
                        Common.showMessageAlert(this@ResetPassword, jobb.getString(Common.Message))
                    }
                    val job = jobb.getJSONObject(Common.Result_Key)
                    if (apiCall == resendApiCall) {
//                    infoDialog(true);
//                    Utils.showToast(ResetPassword.this, job.getString(Common.Message), Common.sucessCase);
                        Common.showMessageAlert(this@ResetPassword, job.getString(Common.Message))
                    } else if (apiCall == setPasswordApiCall) {
                        println("setPasswordApiCall:: " + job.getBoolean(Common.Sucess_Key))
                        if (job.getBoolean(Common.Sucess_Key)) {
                            val intent: Intent? = null
                            /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            intent = new Intent(ResetPassword.this, Login.class);
                        }
                        startActivity(intent);*/
//                        if (Utils.isNetworkAvailable(ResetPassword.this)) {
                            val jsonObj = loginJson
                            /*   TransparentProgressDialog dialog = null;
                        ResetPassword.this.runOnUiThread(new Runnable() {
                            public void run() {
                                dialog=new TransparentProgressDialog(this, R.drawable.ic_loading)
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                dialog.show();
                            }
                        });*/controller!!.webApiCall()
                                .postData(Common.centralUserLogin_Url, jsonObj, this@ResetPassword)


                            /*   controller.webApiCall().postData(
                                    Common.centralUserLogin_Url,
                                    jsonObj,
                                    ResetPassword.this,
                            Utils.getProgress(ResetPassword.this));*/apiCall = setLoginApiCall
                            controller!!.analytics.loginScreenLoginButtonTouchEvent()

//                        }
//                        controller.setProfileInfo(job.toString());
//                        finish();
                        } else {
                            Utils.showToast(
                                this@ResetPassword,
                                job.getString(Common.Message),
                                Common.sucessCase
                            )
                        }
                        Utils.showToast(
                            this@ResetPassword,
                            job.getString(Common.Message),
                            Common.sucessCase
                        )
                    } else if (apiCall == setLoginApiCall) {
                        println("setPasswordApiCall:: " + job.getBoolean(Common.Sucess_Key))
                        controller!!.setProfileInfo(job.toString())
                        Utils.showToast(
                            this@ResetPassword,
                            "Loggedin Sucessfully.",
                            Common.sucessCase
                        )
                        controller!!.isLoggedIn = true
                        if (controller!!.userProfile.userDetails[0].isMyPlaceAccessible) {
                            val intent =
                                Intent(this@ResetPassword, DashboardNewActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@ResetPassword, EmptyDashboard::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                            finish()
                        }
                    }
                } catch (ex: Exception) {
                    ex.fillInStackTrace()
                }
            } else {
                Utils.showToast(this@ResetPassword, Common.somethingErrorMessage, Common.errorCase)
            }
        }
    }

    override fun onErrorResult(error: String) {
        if(Utils.isNetworkAvailableWithError(this))
        Utils.showToast(this@ResetPassword, Common.somethingErrorMessage, Common.errorCase)
    }

    private val loginJson: String
        private get() {
            val job = JSONObject()
            try {
                job.put("Email", setPasswordEmailET!!.text.toString())
                job.put("centralLoginPassword", forgotPwdConfirmPwdEditText!!.text)
            } catch (e: Exception) {
                e.fillInStackTrace()
            }
            return job.toString()
        }

    companion object {
        var resendApiCall = 1
        var setPasswordApiCall = 2
        var setLoginApiCall = 3
    }
}
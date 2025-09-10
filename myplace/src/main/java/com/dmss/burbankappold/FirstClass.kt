package com.dmss.burbankappold

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.Layout
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.text.style.AlignmentSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import interfaces.WebApiResponseCallback
import com.google.gson.Gson
import com.dmss.burbankappold.ResetPassword
import models.UserProfile
import com.dmss.burbankappold.SetPassword
import com.dmss.burbankappold.MyPlaceLogin
import com.dmss.burbankappold.ValidateEmailId
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.AppConstants
import common.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.MyPlaceEmail
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList


class FirstClass : BaseActivity(), View.OnClickListener, WebApiResponseCallback {


    var next: LinearLayout? = null
    var signINLL: LinearLayout? = null
    var emailLL: LinearLayout? = null
    var submitBT: LinearLayout? = null
    var heading: TextView? = null
    var nextTextView: TextView? = null
    var forgotPasswordTV: TextView? = null
    var emailHeadingTV: TextView? = null
    var controller: AppController? = null
    var emailOrJobNumberEditText: CustomEditText? = null
    var passwordEditText: CustomEditText? = null
    var emailET: CustomEditText? = null
    var enteredEmail = 0
    var enteredJobNumber = 1
    var enteredValue = 0
    var isPasswordVisible = false
    var myBroadcastReceiver: Receiver? = null
    var lastLoggedInUser = ""
    var showFingerPrint = false
    var manualKill = false
    var forgotPasswordUIVisible = false
    var forgotEmailCheck = false
    var isForgetPasswordRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firstscreen)
        //changeStatusBarColor();
        controller = applicationContext as AppController
        controller!!.getAnalytics().landingScreenLoadingEvent()
        controller!!.getAnalytics().setScreen(this, "Landing_screen")
        customActionBar()
        initializeAllView()
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermission()) {
                Log.v("TAG", "Permission is granted")
                //File write logic here
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@FirstClass,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@FirstClass,
                "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@FirstClass,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                22
            )
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@FirstClass,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            false
        }
    }



    public override fun onPause() {
        super.onPause()
        unregisterReceiver(myBroadcastReceiver)
        /* if(manualKill){
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), "com.dmss.burbankapp.ui.login.ChooseOptionsActivity");
            startActivity(intent);
            finish();
        }*/
    }

    /**
     * Method for custom action bar
     *
     * @return void.
     */
    fun customActionBar() {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        //getSupportActionBar().hide();

        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setCustomView(R.layout.tool_home);
        View view = getActionBar().getCustomView();*/
        val back = findViewById<View>(R.id.back_image) as ImageView
        back.setOnClickListener {
            onBackPressed()
            /* Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.dmss.burbankappv2");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                    if(forgotPasswordUIVisible){
                        finish();
                        startActivity(getIntent());
                    }else{
                        manualKill = true;
                        finish();
                    }*/
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun initializeAllView() {
        lastLoggedInUser = controller!!.lastLoggedEmailId.toString().trim { it <= ' ' }
        myBroadcastReceiver = Receiver()
        heading = findViewById<View>(R.id.heading) as TextView
        nextTextView = findViewById<View>(R.id.nextTextView) as TextView
        forgotPasswordTV = findViewById<View>(R.id.forgotPasswordTV) as TextView
        emailHeadingTV = findViewById<View>(R.id.emailHeadingTV) as TextView
        emailOrJobNumberEditText =
            findViewById<View>(R.id.emailOrJobNumberEditText) as CustomEditText
        passwordEditText = findViewById<View>(R.id.passwordEditText) as CustomEditText
        emailET = findViewById<View>(R.id.emailET) as CustomEditText
        emailET!!.setImage(R.drawable.mail_icon)
        next = findViewById<View>(R.id.next_btn) as LinearLayout
        signINLL = findViewById<View>(R.id.signINLL) as LinearLayout
        emailLL = findViewById<View>(R.id.emailLL) as LinearLayout
        submitBT = findViewById<View>(R.id.submitBT) as LinearLayout
        emailOrJobNumberEditText!!.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        passwordEditText!!.setImage(R.drawable.passsword_new)
        passwordEditText!!.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        passwordEditText!!.setHint(" Enter MyPlace Password")
        emailOrJobNumberEditText!!.setHint("Email / Job Number")
        passwordEditText!!.setTransformationMethod(PasswordTransformationMethod.getInstance())
        nextTextView!!.text = "Next"
        nextTextView!!.setTextColor(resources.getColor(R.color.white))
        next!!.setOnClickListener(this)
        emailOrJobNumberEditText!!.setImage(0)
        if (controller!!.lastLoggedEmailId.length > 0) {
            emailOrJobNumberEditText!!.text =
                controller!!.lastLoggedEmailId.toString().trim { it <= ' ' }
            emailOrJobNumberEditText!!.setSelection(
                controller!!.lastLoggedEmailId.toString().trim { it <= ' ' }.length
            )
            controller!!.isUserLoggedInWithEmailId = true
            /*if (controller.getLastLoggedEmailId().contains("@")) {
                emailOrJobNumberEditText.setImage(R.drawable.mail_icon);
            } else {
                emailOrJobNumberEditText.setImage(R.drawable.jobnumber);
            }*/
        } else {
            controller!!.isUserLoggedInWithEmailId = false
        }
        emailOrJobNumberEditText!!.customEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().contains("@")) {
                    isPasswordVisible = false
                    enable_disablePasswordScreen(isPasswordVisible)
                    controller!!.isUserLoggedInWithEmailId = true
                    //  emailOrJobNumberEditText.setImage(R.drawable.mail_icon);
                } else {
                    /* if (s.toString().length() == 0) {
                        emailOrJobNumberEditText.setImage(R.drawable.icon_user);
                    } else {
                        emailOrJobNumberEditText.setImage(R.drawable.jobnumber);
                    }*/
                    controller!!.isUserLoggedInWithEmailId = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onClick(v: View) {
        if (v.id == next!!.id) {
            forgotEmailCheck = false
            if (emailOrJobNumberEditText!!.text.trim { it <= ' ' }.length > 0) {
                if (emailOrJobNumberEditText!!.text.toString()
                        .matches(Regex("[a-zA-Z]+")) && !controller!!.validation.validateEmail(
                        emailOrJobNumberEditText,
                        this@FirstClass
                    )
                ) {
                    Common.showMessageAlert(this@FirstClass, "Please enter valid email")
                } else if (emailOrJobNumberEditText!!.text.toString()
                        .contains("@") && emailOrJobNumberEditText!!.text.toString().contains(".")
                ) {
                    enteredValue = enteredEmail
                    controller!!.isUserLoggedInWithEmailId = true
                    if (Utils.isNetworkAvailableWithError(this@FirstClass)) {
                        isForgetPasswordRequested = false
                        controller!!.webApiCall().postData(
                            Common.IsUserPresent_Url,
                            json,
                            this@FirstClass,
                            Utils.getProgress(this@FirstClass)
                        )
                    }
                    controller!!.getAnalytics().landingScreenNextButtonTouchEvent()
                } else {
                    enteredValue = enteredJobNumber
                    controller!!.jobNumber = emailOrJobNumberEditText!!.text.toString()
                    controller!!.isUserLoggedInWithEmailId = false
                    if (isPasswordVisible == true) {
                        if (passwordEditText!!.text.length > 0) {
                            if (Utils.isNetworkAvailableWithError(this@FirstClass)) {
                                isForgetPasswordRequested = false
                                controller!!.webApiCall().postData(
                                    Common.IsUserPresent_Url,
                                    jobNumberJson,
                                    this@FirstClass,
                                    Utils.getProgress(this@FirstClass)
                                )
                            } else {
//                                Utils.showToast(FirstClass.this, "Please enter my place password.", Common.errorCase);
                                Common.showMessageAlert(
                                    this@FirstClass,
                                    "Please enter my place password."
                                )
                            }
                        }
                    } else {
                        if (Utils.isNetworkAvailableWithError(this@FirstClass)) {
                            isForgetPasswordRequested = false
                            controller!!.webApiCall().postData(
                                Common.IsUserPresent_Url,
                                jobNumberJson,
                                this@FirstClass,
                                Utils.getProgress(this@FirstClass)
                            )
                        }
                    }
                }
            } else {
//                Utils.showToast(FirstClass.this, "Email/ Job field should not be empty", Common.errorCase);
                Common.showMessageAlert(this@FirstClass, "Please enter email or job number")
            }
        } else if (v.id == forgotPasswordTV!!.id) {
            forgotPasswordUI()
        } else if (v.id == submitBT!!.id) {
            forgotEmailCheck = true
            if (emailET!!.text.trim { it <= ' ' }.length > 0) {
                if (controller!!.validation.validateEmail(emailET, this@FirstClass)) {
                    if (Utils.isNetworkAvailableWithError(this@FirstClass)) {
                        isForgetPasswordRequested = false
                        controller!!.webApiCall().postData(
                            Common.IsUserPresent_Url,
                            forgotEmailCheckJson,
                            this@FirstClass,
                            Utils.getProgress(this@FirstClass)
                        )
                    }
                } else {
//                    Utils.showToast(FirstClass.this, "Please enter valid Email ", Common.errorCase);
                    Common.showMessageAlert(this@FirstClass, "Please enter valid Email ")
                }
            } else {
                Utils.showToast(this@FirstClass, "Please enter Email ", Common.errorCase)

//                Utils.showToast(FirstClass.this, "Email field should not be empty", Common.errorCase);
            }
        }
    }

    val json: String
        get() {
            val job = JSONObject()
            try {
                job.put("Email", emailOrJobNumberEditText!!.text.toString().trim { it <= ' ' })
                var length = emailOrJobNumberEditText!!.text.toString().trim { it <= ' ' }.length
                length = length
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }
    val jobNumberJson: String
        get() {
            val job = JSONObject()
            try {
                job.put("JobNumber", emailOrJobNumberEditText!!.text.toString().trim { it <= ' ' })
                val length = emailOrJobNumberEditText!!.text.toString().trim { it <= ' ' }.length
                if (isPasswordVisible == true) {
                    job.put("MyPlacePassword", passwordEditText!!.text.toString())
                }
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }
    val forgotEmailCheckJson: String
        get() {
            val job = JSONObject()
            try {
                job.put("Email", emailET!!.text.toString().trim { it <= ' ' })
                var length = emailOrJobNumberEditText!!.text.toString().trim { it <= ' ' }.length
                length = length
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }
    val forgetPasswordJson: String
        get() {
            val job = JSONObject()
            try {
                job.put("JobNumber", emailOrJobNumberEditText!!.text.toString().trim { it <= ' ' })
                job.put("Email", emailET!!.text.toString())
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }

    override fun onSuccessResult(result: String) {
        if (result != null) {
//            println("onSuccessResult:: " + Gson().toJson(result))
            try {
                val job = JSONObject(result)
                if (job.has(Common.Result_Key)) {
                    val jobb = job.getJSONObject(Common.Result_Key)
                    Log.d("TAG", "onSuccessResult: $jobb")
                    val jobnumber = jobb.getString("JobNumber")
                    AppController.setPreference(this, AppController.JOBNUMBER, jobnumber)
                    if (isForgetPasswordRequested) {
                        isForgetPasswordRequested = false
                        if (job.getBoolean(Common.Status_Key) && jobb.getBoolean(Common.Sucess_Key)) {
                            Utils.showToast(
                                this@FirstClass,
                                job.getString(Common.Message),
                                Common.sucessCase
                            )
                            controller!!.setProfileInfo(jobb.toString())
                            val `in` = Intent(this@FirstClass, ResetPassword::class.java)
                            startActivity(`in`)
                        } else {
//                            Utils.showToast(FirstClass.this, job.getString(Common.Message), Common.errorCase);
                            Common.showMessageAlert(this@FirstClass, job.getString(Common.Message))
                        }
                    } else {
                        if (job.getBoolean(Common.Status_Key)) {
                            if (forgotEmailCheck) {
                                if (jobb.getBoolean(Common.Sucess_Key)) {
                                    if (jobb.getBoolean(Common.NewUser_Key)) {
                                        Utils.showToast(
                                            this@FirstClass,
                                            "Email address is not found. Please contact your New Home Coordinator for assistance.",
                                            Common.errorCase
                                        )
                                    } else {
                                        isForgetPasswordRequested = true
                                        controller!!.webApiCall().postData(
                                            Common.forgetPasswordUrl,
                                            forgetPasswordJson,
                                            this@FirstClass,
                                            Utils.getProgress(this@FirstClass)
                                        )
                                    }
                                }
                            } else {
                                if (job.getString(Common.Message)
                                        .contains("Please enter myplace password for the Job")
                                ) {
                                    isPasswordVisible = true
                                    enable_disablePasswordScreen(isPasswordVisible)
                                    Common.showMessageAlert(
                                        this@FirstClass,
                                        job.getString(Common.Message)
                                    )
                                } else {
                                    if (jobb.getBoolean(Common.Sucess_Key)) {
                                        if (jobb.getBoolean(Common.NewUser_Key)) {
                                            Common.showMessageAlert(
                                                this@FirstClass,
                                                "Email address is not found. Please contact your New Home Coordinator for assistance."
                                            )

//                                            Utils.showToast(FirstClass.this, "Email address is not found. Please contact your New Home Coordinator for assistance.", Common.errorCase);
                                        } else {
                                            controller!!.setLoggedInEmailId(emailOrJobNumberEditText!!.text.toString())
                                            controller!!.setProfileInfo(
                                                job.getJSONObject(Common.Result_Key).toString()
                                            )
                                            if (controller!!.userProfile.myPlacePassword.length == 0) {
                                                controller!!.userProfile.myPlacePassword =
                                                    passwordEditText!!.text.toString()
                                            }
                                            controller!!.setProfileInfo(jobb.toString())
                                            CallScreen_According_To_Response()
                                        }
                                    } else {
                                        Utils.showToast(
                                            this@FirstClass,
                                            job.getString(Common.Message),
                                            Common.errorCase
                                        )
                                        if (job.getString(Common.Message)
                                                .contains("You haven't mapped your email")
                                        ) {
                                            controller!!.setLoggedInEmailId(emailOrJobNumberEditText!!.text.toString())
                                            controller!!.setProfileInfo(jobb.toString())
                                            controller!!.isEmailNotMapped = true
                                            navigateToScreen(4)
                                        }
                                    }
                                }
                            }
                        } else {
                            Common.showMessageAlert(this@FirstClass, job.getString(Common.Message))
                        }
                    }
                } else {
                    Common.showMessageAlert(this@FirstClass, job.getString(Common.Message))
                }
            } catch (ex: Exception) {
                ex.fillInStackTrace()
                Common.showMessageAlert(this@FirstClass, Common.somethingErrorMessage)
            }
        } else {
            Common.showMessageAlert(this@FirstClass, Common.somethingErrorMessage)
        }
    }

    fun enable_disablePasswordScreen(`val`: Boolean) {
        runOnUiThread {
            if (`val` == true) {
                passwordEditText!!.visibility = View.VISIBLE
                forgotPasswordTV!!.visibility = View.VISIBLE
            } else {
                passwordEditText!!.text = ""
                passwordEditText!!.visibility = View.GONE
                forgotPasswordTV!!.visibility = View.GONE
            }
        }
    }

    override fun onErrorResult(error: String) {
        Utils.showToast(this@FirstClass, Common.somethingErrorMessage, Common.errorCase)
        Log.d("ServerUrl", error)
    }

    fun CallScreen_According_To_Response() {
        val profile = controller!!.userProfile
        if (enteredValue == enteredEmail) {
            if (profile.isCentralLoginUser == true) {
                navigateToScreen(1)
            } else if (profile.isNewUser == true ||
                profile.isNewUser == false && profile.isMultipleJobs == false && profile.isMultipleEmails == false && profile.isCentralLoginUser == false && profile.jobNumer != null && profile.jobNumer.length > 0
                || profile.isNewUser == false && profile.isMultipleJobs && profile.passCode.length > 0
            ) {
                navigateToScreen(2)
            } else if (profile.isMultipleJobs == true && profile.isMultipleEmails == true) {
                navigateToScreen(4)
            } else {
                if (needToShowSecondaryDialog(profile)) {
                    showAlertForSecondUser()
                } else {
                    navigateToScreen(3)
                }
            }
        } else {
            if (profile.isCentralLoginUser == true) {
                navigateToScreen(1)
            } else if (profile.passCode == null && profile.isNewUser == false && profile.isMultipleJobs == false && profile.isMultipleEmails == false && profile.isCentralLoginUser == false) {
                navigateToScreen(3)
                // need to call another service
            } else if (profile.isMultipleEmails == true) {
                navigateToScreen(4)
                Utils.showToast(this@FirstClass, profile.message, Common.sucessCase)
            } else if (profile.passCode != null) {
                navigateToScreen(2)
                Utils.showToast(this@FirstClass, profile.message, Common.sucessCase)
            }
        }
    }

    fun navigateToScreen(caseValue: Int) {
        var `in`: Intent? = null
        when (caseValue) {
            1 -> {
                showFingerPrint = if (emailOrJobNumberEditText!!.text.toString().trim { it <= ' ' }
                        .equals(lastLoggedInUser, ignoreCase = true)) {
                    true
                } else {
                    false
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    `in` = Intent(this@FirstClass, Login::class.java)
                }
                `in`!!.putExtra("showFingerPrint", showFingerPrint)
                startActivity(`in`)
            }
            2 -> {
                `in` = Intent(this@FirstClass, SetPassword::class.java)
                startActivity(`in`)
            }
            3 -> {
                `in` = Intent(this@FirstClass, MyPlaceLogin::class.java)
                startActivity(`in`)
            }
            4 -> {
                `in` = Intent(this@FirstClass, ValidateEmailId::class.java)
                startActivity(`in`)
            }
        }
    }

    fun needToShowSecondaryDialog(profile: UserProfile): Boolean {
        if (profile.isMultipleEmails == true) {
            val myPlaceEmails = ArrayList<MyPlaceEmail>()
            if (profile.userDetailses != null && profile.userDetailses.size > 0 && profile.userDetailses[0].myPlaceJobDetailses != null && profile.userDetailses[0].myPlaceJobDetailses.size > 0) {
                myPlaceEmails.addAll(profile.userDetailses[0].myPlaceJobDetailses[0].myPlaceEmails)
                for (i in myPlaceEmails.indices) {
                    val myPlaceEmail = myPlaceEmails[i]
                    if (myPlaceEmail.isPrimaryUser) {
                        if (myPlaceEmail.email != controller!!.lastLoggedEmailId) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun showAlertForSecondUser() {
        val exitDialog = Dialog(this@FirstClass)
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        exitDialog.setCanceledOnTouchOutside(false)
        exitDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        exitDialog.setContentView(R.layout.custom_dialog)
        val dialogOkButton = exitDialog.findViewById<View>(R.id.dialogOkButton) as Button
        val dialogCancelButton = exitDialog.findViewById<View>(R.id.dialogCancelButton) as Button
        val dialogMessageTextView =
            exitDialog.findViewById<View>(R.id.dialogMessageTextView) as TextView
        dialogMessageTextView.text =
            "An email is already associated with your job number as primary, \n Do you want to continue as a co-applicant?"
        val cancelButtonLayout =
            exitDialog.findViewById<View>(R.id.cancelButtonLayout) as LinearLayout
        cancelButtonLayout.visibility = View.VISIBLE
        dialogOkButton.setOnClickListener {
            navigateToScreen(3)
            exitDialog.dismiss()
        }
        dialogCancelButton.setOnClickListener { exitDialog.dismiss() }
        exitDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent()
        intent.setClassName(packageName, "com.dmss.burbankapp.ui.login.ChooseOptionsActivity")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        overridePendingTransition(0, 0)
        /*if(forgotPasswordUIVisible){
            finish();
            startActivity(getIntent());
        }*/
    }

    fun forgotPasswordUI() {
        forgotPasswordUIVisible = true
        signINLL!!.visibility = View.GONE
        emailLL!!.visibility = View.VISIBLE
    }
    override fun onResume() {
        super.onResume()
        registerReceiver(myBroadcastReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

        if (Utils.isNetworkAvailableWithError(this@FirstClass)) {
            AppConstants.validateVersionCode(this) {

            }


        }
    }

}
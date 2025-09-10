package com.dmss.burbankappold

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.text.Html
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.utils.AppConstants
import com.google.gson.GsonBuilder
import common.*
import interfaces.WebApiResponseCallback
import models.MyPlaceCredentials
import models.photos.PhotosDataItem
import models.profile.UserJobProfile
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*

/**
 * Created by Ashish.Kumar on 30-05-2017.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
class Login : BaseActivity(), View.OnClickListener, WebApiResponseCallback {
    var loginButton: Button? = null
    var controller: AppController? = null
    var loginEmailET: CustomEditText? = null
    var passwordEditText: CustomEditText? = null
    var emailET: CustomEditText? = null
    var heading: TextView? = null
    var forgetPassword: TextView? = null
    var emailHeadingTV: TextView? = null
    var myBroadcastReceiver: Receiver? = null
    var isForgetPasswordRequested = false
    var isGetUserDetailsRequest = false
    var signINLL: LinearLayout? = null
    var emailLL: LinearLayout? = null
    var submitBT: LinearLayout? = null
    var mKeyStore: KeyStore? = null
    var mKeyGenerator: KeyGenerator? = null
    var mSharedPreferences: SharedPreferences? = null
    var defaultCipher: Cipher? = null
    var cipherNotInvalidated: Cipher? = null
    var fingurePrint: TextView? = null
    var showFingerPrint = false
    var isEmailLLVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        changeStatusBarColor()
        showFingerPrint = intent.extras!!.getBoolean("showFingerPrint")
        controller = applicationContext as AppController
        controller!!.analytics.loginScreenLoadingEvent()
        controller!!.analytics.setScreen(this, "Login_Screen")
        customActionBar()
        initializeAll()
    }

    public override fun onResume() {
        super.onResume()
        registerReceiver(myBroadcastReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(myBroadcastReceiver)
    }

    private fun initializeAll() {
        myBroadcastReceiver = Receiver()
        heading = findViewById<View>(R.id.heading) as TextView
        forgetPassword = findViewById<View>(R.id.forgetPassword) as TextView
        emailHeadingTV = findViewById<View>(R.id.emailHeadingTV) as TextView
        loginButton = findViewById<View>(R.id.loginButton) as Button
        loginEmailET = findViewById<View>(R.id.loginEmailET) as CustomEditText
        loginEmailET!!.setEnabledd(false)
        loginEmailET!!.setImage(0)
        emailET = findViewById<View>(R.id.emailET) as CustomEditText
        emailET!!.setImage(0)
        emailET!!.setHint("Enter email")
        heading!!.text = "Enter your password to login"
//        heading!!.typeface = controller!!.typeface
        loginEmailET!!.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        if(controller!!.userProfile.userDetails[0].email!=null && controller!!.userProfile.userDetails[0].email!=""){
            emailET!!.text=controller!!.userProfile.userDetails[0].email
            emailET!!.setEnabledd(false)
//            emailET!!.background=resources.getDrawable(R.drawable.grey_border_edittext)
            emailHeadingTV!!.text=Html.fromHtml("Registered email assigned to the job number " +"<font color='#FF6224'>"+controller!!.userProfile.jobNumer+"</font>")

        }else{
            emailHeadingTV!!.text= Html.fromHtml("Enter Registered email assigned to the job number " +"<font color='#FF6224'>"+controller!!.userProfile.jobNumer+"</font>")
        }
        if (controller!!.lastLoggedEmailId != null && controller!!.lastLoggedEmailId.length > 0) {
            if (controller!!.lastLoggedEmailId.contains("@")) {
                loginEmailET!!.setImage(0)
            } else {
                loginEmailET!!.setImage(0)
            }
            loginEmailET!!.text = controller!!.lastLoggedEmailId
        } else if (!controller!!.userProfile.jobNumer.equals("null", ignoreCase = true)) {
            loginEmailET!!.text = controller!!.userProfile.jobNumer
            loginEmailET!!.setImage(0)
        }
        forgetPassword!!.visibility = View.VISIBLE
        /*if (loginEmailET.getText().toString().contains("@")) {
            forgetPassword.setVisibility(View.VISIBLE);
        } else {
            forgetPassword.setVisibility(View.GONE);
        }*/passwordEditText = findViewById<View>(R.id.passwordEditText) as CustomEditText
        passwordEditText!!.setImage(0)
        passwordEditText!!.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        passwordEditText!!.setHint("Enter Password")

        passwordEditText!!.setTransformationMethod(PasswordTransformationMethod.getInstance())
       /* if (!loginEmailET!!.text.toString().contains("@")) {
            emailHeadingTV!!.text =
                emailHeadingTV!!.text.toString() + controller!!.userProfile.jobNumer
        }*/
        forgetPassword!!.setOnClickListener {

            if (!loginEmailET!!.text.toString().contains("@")) {
                isEmailLLVisible = true
                signINLL!!.visibility = View.GONE
                emailLL!!.visibility = View.VISIBLE

            } else {
                validateEmailID(loginEmailET!!.text.toString())
            }
        }
        fingurePrint = findViewById<View>(R.id.fingurePrint) as TextView
        fingurePrint!!.setOnClickListener {
            fingerPrintAuth()
            controller!!.analytics.loginScreenUseTouchIdTouchEvent()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fingurePrint!!.visibility = View.GONE
        } else {
            if (showFingerPrint) {
                val fingerprintManager =
                    getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                if (fingerprintManager != null) {
                    if (!fingerprintManager.isHardwareDetected) {
                        fingurePrint!!.visibility = View.GONE
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        fingurePrint!!.visibility = View.GONE
                    } else {
                        fingurePrint!!.visibility = View.VISIBLE
                    }
                } else {
                    fingurePrint!!.visibility = View.GONE
                }
            } else {
                fingurePrint!!.visibility = View.GONE
            }
        }
        signINLL = findViewById<View>(R.id.signINLL) as LinearLayout
        emailLL = findViewById<View>(R.id.emailLL) as LinearLayout
        submitBT = findViewById<View>(R.id.submitBT) as LinearLayout
        submitBT!!.setOnClickListener { validateEmailID(emailET!!.text) }
        backPressed()
    }

    private fun validateEmailID(email: String) {
        if (email.length > 0) {
            if (email.contains("@") && email.contains(".")) {
                if (Utils.isNetworkAvailableWithError(this)) {
                    isForgetPasswordRequested = true
                    controller!!.webApiCall().postData(
                        Common.forgetPasswordUrl,
                        forgetPasswordJson,
                        this@Login,
                        Utils.getProgress(this@Login)
                    )
                    controller!!.analytics.loginScreenForgotPasswordButtonTouchEvent()
                }
            }
            else {
                Utils.showToast(this@Login, "Please enter Valid email Id", Common.errorCase)
            }
        } else {
            Utils.showToast(this@Login, "Please enter email Id", Common.errorCase)
        }
    }

    fun backPressed() {
        val back_image = findViewById<View>(R.id.back_image) as ImageView
        back_image.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if (isEmailLLVisible) {
            signINLL!!.visibility = View.VISIBLE
            emailLL!!.visibility = View.GONE
            isEmailLLVisible = false
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Method for custom action bar
     *
     * @return void.
     */
    fun customActionBar() {
//        getSupportActionBar().hide();
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
                controller.getAnalytics().loginScreenBackButtonTouchEvent();
                onBackPressed();

            }
        });*/
    }

    override fun onClick(view: View) {
        //finishing activity up on click of back arrow button
        if (view.id == R.id.loginButton) {
            if (passwordEditText!!.text != null && passwordEditText!!.text.length > 0) {
                if (Utils.isNetworkAvailableWithError(this@Login)) {
                    controller!!.webApiCall().postData(
                        Common.centralUserLogin_Url,
                        json,
                        this@Login,
                        Utils.getProgress(this@Login)
                    )
                }
                controller!!.analytics.loginScreenLoginButtonTouchEvent()
            } else {
                //information regarding empty password
//                Utils.showToast(this@Login, "Password field should not be empty", Common.errorCase)
                Common.showMessageAlert(this@Login, Common.please_enter_password)
            }
        }
    }

    val json: String
        get() {
            val job = JSONObject()
            try {
                if (loginEmailET!!.text.contains("@")) {
                    job.put("Email", loginEmailET!!.text)
                } else {
                    job.put("JobNumber", loginEmailET!!.text)
                }
                job.put("centralLoginPassword", passwordEditText!!.text)
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }
    val forgetPasswordJson: String
        get() {
            val job = JSONObject()
            try {
                job.put("JobNumber", controller!!.userProfile.jobNumer)
                job.put("name", controller!!.userProfile.userDetails[0].getFirstName());
                if (isEmailLLVisible) {
                    job.put("Email", emailET!!.text.toString())
                } else {
                    job.put("Email", loginEmailET!!.text.toString())
                }
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }

    override fun onSuccessResult(result: String?) {
        if (result != null) {
//            println("pass code output$result")
            try {
                val jobb = JSONObject(result)
                if (jobb.has(Common.Result_Key)) {
                    val job = jobb.getJSONObject(Common.Result_Key)
                    if (job.getBoolean(Common.Sucess_Key)) {
                        if (isForgetPasswordRequested) {
                            isForgetPasswordRequested = false
                           /* Utils.showToast(
                                this@Login,
                                job.getString(Common.Message),
                                Common.sucessCase
                            )*/
                            controller!!.setProfileInfo(job.toString())
                            val `in` = Intent(this@Login, ResetPassword::class.java)
                            `in`.putExtra(Common.SucessMssage,job.getString(Common.Message))
                            startActivity(`in`)
                        }  else {
                            controller!!.setProfileInfo(job.toString())
                            Utils.showToast(this@Login, "Loggedin Sucessfully.", Common.sucessCase)
                            controller!!.isUserLoggedIn = true
                            if (controller!!.userProfile.userDetails[0].isMyPlaceAccessible) {
                                AppConstants.photosList.clear()
                                AppConstants.photosList=mutableListOf<List<PhotosDataItem>>()

                                val intent = Intent(this@Login, DashboardNewActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this@Login, EmptyDashboard::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        if (isEmailLLVisible && job.getString(Common.Message)
                                .contains("No user record found")
                        ) {
                           /* Utils.showToast(
                                this@Login,
                                "Email address is not found. Please contact your New Home Coordinator for assistance.",
                                Common.errorCase
                            )*/
                            Common.showMessageAlert(this@Login,"Email address is not found. Please contact your New Home Coordinator for assistance.")
                        } else {
                            Common.showMessageAlert(this@Login,job.getString(Common.Message))

                            /*Utils.showToast(
                                this@Login,
                                job.getString(Common.Message),
                                Common.errorCase
                            )*/
                        }
                    }
                } else {
//                    Utils.showToast(this@Login, jobb.getString(Common.Message), Common.errorCase)
                    Common.showMessageAlert(this@Login,jobb.getString(Common.Message))

                }
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
        } else {
//            Utils.showToast(this@Login, Common.somethingErrorMessage, Common.errorCase)
            Common.showMessageAlert(this@Login,Common.somethingErrorMessage)


        }
    }

    override fun onErrorResult(error: String) {
        //java.net.SocketTimeoutException: timeout
        if (error.contains("TimeoutException")) {
            Utils.showToast(this@Login, "Unable to reach server, \n Try again", Common.errorCase)
        } else {
            Utils.showToast(this@Login, error, Common.errorCase)
        }
    }

    private fun parseAPIResponse(result: String?, callback: () -> Unit){
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.getBoolean(Common.Status_Key)) {
                val job = jsonObject.getJSONObject(Common.Result_Key)
                if (job.getBoolean(Common.Sucess_Key)) {
                    controller?.setProfileInfo(job.toString())
                    val gson = GsonBuilder().create()
                    val profile: UserJobProfile = gson.fromJson(job.toString(), UserJobProfile::class.java)
                    AppController.controller.setUserJobProfile(profile)
                    AppController.controller.setUserJobDetail(profile.UserDetails)
                    AppController.controller.myPlaceDetail = profile.UserDetails[0].MyPlaceDetails
                    val myPlaceDetails = profile.UserDetails[0].MyPlaceDetails[0]
                    val myPlaceCredentials = MyPlaceCredentials(
                        myPlaceDetails.Region,
                        myPlaceDetails.JobNo,
                        myPlaceDetails.UserName,
                        myPlaceDetails.Password
                    )
                    AppController.controller.my_Place_Details = myPlaceCredentials
                    callback.invoke()
                } else {
                    Utils.showToast(
                        this,
                        jsonObject.getString(Common.Message),
                        Common.errorCase
                    )
                }
            } else {
                Utils.showToast(
                    this,
                    jsonObject.getString(Common.Message),
                    Common.errorCase
                )
            }
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }
    }

    /*public void showErrorMessage(final String message, final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    Utils.showToast(Login.this, message, Common.sucessCase);
                } else {
                    Utils.showToast(Login.this, message, Common.errorCase);
                }

            }
        });
    }*/
    @TargetApi(Build.VERSION_CODES.M)
    fun fingerPrintAuth() {
        mKeyStore = try {
            KeyStore.getInstance("AndroidKeyStore")
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }
        mKeyGenerator = try {
            KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get an instance of KeyGenerator", e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to get an instance of KeyGenerator", e)
        }
        try {
            defaultCipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
            cipherNotInvalidated = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get an instance of Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get an instance of Cipher", e)
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val keyguardManager = getSystemService(
            KeyguardManager::class.java
        )
        val fingerprintManager = getSystemService(
            FingerprintManager::class.java
        )
        assert(keyguardManager != null)
        if (!keyguardManager!!.isKeyguardSecure) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Utils.showToast(
                this@Login, """
     Secure lock screen hasn't set up.
     Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint
     """.trimIndent(),
                Common.errorCase
            )
            return
        }
        assert(fingerprintManager != null)
        // noinspection ResourceType
        if (!fingerprintManager!!.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            Utils.showToast(
                this@Login,
                "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                Common.errorCase
            )
            return
        }
        createKey(DEFAULT_KEY_NAME, true)
        createKey(KEY_NAME_NOT_INVALIDATED, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerPrintAccessStatus(cipherNotInvalidated, KEY_NAME_NOT_INVALIDATED)
        }
        //fingerPrintAccessStatus(defaultCipher, DEFAULT_KEY_NAsME);
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     *
     * @param keyName                          the name of the key to be created
     * @param invalidatedByBiometricEnrollment if `false` is passed, the created key will not
     * be invalidated even if a new fingerprint is enrolled.
     * The default value is `true`, so passing
     * `true` doesn't change the behavior
     * (the key will be invalidated if a new fingerprint is
     * enrolled.). Note that this parameter is only valid if
     * the app works on Android N developer preview.
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun createKey(keyName: String?, invalidatedByBiometricEnrollment: Boolean) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore!!.load(null)
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            val builder = KeyGenParameterSpec.Builder(
                keyName,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC) // Require the user to authenticate with a fingerprint to authorize every use
                // of the key
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.


            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }*/mKeyGenerator!!.init(builder.build())
            mKeyGenerator!!.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun fingerPrintAccessStatus(cipher: Cipher?, keyName: String) {
        // Set up the crypto object for later. The object will be authenticated by use
        // of the fingerprint.
        if (initCipher(cipher, keyName)) {

            // Show the fingerprint dialog. The user has the option to use the fingerprint with
            // crypto, or you can fall back to using a server-side verified password.
            val fragment = FingerprintAuthenticationDialogFragment()
            fragment.setAppNextActivity(this@Login, controller, this)
            fragment.setCryptoObject(FingerprintManager.CryptoObject(cipher))
            val useFingerprintPreference = mSharedPreferences?.getBoolean(
                    getString(R.string.use_fingerprint_to_authenticate_key),
                    true
                )
            if (useFingerprintPreference == true) {
                fragment.setStage(
                    FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT
                )
            } else {
                fragment.setStage(
                    FingerprintAuthenticationDialogFragment.Stage.PASSWORD
                )
            }
            fragment.show(fragmentManager, DIALOG_FRAGMENT_TAG)
        } else {
            // This happens if the lock screen has been disabled or or a fingerprint got
            // enrolled. Thus show the dialog to authenticate with their password first
            // and ask the user if they want to authenticate with fingerprints in the
            // future
            val fragment = FingerprintAuthenticationDialogFragment()
            fragment.setAppNextActivity(this@Login, controller, this)
            fragment.setCryptoObject(FingerprintManager.CryptoObject(cipher))
            fragment.setStage(
                FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED
            )
            fragment.show(fragmentManager, DIALOG_FRAGMENT_TAG)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initCipher(cipher: Cipher?, keyName: String): Boolean {
        return try {
            mKeyStore!!.load(null)
            val key = mKeyStore!!.getKey(keyName, null) as SecretKey
            cipher!!.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }

    /**
     * @param withFingerprint `true` if the purchase was made by using a fingerprint
     * @param cryptoObject    the Crypto object
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun onFingerPrintSuccess(
        withFingerprint: Boolean,
        cryptoObject: FingerprintManager.CryptoObject?
    ) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert(cryptoObject != null)
            tryEncrypt(cryptoObject!!.cipher)
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            showConfirmation(null)
        }
    }

    // Show confirmation, if fingerprint was used show crypto information.
    private fun showConfirmation(encrypted: ByteArray?) {
        Toast.makeText(this, " Finger Print Auth success ", Toast.LENGTH_LONG).show()
        if (Utils.isNetworkAvailableWithError(this)) {
            if (controller!!.validation.validateEmail(loginEmailET, this@Login)) {
                val sample = loginEmailET!!.text.toString()
                    .trim { it <= ' ' } + ":" + controller!!.fingerPrintID
                val message = sample.toByteArray()
                val encoded = Base64.encodeToString(message, Base64.NO_WRAP)
                //controller.webApiCall().postData(Common.loginUrl, getJsonData(), this, Utils.getProgress(LoginActivity.this));
                controller!!.webApiCall().getDataWithHeaders(
                    "http://192.168.100.92:9999/api/Auth/login",
                    encoded,
                    this,
                    Utils.getProgress(this@Login)
                )
            }
        }
        //navigateToActivity(2);
    }

    /**
     * Tries to encrypt some data with the generated key in [.createKey] which is
     * only works if the user has just authenticated via fingerprint.
     */
    private fun tryEncrypt(cipher: Cipher) {
        try {
            val encrypted = cipher.doFinal(SECRET_MESSAGE.toByteArray())
            showConfirmation(encrypted)
        } catch (e: BadPaddingException) {
            Toast.makeText(
                this, "Failed to encrypt the data with the generated key. "
                        + "Retry the purchase", Toast.LENGTH_LONG
            ).show()
            Log.e(
                "Login FingerPrint",
                "Failed to encrypt the data with the generated key." + e.message
            )
        } catch (e: IllegalBlockSizeException) {
            Toast.makeText(
                this, "Failed to encrypt the data with the generated key. "
                        + "Retry the purchase", Toast.LENGTH_LONG
            ).show()
            Log.e(
                "Login FingerPrint",
                "Failed to encrypt the data with the generated key." + e.message
            )
        }
    }

    companion object {
        const val DIALOG_FRAGMENT_TAG = "myFragment"
        const val SECRET_MESSAGE = "Very secret message"
        const val KEY_NAME_NOT_INVALIDATED = "key_not_invalidated"
        const val DEFAULT_KEY_NAME = "default_key"
    }
}
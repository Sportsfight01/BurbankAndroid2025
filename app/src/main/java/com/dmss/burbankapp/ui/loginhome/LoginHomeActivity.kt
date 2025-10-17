package com.dmss.burbankapp.ui.loginhome

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.SignUpModel
import com.dmss.burbankapp.data.model.UserInfoModel
import com.dmss.burbankapp.databinding.ActivityLoginHomeBinding
import com.dmss.burbankapp.helpers.FacebookHelper
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.login.ChooseOptionsActivity
import com.dmss.burbankapp.ui.login.EnterEmailOrJobActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.dmss.burbankapp.viewmodel.ProfilePicViewModel
import com.dmss.burbankapp.viewmodel.SingletonNameViewModelFactory
import com.dmss.burbankappold.FirstClass
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.net.URL


class LoginHomeActivity : BaseActivity(), FacebookHelper.OnFbSignInListener {
    private val EMAIL = "email"
    lateinit var binding: ActivityLoginHomeBinding
    var callbackManager: CallbackManager? = null
    var id: String? = null
    var firstName: String = ""
    var email: String? = null
    var last_name: String = ""
    var birthday: String? = null
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var myPreference: CustomSharedPreferences
    lateinit var gmailJsonUser: JSONObject
    lateinit var fbConnectHelper: FacebookHelper


    var mGoogleSignInClient: GoogleSignInClient? = null

    lateinit var profilePickViewModel: ProfilePicViewModel
    lateinit var loginHomeViewModel: LoginHomeViewModel
    lateinit var singletonNameViewModelFactory: SingletonNameViewModelFactory
    var RC_SIGN_IN = 2
    private fun setupViewModel() {
        singletonNameViewModelFactory = SingletonNameViewModelFactory();
        profilePickViewModel = ViewModelProviders.of(this, singletonNameViewModelFactory)
            .get(ProfilePicViewModel::class.java)

        loginHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(LoginHomeViewModel::class.java)


    }

    fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
    }


    public fun bitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    class convertToBitmap(var url: URL) :
        AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {

            val image = BitmapFactory.decodeStream(
                url.openConnection().getInputStream()
            )

            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()
            return Base64.encodeToString(
                data,
                Base64.DEFAULT
            )


        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Timber.e("Facebook Base64 1----->${result}")
            // Timber.e("Facebook Base64 Trim() ----->${base64.trim()}")

        }

    }

    private fun facebookLogin() {
        fbConnectHelper = FacebookHelper(this, this)
        callbackManager = CallbackManager.Factory.create()
        binding.llFacebook.setOnClickListener {
            binding.loginButton.performClick()
            myPreference.setUserLogin(true)
        }
        binding.loginButton.setPermissions(listOf("email", "public_profile"));
//         binding.loginButton.setReadPermissions(listOf(EMAIL));
//        binding.loginButton.setReadPermissions("email", "public_profile", "user_friends");
        binding.loginButton.registerCallback(callbackManager, object  : FacebookCallback<LoginResult>{
            override fun onCancel() {
                Timber.e("onCancel")
            }

            override fun onError(error: FacebookException) {
                Timber.e("onError")
            }

            override fun onSuccess(loginResult: LoginResult) {
                val request =
                    GraphRequest.newMeRequest(loginResult.accessToken) { jObject: JSONObject?, response ->
                        try {
                            jObject?.let {jsonObject->
                                id = jsonObject.optString("id")
                                firstName = jsonObject.optString("first_name")
                                email = jsonObject.optString("email")
                                last_name = jsonObject.optString("last_name")
                                val profileModel = Profile.getCurrentProfile()
                                if (profileModel == null) {
                                    Profile.fetchProfileForCurrentAccessToken()
                                }
                                val imageURL =
                                    "https://graph.facebook.com/" + jsonObject.optString("id")
                                        .toString() + "/picture?type=normal"


                                Timber.e("API user Name (Login) ::${firstName}")


                                gmailJsonUser = JSONObject()
                                gmailJsonUser.put("LoginType", "facebook")
                                gmailJsonUser.put("FirstName", firstName)
                                gmailJsonUser.put("LastName", last_name)
                                gmailJsonUser.put("Email", email)
                                gmailJsonUser.put("Password", "")

                                val value = convertToBitmap(URL(imageURL)).execute().get()
                                gmailJsonUser.put("ImageContent", value)
                                val userInfoModel = UserInfoModel(
                                    gmailJsonUser.optString("LoginType"),
                                    gmailJsonUser.optString("FirstName"),
                                    gmailJsonUser.optString("LastName"),
                                    gmailJsonUser.optString("Email"),
                                    imageURL, "", ""
                                )
                                userInfoModel.LoginType =
                                    gmailJsonUser.optString("LoginType")
                                userInfoModel.FirstName =
                                    firstName
                                Timber.e("Local user Name (Login)::${userInfoModel.FirstName}")
                                userInfoModel.LastName = gmailJsonUser.optString("LastName")
                                userInfoModel.Email = gmailJsonUser.optString("Email")
                                userInfoModel.ProfileImage = imageURL
                                myPreference.saveUserInfoModel(userInfoModel)
                                myPreference.setLoginType("facebook")
                                binding.progress.visibility = View.VISIBLE
                                createUserApi(gmailJsonUser)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    }

                val parameters = Bundle()
                parameters.putString("fields", "id, email, first_name, last_name, gender,age_range")
                request.parameters = parameters
                request.executeAsync()
            }

        })
    }


    private fun gmailLogin() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestIdToken(resources.getString(R.string.googleAccountWebClientID))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.llGoogle.setOnClickListener(View.OnClickListener {
            signIn()
            myPreference.setUserLogin(true)
        })
    }


    private fun skipDialog() {
        binding.tvSkip.setOnClickListener {
            showSkipDialog()
        }

    }
    private fun backClick(){
        val intent = Intent(
            this,
            ChooseOptionsActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        backClick()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.app_bg))
        binding = ActivityLoginHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreference = CustomSharedPreferences(this)
        /*StatusBarUtil.setTransparent(this)*/
        setupViewModel()
        facebookLogin()
        gmailLogin()
        skipDialog()
        continueWithEmail()
        setUpObservers()
        binding.backImage.setOnClickListener {
//            onBackPressed()
            backClick()


        }

        binding.ivProfilePic.setOnClickListener {
            // loginHomeViewModel.saveLoginDetails()
        }
        signOut()
        val account = GoogleSignIn.getLastSignedInAccount(this)
    }

    private fun setUpObservers() {
        loginHomeViewModel.createUserSignUpData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progress.visibility = View.GONE
                    var loginModel: SignUpModel? = it.data
                    if (loginModel != null) {
                        if (loginModel.status) {
                            myPreference.saveUserId(loginModel.Userinfo)
                            myPreference.setUserLoggedIn(true)
                            Toast.makeText(this@LoginHomeActivity,it.data!!.Message,Toast.LENGTH_SHORT).show()
                            val signUpIntent = Intent(this, DashboardActivity::class.java)
                            signUpIntent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            signUpIntent.putExtra(AppConstants.USER_CREATED, true)
                            startActivity(signUpIntent)
                            val appEvent = AppEvent(AppEvent.SIGNUP, null)
                            EventBus.getDefault().post(appEvent)
                        }

                    }
                }
                Status.LOADING -> {
                    binding.progress.visibility = View.VISIBLE

                }
                Status.ERROR -> {
                    //Handle Error
                    binding.progress.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })


    }

    private fun continueWithEmail() {
        binding.llContinueEmail.setOnClickListener {
            myPreference.setUserLogin(true)
            startActivity(Intent(this, EnterEmailOrJobActivity::class.java))
        }
    }

    private fun signIn() {
        binding.progress.visibility = View.VISIBLE
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            binding.progress.visibility = View.GONE
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val personName: String = account.displayName.toString()
                val personPhotoUrl: String = account.photoUrl.toString()
                val email: String = account.email.toString()
                gmailJsonUser = JSONObject()
                /*Picasso.get()
                    .load(personPhotoUrl)
                    .into(binding.ivProfilePic);*/
                gmailJsonUser.put("LoginType", "google")
                gmailJsonUser.put("FirstName", personName)
                gmailJsonUser.put("LastName", personName)
                gmailJsonUser.put("Email", email)
                gmailJsonUser.put("Password", "")
                //user.put("ImageContent", personPhotoUrl)
                Timber.e("Gmail User Data api ::---{${personName}}")
                if (personPhotoUrl.isNotEmpty()) {
                    var value = ""
                    if (personPhotoUrl != "null") {
                        value = convertToBitmap(URL(personPhotoUrl)).execute().get()
                    }

                    gmailJsonUser.put("ImageContent", value)
                    val userInfoModel = UserInfoModel(
                        gmailJsonUser.optString("LoginType"),
                        gmailJsonUser.optString("FirstName"),
                        gmailJsonUser.optString("LastName"),
                        gmailJsonUser.optString("Email"),
                        personPhotoUrl, "", ""
                    )
                    userInfoModel.LoginType = gmailJsonUser.optString("LoginType")
                    userInfoModel.FirstName =
                        gmailJsonUser.optString("FirstName")

                    Timber.e("Gmail User Data ::---{${userInfoModel.FirstName}}")
                    userInfoModel.LastName = gmailJsonUser.optString("LastName")
                    userInfoModel.Email = gmailJsonUser.optString("Email")
                    userInfoModel.ProfileImage = personPhotoUrl
                    myPreference.setLoginType("google")
                    myPreference.saveUserInfoModel(userInfoModel)
                    binding.progress.visibility = View.VISIBLE
                    createUserApi(gmailJsonUser)
                }
            }


        } catch (e: ApiException) {
            Timber.e("Error from Gmail ---> ${e.message}")
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

        }
    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(
                this
            ) { }
    }

    private fun showSkipDialog() {
        dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.signup_note_dialog, null)

        dialogBuilder.setView(dialogView)
        val tv_continue = dialogView.findViewById(R.id.tv_continue) as TextView
        val tv_sigIn = dialogView.findViewById(R.id.tv_sigIn) as TextView
        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        tv_continue.setOnClickListener {
            var userInfoModel = UserInfoModel(
                "",
                "",
                "",
                "",
                null,
                "", ""
            )
            myPreference.saveUserInfoModel(userInfoModel)
            myPreference.setUserLogin(false)

            val signUpIntent = Intent(this, DashboardActivity::class.java)
            signUpIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(signUpIntent)




            alertDialog.dismiss()
        }
        tv_sigIn.setOnClickListener {
//            startActivity(Intent(this, EnterEmailOrJobActivity::class.java))
            alertDialog.dismiss()
//            myPreference.setUserLogin(true)
        }

        alertDialog.show()
    }

    private fun createUserApi(user: JSONObject) {
        Timber.e("Facebook Base64 ----->3")
        loginHomeViewModel.createUser(user)

    }

    override fun OnFbSignInComplete(graphResponse: GraphResponse?, error: String?) {
        val jsonObject: JSONObject? = graphResponse?.jsonObject
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppEvent(event: AppEvent) {

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }


}






package com.dmss.burbankapp.ui.login

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.method.ScrollingMovementMethod
import android.text.style.AlignmentSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.UserLoginModel
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.loginhome.LoginHomeActivity
import com.dmss.burbankapp.ui.splash.SpalashViewModel
import com.dmss.burbankapp.ui.video.PlayerActivity
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.dmss.burbankappold.FirstClass
import com.dmss.burbankappold.MaintenanceActivity
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.utils.AppConstants
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import common.AppController
import common.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.photos.PhotosDataItem
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


class ChooseOptionsActivity : BaseActivity(), View.OnClickListener {

    var from: Int = 0
    val REQUEST_IMAGE = 100
    lateinit var iv_profile: ImageView
    val UPDATE_REQUEST_CODE =1
    lateinit var spalashViewModel: SpalashViewModel
    lateinit var customSharedPreferences: CustomSharedPreferences
    var mLastClickTime: kotlin.Long = 0
    private lateinit var analytics: FirebaseAnalytics
    lateinit var iv_check_deposit: ImageView
    lateinit var iv_check_just: ImageView
    var updateDialog: Dialog? = null
    var controller: AppController = AppController.controller
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var settingsClient: SettingsClient
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private val REQUEST_CHECK_SETTINGS = 1001
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    var fcmTocken=""
    var deviceId=""
    var isGpsEnable = false
    var startInspectionClicked= false
    var isDecline= true
    private val REQUEST_READ_PHONE_STATE = 100
    var token=""

    fun promptUpdateDialog() {
        // Notify the user about the update or start the update process
        showCheckVersionUpdateDialog(this,"Avalable")
    }


    fun showCheckVersionUpdateDialog(context: Activity, message: String) {
        val dialogBuilder = AlertDialog.Builder(context)

        val title = SpannableString("MyPlace")
        title.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0,
            title.length,
            0
        )
        dialogBuilder.setTitle(title)//Should be in center
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${context!!.resources.getColor(R.color.orange_bg_3_1)}'>Update</font>")) { dialog, _ ->
                dialog.cancel()

                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
//                    activity.finish()
            }
//            .setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>")) { dialog, _ ->
//                dialog.cancel()
//            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        //alert.setTitle("Select ${state.name}?")
        // show alert dialog
        alert.show()
        val negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)

        negativeButton.isAllCaps = false
        positiveButton.isAllCaps = false

        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10F
        positiveButton.layoutParams = layoutParams
        negativeButton.layoutParams = layoutParams
    }


    private fun logoutIamBuilding(){
        controller.analytics.settingsLogoutButtonTouchEvent()
        controller.isUserLoggedIn = false
        controller.loggedInFromSocial = false
        controller.my_Place_Details = null
        controller.profilePicUrl = ""
        controller.setProfileInfo("")
        AppConstants.financeItemStatus=false
        controller.selectedJobPosition = 0
        AppConstants.filterList= emptyList()
        AppConstants.NoRecentPhotos=""
        AppConstants.PhotosSubheader=""
        AppConstants.AppCookieContactUs=""
        AppConstants.savedphotosList.clear()
        AppConstants.savedphotosList=mutableListOf<List<PhotosDataItem>>()
        AppController.setPreference(this,AppController.SELECTEDJOBNUMBER,AppController.defaultValue)
        AppController.setPreference(this,AppController.PREVIOUSSELECTEDJOBNUMBER,AppController.defaultValue)

        PrefsHelper.clearPrefs()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_options)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.app_bg))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        checkAndRequestNotificationPermission()
        deviceId= AppConstants.getDeviceId(this)
        println("deviceId:: "+deviceId)
        setGps()
        var myPreference = CustomSharedPreferences(this)

         myPreference.setUserLogin(false)
        var isUserLoggedIn = myPreference.getUserLogin()

        println("isUserLoggedIn ChooseOption ACTIVITY:: "+isUserLoggedIn)
        inintView()
        setUpViewModel()
    }
    private fun setGps(){
        if(isGpsEnable){
            getCurrentLocationAndNavigate()

        }else {
            gpsValidation()
        }
    }
    private fun gpsValidation(){
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true) // This displays the dialog

        locationSettingsRequest = builder.build()
        settingsClient = LocationServices.getSettingsClient(this)

        checkGPS()
    }

    private fun checkGPS() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                // GPS is already on
                isGpsEnable = true
                getCurrentLocationAndNavigate()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error
                    }
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // GPS is turned on
                    isGpsEnable = true
                    getCurrentLocationAndNavigate()
                }
                Activity.RESULT_CANCELED -> {
                    // User refused to turn on GPS
                    isGpsEnable = false

                }
            }
        }
    }
    /* private fun getCurrentLocationAndNavigate() {

         // Check for location permissions
         if (ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED &&
             ActivityCompat.checkSelfPermission(
                 this,
                 Manifest.permission.ACCESS_COARSE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             // Request permissions if not granted
             ActivityCompat.requestPermissions(
                 this,
                 arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                 LOCATION_PERMISSION_REQUEST_CODE
             )
             return
         }

         // Attempt to retrieve the last known location

     }*/
    private fun getCurrentLocationAndNavigate() {
        val permissionsNeeded = mutableListOf<String>()

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Check notification permission (Android 13+)
        if (
            ActivityCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
            != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add("android.permission.POST_NOTIFICATIONS")
        }

        // Request any missing permissions
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // All required permissions granted — proceed with location logic
        // Attempt to retrieve the last known location or use getCurrentLocation()
    }


    private fun sendDeviceDetalsForPushNotification(){
        deviceId= AppConstants.getDeviceId(this)
        Timber.e(" Choose OPtion deviceId ----- $deviceId")


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            // Get new FCM registration token
            token = task.result.toString()
            // Log and toast
            Timber.e(" Choose OPtion Firebase Token ----- $token")



        }

    }
    private fun setUpViewModel() {
        spalashViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        )[SpalashViewModel::class.java]
//        sendDeviceDetalsForPushNotification()
        setUpObserver()
    }


    private fun setUpObserver() {
        spalashViewModel.getUserLoginData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var loginModel: UserLoginModel? = it.data
                    if (loginModel != null) {
//                        if (loginModel.status) {
                        if (loginModel.token.isNotEmpty()) {
                            println("loginModel"+loginModel.token)
                            customSharedPreferences.saveToken(loginModel.token)
                            /*if (loginModel.Userid.isNotEmpty()) {
                                customSharedPreferences.saveUserId(loginModel.Userid.toInt())
                            }
*/                             customSharedPreferences.saveselectedoption(from)
                            when (from) {
                                0 -> {
                                    val params = Bundle()

                                    params.putString(
                                        FirebaseAnalytics.Param.ITEM_ID,
                                        "choose_option"
                                    )

                                    params.putString(
                                        FirebaseAnalytics.Param.ITEM_CATEGORY,
                                        "choose_option"
                                    )


                                    analytics.logEvent(
                                        FirebaseAnalytics.Event.SCREEN_VIEW,
                                        params
                                    )
                                    navigateToIamLooking()

                                }
                                1 -> {


                                    val params = Bundle()

                                    params.putString(
                                        FirebaseAnalytics.Param.ITEM_ID,
                                        "deposited"
                                    )

                                    params.putString(
                                        FirebaseAnalytics.Param.ITEM_CATEGORY,
                                        "deposited"
                                    )


                                    analytics.logEvent(
                                        FirebaseAnalytics.Event.SCREEN_VIEW,
                                        params
                                    )
                                    PrefsHelper.init(this)

                                    lifecycleScope.launch {
                                        callAPi()
                                    }
//                                        startActivity(Intent(this, Splash::class.java))
                                }
                                2 -> {
                                    startActivity(Intent(this, LoginHomeActivity::class.java))
                                }
                            }
                        }
//                        }
                    }


                }
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                }
            }
        })
        spalashViewModel.getDeviceDetailsData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()

                    println("getDeviceDetailsData:: $it")
                    callLoginData()

                }
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                }
            }
        })

    }
    fun navigateToIamLooking(){
        if (customSharedPreferences.getUserLoggedIn()) {

            val appEvent = AppEvent(AppEvent.SIGNUP, null)
            EventBus.getDefault().post(appEvent)

            val loginSuccess = Intent("LoginSuccess")
            LocalBroadcastManager.getInstance(this).sendBroadcast(loginSuccess)

            val signUpIntent = Intent(this, DashboardActivity::class.java)
            signUpIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            signUpIntent.putExtra(com.dmss.burbankapp.utils.AppConstants.USER_CREATED, false)
            startActivity(signUpIntent)
        } else {
            startActivity(Intent(this, LoginHomeActivity::class.java))
        }
    }
    private fun getLocation(){
        var mLocation: Location? =null
        sendDeviceDetalsForPushNotification()
        fusedLocationClient.lastLocation .addOnSuccessListener { location: Location? ->
            if (location != null) {
                // Use the location
//                    Toast.makeText(requireActivity(), "Last Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_LONG).show()
                println("location:: "+location.latitude+location.longitude)
                mLocation = location
                callServiceApi(location)

            } else {
                println("location:: "+"Not found")
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                100
            )

            showProgressDialog()

            GlobalScope.launch {
                println("Coroutine started")
                delay(2000) // Waits for 2 seconds
                println("Coroutine resumed after 2 seconds")
                dismissProgressDialog()
                spalashViewModel.sendDeviceDetailsData(token,deviceId,"","","")

            }



        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                println("location:: "+location.latitude+location.longitude)
            } ?: run {
                spalashViewModel.sendDeviceDetailsData(token,deviceId,"","","")
            }
        }
    }
    fun callServiceApi(location: Location){
        if (token != null && location!=null) {
            var mLat = location.latitude
            var mLog = location.longitude
            if(deviceId=="8349d545c0cb2b69" || deviceId == "961f1bb7ae1026a6"){
                mLat = -36.9848
                mLog= 143.3906
            }
            spalashViewModel.sendDeviceDetailsData(token,deviceId,""+mLat,""+mLog,"")
        }else if(token != null && location==null){
            spalashViewModel.sendDeviceDetailsData("",deviceId,"","","")

        }else if(token == null && location!=null){
            val mLat = location.latitude
            val mLog = location.longitude
            spalashViewModel.sendDeviceDetailsData("",deviceId,""+mLat,""+mLog,"")

        }else{
            spalashViewModel.sendDeviceDetailsData("",deviceId,"","","")

        }
    }
    private suspend fun callAPi(){
        val result1 = withContext(Dispatchers.IO){
            getAPI()
        }
        updateUI(result1)
        /*lifecycleScope.launch {
            val result = async { getAPI()}
            async { callProfileAPi()}
            onSuccessResult(result.toString())
        }*/
    }
    private fun updateUI(result1: String?) {
        onSuccessResult(result1){
            if (it){
                startActivity(Intent(this, DashboardNewActivity::class.java))
//                startActivity(Intent(this, HomeActivity::class.java))

            }
        }
    }
    private suspend fun getAPI() = withContext(Dispatchers.IO){
        AppController.controller.webApiCall().getData_From_MyPlace(Common.maintenanceUrl)
    }
    fun onSuccessResult(result: String?, callback: (Boolean) -> Unit) {
        Handler(Looper.getMainLooper()).post {
            try {
                Log.e("This maintenance test", result!!)
                val jsonArray = JSONArray(result)
                val maintenanceObject = JSONObject()
                var displayText = ""
                var updateCheck = false
                var maintenance = false
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val groupName =
                        if (jsonObject.isNull("GroupName")) "" else jsonObject.getString("GroupName")
                    val contentName =
                        if (jsonObject.isNull("ContentName")) "" else jsonObject.getString("ContentName")
                    val contentValue =
                        if (jsonObject.isNull("ContentValue")) "" else jsonObject.getString("ContentValue")
                    val isActive =
                        if (jsonObject.isNull("isActive")) false else jsonObject.getBoolean("isActive")
                    when (groupName.lowercase()) {
                        "notice" -> when (contentName.lowercase()) {
                            "available" -> if (contentValue.equals("true", ignoreCase = true)) {
                                updateCheck = true
                            }
                            "message" -> displayText = contentValue
                        }
                        "maintenance" -> when (contentName.lowercase()) {
                            "undermaintenance" -> if (contentValue.equals(
                                    "true",
                                    ignoreCase = true
                                )
                            ) {
                                maintenance = true
                            }
                            "title" -> maintenanceObject.put("Title", contentValue)
                            "message" -> maintenanceObject.put("Message", contentValue)
                            "startdatetime" ->                                         //10/31/2019 15:30:00 this is the format given by server.
                                maintenanceObject.put("StartDateTime", contentValue)
                            "enddatetime" ->                                         //10/31/2019 15:30:00 this is the format given by server.
                                maintenanceObject.put("EndDateTime", contentValue)
                        }
                        "pcinspection" -> if (contentName.equals("Available", ignoreCase = true)
                            && contentValue.equals("true", ignoreCase = true)
                        ) {
                            controller!!.isShowPcInspection = true
                        }
                    }
                }
                if (updateCheck) {
                    if (displayText.length > 0) {
                        displayProfileDialog(displayText, callback)
                    } else if (maintenance) {
                        controller!!.maintenanceObject = maintenanceObject
                        startActivity(Intent(this@ChooseOptionsActivity, MaintenanceActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    } else {
                        switchToNextActivity(callback)
                    }
                } else if (maintenance) {
                    controller!!.maintenanceObject = maintenanceObject
                    startActivity(Intent(this@ChooseOptionsActivity, MaintenanceActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                } else {
                    switchToNextActivity(callback)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                switchToNextActivity(callback)
            }
        }
    }

    private fun displayProfileDialog(display: String?, callback: (Boolean) -> Unit) {
        updateDialog = Dialog(this@ChooseOptionsActivity)
        updateDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        updateDialog?.let { it ->
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            updateDialog!!.setCanceledOnTouchOutside(false)
            updateDialog!!.setContentView(com.dmss.burbankappold.R.layout.app_update_dialog)
            val updateMessageTextView =
                updateDialog!!.findViewById<View>(com.dmss.burbankappold.R.id.updateMessageTextView) as TextView
            updateMessageTextView.text = display
            val notNowButton = updateDialog!!.findViewById<View>(com.dmss.burbankappold.R.id.notNowButton) as Button
            notNowButton.typeface = controller!!.typeface
            val updateAppButton = updateDialog!!.findViewById<View>(com.dmss.burbankappold.R.id.updateAppButton) as Button
            updateAppButton.typeface = controller!!.typeface
            val fingerPrintCheckBox =
                updateDialog!!.findViewById<View>(com.dmss.burbankappold.R.id.fingerPrintCheckBox) as CheckBox
            notNowButton.setOnClickListener {
                updateDialog!!.dismiss()
                switchToNextActivity(callback)
            }
            updateAppButton.setOnClickListener {
                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "market://details?id=$appPackageName"
                            )
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            )
                        )
                    )
                }
            }
            updateDialog!!.show()
        }
    }
    private fun inintView() {
        analytics = Firebase.analytics
        customSharedPreferences = CustomSharedPreferences(this)
        customSharedPreferences.saveToken("")

        //tv_app_version.text = BuildConfig.VERSION_NAME;
        /* StatusBarUtil.setTransparent(this)*/
        val rlJust = findViewById(R.id.rl_just) as RelativeLayout
        val rlDeposit = findViewById(R.id.rl_deposite) as RelativeLayout
        val rl_finish = findViewById(R.id.rl_finish) as RelativeLayout
        val tv_continue = findViewById(R.id.tv_continue) as TextView
        iv_profile = findViewById(R.id.iv_profile) as ImageView
        val tv_favorites = findViewById(R.id.tv_favorites) as TextView
        val tv_content = findViewById(R.id.tv_content) as TextView

        val myPreference = CustomSharedPreferences(this)
        tv_content.setMovementMethod(ScrollingMovementMethod())

        Log.e("GMAIL", "" + myPreference.getStateID())
        iv_check_just = findViewById(R.id.iv_check_just) as ImageView
        iv_check_deposit = findViewById(R.id.iv_check_deposit) as ImageView
        val iv_check_finish = findViewById(R.id.iv_check_finish) as ImageView
        val tv_how_does_it = findViewById(R.id.tv_how_does_it) as TextView
        checkChooseOPtion()
        tv_how_does_it.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        tv_favorites.setOnClickListener {
            AppUtils.showPleaseLoginDialog(
                this,
                this,
                "Please login to view/edit profile"
            )
        }


        rlJust.setOnClickListener {
            from = 0
            iv_check_just.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.checked))
            iv_check_deposit.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
            iv_check_finish.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
            getLocation()

        }
        rlDeposit.setOnClickListener {
            from = 1
            iv_check_just.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
            iv_check_deposit.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.checked))
            iv_check_finish.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
            callLoginData()
        }
        rl_finish.setOnClickListener {
            from = 2
            iv_check_just.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
            iv_check_deposit.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
            iv_check_finish.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.checked))
        }

        tv_continue.setOnClickListener(this)

        /*  tv_continue.setOnClickListener {
              customSharedPreferences.saveToken("")
              spalashViewModel.fetchLoginData()
          }*/
    }

    private fun switchToNextActivity(callback: (Boolean) -> Unit) {
        if (!controller.isUserLoggedIn) {
            startActivity(Intent(this, FirstClass::class.java))
        } else {
            if (controller.userProfile.userDetailses == null || !controller.userProfile.userDetails[0].isMyPlaceAccessible) {
                startActivity(Intent(this, DashboardNewActivity::class.java))
//                startActivity(Intent(this, HomeActivity::class.java))

            } else {
                callback.invoke(true)
            }
        }
        finish()
    }

    fun checkChooseOPtion(){
        if(customSharedPreferences.getselectedoption()==0) {
            iv_check_just.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.checked))
            iv_check_deposit.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
        }else{
            iv_check_just.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.unchecked))
            iv_check_deposit.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.checked))
        }
    }
    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (v != null) {
            if (v.id == R.id.tv_continue) {
                callLoginData()
            }
        }
    }

    private fun callLoginData(){
        customSharedPreferences.saveToken("")
        spalashViewModel.fetchLoginData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        AppConstants.validateVersionCode(this) {
            if(it) {
                logoutIamBuilding()
            }
        }
        /*GlobalScope.launch (Dispatchers.IO){
            var currentVersionCode=spalashViewModel.checkVersionCodeAPi()
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName.toDouble()
            println("currentVersionCode:: "+currentVersionCode+" version:: "+version)

            if(version<currentVersionCode){
                runOnUiThread( Runnable() {
                    showUpdateVewVersionAppDialog(

                        "New version Available"
                    )
                })
            }

        }*/

    }
    fun showUpdateVewVersionAppDialog( message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val title = SpannableString("MyPlace")
        title.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0,
            title.length,
            0
        )
        dialogBuilder.setTitle(title)//Should be in center
        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(true)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>Update</font>")) { dialog, _ ->
                dialog.cancel()
                //Removing All Saved Local Data
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        //alert.setTitle("Select ${state.name}?")
        // show alert dialog
        alert.show()
        val negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)

        negativeButton.isAllCaps = false
        positiveButton.isAllCaps = false

        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10F
        positiveButton.layoutParams = layoutParams
        negativeButton.layoutParams = layoutParams
    }
    private fun checkAndRequestNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") ==
                PackageManager.PERMISSION_GRANTED
            ) {



//                Log.e(TAG, "PERMISSION_GRANTED")
                // FCM SDK (and your app) can post notifications.
            } else {
//                Log.e(TAG, "NO_PERMISSION")
                // Directly ask for the permission
                requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
            }
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // ✅ Permission granted – show your notification
            showNotification()
        } else {
            // ❌ Permission denied
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showNotification() {
        // Your notification logic here
        Toast.makeText(this, "Notification shown", Toast.LENGTH_SHORT).show()
    }
}
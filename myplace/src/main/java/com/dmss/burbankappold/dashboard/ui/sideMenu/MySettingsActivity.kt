package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.text.InputType
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dmss.burbankappold.FirstClass
import com.dmss.burbankappold.MyPlaceSettingActivity
import com.dmss.burbankappold.R
import com.dmss.burbankappold.UpdatePassword
import com.dmss.burbankappold.dashboard.HomeActivity
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.documents.WebViewActivity
import com.dmss.burbankappold.databinding.ActivityMySettingsBinding
import com.dmss.burbankappold.utils.*
import com.squareup.picasso.Picasso
import common.AppController
import common.Common
import common.CustomEditText
import common.Utils
import interfaces.WebApiResponseCallback
import models.photos.PhotosDataItem
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.util.*

class MySettingsActivity : AppCompatActivity(), WebApiResponseCallback {
    private var alertDialog: AlertDialog? = null
    private var fullName: String? = null
    private lateinit var mBinding : ActivityMySettingsBinding
    var controller: AppController? = null
    var apiCall = 0
    var updateSettings = 1
    var updateProfilePic:Int = 2
    var updateProfile:Int = 3
    var getNotification:Int = 4
    var callback: WebApiResponseCallback? = null
    private var is_New_Photo_NotificationActivated = false
    private var is_ChangesInConstructionStatus_NotificationActivated = false
    private var is_StageCompletedNotificationActivated = false

    private var photo_NotificationActivated_status = false
    private var changesInConstructionStatus_NotificationActivated_status = false
    private var stageCompletedNotificationActivated_status = false
    val permissionReadCamera = 1
    val SELECT_FILE = 2
    var isImageCaptured = false
    var profileDialog: Dialog? = null
    var photosText: String? = null
    var stageText: String? = null
    var constructionText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.toolbar.line.visibility=View.VISIBLE
        callback = this
        controller = applicationContext as AppController
        controller!!.analytics.setScreen(this, "Settings_Screen")
        initViews()
        setOnClickListeners()
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    permissionReadCamera
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Common.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Common.tempPath = Common.imageUri.path
                    isImageCaptured = true
                    showDialog()
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == MyPlaceSettingActivity.SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.let {
                    val filePath = arrayOf(MediaStore.Images.Media.DATA)
                    val c = contentResolver.query(it, filePath, null, null, null)
                    c!!.moveToFirst()
                    val columnIndex = c.getColumnIndex(filePath[0])
                    Common.tempPath = c.getString(columnIndex)
                    c.close()
                    isImageCaptured = true
                    showDialog()
                }

            } else {
                Toast.makeText(
                    this,
                    " This Image cannot be stored .please try with some other Image. ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initViews() {
        mBinding.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        mBinding.toolbar.constraint.setBackgroundColor(Color.TRANSPARENT)
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivChat.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.tvToolbarTitle.changeIconColor(this)
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
//            onBackPressed()
           /* private var is_New_Photo_NotificationActivated = false
            private var is_ChangesInConstructionStatus_NotificationActivated = false
            private var is_StageCompletedNotificationActivated = false

            private var photo_NotificationActivated_status = false
            private var changesInConstructionStatus_NotificationActivated_status = false
            private var stageCompletedNotificationActivated_status = false*/

            println("photo_NotificationActivated_status:: "+
                    photo_NotificationActivated_status+" is_New_Photo_NotificationActivated:: "+is_New_Photo_NotificationActivated+
                    "changesInConstructionStatus_NotificationActivated_status:: "+changesInConstructionStatus_NotificationActivated_status
            +"is_ChangesInConstructionStatus_NotificationActivated:: "+is_ChangesInConstructionStatus_NotificationActivated
            +"stageCompletedNotificationActivated_status::  "+stageCompletedNotificationActivated_status+" is_StageCompletedNotificationActivated:: "+is_StageCompletedNotificationActivated)
            if(photo_NotificationActivated_status!=is_New_Photo_NotificationActivated || changesInConstructionStatus_NotificationActivated_status!=is_ChangesInConstructionStatus_NotificationActivated
                || stageCompletedNotificationActivated_status!=is_StageCompletedNotificationActivated) {
                showChangesMessageAlert(this)
            }

           /* }else if(changesInConstructionStatus_NotificationActivated_status!=is_ChangesInConstructionStatus_NotificationActivated){
//                Toast.makeText(this,"Do you want to save changes",Toast.LENGTH_SHORT).show()
                showChangesMessageAlert(this)

            }else if(stageCompletedNotificationActivated_status!=is_StageCompletedNotificationActivated){
//                Toast.makeText(this,"Do you want to save changes",Toast.LENGTH_SHORT).show()
                showChangesMessageAlert(this)

            }*/
            else{
                onBackPressed()
            }
        }
        mBinding.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
//        mBinding.tvNotificationsCount.text = PrefsHelper.notificationCount
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            mBinding.tvNotificationsCount.text = notificationCount
        }
        mBinding.ivPhoto.setOnClickListener {
            startActivity(Intent(this, MyNotificationActivity::class.java))
        }
    }

    private fun setOnClickListeners() {
        mBinding.editBtn.setOnClickListener{
            Utils.selectImageDialog(this)
        }
        mBinding.imageView2.setOnClickListener{
            Utils.selectImageDialog(this)
        }
        mBinding.logoutMyPlaceTextView.setOnClickListener {
            controller!!.analytics.settingsLogoutButtonTouchEvent()
            controller!!.isUserLoggedIn = false
            controller!!.loggedInFromSocial = false
            controller!!.my_Place_Details = null
            controller!!.profilePicUrl = ""
            controller!!.setProfileInfo("")
            AppConstants.financeItemStatus=false
            controller!!.selectedJobPosition = 0
            AppConstants.filterList= emptyList()
            AppConstants.NoRecentPhotos=""
            AppConstants.PhotosSubheader=""
            AppConstants.AppCookieContactUs=""
            AppConstants.savedphotosList.clear()
            AppConstants.savedphotosList=mutableListOf<List<PhotosDataItem>>()
            AppController.setPreference(this,AppController.SELECTEDJOBNUMBER,AppController.defaultValue)
            AppController.setPreference(this,AppController.PREVIOUSSELECTEDJOBNUMBER,AppController.defaultValue)

            PrefsHelper.clearPrefs()
            val intent = Intent(
                this,
                FirstClass::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        mBinding.resetPassword.setOnClickListener {
            controller!!.analytics.settingsResetPasswordTouchEvent()
            val intent = Intent(
                this,
                UpdatePassword::class.java
            )
            startActivity(intent)
        }
        mBinding.editProfile.setOnClickListener{
            controller!!.analytics.settingsEditIconTouchEvent()
            displayProfileDialog()
        }

        mBinding.tvSave.setOnClickListener{
            if (Utils.isNetworkAvailable(this)) {
                apiCall = updateSettings
                controller!!.webApiCall().postData(
                    Common.updateMyPlaceNotificationSetting,
                    getUpdateSettingsJson(),
                    callback,
                    Utils.getProgress(this)
                )
            }
        }
       if(controller!!.userProfile.userDetailses.size>0) {
           mBinding.userName.text = controller!!.userProfile.userDetailses[0].fullName
           mBinding.userEmailId.text = controller!!.userProfile.userDetailses[0].email
       }


        //lastUpdatedTime = controller.getLastUpdatedTime();


        //lastUpdatedTime = controller.getLastUpdatedTime();
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName
            mBinding.versionCode.text = version
            //versionCode.setText("0.5");
        } catch (ex: java.lang.Exception) {
            ex.fillInStackTrace()
        }
        mBinding.about.setOnClickListener{
            controller!!.analytics.settingsWhatIsBurbankAppForwardArrowTouchEvent()
            startActivity(Intent(this, WebViewActivity::class.java)
                .putExtra(BundleKey.URL, "file:///android_asset/about_burbank.html"))
        }

        mBinding.photoNotificationImg.setOnClickListener(View.OnClickListener {
            controller!!.analytics.settingsPhotosAddedOnOffIconTouchEvent()
            if (is_New_Photo_NotificationActivated) {
                is_New_Photo_NotificationActivated = false
                mBinding.photoNotificationImg.setImageResource(R.drawable.check_unfill)
            } else {
                is_New_Photo_NotificationActivated = true
                mBinding.photoNotificationImg.setImageResource(R.drawable.check_fill)
            }
        })
        mBinding.constructionNotificationImg.setOnClickListener{
            controller!!.analytics.settingsStagesChangesOnOffIconTouchEvent()
            if (is_ChangesInConstructionStatus_NotificationActivated) {
                is_ChangesInConstructionStatus_NotificationActivated = false
                mBinding.constructionNotificationImg.setImageResource(R.drawable.check_unfill)
            } else {
                is_ChangesInConstructionStatus_NotificationActivated = true
                mBinding.constructionNotificationImg.setImageResource(R.drawable.check_fill)
            }
        }

        mBinding.stageNotificationImg.setOnClickListener{
            controller!!.analytics.settingsStageCompletionAddedOnOffIconTouchEvent()
            if (is_StageCompletedNotificationActivated) {
                is_StageCompletedNotificationActivated = false
                mBinding.stageNotificationImg.setImageResource(R.drawable.check_unfill)
            } else {
                is_StageCompletedNotificationActivated = true
                mBinding.stageNotificationImg.setImageResource(R.drawable.check_fill)
            }
        }

        if (Utils.isNetworkAvailable(this)) {
            apiCall = getNotification
            controller!!.webApiCall().postData(
                Common.getMyPlaceNotificationSetting,
                getJson(),
                this,
                Utils.getProgress(this)
            )
        }
        mBinding.terms.setOnClickListener{
            controller!!.analytics.settingsTermsOfUseForwardArrowTouchEvent()
            val `in` = Intent(
                this,
                TermsOfUseActivity::class.java
            )
            startActivity(`in`)
        }
        mBinding.privacy.setOnClickListener{
            controller!!.analytics.settingsPrivacyPolicyForwardArrowTouchEvent()
            val `in` = Intent(
                this,
                PrivacyPolicyActivity::class.java
            )
            startActivity(`in`)
        }
    }
    private fun getJson(): String? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("Id", controller!!.userProfile.userDetailses[0].id)
        } catch (ex: java.lang.Exception) {
            ex.fillInStackTrace()
        }
        return jsonObject.toString()
    }

    private fun getUpdateSettingsJson(): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("UserName", controller!!.userProfile.userDetailses[0].fullName)
            jsonObject.put("Email", controller!!.userProfile.userDetailses[0].email)
            jsonObject.put("UserId", controller!!.userProfile.userDetailses[0].id)
            val jsonArray = JSONArray()
            for (i in 1..3) {
                val notication = JSONObject()
                notication.put("NotificationTypeId", i)
                notication.put("IsActive", true)
                when (i) {
                    1 -> {
                        notication.put("IsUserOpted", is_New_Photo_NotificationActivated)
                        jsonArray.put(0, notication)
                    }
                    2 -> {
                        notication.put("IsUserOpted", is_StageCompletedNotificationActivated)
                        jsonArray.put(1, notication)
                    }
                    3 -> {
                        notication.put(
                            "IsUserOpted",
                            is_ChangesInConstructionStatus_NotificationActivated
                        )
                        jsonArray.put(2, notication)
                    }
                }
            }
            jsonObject.put("NotificationTypes", jsonArray)
        } catch (ex: Exception) {
            ex.fillInStackTrace()
        }
        return jsonObject.toString()
    }

    override fun onSuccessResult(result: String?) {
        if (result != null) {
            try {
                val job = JSONObject(result)
                Log.d("Status", job.toString())
                val status = job.getBoolean("Status")
                val message = job.getString("Message")
                if (status) {
                    if (apiCall == updateProfilePic) {
                        val uri = Uri.fromFile(File(Common.tempPath))
                        Utils.showToast(this, message, Common.sucessCase)
                        if (alertDialog != null) {
                            alertDialog!!.cancel()
                        }
                        val jsonObject = job.getJSONObject(Common.Result_Key)
                        val profilePicString =
                            if (jsonObject.isNull("ProfilePicPath")) "" else jsonObject.getString("ProfilePicPath")
                        PrefsHelper.profileUrl = profilePicString
                        runOnUiThread {
                            DownloadImageTask()
                                .execute(profilePicString)
                        }
                    } else if (apiCall == updateSettings) {
                        runOnUiThread {
                          /*  Utils.showToast(
                                this,
                                message,
                                Common.sucessCase
                            )*/

                            controller!!.isPhotoNotifications = is_New_Photo_NotificationActivated
                            controller!!.isProgressNotifications =
                                is_ChangesInConstructionStatus_NotificationActivated
                            controller!!.isStageNotifications =
                                is_StageCompletedNotificationActivated
                            showChangesDoneAlert(message)

                        }

                    } else if (apiCall == getNotification) {
                        val jsonObject = job.getJSONObject("Result")
                        val profilePicString =
                            if (jsonObject.isNull("ProfilePicPath")) "" else jsonObject.getString("ProfilePicPath")
                        val jsonArray = jsonObject.getJSONArray("NotificationTypes")
                        controller!!.setLastUpdatedTime(jsonObject.getString("UpdatedDate"))
                        val userProfileName =
                            if (jsonObject.isNull("UserName")) "" else jsonObject.getString("UserName")
                        val emailProfileId =
                            if (jsonObject.isNull("Email")) "" else jsonObject.getString("Email")
                        runOnUiThread {
                            mBinding.userName.text = userProfileName
                            mBinding.userEmailId.text = emailProfileId
                            PrefsHelper.profileUrl = profilePicString
                            DownloadImageTask().execute(profilePicString)
                        }
                        for (i in 0 until jsonArray.length()) {
                            val notification = jsonArray.getJSONObject(i)
                            when (i) {
                                0 -> {
                                    is_New_Photo_NotificationActivated =
                                        if (notification.isNull("IsUserOpted")) false else notification.getBoolean(
                                            "IsUserOpted"
                                        )
                                    controller!!.isPhotoNotifications =
                                        is_New_Photo_NotificationActivated
                                    photosText =
                                        if (notification.isNull("Name")) "" else notification.getString(
                                            "Name"
                                        )
                                    photo_NotificationActivated_status=is_New_Photo_NotificationActivated
                                }
                                1 -> {
                                    is_StageCompletedNotificationActivated =
                                        if (notification.isNull("IsUserOpted")) false else notification.getBoolean(
                                            "IsUserOpted"
                                        )
                                    controller!!.isStageNotifications =
                                        is_StageCompletedNotificationActivated
                                    stageText =
                                        if (notification.isNull("Name")) "" else notification.getString("Name")

                                    stageCompletedNotificationActivated_status=is_StageCompletedNotificationActivated
                                }
                                2 -> {
                                    is_ChangesInConstructionStatus_NotificationActivated =
                                        if (notification.isNull("IsUserOpted")) false else notification.getBoolean("IsUserOpted"  )
                                    controller!!.isProgressNotifications =
                                        is_ChangesInConstructionStatus_NotificationActivated
                                    constructionText =
                                        if (notification.isNull("Name")) "" else notification.getString(
                                            "Name"
                                        )
                                    changesInConstructionStatus_NotificationActivated_status=is_ChangesInConstructionStatus_NotificationActivated
                                }
                            }
                        }
                        runOnUiThread {
                            if (photosText != null && photosText!!.isNotEmpty()) {
                                mBinding.photosTextView.text = photosText
                            }
                            if (stageText != null && stageText!!.isNotEmpty()) {
                                mBinding.stageTextView.text = stageText
                            }
                            if (constructionText != null && constructionText!!.isNotEmpty()) {
                                mBinding.constructionTextView.text = constructionText
                            }
                            /*if (mBinding.userName.length() > 0) {
                                controller!!.dashBoardWelcomeText.text = "Welcome " + Utils.getCamelCase(userProfileName)
                            }*/
                        }
                        updateSettings()
                    } else if (apiCall == updateProfile) {
                        val jsonObject = job.getJSONObject(Common.Result_Key)
                        if (profileDialog != null) {
                            profileDialog!!.cancel()
                        }
                        controller!!.setUserProfileShown(
                            controller!!.userProfile.userDetails[0].email.trim { it <= ' ' },
                            true
                        )
                        runOnUiThread {
                            controller!!.userProfile.userDetails[0].updateProfile(jsonObject)
                            controller!!.updateUserProfile(jsonObject)
                            mBinding.userName.text = controller!!.userProfile.userDetails[0].fullName
                            mBinding.userEmailId.text = controller!!.userProfile.userDetails[0].email
                            /*controller!!.dashBoardWelcomeText.text =
                                "Welcome " + Utils.getCamelCase(
                                    controller!!.userProfile.userDetails[0].fullName
                                )*/
                        }
                        Utils.showToast(
                            this,
                            job.getString(Common.Message),
                            Common.sucessCase
                        )
                    }
                } else {
                    Utils.showToast(this, message, Common.errorCase)
                }
            } catch (ex: java.lang.Exception) {
                ex.fillInStackTrace()
            }
        }
    }

    override fun onErrorResult(error: String?) {

    }

    private fun updateSettings() {
        runOnUiThread {
            if (is_New_Photo_NotificationActivated) {
                mBinding.photoNotificationImg.setImageResource(R.drawable.check_fill)
            } else {
                mBinding.photoNotificationImg.setImageResource(R.drawable.check_unfill)
            }
            if (is_ChangesInConstructionStatus_NotificationActivated) {
                mBinding.constructionNotificationImg.setImageResource(R.drawable.check_fill)
            } else {
                mBinding.constructionNotificationImg.setImageResource(R.drawable.check_unfill)
            }
            if (is_StageCompletedNotificationActivated) {
                mBinding.stageNotificationImg.setImageResource(R.drawable.check_fill)
            } else {
                mBinding.stageNotificationImg.setImageResource(R.drawable.check_unfill)
            }
        }
    }


    private fun displayProfileDialog() {
        //try {
        profileDialog = Dialog(this)
        profileDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        profileDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        profileDialog?.setCanceledOnTouchOutside(false)
        profileDialog?.setContentView(R.layout.set_profile_dialog)
        val heading = profileDialog?.findViewById<View>(R.id.textView) as TextView
        heading.typeface = controller!!.typeface
        heading.text = "Edit Profile"
        val firstNameEditText =
            profileDialog?.findViewById<View>(R.id.firstNameEditText) as CustomEditText
        firstNameEditText.setInputType(InputType.TYPE_CLASS_TEXT)
        firstNameEditText.setHint("First Name")
        firstNameEditText.setFont(controller!!.typeface)
        firstNameEditText.setImage(R.drawable.icon_user)
        firstNameEditText.setLayoutBackground(R.drawable.grey_border_edittext)
        firstNameEditText.setSelection(firstNameEditText.text.length)
        val lastNameEditText =
            profileDialog?.findViewById<View>(R.id.lastNameEditText) as CustomEditText
        lastNameEditText.setInputType(InputType.TYPE_CLASS_TEXT)
        lastNameEditText.setHint("Last Name")
        lastNameEditText.setFont(controller!!.typeface)
        lastNameEditText.setImage(R.drawable.icon_user)
        lastNameEditText.setLayoutBackground(R.drawable.grey_border_edittext)
        lastNameEditText.setSelection(firstNameEditText.text.length)
        val phoneNumberEditText =
            profileDialog?.findViewById<View>(R.id.phoneNumberEditText) as CustomEditText
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE)
        phoneNumberEditText.setHint("Mobile Number")
        phoneNumberEditText.setFont(controller!!.typeface)
        phoneNumberEditText.setImage(R.drawable.icon_mobile)
        firstNameEditText.text = controller!!.userProfile.userDetails[0].firstName
        lastNameEditText.text = controller!!.userProfile.userDetails[0].lastName
        phoneNumberEditText.text = controller!!.userProfile.userDetails[0].mobile
        if (firstNameEditText.text.isNotEmpty()) {
            firstNameEditText.setSelection(firstNameEditText.text.length)
        }
        if (phoneNumberEditText.text.isNotEmpty()) {
            phoneNumberEditText.setSelection(phoneNumberEditText.text.length)
        }
        if (lastNameEditText.text.isNotEmpty()) {
            lastNameEditText.setSelection(lastNameEditText.text.length)
        }
        phoneNumberEditText.setLayoutBackground(R.drawable.grey_border_edittext)
        phoneNumberEditText.customEditText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Method for validating the input data
                validatingInputProfileDetails(
                    firstNameEditText,
                    lastNameEditText,
                    phoneNumberEditText
                )
            }
            false
        }
        val skipProfileButton = profileDialog?.findViewById<View>(R.id.skipProfileButton) as Button
        skipProfileButton.typeface = controller!!.typeface
        skipProfileButton.text = "Cancel"
        val saveProfileButton = profileDialog?.findViewById<View>(R.id.saveProfileButton) as Button
        saveProfileButton.typeface = controller!!.typeface
        val fingerPrintCheckBox =
            profileDialog?.findViewById<View>(R.id.fingerPrintCheckBox) as CheckBox
        skipProfileButton.setOnClickListener { profileDialog?.dismiss() }
        saveProfileButton.setOnClickListener {
            validatingInputProfileDetails(
                firstNameEditText,
                lastNameEditText,
                phoneNumberEditText
            )
        }
        profileDialog?.show()
        /*} catch (Exception ex) {
            ex.fillInStackTrace();
        }*/
    }

    private fun validatingInputProfileDetails(
        firstNameEditText: CustomEditText,
        lastNameEditText: CustomEditText,
        phoneNumberEditText: CustomEditText
    ) {
        //Checking whether network is available or not.
        if (Utils.isNetworkAvailable(this)) {
            //Validating the first name given by the user.
            if (!controller!!.validation.isNotNull(firstNameEditText)) {
              /*  Utils.showToast(
                    this,
                    "First name should not be empty",
                    Common.errorCase
                )*/
                Common.showMessageAlert(this@MySettingsActivity,"Please enter first name")

            } else if (!controller!!.validation.isNotNull(lastNameEditText)) //Validating the last name given by the user.
            {
               /* Utils.showToast(
                    this,
                    "Last name should not be empty",
                    Common.errorCase
                )*/
                Common.showMessageAlert(this@MySettingsActivity,"Please enter last name")

            } else if (controller!!.validation.validPhoneLength(this@MySettingsActivity,phoneNumberEditText)) //Validating the mobile number given by the user.
            {
                val jsonObject = JSONObject()
                try {
                    fullName = firstNameEditText.text.toString()
                        .trim { it <= ' ' } + " " + lastNameEditText.text.toString()
                        .trim { it <= ' ' }
                    jsonObject.put("Email", controller!!.userProfile.userDetailses[0].email)
                    jsonObject.put(
                        "FirstName",
                        firstNameEditText.text.toString().trim { it <= ' ' })
                    jsonObject.put("LastName", lastNameEditText.text.toString().trim { it <= ' ' })
                    jsonObject.put("FullName", fullName)
                    jsonObject.put("Mobile", phoneNumberEditText.text.toString().trim { it <= ' ' })
                    jsonObject.put("Id", controller!!.userProfile.userDetails[0].id)
                    jsonObject.put("Region", controller!!.userProfile.userDetails[0].region)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apiCall = updateProfile
                controller!!.webApiCall().postData(
                    Common.updateUser_Url,
                    jsonObject.toString(),
                    this,
                    Utils.getProgress(this)
                )
            }
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Update Profile Pic")
        val frameView = FrameLayout(this)
        builder.setView(frameView)
        alertDialog = builder.create()
        val inflater: LayoutInflater = alertDialog!!.layoutInflater
        val dialoglayout = inflater.inflate(R.layout.uploadprofilepic, frameView)
        val image = dialoglayout.findViewById<View>(R.id.pic) as ImageView
        val upload = dialoglayout.findViewById<View>(R.id.upload) as Button
        upload.typeface = controller!!.typeface
        val uri = Uri.fromFile(File(Common.tempPath))
        Picasso.get().load(uri)
            .resize(500, 500).centerCrop().into(image)
        upload.typeface = controller!!.typeface
        upload.setOnClickListener {
            if (isImageCaptured == true) {
                apiCall = updateProfilePic
                val pd = Utils.getProgress(this)
                controller!!.webApiCall().postData(
                    Common.updateProfilePicUrl,
                    getUpdateProfileJson(Common.tempPath),
                    this,
                    pd
                )
            }
        }
        alertDialog?.show()
    }
    private fun getUpdateProfileJson(path: String?): String? {
        val job = JSONObject()
        try {
            job.put("UserId", controller!!.userProfile.userDetails[0].id)
            job.put("ImageContent", MyPlaceSettingActivity.getBase64(path))
        } catch (ex: java.lang.Exception) {
            ex.fillInStackTrace()
        }
        return job.toString()
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadImageTask :
        AsyncTask<String?, Void?, Bitmap?>() {
        protected override fun doInBackground(vararg urls: String?): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: java.lang.Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }
            return mIcon11
        }

        protected override fun onPostExecute(result: Bitmap?) {
            runOnUiThread {
                if (result != null) {
                    mBinding.imageView2.setImageBitmap(result)
                    mBinding.ivPhoto.setImageBitmap(result)
                }
                // Stuff that updates the UI
            }
        }
    }
    fun showChangesDoneAlert(message:String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            val title = SpannableString("MyPlace")
            title.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            val message = SpannableString(message)
            message.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            builder.setMessage(message)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml(
                    "<font color=" + resources.getColor(
                        R.color.appColor
                    ) + ">OK</font>"
                ),
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()

                    finish()

                })
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
            val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            btnPositive.layoutParams = layoutParams
            btnNegative.layoutParams = layoutParams
        }
    }
    fun showChangesMessageAlert(mContext: Activity) {
        mContext.runOnUiThread {
            val builder = AlertDialog.Builder(mContext)
            val title = SpannableString("MyPlace")
            title.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            val message = SpannableString("Do you wish to save the changes to your notifications?")
            message.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            builder.setMessage(message)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml(
                    "<font color=" + mContext.resources.getColor(
                        R.color.appColor
                    ) + ">OK</font>"
                ),
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                    if (Utils.isNetworkAvailable(this)) {
                        apiCall = updateSettings
                        controller!!.webApiCall().postData(
                            Common.updateMyPlaceNotificationSetting,
                            getUpdateSettingsJson(),
                            callback,
                            Utils.getProgress(this)
                        )
                    }

                })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, which: Int -> dialog.cancel()
                    onBackPressed()
                })
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
            val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            btnPositive.layoutParams = layoutParams
            btnNegative.layoutParams = layoutParams
        }
    }
}
package com.dmss.burbankapp.ui.dashboard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.*
import com.dmss.burbankapp.databinding.*
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.login.ChooseOptionsActivity
import com.dmss.burbankapp.ui.loginhome.LoginHomeActivity
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.splash.SpalashViewModel
import com.dmss.burbankapp.ui.view.CircleTransform
import com.dmss.burbankapp.utility.ExpandableLayout
import com.dmss.burbankapp.utility.ImagePickerActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.dmss.burbankapp.utils.customviews.SpinnerImage
import com.dmss.burbankapp.utils.customviews.Utility
import com.dmss.burbankapp.viewmodel.ProfilePicViewModel
import com.dmss.burbankappold.network.ApiRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import common.AppController
import common.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class DashboardActivity : BaseActivity(), StateListAdapter.IUpdateState,
    ShareListAdapter.ShareAccountItemClick {
    lateinit var binding: ActivityDashboardBinding
    lateinit var profileLayoutBinding: ProfileLayoutBinding;
    lateinit var dashboardProfileWidthDadgeBinding: DashboardProfileWidthDadgeBinding
    lateinit var spalashViewModel: SpalashViewModel

    lateinit var stateDialogBinding: LayoutChooseStateDialogBinding
    var isStateSelectedPreviously: Boolean = false
    lateinit var sharingAccountBinding: LayoutEmailSharingDialogBinding
    lateinit var stateAdapter: StateListAdapter
    lateinit var statesList: ArrayList<StateModel>
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var stateAlertBuilder: AlertDialog.Builder
    private lateinit var profileProgress: ProgressBar
    private val CAMERA_REQUEST_CODE = 100
    var REQUEST_CODE_LOCATION_PERMISSION = 12


    lateinit var shareAdapter: ShareListAdapter
    private var placePicture: String = ""

    lateinit var myPreference: CustomSharedPreferences

    lateinit var viewModel: ProfilePicViewModel;
    lateinit var tv_state: TextView
    lateinit var loginModel: LoginModel
    lateinit var dashboardViewModel: DashboardViewModel
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    lateinit var stateAlertDialog: AlertDialog

    lateinit var iv_profile_dialog: ImageView
    var selectedStateId: Int = -1
    lateinit var userInfoModel: UserInfoModel
    private lateinit var relative_share_list: RelativeLayout

    private lateinit var tv_home_land_price: TextView
    private lateinit var tv_home_land_region: TextView
    private lateinit var tv_home_land_save_packages: TextView
    private lateinit var myCollectionRecentSearchText: String
    var isFromSignUp: Boolean? = false
    private var options: RequestOptions? = null

    var userId: Int = 0

    var isMyCollectionRecentSearchDataEmpty: Boolean = true
    var isHomeAndLandRecentSearchDataEmpty: Boolean = true

    var recentSearchHashMap: HashMap<String, ArrayList<NewHomeJsonObject>> = LinkedHashMap()

    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null
    var recentSearchJsonModel: SearchJsonModel?=null


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
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        dashboardProfileWidthDadgeBinding = DashboardProfileWidthDadgeBinding.bind(binding.root)
        setContentView(binding.root)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        /*try {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                mReceiver,
                IntentFilter("LoginSuccess")
            )
        }catch (e : Exception){
            e.printStackTrace()
        }*/

        locationPermission()

        options = RequestOptions()
            .centerCrop()
            .priority(Priority.HIGH)


        statesList = ArrayList()
        myPreference = CustomSharedPreferences(this)

        selectedStateId = myPreference.getStateID()


        isFromSignUp = intent?.getBooleanExtra(AppConstants.USER_CREATED, false)
        isFromSignUp?.let {
            if (it) {
              /*  runOnUiThread() {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Logged in Successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }*/
            }
        }


        setupViewModel()

        tv_state = findViewById(R.id.tv_state)
        binding.backDBIV.setOnClickListener {
            var isUserLoggedIn = myPreference.getUserLogin()
            if (isUserLoggedIn) {
                Utility.logoutDialog(this)
            }else{
                val intent = Intent(this, ChooseOptionsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.llHome.setOnClickListener {
            AppConstants.HOMEANDLAND_TAP = 0
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 0)
            )
        }
        binding.llHomeAndLand.setOnClickListener {
            AppConstants.HOMEANDLAND_TAP = 1
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 1)
            )
        }
        binding.llDisplayHomes.setOnClickListener {
            AppConstants.HOMEANDLAND_TAP = 1
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 2)
            )
        }
        binding.rlProfile.setOnClickListener {
            var isUserLoggedIn = myPreference.getUserLogin()
            if (isUserLoggedIn) {
                Utility.logoutDialog(this)
            }

/*
            var isUserLoggedIn = myPreference.getUserLogin()
            if (isUserLoggedIn) {
                val userId: Int = myPreference.getUserId()
                dashboardViewModel.fetchShareAccountDetails(userId)
                getRecentSearchForHomeLandAndMyCollection()
                showProfileDialog(this)


            } else {
                AppUtils.showPleaseLoginDialog(
                    this,
                    this,
                    "Please login to view/edit profile"
                )
            }*/


        }
        dashboardProfileWidthDadgeBinding.tvFavorites.setOnClickListener {

            var isUserLoggedIn = myPreference.getUserLogin()
            if (isUserLoggedIn) {
                val userId: Int = myPreference.getUserId()
                dashboardViewModel.fetchShareAccountDetails(userId)
                getRecentSearchForHomeLandAndMyCollection()
                showProfileDialog(this)


            } else {
                AppUtils.showPleaseLoginDialog(
                    this,
                    this,
                    "Please login to view/edit profile"
                )
            }


        }
        binding.llStateSelection.setOnClickListener {
            showStateDialog();
        }
        var isUserLoggedIn = myPreference.getUserLogin()
        println("isUserLoggedIn Dashboard Activity:: "+isUserLoggedIn)
        if (!isUserLoggedIn) {
            binding.profileProgress.visibility = View.GONE
            dashboardProfileWidthDadgeBinding.tvFavorites.background = resources.getDrawable(R.drawable.disable_gery)

            dashboardProfileWidthDadgeBinding.profileNotification.visibility = View.GONE

        }
        binding.profileProgress.visibility = View.GONE
    }

    private fun getUserFavoritesDisplays() {
        displayHomeViewModel.getUserFavoritesDisplays()
    }

    private fun showSharingAccountDialog(checkEmailForSharingModel: CheckEmailForSharingModel) {

        val checkEmailStatusModel: CheckEmailStatusModel? =
            checkEmailForSharingModel.checkEmailStatusModel
        sharingAccountBinding = LayoutEmailSharingDialogBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        shareAlertBuilder.setView(sharingAccountBinding.root)
        val alertDialog: AlertDialog = shareAlertBuilder.create()
        sharingAccountBinding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        var isCheck = false
        sharingAccountBinding.ivCheck.setOnClickListener {
            isCheck = !isCheck
            if (isCheck) {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardActivity,
                        R.drawable.check_share
                    )
                )
            } else {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardActivity,
                        R.drawable.uncheck_share
                    )
                )
            }
        }
        if (checkEmailStatusModel != null) {
            sharingAccountBinding.tvFullName.text = checkEmailStatusModel.fullName
            sharingAccountBinding.tvEmail.text = checkEmailStatusModel.email
            sharingAccountBinding.tvIAgree.text =
                "I agree for sharing my favourites\nwith ${checkEmailStatusModel.fullName}"


            var shareProfilePic: String? = null

            if (checkEmailStatusModel.profilePic.isNullOrEmpty().not() && checkEmailStatusModel.profilePic!!.contains("~")) {
                shareProfilePic =
                    checkEmailStatusModel.profilePic!!.replace("~", "")
                shareProfilePic = "${AppConstants.PROFILEPIC_BASE}${shareProfilePic}"

            } else {
                shareProfilePic = checkEmailStatusModel.profilePic
            }
            shareProfilePic?.let {
                Picasso.get().load(shareProfilePic).transform(CircleTransform())
                    .error(R.mipmap.logo_myplace)
                    .into(sharingAccountBinding.cvProfile, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {

                        }


                    })
            }
        }
        sharingAccountBinding.tvConfirm.setOnClickListener {
            if (isCheck) {
                alertDialog.dismiss()
                val userID: Int = myPreference.getUserId();
                val jsonObj = JsonObject()
                if (checkEmailStatusModel != null) {
                    jsonObj.addProperty("OthersEmail", checkEmailStatusModel.email)
                    jsonObj.addProperty("FavouriteAdded", true)
                    jsonObj.addProperty("ShareAccount", 1)
                    dashboardViewModel.fetchShareAccountWithEmail(userID, jsonObj)
                }
            } else {
                showSnackbar(sharingAccountBinding.tvConfirm, "Please agree the sharing")
            }
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()


    }


    private fun showStateDialog() {
        stateDialogBinding = LayoutChooseStateDialogBinding.inflate(layoutInflater)
        stateAlertBuilder = AlertDialog.Builder(this)
        stateAlertBuilder.setView(stateDialogBinding.root)
        stateAlertBuilder.setCancelable(false)
        stateAlertDialog = stateAlertBuilder.create()
        stateDialogBinding.ivClose.setOnClickListener {
            myPreference.getStateID().let {
                if (it == -1) {
                    AppUtils.showCustomCenterToast(
                        stateDialogBinding.ivClose.context,
                        "Please select state"
                    )
                } else {
                    stateAlertDialog.dismiss()
                }
            }

        }
        val stateId = myPreference.getStateID()
        if (statesList.size > 0) {
            for (stateModel in statesList) stateModel.isSelected = stateId == stateModel.id
        }
        stateDialogBinding.stateRecyclerview.apply {
            stateAdapter = StateListAdapter(this@DashboardActivity, this@DashboardActivity)
            adapter = stateAdapter
            stateAdapter.setData(statesList)
        }
        stateAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        stateAlertDialog.show()
    }


    private fun showProfileDialog(activity: Activity) {

        profileLayoutBinding = ProfileLayoutBinding.inflate(layoutInflater)
        var userInfoModel = myPreference.getUserInfoModel()
        if (userInfoModel.ProfileImage.isNullOrEmpty().not()) {
            userInfoModel.ProfileImage?.let {
                Picasso.get()
                    .load(it).error(R.mipmap.logo_myplace)
                    .into(profileLayoutBinding.ivProfile)
            }
        }else{
            userInfoModel.ProfileImage?.let {
                Picasso.get()
                    .load(R.mipmap.logo_myplace)
                    .into(profileLayoutBinding.ivProfile)
            }
        }
        dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setView(profileLayoutBinding.root)
        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

        val window: Window = alertDialog.window!!
        val currentDialogAttributes: WindowManager.LayoutParams = window.getAttributes()
        val newDialogAttributes = WindowManager.LayoutParams()
        newDialogAttributes.copyFrom(currentDialogAttributes)
        newDialogAttributes.width = WindowManager.LayoutParams.MATCH_PARENT
        newDialogAttributes.height = WindowManager.LayoutParams.MATCH_PARENT
//        newDialogAttributes.gravity = Gravity.TOP
        window.attributes = newDialogAttributes

        iv_profile_dialog = alertDialog.findViewById<ImageView>(R.id.iv_profile)
        val profile_back =alertDialog.findViewById<ImageView>(R.id.profile_back)
        iv_profile_dialog.setOnClickListener {
            alertDialog.dismiss()
        }
        profile_back.setOnClickListener {
            alertDialog.dismiss()

        }
        profileLayoutBinding.shareAccountNotification.text =
            myPreference.getShareAccountNotification().toString()
        profileLayoutBinding.tvMyCollectionNotification.text =
            AppConstants.MY_COLLECTION_NOTIFICATION


        profileLayoutBinding.ivHomedesignNotification.text = AppConstants.HOME_DESIGN_NOTIFICATION
        profileLayoutBinding.tvHomeandlandNotification.text =
            AppConstants.HOME_AND_LAND_NOTIFICATION

        profileLayoutBinding.tvMyCollectionSaveDesign.setOnClickListener {

            if (AppConstants.newHomeHashMap.size > 0) {
                AppConstants.newHomeHashMap.clear()
            }

            AppConstants.newHomeHashMap.putAll(recentSearchHashMap)

            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 9)
            )
            alertDialog.dismiss()
        }

        profileLayoutBinding.llMyHomeAndDesign.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 6)
            )

            alertDialog.dismiss()
        }
        profileLayoutBinding.llMyfav.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 9)
            )

            alertDialog.dismiss()
        }
        profileLayoutBinding.llHomandlandsearchview.setOnClickListener {
            recentSearchJsonModel?.let {
                AppConstants.setRecentSearchData(it)
            }
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 7)
            )
            alertDialog.dismiss()
        }
        profileLayoutBinding.llMyHomeAndLand.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 8)
            )
            alertDialog.dismiss()

        }

        if (AppConstants.HOME_AND_LAND_NOTIFICATION.toInt() == 0) {
            profileLayoutBinding.tvHomeLandSavePackages.text =
                ("NO SAVED PACKAGES")
        } else {
            profileLayoutBinding.tvHomeLandSavePackages.text =
                ("${AppConstants.HOME_AND_LAND_NOTIFICATION} SAVED PACKAGES")
        }


        /* profileLayoutBinding.tvHomeLandSavePackages.text =
             ("${myPreference.getHomeAndLandnNotification().toString()} SAVED PACKAGES")*/


        profileLayoutBinding.tvMyCollectionSaveDesign.text =
            ("${AppConstants.MY_COLLECTION_NOTIFICATION} DESIGNS")

        profileLayoutBinding.tvHomeDesignSaved.text =
            ("${AppConstants.HOME_DESIGN_NOTIFICATION} SAVED DESIGNS")


        profileLayoutBinding.tvMySavedDisplays.text =
            ("${AppConstants.MY_SAVED_DISPLAY} SAVED DISPLAYS")
        profileLayoutBinding.tvMydisplaysNotification.text = AppConstants.MY_SAVED_DISPLAY
        AppConstants.TotalMyFavs=AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.MY_SAVED_DISPLAY.toInt()
        profileLayoutBinding.favNotification.text=""+AppConstants.TotalMyFavs
        Timber.e("My Saved Displays1 ${AppConstants.MY_SAVED_DISPLAY}")
        val shareRecyclerView = alertDialog.findViewById<RecyclerView>(R.id.share_recycler_view)
        val ll_upload_image = alertDialog.findViewById<LinearLayout>(R.id.ll_upload_image)

        if (myPreference.getLoginType() == "email") {
            ll_upload_image.visibility = View.GONE
        } else {
            ll_upload_image.visibility = View.GONE
        }
        profileProgress = alertDialog.findViewById(R.id.profileProgress)
        relative_share_list = alertDialog.findViewById(R.id.relative_share_list)
        shareRecyclerView.apply {
            shareAdapter =
                ShareListAdapter(this@DashboardActivity, ArrayList(), this@DashboardActivity)
            adapter = shareAdapter
            this.isNestedScrollingEnabled = false
        }

        //MY COLLECTION
        profileLayoutBinding.llMyCollectionRecentSearch.setOnClickListener {

            if (isMyCollectionRecentSearchDataEmpty) {
                if (AppConstants.newHomeHashMap.size > 0) {
                    AppConstants.newHomeHashMap.clear()
                }

                AppConstants.newHomeHashMap.putAll(recentSearchHashMap)

                startActivity(
                    Intent(
                        this@DashboardActivity,
                        MainActivity::class.java
                    ).putExtra("SELECTED ITEM", 5)
                )
            } else {
                AppUtils.showCustomCenterToast(
                    this@DashboardActivity,
                    "Recent Searches not available"
                )
            }


        }


        val etFirstName = alertDialog.findViewById<EditText>(R.id.et_first_name)
        val etLastName = alertDialog.findViewById<EditText>(R.id.et_last_name)
        val etEmail = alertDialog.findViewById<EditText>(R.id.et_email)
        val etPhone = alertDialog.findViewById<EditText>(R.id.et_phone)

        profileLayoutBinding.llUpdate.setOnClickListener {
            if (etFirstName.text.toString().trim().isNotEmpty()) {
                if (etLastName.text.toString().trim().isNotEmpty()) {
                    var firstName: String = profileLayoutBinding.etFirstName.text.toString()
                    var lastName: String = profileLayoutBinding.etLastName.text.toString()
                    var name: String = profileLayoutBinding.etFirstName.text.toString()
                    var phoneNumber: String = profileLayoutBinding.etPhone.text.toString()

                    var email: String = ""
                    userInfoModel.Email?.let {
                        email = it
                    }
                    var loginType = myPreference.getLoginType()
                    var loginPassword = myPreference.getLoginPassword()
                    var jsonObject = JsonObject()
                    jsonObject.addProperty("LoginType", loginType)
                    jsonObject.addProperty("FirstName", firstName)
                    jsonObject.addProperty("LastName", lastName)
                    jsonObject.addProperty("Email", email)
                    jsonObject.addProperty("PhoneNumber", phoneNumber)
                    jsonObject.addProperty("Password", loginPassword)
                    dashboardViewModel.updateUserDetailsApi(jsonObject)

                } else {
                    showSnackbar(etFirstName, "Please enter your Last Name")
                }
            } else {
                showSnackbar(etFirstName, "Please enter your First Name")
            }

        }
        userInfoModel.FirstName?.let {
            etFirstName.setText(it.capitalize())
        }
        userInfoModel.LastName?.let {
            etLastName.setText(it.capitalize())
        }
        etFirstName.setText(etFirstName.text.toString()+" "+etLastName.text.toString())

        var email: String = ""
        userInfoModel.Email?.let {
            email = it
        }
        etEmail.setText(email)
        userInfoModel.PhoneNumber?.let {
            etPhone.setText(it)
        }


        val rl_my_details = alertDialog.findViewById<RelativeLayout>(R.id.rl_my_details)
        val el_mydetails = alertDialog.findViewById<ExpandableLayout>(R.id.el_mydetails)
        val spin_arrow_mydetails = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_mydetails)

        val rl_share = alertDialog.findViewById<RelativeLayout>(R.id.rl_share)
        val tv_share = alertDialog.findViewById<TextView>(R.id.tv_share)
        val et_share = alertDialog.findViewById<AppCompatEditText>(R.id.et_share)

        tv_share.setOnClickListener {
            val emailId: String = et_share.text.toString().trim()
            if (emailId.isNotEmpty()) {
                et_share.text?.clear()
                dashboardViewModel.checkEmailForSharing(emailId)
            }
        }
        tv_home_land_price = alertDialog.findViewById<TextView>(R.id.tv_home_land_price)
        tv_home_land_region = alertDialog.findViewById<TextView>(R.id.tv_home_land_region)
        tv_home_land_save_packages =
            alertDialog.findViewById<TextView>(R.id.tv_home_land_save_packages)

        val el_share = alertDialog.findViewById<ExpandableLayout>(R.id.el_share)
        val el_appsettings = alertDialog.findViewById<ExpandableLayout>(R.id.el_appsettings)


        val rl_my_collection = alertDialog.findViewById<RelativeLayout>(R.id.rl_my_collection)
        val el_mycollection = alertDialog.findViewById<ExpandableLayout>(R.id.el_mycollection)


        val rl_home_and_land = alertDialog.findViewById<RelativeLayout>(R.id.rl_home_and_land)
        val el_homeandland = alertDialog.findViewById<ExpandableLayout>(R.id.el_homeandland)

        val rl_home_and_design = alertDialog.findViewById<RelativeLayout>(R.id.rl_home_and_design)
        val el_homedesign = alertDialog.findViewById<ExpandableLayout>(R.id.el_homedesign)

        val rl_fav = alertDialog.findViewById<RelativeLayout>(R.id.rl_fav)

        val rl_app_setting = alertDialog.findViewById<RelativeLayout>(R.id.rl_app_setting)

        val iv_arrow_share = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_share)
        val iv_arrow_mycollection =
            alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_mycollection)
        val iv_arrow_homeandland = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_homeandland)
        val iv_arrow_homeanddesign =
            alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_homeanddesign)

        val iv_arrow_appsettings = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_appsettings)
      /*  val mydeailsview_line = alertDialog.findViewById<View>(R.id.mydetails_line)
        val appsetting_viewline = alertDialog.findViewById<View>(R.id.appsetting_viewline)*/


        val rl_logout = alertDialog.findViewById<RelativeLayout>(R.id.rl_logout)
        val tv_select = alertDialog.findViewById<TextView>(R.id.tv_select)
        tv_select.setOnClickListener {
            ImagePickerActivity.clearCache(activity);
            Utility.onProfileImageClick(activity)
        }

        fun showHideViews(position: Int) {
            when (position) {
                0 -> {
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()



                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_mydetails.isExpanded) {
                        spin_arrow_mydetails.reverse()
                        el_mydetails.collapse()
                        rl_fav
//                        mydeailsview_line.visibility=View.VISIBLE
                    } else {
                        spin_arrow_mydetails.rotate()
                        el_mydetails.expand()
//                        mydeailsview_line.visibility=View.GONE

                    }

                }
                1 -> {
                    el_mydetails.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_share.isExpanded) {
                        iv_arrow_share.reverse()
                        el_share.collapse()
                    } else {
                        iv_arrow_share.rotate()
                        el_share.expand()
                    }
                }
                2 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_mycollection.isExpanded) {
                        iv_arrow_mycollection.reverse()
                        el_mycollection.collapse()
                    } else {
                        iv_arrow_mycollection.rotate()
                        el_mycollection.expand()
                    }
                }
                3 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homedesign.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeanddesign.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_homeandland.isExpanded) {
                        iv_arrow_homeandland.reverse()
                        el_homeandland.collapse()
                    } else {
                        iv_arrow_homeandland.rotate()
                        el_homeandland.expand()
                    }
                }
                4 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_appsettings.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_appsettings.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_homedesign.isExpanded) {
                        iv_arrow_homeanddesign.reverse()
                        el_homedesign.collapse()
                    } else {
                        iv_arrow_homeanddesign.rotate()
                        el_homedesign.expand()
                    }
                }
                5 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    profileLayoutBinding.elMydisplay.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    if (el_appsettings.isExpanded) {
                        iv_arrow_appsettings.reverse()
                        el_appsettings.collapse()
//                        appsetting_viewline.visibility=View.VISIBLE

                    } else {
                        iv_arrow_appsettings.rotate()
                        el_appsettings.expand()
//                        appsetting_viewline.visibility=View.GONE

                    }
                }
                6 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()
                    profileLayoutBinding.elMyfav.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    profileLayoutBinding.ivArrowFav.reverse()

                    profileLayoutBinding.ivArrowMydisplay.reverse()

                    if (profileLayoutBinding.elMydisplay.isExpanded) {
                        profileLayoutBinding.ivArrowMydisplay.reverse()
                        profileLayoutBinding.elMydisplay.collapse()
                    } else {
                        profileLayoutBinding.ivArrowMydisplay.rotate()
                        profileLayoutBinding.elMydisplay.expand()
                    }
                }
                7 -> {
                    el_mydetails.collapse()
                    el_share.collapse()
                    el_mycollection.collapse()
                    el_homeandland.collapse()
                    el_homedesign.collapse()

                    spin_arrow_mydetails.reverse()
                    iv_arrow_share.reverse()
                    iv_arrow_mycollection.reverse()
                    iv_arrow_homeandland.reverse()
                    iv_arrow_homeanddesign.reverse()
                    profileLayoutBinding.ivArrowMydisplay.reverse()

                    if (profileLayoutBinding.elMyfav.isExpanded) {
                        profileLayoutBinding.ivArrowFav.reverse()
                        profileLayoutBinding.elMyfav.collapse()
                    } else {
                        profileLayoutBinding.ivArrowFav.rotate()
                        profileLayoutBinding.elMyfav.expand()
                    }
                }

            }
        }
        rl_share.setOnClickListener {
            showHideViews(1)
            val userId: Int = myPreference.getUserId()
            dashboardViewModel.fetchShareAccountDetails(userId)
        }
        rl_logout.setOnClickListener {
            Utility.logoutDialog(this)
        }

        rl_my_details.setOnClickListener {
            showHideViews(0)


        }
        profileLayoutBinding.rlMyCollection.setOnClickListener {
            if (isMyCollectionRecentSearchDataEmpty) {
                showHideViews(2)
                var userId: Int = myPreference.getUserId()
                // var typeId:Int =mPreferences.
                var stateId: Int = myPreference.getStateID()
                dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
            } else {
                AppUtils.showCustomCenterToast(
                    this@DashboardActivity,
                    "Recent Searches not available"
                )
            }


        }
        rl_home_and_land.setOnClickListener {
            if (isHomeAndLandRecentSearchDataEmpty) {
                showHideViews(3)
                var userId: Int = myPreference.getUserId()
                // var typeId:Int =mPreferences.
                var stateId: Int = myPreference.getStateID()
                dashboardViewModel.getRecentSearchData(userId, 1, stateId)
            } else {
                AppUtils.showCustomCenterToast(
                    this@DashboardActivity,
                    "Recent Searches not available"
                )
            }
        }
        rl_home_and_design.setOnClickListener {
            var notification = AppConstants.HOME_DESIGN_NOTIFICATION.toInt()
            if (notification == 0) {
                showNoFavoriteDialog()
            } else {
                showHideViews(4)
            }
        }
        rl_app_setting.setOnClickListener {
            showHideViews(5)

        }

        profileLayoutBinding.llMySavedDisplays.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 11)
            )
            alertDialog.dismiss()
        }

        profileLayoutBinding.rlMyDisplays.setOnClickListener {
            var notification = AppConstants.MY_SAVED_DISPLAY.toInt()
            if (notification == 0) {
                showNoFavoriteDialog()
            } else {
                showHideViews(6)
            }
        }
        profileLayoutBinding.rlFav.setOnClickListener {
            /*var notification = AppConstants.MY_SAVED_DISPLAY.toInt()
            if (notification == 0) {
                showNoFavoriteDialog()
            } else {*/

//            commented by durga laer will uncomment
//                showHideViews(7)

            startActivity(
                Intent(
                    this@DashboardActivity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 9)
            )

            alertDialog.dismiss()
//            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupViewModel() {


        spalashViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        )[SpalashViewModel::class.java]



        dashboardViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(DashboardViewModel::class.java)
        displayHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(DisplayHomesViewModel::class.java)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(ProfilePicViewModel::class.java)

//        dashboardViewModel.fetchStates()
        userId = myPreference.getUserId()
        if (userId != 0) {
            dashboardViewModel.fetchUserDetails(userId)
        }
        dashboardViewModel.fetchShareAccountDetails(userId)
        getRecentSearchForHomeLandAndMyCollection()

        setupObserver()



    }


    private fun setProfileImage(fileUri: Uri) {
      /*  Picasso.get()
            .load(fileUri.toString()).error(R.mipmap.logo_myplace)
            .into(binding.ivProfile)*/

        if (this::profileLayoutBinding.isInitialized) {
          /*  Picasso.get()
                .load(fileUri.toString()).error(R.mipmap.logo_myplace)
                .into(profileLayoutBinding.ivProfile);*/
        }
        Glide.with(this).asBitmap()
            .load(fileUri)
            .apply(
                RequestOptions().override(
                    800 // 800 pixels
                )
            )
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    val baos = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                    val data = baos.toByteArray()
                    val base64 = Base64.encodeToString(
                        data,
                        Base64.DEFAULT
                    )

                    Timber.e("Dashboard Profile--->${base64}")

                    //fastApi(myPreference.getUserId(), base64)
                    dashboardViewModel.updateImageApi(myPreference.getUserId(), base64)

                    //uploadImage(myPreference.getUserId(), base64)

                    return false
                }
            }).submit()
    }

    override fun onResume() {
        super.onResume()
        com.dmss.burbankappold.utils.AppConstants.validateVersionCode(this) {

        }

        AppConstants.selectedmaximumPriceForPriceRange=-1.0
        AppConstants.totalmaximumPrice=0
        AppConstants.totalminimumPrice=0
        AppConstants.homeLandregionsData=null
        AppConstants.SELECTED_STOREY_PREVIOUS=0
        AppConstants.SELECTED_BEDROOM_COUNT=-2
        AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.STOREYS)
        AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.BEDROOMS)
        AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.REGION)
        AppConstants.toolheaderHashMapHomeLand.remove(AppConstants.PRICERANGE)
        var myPreferences = CustomSharedPreferences(this)
        setProfileNotification()
        if (myPreference.getIsFirstTimeLogin()) {
            binding.profileProgress.visibility = View.GONE

            var userModel = myPreferences.getUserInfoModel()
            if (userModel != null  && userModel.ProfileImage !=null && userModel.ProfileImage!="") {
              /*  Picasso.get()
                    .load(userModel.ProfileImage).error(R.mipmap.logo_myplace)
                    .into(binding.ivProfile)*/
            }else{
                binding.profileProgress.visibility = View.GONE

            }

        }
//        val tvState=binding.tvState.text.toString()
//        if(tvState=="") {

        selectedStateId = myPreference.getStateID()

        println("selectedStateId:: "+selectedStateId)
            dashboardViewModel.fetchStates()

//        }

    }
    fun setProfileNotification(){
        var isUserLoggedIn = myPreference.getUserLogin()
        if (isUserLoggedIn) {
            dashboardProfileWidthDadgeBinding.profileNotification.text =
                AppConstants.TotalMyFavs.toString()
            if(AppConstants.TotalMyFavs==0) {
                dashboardProfileWidthDadgeBinding.profileNotification.visibility=View.GONE
            }else{
                dashboardProfileWidthDadgeBinding.profileNotification.visibility=View.VISIBLE

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.getParcelableExtra<Uri>("path")
                    if (uri != null) {
                        setProfileImage(uri)
                    }
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code ", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupObserver() {
        spalashViewModel.getUserLoginData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var loginModel: UserLoginModel? = it.data
                    println("getUserLoginData:: " + it.data!!.status)


                    if (loginModel != null) {
//                        if (loginModel.status) {
                        if (loginModel.token.isNotEmpty()) {
                            CustomSharedPreferences.instance.saveToken(loginModel.token)

                            showCustomDialog()
                        }
                    }
                }
            }
                })



        dashboardViewModel.getSearchTypeLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var searchTypeRecentModel: SearchTypeRecentModel? = it.data


                }
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                }
            }

        })
        dashboardViewModel.getStatesLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var stateList: ArrayList<StateModel>? = it.data

                    //SORTING BASED ON ID
                    Collections.sort(stateList, Comparator<StateModel> { o1, o2 ->
                        if (o1.stateOrder === o2.stateOrder) return@Comparator 0
                        if (o1.stateOrder!! < o2.stateOrder!!) -1 else 1
                    })

                    if (stateList != null) {
                        for (state in stateList) {
                            state.isSelected = selectedStateId == state.id
                            if (selectedStateId == state.id) {
                                binding.tvState.text = state.name
                                isStateSelectedPreviously = true
                            }
                        }
                        statesList = stateList

                        if (!isStateSelectedPreviously) {
                            showStateDialog()
                        }

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



        dashboardViewModel.getUserDetailsLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                 /*   var userModel = myPreference.getUserInfoModel()
                    println("placePicture:: "+placePicture)
                    if( userModel!=null && userModel.ProfileImage!=null) {
                        placePicture = userModel.ProfileImage!!
                        println("placePicture:: "+placePicture+" ProfileImage:: "+userModel.ProfileImage!!)
                    }*/
                    val userDetailResponseModel: UserDetailResponseModel? = it.data
                    if (userDetailResponseModel != null && userDetailResponseModel.status) {
                        if (userDetailResponseModel.userInfo.ProfileImage != "No Image") {
                            userDetailResponseModel.userInfo.ProfileImage.let {
                                if (userDetailResponseModel.userInfo.ProfileImage!=null && userDetailResponseModel.userInfo.ProfileImage.contains("~")) {
                                    placePicture =
                                        userDetailResponseModel.userInfo.ProfileImage.replace(
                                            "~",
                                            ""
                                        )
                                    placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"

                                } else {
                                    val userInfoModel = myPreference.getUserInfoModel()
                                    placePicture = userInfoModel.ProfileImage?:""
                                }
                            }
                        }else{
//                            val userInfoModel = myPreference.getUserInfoModel()
//                            placePicture = userInfoModel.ProfileImage?:""
                        }

                     /*   if (placePicture.isNotEmpty()) {
                            Picasso.get().load(placePicture).error(R.mipmap.logo_myplace)
                                .into(
                                    binding.ivProfile,
                                    object : com.squareup.picasso.Callback {
                                        override fun onSuccess() {
                                            binding.profileProgress.visibility = View.GONE
                                        }

                                        override fun onError(e: Exception?) {
                                            binding.profileProgress.visibility = View.GONE
                                        }


                                    })
                        }*/


                        Timber.e("Api user Name (getUserDetailsLiveData()) ::${userDetailResponseModel.userInfo.name}")
                        val userInfoModel = UserInfoModel(
                            "",
                            userDetailResponseModel.userInfo.FirstName,
                            userDetailResponseModel.userInfo.LastName,
                            userDetailResponseModel.userInfo.Email,
                            placePicture,
                            "", userDetailResponseModel.userInfo.PhoneNumber
                        )

                        myPreference.saveUserInfoModel(userInfoModel)
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

        dashboardViewModel.getShareAccountLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()

                    val shareAccountModel: ShareAccountModel? = it.data
                    if (shareAccountModel != null) {
                        val shareList: ArrayList<ShareListModel> =
                            shareAccountModel.shareListObj.shareList
                        myPreference.saveShareAccountNotificationNumber(shareAccountModel.shareListObj.count)


                        for (shareListModel in shareList) {
                            if (!shareListModel.favouriteAdded) {
                                Timber.e("Favorites Not Added :  ${shareListModel.email}")
                                showPendingShareAccountDialog(shareListModel)
                            }

                        }

                        if (this::relative_share_list.isInitialized) {
                            if (shareList.size > 0) {
                                relative_share_list.visibility = View.VISIBLE
                                shareAdapter.setData(shareList)
                            } else {
                                relative_share_list.visibility = View.GONE
                            }
                        }
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
        dashboardViewModel.getCheckEmailSharingLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var checkEmailForSharingModel: CheckEmailForSharingModel? = it.data
                    if (checkEmailForSharingModel != null) {
                        showSharingAccountDialog(checkEmailForSharingModel)
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

        dashboardViewModel.getShareAccWithEmailLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    val shareAccountModel: ShareAccountModel? = it.data
                    if (shareAccountModel != null) {
                        val shareList: ArrayList<ShareListModel> =
                            shareAccountModel.shareListObj.shareList

                        if (shareList.size > 0) {
                            if (this::relative_share_list.isInitialized) {
                                relative_share_list.visibility = View.VISIBLE
                                shareAdapter.setData(shareList)
                            }
                        } else {
                            if (this::relative_share_list.isInitialized) {
                                relative_share_list.visibility = View.GONE
                            }
                        }
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
        dashboardViewModel.getRecentSearchLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var regionsData: RecentSearchDataResponseModel? = it.data
                    if (regionsData != null && regionsData.status) {

                        recentSearchJsonModel = regionsData.searchJsonModel

                        regionsData.searchJsonModel?.let { model ->
                            AppConstants.minimumPriceForPriceRange = model.minPrice
                            AppConstants.maximumPriceForPriceRange = model.maxPrice

                        }

                        if (regionsData.status) {
                            isHomeAndLandRecentSearchDataEmpty = true
                            Timber.e("HomeLand Notification count ---${regionsData.userfavourites}")
                            AppConstants.HOME_AND_LAND_NOTIFICATION =
                                regionsData.userfavourites.toString()


                            AppConstants.TotalMyFavs=AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.MY_SAVED_DISPLAY.toInt()
                            setProfileNotification()
                            AppConstants.MIN_PRICE =
                                regionsData.searchJsonModel?.minPrice.toString()
                            AppConstants.MAX_PRICE =
                                regionsData.searchJsonModel?.maxPrice.toString()


                            var minPrice: Double =
                                (regionsData.searchJsonModel?.minPrice ?: 0.0)
                            var maxPrice: Double =
                                (regionsData.searchJsonModel?.maxPrice ?: 0.0)

                            var minValue = minPrice.toInt()
                            var maxValue = maxPrice.toInt()
                            var totalPrice: String =
                                "$${minValue / 1000}K to $${maxValue / 1000}K"



                            if (this::profileLayoutBinding.isInitialized) {
                                profileLayoutBinding.tvHomeLandPrice.text = totalPrice

                                if (regionsData.userfavourites == 0) {
                                    profileLayoutBinding.tvHomeLandSavePackages.text =
                                        ("NO SAVED PACKAGES")
                                    profileLayoutBinding.tvHomeandlandNotification.text = "0"
                                } else {
                                    profileLayoutBinding.tvHomeLandSavePackages.text =
                                        ("${regionsData.userfavourites} SAVED PACKAGES")
                                    if (regionsData.userfavourites != null) {
                                        profileLayoutBinding.tvHomeandlandNotification.text =
                                            regionsData.userfavourites.toString()
                                    }
                                }

//                                AppConstants.TotalMyFavs= regionsData.userfavourites

                                if (regionsData.searchJsonModel?.regionsList?.size ?: 0 > 0) {
                                    var regionData: String =
                                        regionsData.searchJsonModel?.regionsList!![0].regionName
                                    val regionText = "Region &nbsp; <font color=#5c5e5e>$regionData</font>"
                                    profileLayoutBinding.tvHomeLandRegion.text = Html.fromHtml(regionText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                    AppConstants.SELECTED_REGION = regionData

                                    val bedRooms = regionsData.searchJsonModel?.BedRoomFilters?.filter { it.IsChecked }
                                    when(bedRooms?.size){
                                        1 ->{
                                            bedRooms.forEach {
                                                profileLayoutBinding.tvBedrooms.text = it.displayName
                                            }
                                        }
                                        2 ->{
                                            profileLayoutBinding.tvBedrooms.text = "5+"
                                        }
                                        else ->{
                                            profileLayoutBinding.tvBedrooms.text = "All"
                                        }
                                    }
                                }

                                //tvStorey

                                if (regionsData.searchJsonModel?.StoreyFilters?.size ?: 0 > 0) {
                                    var storeyString = ""
                                    val storeyFilter = regionsData.searchJsonModel?.StoreyFilters?.filter { it.IsChecked }
                                    if (storeyFilter!!.size>1) storeyString = "All"
                                    else{
                                        for (searchSubModel in regionsData.searchJsonModel?.StoreyFilters!!) {
                                            if (searchSubModel.IsChecked) {
                                                storeyString = searchSubModel.displayName
                                            }
                                        }
                                    }
                                    profileLayoutBinding.tvStorey.text = storeyString
                                }

                            }
                        } else {
                            if (regionsData.FetchData != null) {
                                if (this::profileLayoutBinding.isInitialized) {
                                    showSnackbar(
                                        profileLayoutBinding.llMyCollection,
                                        regionsData.FetchData
                                    )
                                }
                            }
                        }

                    } else {
                        AppConstants.HOME_AND_LAND_NOTIFICATION = "0"
                        isHomeAndLandRecentSearchDataEmpty = false
                    }

                }
                Status.LOADING -> {
                    dismissProgressDialog()
                }
                Status.ERROR -> {
                    isHomeAndLandRecentSearchDataEmpty = false
                    dismissProgressDialog()
                }
            }
        })
        dashboardViewModel.getRecentSearchMyCollectionLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var regionsData: MyCollectionRecentSearchModel? = it.data
                    if (regionsData != null && regionsData.status) {
                        isMyCollectionRecentSearchDataEmpty = true
                        AppConstants.HOME_DESIGN_NOTIFICATION =
                            regionsData.userfavourites.toString()
                        AppConstants.TotalMyFavs=AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.MY_SAVED_DISPLAY.toInt()
                        setProfileNotification()
                        val newHomeJsonArrayList = ArrayList<NewHomeJsonObject>()

                        if (regionsData.searchJsonList.size > 0) {
                            for (myCollectionRecentSearchAnswerModel: MyCollectionRecentSearchAnswerModel in regionsData.searchJsonList) {
                                val newHomeJsonObject: NewHomeJsonObject? =
                                    if (myCollectionRecentSearchAnswerModel.minValue == null && myCollectionRecentSearchAnswerModel.maxValue == null) {
                                        NewHomeJsonObject(
                                            myCollectionRecentSearchAnswerModel.feature,
                                            myCollectionRecentSearchAnswerModel.question,
                                            myCollectionRecentSearchAnswerModel.answer
                                        )
                                    } else {
                                        NewHomeJsonObject(
                                            myCollectionRecentSearchAnswerModel.feature,
                                            myCollectionRecentSearchAnswerModel.question,
                                            myCollectionRecentSearchAnswerModel.answer,
                                            myCollectionRecentSearchAnswerModel.minValue,
                                            myCollectionRecentSearchAnswerModel.maxValue
                                        )
                                    }
                                if (newHomeJsonObject != null) {
                                    if (newHomeJsonObject.feature != "resultsCount") {
                                        newHomeJsonArrayList.add(newHomeJsonObject)
                                    } else {
                                        if (this::profileLayoutBinding.isInitialized) {
                                            profileLayoutBinding.tvMyCollectionNotification.text =
                                                newHomeJsonObject.answer.toString()
                                        }
                                    }

                                }

                            }
                        }
                        recentSearchHashMap[AppConstants.MYCOLLECTION_RECENT_SEARCH_KEY] =
                            newHomeJsonArrayList


                        if (this::profileLayoutBinding.isInitialized) {
                            profileLayoutBinding.ivHomedesignNotification.text =
                                regionsData.userfavourites.toString()


                            if (regionsData.userfavourites == 0) {
                                profileLayoutBinding.tvHomeDesignSaved.text =
                                    ("NO SAVED DESIGNS")
                            } else {
                                profileLayoutBinding.tvHomeDesignSaved.text =
                                    ("${regionsData.userfavourites} SAVED DESIGNS")
                            }
                        }

//                        AppConstants.TotalMyFavs= AppConstants.TotalMyFavs+regionsData.userfavourites

                        myCollectionRecentSearchText = ""

                        if (regionsData.searchJsonList.size > 0) {

                            if (regionsData.searchJsonList.size > 0) {
                                for (resultCountModel in regionsData.searchJsonList) {
                                    if (resultCountModel.feature == "resultsCount") {

                                        if (resultCountModel.answer.isNotEmpty()) {
                                            AppConstants.MY_COLLECTION_NOTIFICATION =
                                                resultCountModel.answer.toString()
                                            // myCollectionRecentSearchText = resultCountModel.feature
                                            if (this::profileLayoutBinding.isInitialized) {
//                                                profileLayoutBinding.ivMyDetailsNotification.text =
//                                                    resultCountModel.answer
                                                profileLayoutBinding.tvMyCollectionSaveDesign.text =
                                                    ("${resultCountModel.answer} DESIGNS")
                                            }
                                        } else {
                                            AppConstants.MY_COLLECTION_NOTIFICATION = "0"
                                        }
                                    }

                                }


                            }


                            var arrayList: ArrayList<MyCollectionRecentSearchAnswerModel> =
                                regionsData.searchJsonList

                            val isLotWidthAvailable = regionsData.searchJsonList.any { it.feature == "Lot Width"}

                            for (myCollectionRecentSearchAnswerModel in regionsData.searchJsonList.filter { it.answer != "I don't mind" }) {

                                when (myCollectionRecentSearchAnswerModel.feature) {
                                    "Lot Width" -> {
                                        if (myCollectionRecentSearchAnswerModel.maxValue.isNotEmpty()) {
                                            var answer = AppUtils.roundRemainingString(
                                                myCollectionRecentSearchAnswerModel.maxValue
                                            )
                                            myCollectionRecentSearchText += "$answer" + "M"
                                        }
                                    }
                                    "Storeys" -> {
                                        var storey = ""
                                        if (myCollectionRecentSearchAnswerModel.answer == "1") {
                                            storey = if (isLotWidthAvailable) " | Single" else "Single"
                                        } else if (myCollectionRecentSearchAnswerModel.answer == "2") {
                                            storey = if (isLotWidthAvailable) " | Double" else "Double"
                                        }
                                        myCollectionRecentSearchText += storey

                                    }
                                    "No Of Bedrooms" -> {
                                        var bedRooms = ""
                                        when (myCollectionRecentSearchAnswerModel.answer) {
                                            "3" -> {
                                                bedRooms = "3 Bed"
                                            }
                                            "4" -> {
                                                bedRooms = "4 Bed"

                                            }
                                            "5" -> {
                                                bedRooms = "5 Bed"
                                            }
                                        }
                                        myCollectionRecentSearchText += " | $bedRooms"

                                    }
                                    "Grand Alfresco" -> {
                                        myCollectionRecentSearchText += " | Alfresco"

                                    }
                                    "Storage (more than 1 per room)" -> {
                                        myCollectionRecentSearchText += "| Storage (more than 1 per room)"
                                    }
                                    "European Laundry" -> {
                                        myCollectionRecentSearchText += " | European Laundry"
                                    }
                                    "Separate Kids Living Area" -> {
                                        myCollectionRecentSearchText += " | Separate Kids Living Area"
                                    }
                                    "Separate Living Area" -> {
                                        myCollectionRecentSearchText += " | Separate Living Area"
                                    }
                                    "Straight Corridor" -> {
                                        myCollectionRecentSearchText += " | Straight Corridor"

                                    }
                                    "Living/Meals Entire Rear" -> {
                                        myCollectionRecentSearchText += " | Living/Meals Entire Rear"

                                    }
                                    "Study" -> {
                                        myCollectionRecentSearchText += " | Study"

                                    }
                                    "Minor Bedrooms Wing" -> {
                                        myCollectionRecentSearchText += " | Minor Bedrooms Wing"

                                    }
                                    "Bedroom At Front" -> {
                                        myCollectionRecentSearchText += " | Bedroom At Front"

                                    }
                                    "Price" -> {
                                        myCollectionRecentSearchText += " | Price"

                                    }

                                }

                                if (myCollectionRecentSearchAnswerModel.feature != "resultsCount") {
                                    var newHomeJsonObject = NewHomeJsonObject(
                                        myCollectionRecentSearchAnswerModel.feature,
                                        myCollectionRecentSearchAnswerModel.question,
                                        myCollectionRecentSearchAnswerModel.answer
                                    )
                                    var arrayList: ArrayList<NewHomeJsonObject> = ArrayList()
                                    arrayList.add(newHomeJsonObject)

                                    AppConstants.newHomeHashMap[myCollectionRecentSearchAnswerModel.feature] =
                                        arrayList
                                }

                                if (regionsData.searchJsonList.isNotEmpty()) {
                                    if (regionsData.searchJsonList.size == 2) {
                                        var model = regionsData.searchJsonList[1]
                                        if (model.feature == "resultsCount") {
                                            myCollectionRecentSearchText =
                                                "Take a Quick Survey to Find Your\nPerfect Design"
                                        }
                                    } else if (regionsData.searchJsonList.size == 1) {
                                        var model = regionsData.searchJsonList[0]
                                        if (model.feature == "resultsCount") {
                                            myCollectionRecentSearchText =
                                                "Take a Quick Survey to Find Your\nPerfect Design"
                                        }
                                    }
                                }
                            }
                        }

                        isMyCollectionRecentSearchDataEmpty =
                            myCollectionRecentSearchText.isNotEmpty()

                        if (this::profileLayoutBinding.isInitialized) {
                            profileLayoutBinding.tvMyCollectionQuiz.text =
                                myCollectionRecentSearchText
                        }

                    } else {
                        AppConstants.MY_COLLECTION_NOTIFICATION = "0"
                        AppConstants.HOME_DESIGN_NOTIFICATION = "0"
                        isMyCollectionRecentSearchDataEmpty = false
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
        dashboardViewModel.getProfilePicLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var regionsData: UpdateProfilePicModel? = it.data
                    if (regionsData != null && regionsData.Status == true) {
                        if (regionsData.file != null) {
                            placePicture = ""
                            if (regionsData.file!!.contains("~")) {
                                placePicture = regionsData.file!!.replace("~", "")
                                placePicture = "${AppConstants.PROFILEPIC_BASE}${placePicture}"
                            } else {
                                placePicture = regionsData.file!!
                            }
                        }
                        Picasso.get().load(placePicture).error(R.mipmap.logo_myplace)
                            .into(binding.ivProfile, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    binding.profileProgress.visibility = View.GONE
                                }

                                override fun onError(e: Exception?) {
                                    binding.profileProgress.visibility = View.GONE
                                }


                            })


                        val appEvent = AppEvent(
                            AppEvent.UPDATE_PROFILE_PIC,
                            placePicture

                        )
                        EventBus.getDefault().post(appEvent)


                        var userInfoModel = myPreference.getUserInfoModel()
                        myPreference.saveUserInfoModel(
                            UserInfoModel(
                                "",
                                userInfoModel.FirstName,
                                userInfoModel.LastName,
                                userInfoModel.Email,
                                placePicture, "", ""
                            )
                        )

                        if (this::profileLayoutBinding.isInitialized) {
                            regionsData.Message?.let { it1 ->
                                showSnackbar(
                                    profileLayoutBinding.tvSelect,
                                    it1
                                )
                            }
                        }

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

        dashboardViewModel.getUpdateUser().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var regionsData: UserInfo? = it.data
                    if (regionsData != null) {

                        if (myPreference.getUserId() != 0) {
                            dashboardViewModel.fetchUserDetails(myPreference.getUserId())
                        }
                        if (regionsData.Message.isNotEmpty()) {
                            var customMessage = "Profile Updated Successfully"
                            AppUtils.showCustomCenterToast(
                                profileLayoutBinding.llUpdate.context,
                                customMessage
                            )
                        }
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

        displayHomeViewModel.getUserFavoritesDisplaysLiveData.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    customProgressDialog?.dismissProgress()
                    if (it.data != null) {
                        if (it.data.status || !it.data.status) {
                            var totalResponse: UserFavoriteDisplaysResponseModel = it.data
                            if (totalResponse.userFavorites != null && totalResponse.userFavorites.size > 0) {

                                AppConstants.MY_SAVED_DISPLAY =
                                    totalResponse.userFavorites.size.toString()

                                Timber.e("My Saved Displays API ${AppConstants.MY_SAVED_DISPLAY}")
                                AppConstants.TotalMyFavs=AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.MY_SAVED_DISPLAY.toInt()
                                setProfileNotification()
                                if (this::profileLayoutBinding.isInitialized) {
                                    profileLayoutBinding.tvMySavedDisplays.text =
                                        ("${AppConstants.MY_SAVED_DISPLAY} SAVED DISPLAYS")
                                    profileLayoutBinding.tvMydisplaysNotification.text =
                                        AppConstants.MY_SAVED_DISPLAY

                                    Timber.e("My Saved Displays1 ${AppConstants.MY_SAVED_DISPLAY}")
                                }
                            } else {
                                AppConstants.MY_SAVED_DISPLAY = "0"
                                if (this::profileLayoutBinding.isInitialized) {
                                    profileLayoutBinding.tvMydisplaysNotification.text = "0"
                                }

                            }
                            AppConstants.TotalMyFavs=AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+AppConstants.MY_SAVED_DISPLAY.toInt()
                            setProfileNotification()


                        }
                    }
                }
                Status.LOADING -> {
                    customProgressDialog?.showProgress()
                }
                Status.ERROR -> {
                    customProgressDialog?.dismissProgress()
                }
            }

        })


    }

    private fun showPendingShareAccountDialog(shareListModel: ShareListModel) {
        sharingAccountBinding = LayoutEmailSharingDialogBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        shareAlertBuilder.setView(sharingAccountBinding.root)
        val alertDialog: AlertDialog = shareAlertBuilder.create()
        sharingAccountBinding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        var isCheck = false
        sharingAccountBinding.ivCheck.setOnClickListener {
            isCheck = !isCheck
            if (isCheck) {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardActivity,
                        R.drawable.check_share
                    )
                )
            } else {
                sharingAccountBinding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardActivity,
                        R.drawable.uncheck_share
                    )
                )
            }
        }
        sharingAccountBinding.tvFullName.text = shareListModel.fullName
        sharingAccountBinding.tvEmail.text = shareListModel.email
        sharingAccountBinding.tvIAgree.text =
            "I agree for sharing my favourites\nwith ${shareListModel.fullName}"

        Picasso.get().load(R.drawable.profile_user).error(R.mipmap.logo_myplace)

            .into(sharingAccountBinding.cvProfile, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    binding.profileProgress.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.profileProgress.visibility = View.GONE
                }


            })

        sharingAccountBinding.tvConfirm.setOnClickListener {
            if (isCheck) {
                alertDialog.dismiss()
                val userID: Int = myPreference.getUserId();
                val jsonObj = JsonObject()
                if (shareListModel != null) {
                    jsonObj.addProperty("OthersEmail", shareListModel.email)
                    jsonObj.addProperty("FavouriteAdded", true)
                    jsonObj.addProperty("ShareAccount", 2)
                    dashboardViewModel.fetchShareAccountWithEmail(userID, jsonObj)
                }
            } else {
                showSnackbar(sharingAccountBinding.tvConfirm, "Please agree the sharing")
            }
        }
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

    }

    override fun selectState(position: Int) {
        if (statesList.size > 0) {
            val stateModel: StateModel = statesList[position]
            var stateId = myPreference.getStateID()
            if (stateId == stateModel.id) {
                binding.tvState.text = stateModel.name
                stateModel.name?.let { myPreference.selectedState(it) }
                stateModel.id?.let {
                    myPreference.setStateID(it)
                }
            } else {
                if (myPreference.getIsFirstTimeLogin()) {
                    showAreYouSureDialog(stateModel)
                } else {
                    myPreference.setFirstTimeAppLogin(true)
                    binding.llStateSelection.visibility = View.VISIBLE
                    binding.tvState.text = stateModel.name
                    AppUtils.showCustomCenterToast(
                        this@DashboardActivity,
                        "Selected State: ${stateModel.name}"
                    )

                    stateModel.id?.let {
                        myPreference.setStateID(it)
                        displayHomeViewModel.getUserFavoritesDisplays(userId,  it)
                        dashboardViewModel.getRecentSearchData(userId, 1, it)
                        dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, it)
                    }
                    stateModel.name?.let {
                        myPreference.selectedState(it)
                    }
                    stateModel.id?.let {
                        myPreference.selectedStateID(it.toString())
                    }
                }


            }
            if (stateAlertDialog.isShowing) {
                stateAlertDialog.dismiss()
            }

            for (state in statesList) {
                state.isSelected = stateModel.id == state.id
            }
            stateAdapter.notifyDataSetChanged()
        }
    }

    private fun showAreYouSureDialog(state: StateModel) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to change the state?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>YES</font>")) { dialog, _ ->
                binding.llStateSelection.visibility = View.VISIBLE
                binding.tvState.text = state.name
                state.id?.let { myPreference.setStateID(it) }
                state.name?.let { myPreference.selectedState(it) }
                stateAlertDialog.dismiss()
                dialog.dismiss()
                AppUtils.showCustomCenterToast(
                    this@DashboardActivity,
                    "Selected State: ${state.name}"
                )
                getRecentSearchForHomeLandAndMyCollection()
            }
            .setNegativeButton(Html.fromHtml("<font color='${resources.getColor(R.color.black_bg_3_1)}'>NO</font>")) { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Select ${state.name}?")
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

    public fun getRecentSearchForHomeLandAndMyCollection() {
        val userId: Int = myPreference.getUserId()
        val stateId: Int = myPreference.getStateID()
        dashboardViewModel.getRecentSearchData(userId, 1, stateId)
        getUserFavoritesDisplays()
        dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
    }


    override fun selectedFavoriteItem(shareListModel: ShareListModel) {
        /*Need to update Share account Favorite and Unfavorite*/

    }

    override fun selectedDeleteItem(shareListModel: ShareListModel) {
        shareDeleteConfirmationDialog(shareListModel)
    }

    private fun showSnackbar(view: View, message: String) {
        val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.show()
    }

    private fun shareDeleteConfirmationDialog(shareListModel: ShareListModel) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure want to delete?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>YES</font>")) { dialog, id ->
                dialog.dismiss()
                val userID: Int = myPreference.getUserId();
                val jsonObj = JsonObject()
                jsonObj.addProperty("OthersEmail", shareListModel.email)
                jsonObj.addProperty("FavouriteAdded", shareListModel.favouriteAdded)
                jsonObj.addProperty("ShareAccount", 0)
                dashboardViewModel.fetchShareAccountWithEmail(userID, jsonObj)
            }
            // negative button text and action
            .setNegativeButton("<font color='${resources.getColor(R.color.black_bg_3_1)}'>NO</font>")  { dialog, id ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Delete")
        // show alert dialog
        alert.show()
    }
    private fun callLoginData(){
//        customSharedPreferences.saveToken("")
        spalashViewModel.fetchLoginData()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppEvent(event: AppEvent) {
        if (event.type == AppEvent.UNAUTHARIZED) {
//            showCustomDialog()

            callLoginData()
        } else if (event.type == AppEvent.SIGNUP) {
            Timber.e("Dashboard SignUp")

        } else if (event.type == AppEvent.UPDATE_PROFILE_PIC) {

            Picasso.get()
                .load(event.data).error(R.mipmap.logo_myplace)
                .into(binding.ivProfile)
            binding.profileProgress.visibility = View.GONE
            if (this::profileLayoutBinding.isInitialized) {
                Picasso.get()
                    .load(event.data).error(R.mipmap.logo_myplace)
                    .into(profileLayoutBinding.ivProfile);
                binding.profileProgress.visibility = View.GONE
            }


        } else if (event.type == AppEvent.DISPLAYHOMES_EVENT) {
            binding.llDisplayHomes.performClick()
        }

    }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    /*private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.e("Login Success")
            Toast.makeText(this@DashboardActivity, "Logged in Successfully", Toast.LENGTH_SHORT)
                .show()
        }
    }*/


    fun getCurrentLocation() {
        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                        locationResult.lastLocation

                    AppConstants.dummyLatitude = location.latitude.toString()
                    AppConstants.dummyLongitude = location.longitude.toString()

                }


            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())


    }


    private fun locationPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    getCurrentLocation()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        // navigate user to app settings
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


}




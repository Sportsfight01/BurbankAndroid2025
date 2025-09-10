package com.dmss.burbankapp.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.NewHomeJsonObject
import com.dmss.burbankapp.data.model.SearchJsonModel
import com.dmss.burbankapp.databinding.ProfileLayoutBinding
import com.dmss.burbankapp.ui.dashboard.DashboardViewModel
import com.dmss.burbankapp.ui.dashboard.ShareListAdapter
import com.dmss.burbankapp.ui.main.MainActivity
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.utility.ExpandableLayout
import com.dmss.burbankapp.utility.ImagePickerActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.SpinnerImage
import com.dmss.burbankapp.utils.customviews.Utility
import com.dmss.burbankapp.viewmodel.ProfilePicViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.HashMap
import java.util.LinkedHashMap

class ProfileDialog {
    lateinit var profileLayoutBinding: ProfileLayoutBinding;
    lateinit var myPreference: CustomSharedPreferences
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var iv_profile_dialog: ImageView
    var recentSearchHashMap: HashMap<String, ArrayList<NewHomeJsonObject>> = LinkedHashMap()
    private lateinit var profileProgress: ProgressBar
    var recentSearchJsonModel: SearchJsonModel?=null
    private lateinit var relative_share_list: RelativeLayout
    lateinit var shareAdapter: ShareListAdapter
    var isMyCollectionRecentSearchDataEmpty: Boolean = true
    var isHomeAndLandRecentSearchDataEmpty: Boolean = true
    private lateinit var tv_home_land_price: TextView
    private lateinit var tv_home_land_region: TextView
    private lateinit var tv_home_land_save_packages: TextView
    private lateinit var myCollectionRecentSearchText: String
    lateinit var dashboardViewModel: DashboardViewModel
    lateinit var displayHomeViewModel: DisplayHomesViewModel
    lateinit var viewModel: ProfilePicViewModel;
    var userId: Int = 0

    /*private fun showProfileDialog(activity: Activity) {
        myPreference = CustomSharedPreferences(activity)

        profileLayoutBinding = ProfileLayoutBinding.inflate(activity.layoutInflater)
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
        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

        val window: Window = alertDialog.getWindow()!!
        val currentDialogAttributes: WindowManager.LayoutParams = window.getAttributes()
        val newDialogAttributes = WindowManager.LayoutParams()
        newDialogAttributes.copyFrom(currentDialogAttributes)
        newDialogAttributes.width = WindowManager.LayoutParams.MATCH_PARENT
        newDialogAttributes.height = WindowManager.LayoutParams.MATCH_PARENT
//        newDialogAttributes.gravity = Gravity.TOP
        window.attributes = newDialogAttributes

        iv_profile_dialog = alertDialog.findViewById<CircularImageView>(R.id.iv_profile)
        iv_profile_dialog.setOnClickListener {
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

            activity.startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 9)
            )
            alertDialog.dismiss()
        }

        profileLayoutBinding.llMyHomeAndDesign.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 6)
            )

            alertDialog.dismiss()
        }
        profileLayoutBinding.llMyfav.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 9)
            )

            alertDialog.dismiss()
        }
        profileLayoutBinding.llHomandlandsearchview.setOnClickListener {
            recentSearchJsonModel?.let {
                AppConstants.setRecentSearchData(it)
            }
            activity.startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 7)
            )
            alertDialog.dismiss()
        }
        profileLayoutBinding.llMyHomeAndLand.setOnClickListener {
            activity.startActivity(
                Intent(
                    activity,
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


        *//* profileLayoutBinding.tvHomeLandSavePackages.text =
             ("${myPreference.getHomeAndLandnNotification().toString()} SAVED PACKAGES")*//*


        profileLayoutBinding.tvMyCollectionSaveDesign.text =
            ("${AppConstants.MY_COLLECTION_NOTIFICATION} DESIGNS")

        profileLayoutBinding.tvHomeDesignSaved.text =
            ("${AppConstants.HOME_DESIGN_NOTIFICATION} SAVED DESIGNS")


        profileLayoutBinding.tvMySavedDisplays.text =
            ("${AppConstants.MY_SAVED_DISPLAY} SAVED DISPLAYS")
        profileLayoutBinding.tvMydisplaysNotification.text = AppConstants.MY_SAVED_DISPLAY
        AppConstants.TotalMyFavs=
            AppConstants.HOME_AND_LAND_NOTIFICATION.toInt()+ AppConstants.HOME_DESIGN_NOTIFICATION.toInt()+ AppConstants.MY_SAVED_DISPLAY.toInt()
        println("TotalMyFavs::"+ AppConstants.TotalMyFavs)
        profileLayoutBinding.favNotification.text=""+ AppConstants.TotalMyFavs
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
                ShareListAdapter(activity, ArrayList(), this)
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

                activity.startActivity(
                    Intent(
                        activity,
                        MainActivity::class.java
                    ).putExtra("SELECTED ITEM", 5)
                )
            } else {
                AppUtils.showCustomCenterToast(
                    activity,
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


        val rl_app_setting = alertDialog.findViewById<RelativeLayout>(R.id.rl_app_setting)

        val iv_arrow_share = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_share)
        val iv_arrow_mycollection =
            alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_mycollection)
        val iv_arrow_homeandland = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_homeandland)
        val iv_arrow_homeanddesign =
            alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_homeanddesign)
        val iv_arrow_appsettings = alertDialog.findViewById<SpinnerImage>(R.id.iv_arrow_appsettings)


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
                    } else {
                        spin_arrow_mydetails.rotate()
                        el_mydetails.expand()
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
                    } else {
                        iv_arrow_appsettings.rotate()
                        el_appsettings.expand()
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
//                dashboardViewModel.getRecentSearchDataMyCollection(userId, 2, stateId)
            } else {
                AppUtils.showCustomCenterToast(
                    activity,
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
//                dashboardViewModel.getRecentSearchData(userId, 1, stateId)
            } else {
                AppUtils.showCustomCenterToast(
                    activity,
                    "Recent Searches not available"
                )
            }
        }
        rl_home_and_design.setOnClickListener {
            var notification = AppConstants.HOME_DESIGN_NOTIFICATION.toInt()
            if (notification == 0) {
//                showNoFavoriteDialog()
            } else {
                showHideViews(4)
            }
        }
        rl_app_setting.setOnClickListener {
            showHideViews(5)

        }

        profileLayoutBinding.llMySavedDisplays.setOnClickListener {
           activity.startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                ).putExtra("SELECTED ITEM", 11)
            )
            alertDialog.dismiss()
        }

        profileLayoutBinding.rlMyDisplays.setOnClickListener {
            var notification = AppConstants.MY_SAVED_DISPLAY.toInt()
            if (notification == 0) {
//                showNoFavoriteDialog()
            } else {
                showHideViews(6)
            }
        }
        profileLayoutBinding.rlFav.setOnClickListener {
            *//*var notification = AppConstants.MY_SAVED_DISPLAY.toInt()
            if (notification == 0) {
                showNoFavoriteDialog()
            } else {*//*
            showHideViews(7)
//            }
        }


    }
    private fun showSnackbar(view: View, message: String) {
        val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.show()
    }
    private fun setupViewModel(context: AppCompatActivity) {
        dashboardViewModel = ViewModelProviders.of(
            context,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(context))
            )
        ).get(DashboardViewModel::class.java)
        displayHomeViewModel = ViewModelProviders.of(
            context,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(context))
            )
        ).get(DisplayHomesViewModel::class.java)

        viewModel = ViewModelProviders.of(
            context,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(context))
            )
        ).get(ProfilePicViewModel::class.java)

        dashboardViewModel.fetchStates()
        userId = myPreference.getUserId()
        if (userId != 0) {
            dashboardViewModel.fetchUserDetails(userId)
        }
        dashboardViewModel.fetchShareAccountDetails(userId)
//        setupObserver()



    }*/
}